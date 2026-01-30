package rccl.diploma.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rccl.diploma.crm.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
