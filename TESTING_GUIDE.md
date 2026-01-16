# 🧪 Guide de Test Complet - Gestion des Stages

## 🚀 Préparation initiale

### Étape 1 : Démarrer l'application

**Option 1 : Avec Maven**
```bash
cd C:\Users\pc\IdeaProjects\authentification
mvn spring-boot:run
```

**Option 2 : Avec le JAR**
```bash
cd C:\Users\pc\IdeaProjects\authentification
mvn clean install -DskipTests
java -jar target/authentification-0.0.1-SNAPSHOT.jar
```

L'application démarrera sur **http://localhost:8081**

### Étape 2 : Créer les bases de données MySQL

```bash
mysql -u root -p
```

Exécutez les commandes SQL :
```sql
CREATE DATABASE authdb CHARACTER SET utf8mb4;
CREATE DATABASE filiere_db CHARACTER SET utf8mb4;
CREATE DATABASE stage_db CHARACTER SET utf8mb4;
```

### Étape 3 : Vérifier que l'application est prête

```bash
curl http://localhost:8081/api/filieres
# Vous devriez obtenir [] (liste vide)
```

---

## 🧪 Scénario de test complet

### 1️⃣ INSCRIPTION - Créer 3 utilisateurs

#### Inscription Étudiant
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "Password123!",
    "nom": "Dupont",
    "prenom": "Alice",
    "role": "ETUDIANT",
    "filiereId": 1,
    "yearLevel": "M2"
  }'
```

**Réponse attendue :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "alice@example.com",
  "role": "ETUDIANT",
  "nom": "Dupont",
  "prenom": "Alice"
}
```

**Sauvegardez le token :**
```bash
# PowerShell
$TOKEN_ETUDIANT = "eyJhbGciOiJIUzI1NiJ9..."
$ETUDIANT_ID = 1
```

#### Inscription Enseignant
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "prof@example.com",
    "password": "Password123!",
    "nom": "Martin",
    "prenom": "Jean",
    "role": "ENSEIGNANT",
    "filiereId": 1
  }'
```

**Sauvegardez :**
```bash
$TOKEN_ENSEIGNANT = "..."
$ENCADRANT_ID = 2
```

#### Inscription Admin
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "Password123!",
    "nom": "Admin",
    "prenom": "Super",
    "role": "ADMIN"
  }'
```

**Sauvegardez :**
```bash
$TOKEN_ADMIN = "..."
$ADMIN_ID = 3
```

---

### 2️⃣ CONNEXION - Tester le login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "Password123!"
  }'
```

**Réponse :**
```json
{
  "token": "...",
  "userId": 1,
  "email": "alice@example.com",
  "role": "ETUDIANT",
  "nom": "Dupont",
  "prenom": "Alice"
}
```

---

### 3️⃣ FILIÈRES - Gestion des filières

#### Créer une filière (ADMIN)
```bash
curl -X POST http://localhost:8081/api/filieres \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d '{
    "nom": "Génie Informatique",
    "niveau": "M2",
    "description": "Master en Informatique généraliste"
  }'
```

**Réponse :**
```json
{
  "id": 1,
  "nom": "Génie Informatique",
  "niveau": "M2",
  "description": "Master en Informatique généraliste"
}
```

#### Lister toutes les filières
```bash
curl http://localhost:8081/api/filieres \
  -H "Authorization: Bearer $TOKEN_ETUDIANT"
```

#### Récupérer une filière spécifique
```bash
curl http://localhost:8081/api/filieres/1 \
  -H "Authorization: Bearer $TOKEN_ETUDIANT"
```

#### Filtrer par niveau
```bash
curl http://localhost:8081/api/filieres/niveau/M2 \
  -H "Authorization: Bearer $TOKEN_ETUDIANT"
```

#### Modifier une filière (ADMIN)
```bash
curl -X PUT http://localhost:8081/api/filieres/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -d '{
    "nom": "Génie Informatique - Mise à jour",
    "niveau": "M2",
    "description": "Master mis à jour"
  }'
```

#### Supprimer une filière (ADMIN)
```bash
curl -X DELETE http://localhost:8081/api/filieres/1 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

### 4️⃣ STAGES - Workflow complet

#### Créer un stage (ÉTUDIANT) - État BROUILLON
```bash
curl -X POST http://localhost:8081/api/stages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ETUDIANT" \
  -d '{
    "sujet": "Développement dune plateforme e-learning",
    "description": "Création dune plateforme web complète pour lenseignement en ligne",
    "entreprise": "TechCorp",
    "ville": "Paris",
    "dateDebut": "2026-06-01",
    "dateFin": "2026-08-31",
    "etudiantId": 1,
    "filiereId": 1
  }'
```

**Réponse :**
```json
{
  "id": 1,
  "sujet": "Développement dune plateforme e-learning",
  "etat": "BROUILLON",
  "etudiantId": 1,
  "encadrantId": null,
  "...": "..."
}
```

#### Voir mes stages (ÉTUDIANT)
```bash
curl http://localhost:8081/api/stages/etudiant/1 \
  -H "Authorization: Bearer $TOKEN_ETUDIANT"
```

#### Soumettre pour validation (ÉTUDIANT) - État EN_ATTENTE_VALIDATION
```bash
curl -X PUT http://localhost:8081/api/stages/1/submit \
  -H "Authorization: Bearer $TOKEN_ETUDIANT"
```

**Réponse :**
```json
{
  "id": 1,
  "etat": "EN_ATTENTE_VALIDATION",
  "..."
}
```

#### Voir les stages à valider (ENSEIGNANT)
```bash
curl http://localhost:8081/api/stages/filiere/1 \
  -H "Authorization: Bearer $TOKEN_ENSEIGNANT"
```

#### Valider un stage (ENSEIGNANT) - État VALIDE
```bash
curl -X PUT "http://localhost:8081/api/stages/1/validate?encadrantId=2" \
  -H "Authorization: Bearer $TOKEN_ENSEIGNANT"
```

**Réponse :**
```json
{
  "id": 1,
  "etat": "VALIDE",
  "encadrantId": 2,
  "..."
}
```

#### Refuser un stage (ENSEIGNANT) - État REFUSE
```bash
# Créer d'abord un autre stage en attente
curl -X POST http://localhost:8081/api/stages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_ETUDIANT" \
  -d '{
    "sujet": "Stage test refus",
    "description": "Test",
    "entreprise": "TestCorp",
    "ville": "Lyon",
    "dateDebut": "2026-07-01",
    "dateFin": "2026-09-30",
    "etudiantId": 1,
    "filiereId": 1
  }'
```

Puis le soumettre :
```bash
curl -X PUT http://localhost:8081/api/stages/2/submit \
  -H "Authorization: Bearer $TOKEN_ETUDIANT"
```

Puis le refuser :
```bash
curl -X PUT "http://localhost:8081/api/stages/2/refuse?commentaire=Sujet%20non%20approprié" \
  -H "Authorization: Bearer $TOKEN_ENSEIGNANT"
```

**Réponse :**
```json
{
  "id": 2,
  "etat": "REFUSE",
  "commentaire": "Sujet non approprié",
  "..."
}
```

---

### 5️⃣ RECHERCHE ET FILTRES

#### Rechercher par état
```bash
curl "http://localhost:8081/api/stages/search/etat?etat=VALIDE" \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

#### Rechercher par entreprise
```bash
curl "http://localhost:8081/api/stages/search/entreprise?entreprise=TechCorp" \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

#### Voir tous les stages (ADMIN)
```bash
curl http://localhost:8081/api/stages \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

### 6️⃣ STATISTIQUES

#### Résumé complet
```bash
curl http://localhost:8081/api/stats/summary \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

**Réponse :**
```json
{
  "totalStages": 2,
  "etatCounts": {
    "BROUILLON": 0,
    "EN_ATTENTE_VALIDATION": 0,
    "VALIDE": 1,
    "REFUSE": 1
  },
  "filiereCounts": {
    "FILIERE_1": 2
  }
}
```

#### Comptage par état
```bash
curl http://localhost:8081/api/stats/by-etat \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

#### Comptage par filière
```bash
curl http://localhost:8081/api/stats/by-filiere \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

### 7️⃣ UPLOAD DE FICHIERS

#### Créer un fichier test PDF (PowerShell)
```powershell
# Créer un fichier texte simple (pour le test)
@"
Test Report
Author: Alice
Date: 2026-01-12
"@ | Out-File -FilePath "rapport.txt" -Encoding UTF8

# Note: En production, vous utiliserez des vrais PDFs
```

#### Upload d'un rapport
```bash
curl -X POST "http://localhost:8081/api/files/upload?stageId=1" \
  -F "file=@rapport.txt" \
  -H "Authorization: Bearer $TOKEN_ETUDIANT"
```

**Réponse :**
```
uploads/rapports/stage_1_uuid.txt
```

Sauvegardez le path :
```bash
$RAPPORT_PATH = "uploads/rapports/stage_1_uuid.txt"
```

#### Télécharger un rapport
```bash
curl "http://localhost:8081/api/files/download?filePath=$RAPPORT_PATH" \
  -H "Authorization: Bearer $TOKEN_ETUDIANT" \
  -o rapport_telechargé.txt
```

#### Supprimer un rapport
```bash
curl -X DELETE "http://localhost:8081/api/files/delete?filePath=$RAPPORT_PATH" \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

## 📝 Script de test automatisé (Bash)

```bash
#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Test Script - Gestion des Stages ===${NC}\n"

# 1. Register Student
echo -e "${GREEN}1. Registering student...${NC}"
STUDENT=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "Password123!",
    "nom": "Dupont",
    "prenom": "Alice",
    "role": "ETUDIANT",
    "filiereId": 1,
    "yearLevel": "M2"
  }')

TOKEN_STUDENT=$(echo $STUDENT | jq -r '.token')
STUDENT_ID=$(echo $STUDENT | jq -r '.userId')
echo "Student Token: $TOKEN_STUDENT"
echo "Student ID: $STUDENT_ID\n"

# 2. Register Teacher
echo -e "${GREEN}2. Registering teacher...${NC}"
TEACHER=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "prof@example.com",
    "password": "Password123!",
    "nom": "Martin",
    "prenom": "Jean",
    "role": "ENSEIGNANT",
    "filiereId": 1
  }')

TOKEN_TEACHER=$(echo $TEACHER | jq -r '.token')
TEACHER_ID=$(echo $TEACHER | jq -r '.userId')
echo "Teacher Token: $TOKEN_TEACHER"
echo "Teacher ID: $TEACHER_ID\n"

# 3. Create Filiere
echo -e "${GREEN}3. Creating filiere...${NC}"
FILIERE=$(curl -s -X POST http://localhost:8081/api/filieres \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_STUDENT" \
  -d '{
    "nom": "Génie Informatique",
    "niveau": "M2",
    "description": "Master en Informatique"
  }')

FILIERE_ID=$(echo $FILIERE | jq -r '.id')
echo "Filiere ID: $FILIERE_ID\n"

# 4. Create Stage
echo -e "${GREEN}4. Creating stage...${NC}"
STAGE=$(curl -s -X POST http://localhost:8081/api/stages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_STUDENT" \
  -d "{
    \"sujet\": \"Web Development Platform\",
    \"description\": \"Create a complete e-learning platform\",
    \"entreprise\": \"TechCorp\",
    \"ville\": \"Paris\",
    \"dateDebut\": \"2026-06-01\",
    \"dateFin\": \"2026-08-31\",
    \"etudiantId\": $STUDENT_ID,
    \"filiereId\": $FILIERE_ID
  }")

STAGE_ID=$(echo $STAGE | jq -r '.id')
STAGE_STATE=$(echo $STAGE | jq -r '.etat')
echo "Stage ID: $STAGE_ID"
echo "Stage State: $STAGE_STATE\n"

# 5. Submit Stage
echo -e "${GREEN}5. Submitting stage for validation...${NC}"
SUBMITTED=$(curl -s -X PUT http://localhost:8081/api/stages/$STAGE_ID/submit \
  -H "Authorization: Bearer $TOKEN_STUDENT")
echo "New State: $(echo $SUBMITTED | jq -r '.etat')\n"

# 6. Validate Stage
echo -e "${GREEN}6. Validating stage...${NC}"
VALIDATED=$(curl -s -X PUT "http://localhost:8081/api/stages/$STAGE_ID/validate?encadrantId=$TEACHER_ID" \
  -H "Authorization: Bearer $TOKEN_TEACHER")
echo "New State: $(echo $VALIDATED | jq -r '.etat')\n"

# 7. Get Statistics
echo -e "${GREEN}7. Getting statistics...${NC}"
STATS=$(curl -s http://localhost:8081/api/stats/summary \
  -H "Authorization: Bearer $TOKEN_STUDENT")
echo $STATS | jq '.'
echo ""

echo -e "${GREEN}✅ All tests completed!${NC}"
```

---

## 🧪 Avec Postman

### Importer la collection

1. **Ouvrir Postman**
2. **File → Import**
3. **Coller cette collection JSON :**

```json
{
  "info": {
    "name": "Gestion des Stages",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register Student",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/api/auth/register",
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"alice@example.com\",\n  \"password\": \"Password123!\",\n  \"nom\": \"Dupont\",\n  \"prenom\": \"Alice\",\n  \"role\": \"ETUDIANT\",\n  \"filiereId\": 1,\n  \"yearLevel\": \"M2\"\n}"
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/api/auth/login",
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"alice@example.com\",\n  \"password\": \"Password123!\"\n}"
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8081"
    },
    {
      "key": "token",
      "value": ""
    }
  ]
}
```

4. **Configurer les variables :**
   - `baseUrl` = `http://localhost:8081`
   - `token` = (sera rempli après login)

---

## ✅ Checklist de test

- [ ] **Authentification**
  - [ ] Register étudiant
  - [ ] Register enseignant
  - [ ] Register admin
  - [ ] Login

- [ ] **Filières**
  - [ ] Créer filière
  - [ ] Lister filières
  - [ ] Filtrer par niveau
  - [ ] Modifier filière
  - [ ] Supprimer filière

- [ ] **Stages**
  - [ ] Créer stage (BROUILLON)
  - [ ] Soumettre stage (EN_ATTENTE)
  - [ ] Valider stage (VALIDE)
  - [ ] Refuser stage (REFUSE)
  - [ ] Lister par étudiant
  - [ ] Lister par encadrant
  - [ ] Lister par filière

- [ ] **Recherche**
  - [ ] Rechercher par état
  - [ ] Rechercher par entreprise

- [ ] **Statistiques**
  - [ ] Résumé complet
  - [ ] Comptage par état
  - [ ] Comptage par filière

- [ ] **Fichiers**
  - [ ] Upload rapport
  - [ ] Télécharger rapport
  - [ ] Supprimer rapport

---

**Status** : ✅ Tous les tests sont prêts !

