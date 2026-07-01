package project.hospitalManagement.Project.Repository;

import project.hospitalManagement.Project.Entity.User;
import project.hospitalManagement.Project.Entity.Type.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByProviderIdAndProviderType(String providerId, AuthProviderType providerType);
}
