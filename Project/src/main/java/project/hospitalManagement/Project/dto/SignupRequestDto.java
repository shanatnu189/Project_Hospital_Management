package project.hospitalManagement.Project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.hospitalManagement.Project.Entity.Type.RoleType;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    private String username;
    private String password;
    private String name;
    private Set<RoleType> roles = new HashSet<>();
}
