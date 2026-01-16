package com.example.authentification.service;


import com.example.authentification.dto.AuthResponse;
import com.example.authentification.dto.LoginRequest;
import com.example.authentification.dto.RegisterRequest;
import com.example.authentification.entity.Role;
import com.example.authentification.entity.User;
import com.example.authentification.repository.UserRepository;
import com.example.authentification.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user with email: {}", request.getEmail());
        
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setRole(Role.valueOf(request.getRole() == null ? "ETUDIANT" : request.getRole()));
        user.setFiliereId(request.getFiliereId());
        user.setYearLevel(request.getYearLevel());

        userRepository.save(user);
        logger.info("User registered successfully with email: {}", request.getEmail());

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            logger.info("Authentication successful for email: {}", request.getEmail());
        } catch (AuthenticationException ex) {
            logger.error("Authentication failed for email: {}", request.getEmail());
            throw new BadCredentialsException("Email ou mot de passe incorrect", ex);
        }

        // Récupérer l'utilisateur pour obtenir son rôle
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found after successful authentication: {}", request.getEmail());
                    return new RuntimeException("Utilisateur non trouvé");
                });

        // Générer le token avec le rôle
        String token = jwtService.generateToken(request.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}
