# 🧪 Guide de Test - Application de Gestion des Stages

## 🚀 Démarrage de l'application

```bash
cd C:\Users\pc\IdeaProjects\authentification
mvn spring-boot:run
```

L'application démarrera sur **http://localhost:8081**

---

## 🗄️ Créer les bases de données

Avant de tester, exécutez ces commandes SQL :

```sql
-- Créer les BD
CREATE DATABASE authdb CHARACTER SET utf8mb4;
CREATE DATABASE filiere_db CHARACTER SET utf8mb4;
CREATE DATABASE stage_db CHARACTER SET utf8mb4;

-- Ces tables seront créées automatiquement par Hibernate (ddl-auto=update)
```

---

## 📝 Tests API avec cURL

### **1. Inscription (Register)**

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "etudiant@example.com",
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
  "email": "etudiant@example.com",
  "role": "ETUDIANT",
  "nom": "Dupont",
  "prenom": "Alice"
}
```

### **2. Inscription Enseignant**

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "encadrant@example.com",
    "password": "Password123!",
    "nom": "Martin",
    "prenom": "Jean",
    "role": "ENSEIGNANT",
    "filiereId": 1
  }'
```

### **3. Inscription Admin**

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

### **4. Connexion (Login)**

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "etudiant@example.com",
    "password": "Password123!"
  }'
```

**Réponse attendue :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "etudiant@example.com",
  "role": "ETUDIANT",
  "nom": "Dupont",
  "prenom": "Alice"
}
```

---

## 📚 Tests API Filière

### **5. Créer une Filière**

```bash
# En tant qu'ADMIN
TOKEN="<token_from_login>"

curl -X POST http://localhost:8081/api/filieres \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nom": "Génie Informatique",
    "niveau": "M2",
    "description": "Master en Informatique généraliste"
  }'
```

### **6. Lister toutes les Filières**

```bash
curl http://localhost:8081/api/filieres \
  -H "Authorization: Bearer $TOKEN"
```

### **7. Filtrer par niveau**

```bash
curl "http://localhost:8081/api/filieres/niveau/M2" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📋 Tests API Stages

### **8. Créer un Stage (Étudiant)**

```bash
curl -X POST http://localhost:8081/api/stages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "sujet": "Développement d'\''une app web",
    "description": "Création d'\''une plateforme e-learning",
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
  "sujet": "Développement d'une app web",
  "etat": "BROUILLON",
  "etudiantId": 1,
  "encadrantId": null,
  "...": "..."
}
```

### **9. Lister les stages d'un étudiant**

```bash
curl "http://localhost:8081/api/stages/etudiant/1" \
  -H "Authorization: Bearer $TOKEN"
```

### **10. Lister tous les stages**

```bash
curl http://localhost:8081/api/stages \
  -H "Authorization: Bearer $TOKEN"
```

### **11. Soumettre un stage pour validation**

```bash
curl -X PUT http://localhost:8081/api/stages/1/submit \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse :**
```json
{
  "id": 1,
  "etat": "EN_ATTENTE_VALIDATION",
  "...": "..."
}
```

### **12. Valider un stage (Enseignant)**

```bash
ENCADRANT_TOKEN="<token_enseignant>"

curl -X PUT "http://localhost:8081/api/stages/1/validate?encadrantId=2" \
  -H "Authorization: Bearer $ENCADRANT_TOKEN"
```

**Réponse :**
```json
{
  "id": 1,
  "etat": "VALIDE",
  "encadrantId": 2,
  "...": "..."
}
```

### **13. Refuser un stage**

```bash
curl -X PUT "http://localhost:8081/api/stages/1/refuse?commentaire=Sujet%20non%20approprié" \
  -H "Authorization: Bearer $ENCADRANT_TOKEN"
```

---

## 📊 Tests API Statistiques

### **14. Résumé complet des statistiques**

```bash
curl http://localhost:8081/api/stats/summary \
  -H "Authorization: Bearer $TOKEN"
```

**Réponse :**
```json
{
  "totalStages": 5,
  "etatCounts": {
    "BROUILLON": 1,
    "EN_ATTENTE_VALIDATION": 2,
    "VALIDE": 1,
    "REFUSE": 1
  },
  "filiereCounts": {
    "FILIERE_1": 4,
    "FILIERE_2": 1
  }
}
```

### **15. Comptage par état**

```bash
curl http://localhost:8081/api/stats/by-etat \
  -H "Authorization: Bearer $TOKEN"
```

### **16. Comptage par filière**

```bash
curl http://localhost:8081/api/stats/by-filiere \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📁 Tests API File (Upload/Download)

### **17. Upload d'un rapport PDF**

```bash
# Créer un fichier test.pdf d'abord
curl -X POST "http://localhost:8081/api/files/upload?stageId=1" \
  -F "file=@C:/path/to/rapport.pdf" \
  -H "Authorization: Bearer $TOKEN"
```

### **18. Télécharger un rapport**

```bash
curl "http://localhost:8081/api/files/download?filePath=uploads/rapports/stage_1_uuid.pdf" \
  -H "Authorization: Bearer $TOKEN" \
  -o rapport_telechargé.pdf
```

### **19. Supprimer un rapport**

```bash
curl -X DELETE "http://localhost:8081/api/files/delete?filePath=uploads/rapports/stage_1_uuid.pdf" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🧪 Scénario complet d'utilisation

```bash
#!/bin/bash

# 1. Inscrire un étudiant
ETUDIANT_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com","password":"Pass123","nom":"Dupont","prenom":"Alice","role":"ETUDIANT","filiereId":1,"yearLevel":"M2"}')

ETUDIANT_TOKEN=$(echo $ETUDIANT_RESPONSE | jq -r '.token')
ETUDIANT_ID=$(echo $ETUDIANT_RESPONSE | jq -r '.userId')

# 2. Créer un stage
STAGE_RESPONSE=$(curl -s -X POST http://localhost:8081/api/stages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ETUDIANT_TOKEN" \
  -d "{\"sujet\":\"Web Dev\",\"description\":\"App web\",\"entreprise\":\"TechCorp\",\"ville\":\"Paris\",\"dateDebut\":\"2026-06-01\",\"dateFin\":\"2026-08-31\",\"etudiantId\":$ETUDIANT_ID,\"filiereId\":1}")

STAGE_ID=$(echo $STAGE_RESPONSE | jq -r '.id')

# 3. Soumettre le stage
curl -s -X PUT http://localhost:8081/api/stages/$STAGE_ID/submit \
  -H "Authorization: Bearer $ETUDIANT_TOKEN"

# 4. Inscrire un encadrant
ENCADRANT_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"prof@test.com","password":"Pass123","nom":"Martin","prenom":"Jean","role":"ENSEIGNANT","filiereId":1}')

ENCADRANT_TOKEN=$(echo $ENCADRANT_RESPONSE | jq -r '.token')
ENCADRANT_ID=$(echo $ENCADRANT_RESPONSE | jq -r '.userId')

# 5. Valider le stage
curl -s -X PUT "http://localhost:8081/api/stages/$STAGE_ID/validate?encadrantId=$ENCADRANT_ID" \
  -H "Authorization: Bearer $ENCADRANT_TOKEN"

# 6. Obtenir les stats
curl -s http://localhost:8081/api/stats/summary \
  -H "Authorization: Bearer $ETUDIANT_TOKEN" | jq
```

---

## ✅ Endpoints testés

- [x] POST /api/auth/register - Inscription
- [x] POST /api/auth/login - Connexion
- [x] GET /api/filieres - Lister filières
- [x] POST /api/filieres - Créer filière
- [x] GET /api/filieres/{id} - Récupérer filière
- [x] PUT /api/filieres/{id} - Modifier filière
- [x] DELETE /api/filieres/{id} - Supprimer filière
- [x] GET /api/stages - Lister stages
- [x] POST /api/stages - Créer stage
- [x] GET /api/stages/{id} - Récupérer stage
- [x] PUT /api/stages/{id} - Modifier stage
- [x] PUT /api/stages/{id}/submit - Soumettre
- [x] PUT /api/stages/{id}/validate - Valider
- [x] PUT /api/stages/{id}/refuse - Refuser
- [x] GET /api/stats/summary - Stats résumé
- [x] GET /api/stats/by-etat - Stats par état
- [x] GET /api/stats/by-filiere - Stats par filière
- [x] POST /api/files/upload - Upload
- [x] GET /api/files/download - Download
- [x] DELETE /api/files/delete - Delete

---

**✅ Tous les endpoints sont testables et fonctionnels !**

