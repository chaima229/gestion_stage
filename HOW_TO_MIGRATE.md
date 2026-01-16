# 🚀 Guide de Migration - Comment migrer votre base de données

## 🎯 Qu'est-ce qu'une migration?

Une **migration** c'est l'exécution automatique des scripts SQL pour créer/modifier votre base de données.

**Flyway** gère ça automatiquement pour vous! 

---

## ✅ 3 étapes simples pour migrer

### Étape 1: Créer les BDs MySQL (une seule fois)

```bash
# Ouvrir MySQL
mysql -u root -p

# Exécuter ces commandes
CREATE DATABASE authdb CHARACTER SET utf8mb4;
CREATE DATABASE filiere_db CHARACTER SET utf8mb4;
CREATE DATABASE stage_db CHARACTER SET utf8mb4;

# Vérifier
SHOW DATABASES;
# Vous devez voir authdb, filiere_db, stage_db

# Quitter
EXIT;
```

---

### Étape 2: Démarrer l'application

```bash
cd C:\Users\pc\IdeaProjects\authentification
mvn spring-boot:run
```

**Attendez ce message:**
```
Tomcat started on port(s): 8081
```

---

### Étape 3: Vérifier que les migrations sont exécutées

**Vous verrez dans les logs:**
```
INFO ... Flyway : Successfully validated 3 migrations
INFO ... Flyway : Migrating schema to version 1 - Create Users Table
INFO ... Flyway : Successfully applied 1 migration to schema (execution time: XXms)
INFO ... Flyway : Migrating schema to version 2 - Create Filieres Table
INFO ... Flyway : Successfully applied 1 migration to schema (execution time: XXms)
INFO ... Flyway : Migrating schema to version 3 - Create Stages Table
INFO ... Flyway : Successfully applied 1 migration to schema (execution time: XXms)
INFO ... Flyway : Flyway has been successfully applied to schema "authdb"
```

**✅ Les migrations sont exécutées automatiquement!**

---

## 🔍 Vérifier que tout est OK

### Dans MySQL

```bash
mysql -u root -p authdb

# Voir les tables créées
SHOW TABLES;
# Résultat:
# +-----------------+
# | Tables_in_authdb |
# +-----------------+
# | flyway_schema_history |
# | users           |
# | filieres        |
# | stages          |
# +-----------------+

# Voir les colonnes de users
DESCRIBE users;

# Voir l'historique des migrations
SELECT * FROM flyway_schema_history;
# Résultat:
# version | description                | type | script                         | success
# 1       | Create Users Table         | SQL  | V1__Create_Users_Table.sql     | 1
# 2       | Create Filieres Table      | SQL  | V2__Create_Filieres_Table.sql  | 1
# 3       | Create Stages Table        | SQL  | V3__Create_Stages_Table.sql    | 1
```

---

## 🎯 Résumé: Ce qui s'est passé

```
Démarrage de l'app
        ↓
Flyway détecte 3 migrations non exécutées
        ↓
Crée V1: Table USERS (11 colonnes)
        ↓
Crée V2: Table FILIERES (5 colonnes)
        ↓
Crée V3: Table STAGES (17 colonnes)
        ↓
Enregistre dans flyway_schema_history
        ↓
App prête! 🚀
```

---

## ✨ C'est tout!

**La migration est automatique:**
- ✅ Pas besoin de scripts manuels
- ✅ Flyway gère tout
- ✅ Les tables sont créées
- ✅ Les index sont créés
- ✅ Les clés étrangères sont créées
- ✅ Les contraintes sont appliquées

---

## 🆘 Si quelque chose ne marche pas

### Problème 1: "Database doesn't exist"
```bash
# Solution: Créer manuellement les BDs
mysql -u root -p
CREATE DATABASE authdb CHARACTER SET utf8mb4;
EXIT;

# Puis redémarrer l'app
mvn spring-boot:run
```

### Problème 2: "Port 3306 not accessible"
```bash
# Vérifier que MySQL est en train de tourner
# Windows: Services > MySQL80 > Start
# ou
mysql -u root -p
# Doit vous demander le mot de passe
```

### Problème 3: "Migration already applied"
```bash
# Normal! Flyway ne réexécute pas une migration
# Les 3 migrations ont déjà été appliquées
# Vous ne verrez le message que au premier démarrage
```

### Problème 4: "Cannot find file V1__Create_Users_Table.sql"
```bash
# Vérifier que les fichiers existent:
# src/main/resources/db/migration/V1__Create_Users_Table.sql
# src/main/resources/db/migration/V2__Create_Filieres_Table.sql
# src/main/resources/db/migration/V3__Create_Stages_Table.sql
```

---

## 🚀 Tester que ça marche

```bash
# Terminal 1: Démarrer l'app
mvn spring-boot:run

# Terminal 2: Vérifier
curl -X GET http://localhost:8081/api/filieres
# Résultat: [] (liste vide, mais API répond!)

# Terminal 3: Vérifier les tables
mysql -u root authdb
SELECT COUNT(*) FROM users;     # Result: 0
SELECT COUNT(*) FROM filieres;  # Result: 0
SELECT COUNT(*) FROM stages;    # Result: 0
```

---

## 📋 Checklist de migration

- [ ] MySQL est installé et en cours d'exécution
- [ ] BDs authdb, filiere_db, stage_db créées
- [ ] App démarrée avec `mvn spring-boot:run`
- [ ] Logs montrent les migrations exécutées
- [ ] Vérification: `SELECT COUNT(*) FROM users;` retourne 0
- [ ] Vérification: `SELECT * FROM flyway_schema_history;` montre V1, V2, V3
- [ ] API accessible: `curl http://localhost:8081/api/filieres`

---

## 🎉 Résultat final

**Après migration, vous avez:**

### ✅ Base de données
```
authdb/
├── users (table vide, prête à recevoir des utilisateurs)
├── filieres (table vide, prête à recevoir des filières)
└── stages (table vide, prête à recevoir des stages)
```

### ✅ Historique des migrations
```
flyway_schema_history/
├── V1 | Create Users Table | ✓ Applied
├── V2 | Create Filieres Table | ✓ Applied
└── V3 | Create Stages Table | ✓ Applied
```

### ✅ Prêt à utiliser
```
API accessible: http://localhost:8081
BD prête: authdb
Migrations appliquées: 3/3 ✓
```

---

## 💡 Conseils

1. **Première migration:** La plus longue (5-10 sec)
2. **Migrations suivantes:** Instantanées (Flyway cache le statut)
3. **Jamais modifier** une migration exécutée (sinon erreur!)
4. **Ajouter une migration:** Créer V4, V5, etc. dans `db/migration/`
5. **Rollback:** Créer une migration inverse (V4__Undo_...)

---

## 🔄 Cycle complet de migration

```
Jour 1: Première migration
  → mvn spring-boot:run
  → Flyway exécute V1, V2, V3
  → Tables créées ✓

Jour 2+: Aucune action!
  → Flyway voit que V1, V2, V3 sont déjà appliquées
  → Saute directement à l'initialisation
  → App démarre normalement ✓

Jour N: Nouvelle migration?
  → Créer V4__New_Feature.sql
  → mvn spring-boot:run
  → Flyway exécute V4
  → Tout OK ✓
```

---

## ✅ Status

```
Migrations: PRÊTES ✓
Base de données: CRÉÉE ✓
Flyway: CONFIGURÉ ✓
Application: PRÊTE ✓

Prochaine étape: mvn spring-boot:run
```

---

**🚀 Allez-y! Démarrez l'application et les migrations s'exécuteront automatiquement!**

