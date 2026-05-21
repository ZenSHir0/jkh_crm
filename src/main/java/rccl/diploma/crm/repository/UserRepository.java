package rccl.diploma.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.email)    LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.surname)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.name)     LIKE LOWER(CONCAT('%', :q, '%'))")
    List<User> search(@Param("q") String q);
}
