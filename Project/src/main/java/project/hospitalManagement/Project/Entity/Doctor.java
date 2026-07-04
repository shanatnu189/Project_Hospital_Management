package project.hospitalManagement.Project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String specialization;

    @Column( unique = true, length = 100) //nullable = false, suggested and have to do it
    private String email;

    @ManyToMany(mappedBy = "doctors")
    private Set<Department> departments = new HashSet<>();

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();

    @OneToOne
    @MapsId
    private User user;

}
