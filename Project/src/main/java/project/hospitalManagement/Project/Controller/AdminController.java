package project.hospitalManagement.Project.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import project.hospitalManagement.Project.Entity.User;
import project.hospitalManagement.Project.Service.DoctorService;
import project.hospitalManagement.Project.dto.DoctorResponseDto;
import project.hospitalManagement.Project.dto.OnBoardDoctorRequestDto;
import project.hospitalManagement.Project.dto.PatientResponseDto;
import project.hospitalManagement.Project.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(patientService.getAllPatients(pageNumber, pageSize));
    }

    @PostMapping("/onBoardNewDoctor")
    public ResponseEntity<DoctorResponseDto> onBoardNewDoctor(@RequestBody OnBoardDoctorRequestDto onBoardDoctorRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.onBoardNewDoctor(onBoardDoctorRequestDto));
    }

}