package com.example.authentification.service;


import com.example.authentification.entity.Role;
import com.example.authentification.entity.User;
import com.example.authentification.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Création d'un utilisateur standard
     */
    public User createUser(String email, String rawPassword) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.ETUDIANT);

        return userRepository.save(user);
    }

    /**
     * Changement de mot de passe
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 🔐 règle métier obligatoire
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Promotion en ADMIN
     */
    public void promoteToAdmin(Long userId, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Accès refusé");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }
}
