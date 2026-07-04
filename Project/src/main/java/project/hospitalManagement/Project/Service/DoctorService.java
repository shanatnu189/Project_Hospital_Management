package project.hospitalManagement.Project.Service;

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

    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }


    public DoctorResponseDto onBoardNewDoctor(OnBoardDoctorRequestDto onBoardDoctorRequestDto) {

        User user = userRepository.findById(onBoardDoctorRequestDto.getUserId()).orElseThrow();

        if(doctorRepository.existsById(onBoardDoctorRequestDto.getUserId())){
            throw new IllegalArgumentException("Already a doctor");
        }

        Doctor doctor = Doctor.builder()
                .name(onBoardDoctorRequestDto.getName())
                .specialization(onBoardDoctorRequestDto.getSpecialization())
                .user(user)
                .build();

        user.getRoles().add(RoleType.DOCTOR);

        return modelMapper.map(doctorRepository.save(doctor), DoctorResponseDto.class);

    }
}
