package project.hospitalManagement.Project.Service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import project.hospitalManagement.Project.Entity.Doctor;
import project.hospitalManagement.Project.Entity.Type.RoleType;
import project.hospitalManagement.Project.Entity.User;
import project.hospitalManagement.Project.Repository.DoctorRepository;
import project.hospitalManagement.Project.Repository.UserRepository;
import project.hospitalManagement.Project.dto.DoctorResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import project.hospitalManagement.Project.dto.OnBoardDoctorRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    // ===== 1. GET ALL DOCTORS (WITH CACHING) =====
    @Cacheable(value = "doctors", key = "'allDoctors'")
    public List<DoctorResponseDto> getAllDoctors() {
        log.info("⚠️ CACHE MISS - Fetching ALL doctors from DATABASE");

        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }

    // ===== 2. GET DOCTOR BY ID (WITH CACHING) =====
    @Cacheable(value = "doctors", key = "#id")
    public DoctorResponseDto getDoctorById(Long id) {
        log.info("⚠️ CACHE MISS - Fetching doctor from DATABASE: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        return modelMapper.map(doctor, DoctorResponseDto.class);
    }

    // ===== 3. GET DOCTOR BY USER ID (WITH CACHING) =====
    @Cacheable(value = "doctors", key = "'user_' + #userId")
    public DoctorResponseDto getDoctorByUserId(Long userId) {
        log.info("⚠️ CACHE MISS - Fetching doctor by user ID from DATABASE: {}", userId);

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with User ID: " + userId));

        return modelMapper.map(doctor, DoctorResponseDto.class);
    }

    // ===== 4. GET DOCTOR BY EMAIL (WITH CACHING) =====
    @Cacheable(value = "doctors", key = "'email_' + #email")
    public DoctorResponseDto getDoctorByEmail(String email) {
        log.info("⚠️ CACHE MISS - Fetching doctor by email from DATABASE: {}", email);

        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + email));

        return modelMapper.map(doctor, DoctorResponseDto.class);
    }

    // ===== 5. ONBOARD NEW DOCTOR (WITH CACHE EVICTION) =====
    @Caching(
            put = @CachePut(value = "doctors", key = "#result.id"),      // Add new doctor to cache
            evict = {
                    @CacheEvict(value = "doctors", key = "'allDoctors'"),    // Clear all doctors list
                    @CacheEvict(value = "doctors", key = "'user_' + #onBoardDoctorRequestDto.userId")  // Clear user-specific cache
            }
    )
    public DoctorResponseDto onBoardNewDoctor(OnBoardDoctorRequestDto onBoardDoctorRequestDto) {
        log.info("Onboarding new doctor - Cache will be updated");

        User user = userRepository.findById(onBoardDoctorRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + onBoardDoctorRequestDto.getUserId()));

        if (doctorRepository.existsById(onBoardDoctorRequestDto.getUserId())) {
            throw new IllegalArgumentException("Already a doctor");
        }

        Doctor doctor = Doctor.builder()
                .name(onBoardDoctorRequestDto.getName())
                .specialization(onBoardDoctorRequestDto.getSpecialization())
                .email(onBoardDoctorRequestDto.getEmail())
                .user(user)
                .build();

        user.getRoles().add(RoleType.DOCTOR);

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor onboarded successfully with ID: {}", savedDoctor.getId());

        return modelMapper.map(savedDoctor, DoctorResponseDto.class);
    }

    // ===== 6. UPDATE DOCTOR (WITH CACHE UPDATE) =====
    @Caching(
            put = @CachePut(value = "doctors", key = "#id"),           // Update specific doctor
            evict = {
                    @CacheEvict(value = "doctors", key = "'allDoctors'"),  // Clear all doctors list
                    @CacheEvict(value = "doctors", key = "'email_' + #email")  // Clear email cache if email changed
            }
    )
    public DoctorResponseDto updateDoctor(Long id, DoctorResponseDto doctorDto) {
        log.info("Updating doctor - Cache will be updated: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        // Update fields
        if (doctorDto.getName() != null) {
            doctor.setName(doctorDto.getName());
        }
        if (doctorDto.getSpecialization() != null) {
            doctor.setSpecialization(doctorDto.getSpecialization());
        }
        if (doctorDto.getEmail() != null) {
            doctor.setEmail(doctorDto.getEmail());
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated successfully: {}", id);

        return modelMapper.map(updatedDoctor, DoctorResponseDto.class);
    }

    // ===== 7. DELETE DOCTOR (WITH CACHE EVICTION) =====
    @Caching(
            evict = {
                    @CacheEvict(value = "doctors", key = "#id"),           // Remove specific doctor
                    @CacheEvict(value = "doctors", key = "'allDoctors'"),  // Clear all doctors list
                    @CacheEvict(value = "doctors", key = "'user_' + #userId"),  // Clear user-specific cache
                    @CacheEvict(value = "doctors", key = "'email_' + #email")   // Clear email cache
            }
    )
    public void deleteDoctor(Long id, Long userId, String email) {
        log.info("Deleting doctor - Cache will be cleared: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        // Remove DOCTOR role from user
        User user = doctor.getUser();
        user.getRoles().remove(RoleType.DOCTOR);
        userRepository.save(user);

        doctorRepository.delete(doctor);
        log.info("Doctor deleted successfully: {}", id);
    }
}