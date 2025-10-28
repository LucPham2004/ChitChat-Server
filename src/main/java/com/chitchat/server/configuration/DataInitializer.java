package com.chitchat.server.configuration;

import com.chitchat.server.entity.Role;
import com.chitchat.server.entity.User;
import com.chitchat.server.repository.RoleRepository;
import com.chitchat.server.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initRoles() {
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("USER");
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByAuthority(roleName).isEmpty()) {
            Role role = new Role();
            role.setAuthority(roleName);
            roleRepository.save(role);
            log.info("Role '{}' created successfully!", roleName);
        } else {
            log.info("Role '{}' already exists.", roleName);
        }

        String adminUsername = "ADMIN";
        String adminEmail = "admin@chitchat.com";
        String defaultPassword = "adminPassword";
        String firstName = "Admin";

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            Set<Role> adminRoles = new HashSet<>();
            Optional<Role> adminRole = roleRepository.findByAuthority("ADMIN");
            Optional<Role> userRole = roleRepository.findByAuthority("USER");

            adminRole.ifPresent(adminRoles::add);
            userRole.ifPresent(adminRoles::add);

            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(defaultPassword));
            adminUser.setActive(true);
            adminUser.setAuthorities(adminRoles);
            adminUser.setFirstName(firstName);

            userRepository.save(adminUser);
            log.info("Admin account '{}' created successfully!", adminUsername);
        } else {
            log.info("Admin account '{}' already exists.", adminUsername);
        }
    }
}

