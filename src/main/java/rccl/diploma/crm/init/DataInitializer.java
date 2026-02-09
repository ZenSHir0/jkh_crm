package rccl.diploma.crm.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.entity.enums.RequestType;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.repository.RequestRepository;
import rccl.diploma.crm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("admin@admin.com")
                        .name("Максим")
                        .surname("Марцинкевич")
                        .lastName("Сергеевич")
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
            }
            if (userRepository.findByRole(Role.RESIDENT).isEmpty()) {
                User resident = User.builder()
                        .username("resident")
                        .password(passwordEncoder.encode("res"))
                        .email("res@res.com")
                        .name("Павел")
                        .surname("Бондаренко")
                        .lastName("Евгенич")
                        .role(Role.RESIDENT)
                        .build();
                userRepository.save(resident);
            }
            if (userRepository.findByRole(Role.MASTER).isEmpty()) {
                User master = User.builder()
                        .username("master")
                        .password(passwordEncoder.encode("mas"))
                        .email("mas@mas.com")
                        .name("Владимир")
                        .surname("Бандера")
                        .lastName("Васильевич")
                        .role(Role.RESIDENT)
                        .build();
                userRepository.save(master);
            }
        };
    }

    @Bean
    public CommandLineRunner initRequests(RequestRepository requestRepository, UserRepository userRepository) {
        return args -> {
            if (requestRepository.findAll().isEmpty()) {

                List<User> users = userRepository.findAll();
                if (users.isEmpty()){
                    return;
                }
                Random random = new Random();
                List<RequestType> types = Arrays.asList(RequestType.values());
                List<RequestStatus> statuses = Arrays.asList(RequestStatus.values());

                for (int i = 0; i < 5; i++) {
                    User resident = users.get(random.nextInt(users.size()));  // рандомный житель
                    User master = random.nextBoolean() ? users.get(random.nextInt(users.size())) : null;  // мастер или null

                    Request request = Request.builder()
                            .resident(resident)
                            .master(master)
                            .title("Тестовая заявка #" + (i + 1))
                            .description("Рандомное описание: проблема с " + types.get(random.nextInt(types.size())).getDisplayName().toLowerCase())
                            .type(types.get(random.nextInt(types.size())))
                            .status(statuses.get(random.nextInt(statuses.size())))
                            .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))  // рандомная дата в последние 30 дней
                            .build();

                    requestRepository.save(request);
                }
            }
        };
    }
}
