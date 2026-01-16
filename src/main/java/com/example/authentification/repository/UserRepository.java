package com.example.authentification.repository;

import com.example.authentification.entity.Role;
import com.example.authentification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    /**
     * Vérifier si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Compter les utilisateurs par rôle (Enum)
     */
    long countByRole(Role role);

    /**
     * Trouver les utilisateurs par rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouver les utilisateurs d'une filière
     */
    List<User> findByFiliereId(Long filiereId);

    /**
     * Trouver les utilisateurs par rôle et filière
     */
    List<User> findByRoleAndFiliereId(Role role, Long filiereId);
}
