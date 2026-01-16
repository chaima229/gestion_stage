# 🚀 Quick Start - Comment tester en 5 minutes

## ⚡ Démarrage ultra-rapide

### 1️⃣ Démarrer l'application (30 secondes)

**Terminal 1:**
```bash
cd C:\Users\pc\IdeaProjects\authentification
mvn spring-boot:run
```

Attendez le message :
```
Tomcat started on port(s): 8081
```

### 2️⃣ Créer les données de test (1 minute)

**Terminal 2:**
```bash
# S'assurer que MySQL est accessible
mysql -u root -p

# Exécuter les scripts de migration
# (Flyway le fait automatiquement, vérifiez les logs)
```

### 3️⃣ Tester avec cURL (3 minutes)

```bash
# Inscription étudiant
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "password":"Pass123!",
    "nom":"Dupont",
    "prenom":"Alice",
    "role":"ETUDIANT",
    "filiereId":1,
    "yearLevel":"M2"
  }'

# Vous devriez voir un token JWT
```

---

## 🎯 Les 3 façons de tester

### Option 1 : cURL (Ligne de commande) ⚡
**Pros:** Rapide, pas de dépendances  
**Cons:** Un peu verbeux  

**Commande rapide:**
```bash
curl http://localhost:8081/api/filieres
```

### Option 2 : Postman (Interface graphique) 🖥️
**Pros:** Interface intuitive, historique des requêtes  
**Cons:** À télécharger et installer  

**Comment faire:**
1. Ouvrir Postman
2. New → Request
3. URL: `http://localhost:8081/api/filieres`
4. Click Send

### Option 3 : PowerShell Script (Automatisé) 🤖
**Pros:** Test complet en une commande, résultats formatés  
**Cons:** Spécifique à Windows  

**Commande rapide:**
```powershell
.\test-api.ps1
```

---

## 📋 Checklist de test rapide (5 min)

- [ ] Application démarrée sur port 8081
- [ ] Inscription d'un utilisateur
- [ ] Connexion réussie
- [ ] Lister les filières
- [ ] Créer une filière
- [ ] Créer un stage
- [ ] Voir les statistiques

---

## 🧪 Test minimal (1 minute)

```bash
# Terminal 1 - Démarrer l'app
mvn spring-boot:run

# Terminal 2 - Test simple
curl -s http://localhost:8081/api/filieres | jq

# Résultat: [] (liste vide)
# ✓ API fonctionne!
```

---

## 🔍 Vérifier que ça marche

Quand vous voyez dans les logs :
```
Tomcat started on port(s): 8081
INFO ... c.e.a.AuthentificationApplication : Started AuthentificationApplication
```

✅ **L'application est prête!**

---

## 🐛 Troubleshooting rapide

| Problème | Solution |
|----------|----------|
| `Connection refused` | Vérifiez que MySQL tourne, et le port 3306 |
| `Port 8081 in use` | Changez `server.port=8082` dans `application.properties` |
| `Database error` | Créez manuellement les BDs MySQL |
| `Flyway error` | Supprimez les fichiers migration erronés dans `db/migration/` |

---

## 📊 Résumé des endpoints

| Méthode | URL | Réponse |
|---------|-----|---------|
| `GET` | `/api/filieres` | `[...]` |
| `POST` | `/api/auth/register` | `{token, userId, ...}` |
| `POST` | `/api/auth/login` | `{token, userId, ...}` |
| `POST` | `/api/stages` | `{id, etat: "BROUILLON", ...}` |
| `GET` | `/api/stats/summary` | `{totalStages, etatCounts, ...}` |

---

## 📚 Documentation complète

Pour des tests plus avancés, consultez :
- **TESTING_GUIDE.md** - Guide complet avec tous les endpoints
- **POWERSHELL_TEST_SCRIPT.md** - Script PowerShell automatisé
- **DATABASE_MODELS_GUIDE.md** - Structure des modèles et migrations
- **API_TESTING_GUIDE.md** - Exemples d'utilisation API

---

## 💡 Conseil rapide

**Pour tester au plus vite:**

```bash
# 1. Démarrer
mvn spring-boot:run &

# 2. Attendre 20 sec
sleep 20

# 3. Tester
curl http://localhost:8081/api/filieres && echo " ✓ API OK"
```

---

**Vous êtes prêt!** 🚀

