# 📊 Modèles de Base de Données et Migrations

## 📍 Localisation des fichiers

### Entités JPA (Modèles)
Les entités sont situées dans :
```
src/main/java/com/example/authentification/
├── entity/
│   ├── User.java         (Entité utilisateurs)
│   └── Role.java         (Enum des rôles)
├── filiere/
│   └── entity/
│       └── Filiere.java  (Entité filières)
└── stage/
    └── entity/
        ├── Stage.java    (Entité stages)
        └── StageEtat.java (Enum des états)
```

### Migrations Flyway
Les scripts SQL de migration sont situés dans :
```
src/main/resources/db/migration/
├── V1__Create_Users_Table.sql        (Création table users)
├── V2__Create_Filieres_Table.sql     (Création table filieres)
└── V3__Create_Stages_Table.sql       (Création table stages)
```

---

## 📋 Structure des modèles

### 1️⃣ **USER** - Gestion des utilisateurs

#### Entité JPA
```java
// src/main/java/.../entity/User.java
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, ENSEIGNANT, ETUDIANT
    
    private Long filiereId;
    private String yearLevel; // M1, M2
}
```

#### Migration SQL (V1)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'ENSEIGNANT', 'ETUDIANT') NOT NULL DEFAULT 'ETUDIANT',
    filiere_id BIGINT,
    year_level VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Champs
| Champ | Type | Contrainte | Description |
|-------|------|-----------|-------------|
| id | BIGINT | PK, AI | Identifiant unique |
| email | VARCHAR(255) | UNIQUE, NOT NULL | Email de l'utilisateur |
| password | VARCHAR(255) | NOT NULL | Mot de passe hashé |
| nom | VARCHAR(255) | NOT NULL | Nom de famille |
| prenom | VARCHAR(255) | NOT NULL | Prénom |
| role | ENUM | NOT NULL | Rôle de l'utilisateur |
| filiere_id | BIGINT | FK | Référence à la filière |
| year_level | VARCHAR(10) | | Année (M1, M2) |

---

### 2️⃣ **FILIERE** - Gestion des filières/programmes

#### Entité JPA
```java
// src/main/java/.../filiere/entity/Filiere.java
@Entity
@Table(name = "filieres")
@Data
@Builder
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nom;
    
    @Column(nullable = false)
    private String niveau; // M1, M2
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
```

#### Migration SQL (V2)
```sql
CREATE TABLE filieres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    niveau ENUM('M1', 'M2') NOT NULL,
    description LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Champs
| Champ | Type | Contrainte | Description |
|-------|------|-----------|-------------|
| id | BIGINT | PK, AI | Identifiant unique |
| nom | VARCHAR(255) | UNIQUE, NOT NULL | Nom de la filière |
| niveau | ENUM | NOT NULL | Niveau (M1 ou M2) |
| description | LONGTEXT | | Description de la filière |

---

### 3️⃣ **STAGE** - Gestion des stages/internships

#### Entité JPA
```java
// src/main/java/.../stage/entity/Stage.java
@Entity
@Table(name = "stages")
@Data
@Builder
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String sujet;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String entreprise;
    
    @Column(nullable = false)
    private String ville;
    
    @Column(nullable = false)
    private LocalDate dateDebut;
    
    @Column(nullable = false)
    private LocalDate dateFin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageEtat etat; // BROUILLON, EN_ATTENTE_VALIDATION, VALIDE, REFUSE
    
    @Column(nullable = false)
    private Long etudiantId;
    
    private Long encadrantId;
    
    @Column(nullable = false)
    private Long filiereId;
    
    @Column(columnDefinition = "TEXT")
    private String commentaire;
    
    private String rapportPath;
}
```

#### Migration SQL (V3)
```sql
CREATE TABLE stages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sujet VARCHAR(255) NOT NULL,
    description LONGTEXT,
    entreprise VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    etat ENUM('BROUILLON', 'EN_ATTENTE_VALIDATION', 'VALIDE', 'REFUSE') NOT NULL DEFAULT 'BROUILLON',
    etudiant_id BIGINT NOT NULL,
    encadrant_id BIGINT,
    filiere_id BIGINT NOT NULL,
    commentaire LONGTEXT,
    rapport_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stages_etudiant FOREIGN KEY (etudiant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_stages_encadrant FOREIGN KEY (encadrant_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_stages_filiere FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE CASCADE
);
```

#### Champs
| Champ | Type | Contrainte | Description |
|-------|------|-----------|-------------|
| id | BIGINT | PK, AI | Identifiant unique |
| sujet | VARCHAR(255) | NOT NULL | Sujet du stage |
| description | LONGTEXT | | Description détaillée |
| entreprise | VARCHAR(255) | NOT NULL | Nom de l'entreprise |
| ville | VARCHAR(100) | NOT NULL | Ville du stage |
| date_debut | DATE | NOT NULL | Date de début |
| date_fin | DATE | NOT NULL | Date de fin |
| etat | ENUM | NOT NULL | État du workflow |
| etudiant_id | BIGINT | FK, NOT NULL | Référence étudiant |
| encadrant_id | BIGINT | FK | Référence encadrant |
| filiere_id | BIGINT | FK, NOT NULL | Référence filière |
| commentaire | LONGTEXT | | Commentaire (refus) |
| rapport_path | VARCHAR(255) | | Chemin du fichier PDF |

---

## 🔄 Workflow des états de stage

```
BROUILLON
    ↓ (étudiant soumet)
EN_ATTENTE_VALIDATION
    ├─→ VALIDE (encadrant valide)
    └─→ REFUSE (encadrant refuse + commentaire)
        ↓ (étudiant modifie)
    BROUILLON (retour)
```

---

## 🚀 Comment exécuter les migrations

### 1️⃣ Au démarrage de l'application
```bash
mvn spring-boot:run
```

Flyway exécutera automatiquement les migrations dans cet ordre :
1. V1__Create_Users_Table.sql
2. V2__Create_Filieres_Table.sql
3. V3__Create_Stages_Table.sql

### 2️⃣ Vérifier l'état des migrations
```bash
# Avec Spring Boot CLI
spring-boot:run

# Dans les logs, vous verrez :
# INFO org.flywaydb.core.internal.command.DbMigrate - Executing migration: V1__Create_Users_Table
# INFO org.flywaydb.core.internal.command.DbMigrate - Executing migration: V2__Create_Filieres_Table
# INFO org.flywaydb.core.internal.command.DbMigrate - Executing migration: V3__Create_Stages_Table
```

### 3️⃣ Ajouter une nouvelle migration
```bash
# Créer un nouveau fichier dans src/main/resources/db/migration/
# Format : V{numéro}__{description}.sql
# Exemple : V4__Add_Columns_To_Stages.sql
```

---

## 📊 Diagramme des relations

```
┌─────────────────┐
│     Users       │
│ (Utilisateurs)  │
├─────────────────┤
│ id (PK)         │
│ email           │
│ password        │
│ nom             │
│ prenom          │
│ role            │
│ filiere_id (FK) │
│ year_level      │
└─────────────────┘
        ▲
        │ 1..N
        │
        │ filiereId
        │
┌───────┴─────────────────┐
│  Stages                 │
│ (Stages/Internships)    │
├─────────────────────────┤
│ id (PK)                 │
│ sujet                   │
│ description             │
│ entreprise              │
│ ville                   │
│ date_debut              │
│ date_fin                │
│ etat (ENUM)             │
│ etudiant_id (FK→Users)  │
│ encadrant_id (FK→Users) │
│ filiere_id (FK→Filieres)│
│ commentaire             │
│ rapport_path            │
└─────────────────────────┘
        ▲
        │ 1..N
        │
        │ filiere_id
        │
┌─────────────────┐
│   Filieres      │
│  (Programs)     │
├─────────────────┤
│ id (PK)         │
│ nom             │
│ niveau (M1/M2)  │
│ description     │
└─────────────────┘
```

---

## ✅ Configuration

### application.properties
```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baselineOnMigrate=true
spring.flyway.outOfOrder=false

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate  # Important: utilise les migrations, pas Hibernate
```

---

## 📝 Bonnes pratiques

1. ✅ **Une migration = un changement**
2. ✅ **Noms explicites** : V1__Create_Users_Table.sql
3. ✅ **Versioning** : V1, V2, V3... (pas V1.1, V1.2)
4. ✅ **Idempotent** : Utiliser "CREATE TABLE IF NOT EXISTS"
5. ✅ **Index** : Ajouter les index sur les colonnes fréquemment recherchées
6. ✅ **Foreign Keys** : Bien configurer les contraintes
7. ✅ **Audit** : created_at, updated_at sur les tables importantes

---

## 🔍 Références

### Fichiers
- **Entités** : `src/main/java/com/example/authentification/**/entity/`
- **Migrations** : `src/main/resources/db/migration/`
- **Configuration** : `src/main/resources/application.properties`

### Repositories
- `src/main/java/com/example/authentification/repository/UserRepository.java`
- `src/main/java/com/example/authentification/filiere/repository/FiliereRepository.java`
- `src/main/java/com/example/authentification/stage/repository/StageRepository.java`

---

**Status** : ✅ Migrations configurées et prêtes à l'emploi !

