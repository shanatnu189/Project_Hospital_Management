package project.hospitalManagement.Project.Service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import project.hospitalManagement.Project.dto.AppointmentResponseDto;
import project.hospitalManagement.Project.dto.CreateAppointmentRequestDto;
import project.hospitalManagement.Project.Entity.Appointment;
import project.hospitalManagement.Project.Entity.Doctor;
import project.hospitalManagement.Project.Entity.Patient;
import project.hospitalManagement.Project.Repository.AppointmentRepository;
import project.hospitalManagement.Project.Repository.DoctorRepository;
import project.hospitalManagement.Project.Repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    // ===== 1. CREATE APPOINTMENT =====
    @Transactional
    @Secured("ROLE_PATIENT")
    @Caching(
            put = @CachePut(value = "appointments", key = "#result.id"),
            evict = {
                    @CacheEvict(value = "appointments", key = "'doctor_' + #createAppointmentRequestDto.doctorId"),
                    @CacheEvict(value = "appointments", key = "'patient_' + #createAppointmentRequestDto.patientId")
            }
    )
    public AppointmentResponseDto createNewAppointment(CreateAppointmentRequestDto createAppointmentRequestDto) {
        log.info("Creating new appointment - Will be cached");

        Long doctorId = createAppointmentRequestDto.getDoctorId();
        Long patientId = createAppointmentRequestDto.getPatientId();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID: " + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with ID: " + doctorId));
        Appointment appointment = Appointment.builder()
                .reason(createAppointmentRequestDto.getReason())
                .appointmentTime(createAppointmentRequestDto.getAppointmentTime())
                .build();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        patient.getAppointments().add(appointment);

        appointment = appointmentRepository.save(appointment);
        return modelMapper.map(appointment, AppointmentResponseDto.class);
    }

    // ===== 2. GET APPOINTMENT BY ID =====
    @Cacheable(value = "appointments", key = "#appointmentId")
    public AppointmentResponseDto getAppointmentById(Long appointmentId) {
        log.info("⚠️ CACHE MISS - Fetching from DATABASE: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with ID: " + appointmentId));

        return modelMapper.map(appointment, AppointmentResponseDto.class);
    }

    // ===== 3. RE-ASSIGN APPOINTMENT (FIXED) =====
    @Transactional
    @PreAuthorize("hasAuthority('appointment:write') or #doctorId == authentication.principal.id) ")
    @Caching(
            put = @CachePut(value = "appointments", key = "#appointmentId"),
            evict = {
                    @CacheEvict(value = "appointments", key = "'doctor_' + #oldDoctorId"),  // ✅ Now oldDoctorId exists!
                    @CacheEvict(value = "appointments", key = "'doctor_' + #doctorId")
            }
    )
    public AppointmentResponseDto reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {  // ✅ Changed return type
        log.info("Re-assigning appointment {} to doctor {}", appointmentId, doctorId);

        // Get appointment first
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with ID: " + appointmentId));

        // ✅ GET OLD DOCTOR ID BEFORE CHANGING
        Long oldDoctorId = appointment.getDoctor().getId();
        log.info("Moving from doctor {} to doctor {}", oldDoctorId, doctorId);

        // Get new doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with ID: " + doctorId));

        // Update appointment
        appointment.setDoctor(doctor);
        doctor.getAppointments().add(appointment);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // ✅ Return DTO instead of entity
        return modelMapper.map(updatedAppointment, AppointmentResponseDto.class);
    }

    // ===== 4. GET ALL APPOINTMENTS OF DOCTOR =====
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('DOCTOR') AND #doctorId == authentication.principal.id) ")
    @Cacheable(value = "appointments", key = "'doctor_' + #doctorId")
    public List<AppointmentResponseDto> getAllAppointmentsOfDoctor(Long doctorId) {
        log.info("⚠️ CACHE MISS - Fetching doctor appointments from DATABASE: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with ID: " + doctorId));

        return doctor.getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }

    // ===== 5. GET APPOINTMENTS BY PATIENT =====
    @Cacheable(value = "appointments", key = "'patient_' + #patientId")
    public List<AppointmentResponseDto> getAppointmentsByPatientId(Long patientId) {
        log.info("⚠️ CACHE MISS - Fetching patient appointments from DATABASE: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID: " + patientId));

        return patient.getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class))
                .collect(Collectors.toList());
    }

    // ===== 6. DELETE APPOINTMENT =====
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "appointments", key = "#appointmentId"),
                    @CacheEvict(value = "appointments", key = "'doctor_' + #doctorId"),
                    @CacheEvict(value = "appointments", key = "'patient_' + #patientId")
            }
    )
    public void deleteAppointment(Long appointmentId, Long doctorId, Long patientId) {
        log.info("Deleting appointment {} - Cache will be cleared", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with ID: " + appointmentId));
        appointmentRepository.delete(appointment);
    }
}