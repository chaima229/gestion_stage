# ⚡ Commandes rapides de test

## 🚀 Démarrage en 3 commandes

```bash
# 1. Démarrer l'app
mvn spring-boot:run

# 2. Attendre le démarrage (20 sec)
# Vous devriez voir: "Tomcat started on port(s): 8081"

# 3. Tester dans un autre terminal
curl http://localhost:8081/api/filieres
# Résultat: [] ✓
```

---

## 📝 Commandes de test essentielles

### 1. Inscription
```bash
# Étudiant
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"student@example.com",
    "password":"Pass123!",
    "nom":"Dupont",
    "prenom":"Alice",
    "role":"ETUDIANT",
    "filiereId":1,
    "yearLevel":"M2"
  }'
```

### 2. Connexion
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"student@example.com",
    "password":"Pass123!"
  }'
```

### 3. Créer une filière
```bash
# Remplacer TOKEN par le token reçu
curl -X POST http://localhost:8081/api/filieres \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "nom":"Génie Informatique",
    "niveau":"M2",
    "description":"Master"
  }'
```

### 4. Créer un stage
```bash
curl -X POST http://localhost:8081/api/stages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "sujet":"Web Dev",
    "description":"Platform",
    "entreprise":"TechCorp",
    "ville":"Paris",
    "dateDebut":"2026-06-01",
    "dateFin":"2026-08-31",
    "etudiantId":1,
    "filiereId":1
  }'
```

### 5. Soumettre un stage
```bash
curl -X PUT http://localhost:8081/api/stages/1/submit \
  -H "Authorization: Bearer TOKEN"
```

### 6. Valider un stage
```bash
curl -X PUT "http://localhost:8081/api/stages/1/validate?encadrantId=2" \
  -H "Authorization: Bearer TOKEN"
```

### 7. Voir les stats
```bash
curl http://localhost:8081/api/stats/summary \
  -H "Authorization: Bearer TOKEN"
```

---

## 🎯 Cas d'usage rapides

### Cas 1 : Étudiant crée et soumet un stage
```bash
# 1. Register
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com","password":"Pass123!","nom":"Alice","prenom":"Dupont","role":"ETUDIANT","filiereId":1,"yearLevel":"M2"}'

# 2. Récupérer le token dans la réponse
# 3. Créer un stage
curl -X POST http://localhost:8081/api/stages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"sujet":"Web Dev","description":"Platform","entreprise":"TechCorp","ville":"Paris","dateDebut":"2026-06-01","dateFin":"2026-08-31","etudiantId":1,"filiereId":1}'

# 4. Soumettre
curl -X PUT http://localhost:8081/api/stages/1/submit \
  -H "Authorization: Bearer TOKEN"
```

### Cas 2 : Enseignant valide un stage
```bash
# 1. Register enseignant
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"prof@test.com","password":"Pass123!","nom":"Jean","prenom":"Martin","role":"ENSEIGNANT","filiereId":1}'

# 2. Récupérer le token
# 3. Valider
curl -X PUT "http://localhost:8081/api/stages/1/validate?encadrantId=2" \
  -H "Authorization: Bearer TOKEN"
```

### Cas 3 : Admin voit les stats
```bash
# 1. Register admin
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"Pass123!","nom":"Admin","prenom":"Super","role":"ADMIN"}'

# 2. Récupérer le token
# 3. Voir les stats
curl http://localhost:8081/api/stats/summary \
  -H "Authorization: Bearer TOKEN"
```

---

## 🔗 Tous les endpoints en une page

```
AUTH
  POST   /api/auth/register          Inscription
  POST   /api/auth/login             Connexion

FILIERE
  GET    /api/filieres               Lister
  POST   /api/filieres               Créer
  GET    /api/filieres/{id}          Récupérer
  PUT    /api/filieres/{id}          Modifier
  DELETE /api/filieres/{id}          Supprimer
  GET    /api/filieres/niveau/{n}    Filtrer par niveau

STAGE
  GET    /api/stages                 Lister tous
  POST   /api/stages                 Créer
  GET    /api/stages/{id}            Récupérer
  PUT    /api/stages/{id}            Modifier
  DELETE /api/stages/{id}            Supprimer
  GET    /api/stages/etudiant/{id}   Par étudiant
  GET    /api/stages/encadrant/{id}  Par encadrant
  GET    /api/stages/filiere/{id}    Par filière
  GET    /api/stages/search/etat     Chercher par état
  GET    /api/stages/search/entreprise Chercher par entreprise

WORKFLOW
  PUT    /api/stages/{id}/submit     Soumettre
  PUT    /api/stages/{id}/validate   Valider
  PUT    /api/stages/{id}/refuse     Refuser

STATS
  GET    /api/stats/summary          Résumé
  GET    /api/stats/by-etat          Par état
  GET    /api/stats/by-filiere       Par filière

FILES
  POST   /api/files/upload           Upload
  GET    /api/files/download         Download
  DELETE /api/files/delete           Supprimer
```

---

## 🐛 Problèmes courants et solutions

| Problème | Cause | Solution |
|----------|-------|----------|
| `Connection refused` | API pas démarrée | `mvn spring-boot:run` |
| `Port 8081 in use` | Port déjà utilisé | `server.port=8082` |
| `No route to host` | MySQL pas accessible | Vérifier MySQL sur 3306 |
| `Unauthorized` | Token manquant/invalide | Ajouter `-H "Authorization: Bearer TOKEN"` |
| `Bad Request` | JSON malformé | Vérifier la syntaxe JSON |
| `Not Found` | Ressource n'existe pas | Vérifier l'ID |

---

## 💡 Tips & Tricks

### Sauvegarder un token dans une variable
```bash
# bash/zsh
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com","password":"Pass123!"}' | jq -r '.token')

# Utiliser
curl http://localhost:8081/api/filieres -H "Authorization: Bearer $TOKEN"
```

### Formater la réponse JSON
```bash
curl http://localhost:8081/api/filieres | jq '.'
```

### Voir les headers de réponse
```bash
curl -i http://localhost:8081/api/filieres
```

### Tester avec des données différentes à chaque fois
```bash
# Générer un email unique
EMAIL="user$(date +%s)@example.com"
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"Pass123!\",...}"
```

---

## 📊 Résultat attendu pour chaque commande

### Register
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

### Créer stage
```json
{
  "id": 1,
  "sujet": "Web Dev",
  "etat": "BROUILLON",
  "etudiantId": 1,
  "encadrantId": null,
  "filiereId": 1
}
```

### Stats
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

---

## ✅ Validation rapide

```bash
# Tester si tout fonctionne
echo "🚀 Test 1: API accessible"
curl -s http://localhost:8081/api/filieres > /dev/null && echo "✓ OK" || echo "✗ FAIL"

echo "🚀 Test 2: Créer utilisateur"
curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Pass123!","nom":"Test","prenom":"User","role":"ETUDIANT"}' | grep -q "token" && echo "✓ OK" || echo "✗ FAIL"

echo "🚀 Test 3: Créer filière"
curl -s -X POST http://localhost:8081/api/filieres \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"nom":"Test","niveau":"M2"}' | grep -q "id" && echo "✓ OK" || echo "✗ FAIL"

echo "✅ Tests complets!"
```

---

**Status** : ✅ Commandes rapides prêtes!

