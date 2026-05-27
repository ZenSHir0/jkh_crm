package rccl.diploma.crm.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rccl.diploma.crm.entity.Building;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.repository.BuildingRepository;
import rccl.diploma.crm.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;

    public UserService(UserRepository userRepository, BuildingRepository buildingRepository) {
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void changeRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    public void toggleEnabled(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    public void updateUser(User user, Long buildingId) {
        User old_user = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        old_user.setName(user.getName());
        old_user.setSurname(user.getSurname());
        old_user.setLastName(user.getLastName());
        old_user.setPhone(blankToNull(user.getPhone()));
        old_user.setApartment(blankToNull(user.getApartment()));

        Building building = buildingId != null
                ? buildingRepository.findById(buildingId).orElse(null)
                : null;
        old_user.setBuilding(building);

        userRepository.save(old_user);
    }
}
