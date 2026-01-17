package com.example.authentification.controller;

import com.example.authentification.dto.CreateUserRequest;
import com.example.authentification.dto.UpdateUserRequest;
import com.example.authentification.dto.UserDTO;
import com.example.authentification.entity.Role;
import com.example.authentification.entity.User;
import com.example.authentification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des utilisateurs
 * Accessible aux ADMIN
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = {"http://localhost:4200", "http://localhost:3000"},
    allowedHeaders = {"Authorization", "Content-Type", "X-Requested-With", "Accept"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true"
)
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Récupérer tous les utilisateurs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer tous les enseignants (encadrants)
     * Accessible à tous les utilisateurs authentifiés
     */
    @GetMapping("/enseignants")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<List<UserDTO>> getAllEnseignants() {
        log.info("Récupération de tous les enseignants");
        List<UserDTO> enseignants = userRepository.findByRole(Role.ENSEIGNANT).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(enseignants);
    }

    /**
     * Récupérer les enseignants d'une filière spécifique
     * Accessible à tous les utilisateurs authentifiés
     */
    @GetMapping("/enseignants/filiere/{filiereId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<List<UserDTO>> getEnseignantsByFiliere(@PathVariable Long filiereId) {
        log.info("Récupération des enseignants de la filière: {}", filiereId);
        List<UserDTO> enseignants = userRepository.findByRoleAndFiliereId(Role.ENSEIGNANT, filiereId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(enseignants);
    }

    /**
     * Récupérer un utilisateur par ID
     * Accessible à tous les utilisateurs authentifiés (pour afficher les encadrants, etc.)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Récupération de l'utilisateur: {}", id);
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(convertToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouvel utilisateur
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        log.info("Création d'un utilisateur: {} {}", request.getNom(), request.getPrenom());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cet email est déjà utilisé"));
        }

        try {
            User user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getMotDePasse()))
                    .role(Role.valueOf(request.getRole()))
                    .filiereId(request.getFiliereId())
                    .yearLevel(request.getAnnee())
                    .build();

            User savedUser = userRepository.save(user);
            log.info("Utilisateur créé avec ID: {}", savedUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedUser));
        } catch (IllegalArgumentException e) {
            log.error("Rôle invalide: {}", request.getRole());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rôle invalide: " + request.getRole()));
        } catch (Exception e) {
            log.error("Erreur lors de la création: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création de l'utilisateur"));
        }
    }

    /**
     * Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        log.info("[updateUser] Tentative de mise à jour de l'utilisateur id={} par {}", id, request.getEmail());

        // Récupérer l'utilisateur connecté
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si non admin, vérifier que l'utilisateur ne modifie que son propre profil
        if (!isAdmin) {
            User currentUser = userRepository.findByEmail(currentEmail).orElse(null);
            log.info("[updateUser] Utilisateur connecté: email={}, id={} | id demandé={}", currentEmail, (currentUser != null ? currentUser.getId() : null), id);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "SESSION_EXPIRED",
                    "message", "Votre session a expiré. Veuillez vous reconnecter."
                ));
            }
            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "ACCESS_DENIED",
                    "message", "Vous ne pouvez modifier que votre propre profil."
                ));
            }
        }

        return userRepository.findById(id)
                .map(user -> {
                    if (request.getNom() != null) user.setNom(request.getNom());
                    if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
                    if (request.getEmail() != null) {
                        // Vérifier que le nouvel email n'est pas déjà utilisé
                        if (!user.getEmail().equals(request.getEmail()) &&
                                userRepository.existsByEmail(request.getEmail())) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error", "Cet email est déjà utilisé"));
                        }
                        user.setEmail(request.getEmail());
                    }
                    if (request.getRole() != null) {
                        try {
                            user.setRole(Role.valueOf(request.getRole()));
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of("error", "Rôle invalide"));
                        }
                    }
                    if (request.getFiliereId() != null) user.setFiliereId(request.getFiliereId());
                    if (request.getAnnee() != null) user.setYearLevel(request.getAnnee());
                    if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(request.getMotDePasse()));
                    }

                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(convertToDTO(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Suppression de l'utilisateur: {}", id);

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer les utilisateurs par filière
     */
    @GetMapping("/filiere/{filiereId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT')")
    public ResponseEntity<List<UserDTO>> getUsersByFiliere(@PathVariable Long filiereId) {
        log.info("Récupération des utilisateurs de la filière: {}", filiereId);
        List<UserDTO> users = userRepository.findByFiliereId(filiereId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Recherche d'utilisateurs
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String nom) {
        log.info("Recherche d'utilisateurs - role: {}, nom: {}", role, nom);

        List<User> users;
        if (role != null && !role.isEmpty()) {
            try {
                users = userRepository.findByRole(Role.valueOf(role));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok(Collections.emptyList());
            }
        } else {
            users = userRepository.findAll();
        }

        if (nom != null && !nom.isEmpty()) {
            String searchLower = nom.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getNom().toLowerCase().contains(searchLower) ||
                            u.getPrenom().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        List<UserDTO> dtos = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Importer des utilisateurs depuis un fichier Excel
     */
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        log.info("Import d'utilisateurs depuis Excel: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le fichier est vide"));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le fichier doit être au format Excel (.xlsx ou .xls)"));
        }

        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Ignorer la première ligne (en-têtes)
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            int rowNum = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNum++;

                try {
                    String nom = getCellStringValue(row.getCell(0));
                    String prenom = getCellStringValue(row.getCell(1));
                    String email = getCellStringValue(row.getCell(2));
                    String motDePasse = getCellStringValue(row.getCell(3));
                    String roleStr = getCellStringValue(row.getCell(4));
                    Long filiereId = getCellLongValue(row.getCell(5));
                    String annee = getCellStringValue(row.getCell(6));

                    // Validation des champs obligatoires
                    if (nom == null || nom.isEmpty()) {
                        errors.add("Ligne " + rowNum + ": Nom manquant");
                        errorCount++;
                        continue;
                    }
                    if (prenom == null || prenom.isEmpty()) {
                        errors.add("Ligne " + rowNum + ": Prénom manquant");
                        errorCount++;
                        continue;
                    }
                    if (email == null || email.isEmpty()) {
                        errors.add("Ligne " + rowNum + ": Email manquant");
                        errorCount++;
                        continue;
                    }
                    if (motDePasse == null || motDePasse.isEmpty()) {
                        errors.add("Ligne " + rowNum + ": Mot de passe manquant");
                        errorCount++;
                        continue;
                    }
                    if (roleStr == null || roleStr.isEmpty()) {
                        errors.add("Ligne " + rowNum + ": Rôle manquant");
                        errorCount++;
                        continue;
                    }

                    // Vérifier si l'email existe déjà
                    if (userRepository.existsByEmail(email)) {
                        errors.add("Ligne " + rowNum + ": Email déjà existant - " + email);
                        errorCount++;
                        continue;
                    }

                    // Parser le rôle
                    Role role;
                    try {
                        role = Role.valueOf(roleStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        errors.add("Ligne " + rowNum + ": Rôle invalide - " + roleStr);
                        errorCount++;
                        continue;
                    }

                    // Créer l'utilisateur
                    User user = User.builder()
                            .nom(nom)
                            .prenom(prenom)
                            .email(email)
                            .password(passwordEncoder.encode(motDePasse))
                            .role(role)
                            .filiereId(filiereId)
                            .yearLevel(annee)
                            .build();

                    userRepository.save(user);
                    successCount++;

                    results.add(Map.of(
                            "ligne", rowNum,
                            "status", "success",
                            "email", email
                    ));

                } catch (Exception e) {
                    errors.add("Ligne " + rowNum + ": " + e.getMessage());
                    errorCount++;
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors de la lecture du fichier Excel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la lecture du fichier: " + e.getMessage()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalTraite", successCount + errorCount);
        response.put("succes", successCount);
        response.put("erreurs", errorCount);
        response.put("detailsErreurs", errors);

        log.info("Import terminé: {} succès, {} erreurs", successCount, errorCount);
        return ResponseEntity.ok(response);
    }

    /**
     * Convertir User en UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setFiliereId(user.getFiliereId());
        dto.setAnnee(user.getYearLevel());
        return dto;
    }

    /**
     * Récupérer la valeur String d'une cellule
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    /**
     * Récupérer la valeur Long d'une cellule
     */
    private Long getCellLongValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * Mettre à jour le profil d'un utilisateur (nom, prénom)
     * Accessible par l'utilisateur lui-même
     */
    @PatchMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> request) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        log.info("Tentative de modification du profil: id={} par email={} (admin={})", id, currentEmail, isAdmin);

        // Si non admin, vérifier que l'utilisateur ne modifie que son propre profil
        if (!isAdmin) {
            User currentUser = userRepository.findByEmail(currentEmail).orElse(null);
            log.info("Vérification identité: email={} id={} userIdBase={}", currentEmail, id, currentUser != null ? currentUser.getId() : null);
            if (currentUser == null || !currentUser.getId().equals(id)) {
                log.warn("Accès refusé: email={} tente de modifier id={}", currentEmail, id);
                // Message explicite pour le frontend
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "SESSION_EXPIRED",
                    "message", "Votre session n'est plus valide. Veuillez vous reconnecter."
                ));
            }
        }

        return userRepository.findById(id)
                .map(user -> {
                    if (request.containsKey("nom")) {
                        user.setNom(request.get("nom"));
                    }
                    if (request.containsKey("prenom")) {
                        user.setPrenom(request.get("prenom"));
                    }

                    User updatedUser = userRepository.save(user);
                    log.info("Profil mis à jour pour l'utilisateur: {}", id);
                    return ResponseEntity.ok(convertToDTO(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Changer le mot de passe d'un utilisateur
     * Accessible par l'utilisateur lui-même
     */
    @PostMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        log.info("Changement de mot de passe pour l'utilisateur: {}", id);

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Le mot de passe actuel et le nouveau mot de passe sont requis"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Le nouveau mot de passe doit contenir au moins 6 caractères"));
        }

        return userRepository.findById(id)
                .map(user -> {
                    // Vérifier le mot de passe actuel
                    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Mot de passe actuel incorrect"));
                    }

                    // Mettre à jour le mot de passe
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);

                    log.info("Mot de passe changé pour l'utilisateur: {}", id);
                    return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
