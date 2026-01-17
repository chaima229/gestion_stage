package com.example.filiere.repository;

import com.example.filiere.entity.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    List<Filiere> findByNiveau(String niveau);
    Optional<Filiere> findByNom(String nom);
    Optional<Filiere> findByNomAndNiveau(String nom, String niveau);
}

