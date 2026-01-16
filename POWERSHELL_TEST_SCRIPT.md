# 🧪 Script de Test PowerShell - Gestion des Stages

## 💻 Exécution facile avec PowerShell

Sauvegardez ce script en tant que `test-api.ps1` puis exécutez :

```powershell
.\test-api.ps1
```

## 📄 Fichier `test-api.ps1`

```powershell
# ========================================
# Test Script for Gestion des Stages API
# ========================================

# Configuration
$BASE_URL = "http://localhost:8081"
$EMAIL_STUDENT = "alice$(Get-Date -Format 'yyyyMMddHHmmss')@example.com"
$EMAIL_TEACHER = "prof$(Get-Date -Format 'yyyyMMddHHmmss')@example.com"
$EMAIL_ADMIN = "admin$(Get-Date -Format 'yyyyMMddHHmmss')@example.com"
$PASSWORD = "Password123!"

# Colors
$GREEN = "`e[32m"
$BLUE = "`e[34m"
$RESET = "`e[0m"

function Write-Title {
    param([string]$Title)
    Write-Host "`n$BLUE=== $Title ===$RESET`n"
}

function Write-Success {
    param([string]$Message)
    Write-Host "$GREEN✓ $Message$RESET"
}

Write-Host "$BLUE" + "╔════════════════════════════════════════╗"
Write-Host "║   Gestion des Stages - Test Script    ║"
Write-Host "╚════════════════════════════════════════╝" + "$RESET`n"

# ========================================
# 1. AUTHENTIFICATION - Inscription
# ========================================
Write-Title "1. AUTHENTIFICATION - Inscription"

Write-Host "📝 Inscription Étudiant..."
$responseStudent = Invoke-RestMethod -Method POST -Uri "$BASE_URL/api/auth/register" `
    -ContentType "application/json" `
    -Body @{
        email = $EMAIL_STUDENT
        password = $PASSWORD
        nom = "Dupont"
        prenom = "Alice"
        role = "ETUDIANT"
        filiereId = 1
        yearLevel = "M2"
    } | ConvertTo-Json | ConvertFrom-Json

$TOKEN_STUDENT = $responseStudent.token
$STUDENT_ID = $responseStudent.userId
Write-Success "Étudiant créé: ID=$STUDENT_ID, Email=$EMAIL_STUDENT"

Write-Host "📝 Inscription Enseignant..."
$responseTeacher = Invoke-RestMethod -Method POST -Uri "$BASE_URL/api/auth/register" `
    -ContentType "application/json" `
    -Body @{
        email = $EMAIL_TEACHER
        password = $PASSWORD
        nom = "Martin"
        prenom = "Jean"
        role = "ENSEIGNANT"
        filiereId = 1
    } | ConvertTo-Json | ConvertFrom-Json

$TOKEN_TEACHER = $responseTeacher.token
$TEACHER_ID = $responseTeacher.userId
Write-Success "Enseignant créé: ID=$TEACHER_ID, Email=$EMAIL_TEACHER"

Write-Host "📝 Inscription Admin..."
$responseAdmin = Invoke-RestMethod -Method POST -Uri "$BASE_URL/api/auth/register" `
    -ContentType "application/json" `
    -Body @{
        email = $EMAIL_ADMIN
        password = $PASSWORD
        nom = "Admin"
        prenom = "Super"
        role = "ADMIN"
    } | ConvertTo-Json | ConvertFrom-Json

$TOKEN_ADMIN = $responseAdmin.token
$ADMIN_ID = $responseAdmin.userId
Write-Success "Admin créé: ID=$ADMIN_ID, Email=$EMAIL_ADMIN"

# ========================================
# 2. AUTHENTIFICATION - Connexion
# ========================================
Write-Title "2. AUTHENTIFICATION - Connexion"

Write-Host "🔐 Test de connexion..."
$loginResponse = Invoke-RestMethod -Method POST -Uri "$BASE_URL/api/auth/login" `
    -ContentType "application/json" `
    -Body @{
        email = $EMAIL_STUDENT
        password = $PASSWORD
    } | ConvertTo-Json | ConvertFrom-Json

Write-Success "Connexion réussie pour $EMAIL_STUDENT"

# ========================================
# 3. FILIÈRES
# ========================================
Write-Title "3. FILIÈRES"

Write-Host "➕ Création de filière..."
$filiereBody = @{
    nom = "Génie Informatique"
    niveau = "M2"
    description = "Master en Informatique généraliste"
} | ConvertTo-Json

$filiereResponse = Invoke-RestMethod -Method POST `
    -Uri "$BASE_URL/api/filieres" `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $TOKEN_ADMIN" } `
    -Body $filiereBody

$FILIERE_ID = $filiereResponse.id
Write-Success "Filière créée: $($filiereResponse.nom) (ID=$FILIERE_ID)"

Write-Host "📋 Liste des filières..."
$filieres = Invoke-RestMethod -Uri "$BASE_URL/api/filieres" `
    -Headers @{ Authorization = "Bearer $TOKEN_STUDENT" }
Write-Success "Nombre de filières: $($filieres.Count)"

# ========================================
# 4. STAGES - Création et Workflow
# ========================================
Write-Title "4. STAGES - Création et Workflow"

Write-Host "📝 Création d'un stage (BROUILLON)..."
$stageBody = @{
    sujet = "Développement d'une plateforme e-learning"
    description = "Création d'une plateforme web complète pour l'enseignement en ligne"
    entreprise = "TechCorp"
    ville = "Paris"
    dateDebut = "2026-06-01"
    dateFin = "2026-08-31"
    etudiantId = $STUDENT_ID
    filiereId = $FILIERE_ID
} | ConvertTo-Json

$stageResponse = Invoke-RestMethod -Method POST `
    -Uri "$BASE_URL/api/stages" `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $TOKEN_STUDENT" } `
    -Body $stageBody

$STAGE_ID = $stageResponse.id
Write-Success "Stage créé (ID=$STAGE_ID, État=$($stageResponse.etat))"

Write-Host "📤 Soumission pour validation (EN_ATTENTE_VALIDATION)..."
$submitResponse = Invoke-RestMethod -Method PUT `
    -Uri "$BASE_URL/api/stages/$STAGE_ID/submit" `
    -Headers @{ Authorization = "Bearer $TOKEN_STUDENT" }
Write-Success "Stage soumis (Nouvel état: $($submitResponse.etat))"

Write-Host "✅ Validation du stage (VALIDE)..."
$validateResponse = Invoke-RestMethod -Method PUT `
    -Uri "$BASE_URL/api/stages/$STAGE_ID/validate?encadrantId=$TEACHER_ID" `
    -Headers @{ Authorization = "Bearer $TOKEN_TEACHER" }
Write-Success "Stage validé (Nouvel état: $($validateResponse.etat), Encadrant=$($validateResponse.encadrantId))"

# Créer un 2e stage pour tester le refus
Write-Host "📝 Création d'un 2e stage pour tester le refus..."
$stageBody2 = @{
    sujet = "Autre stage"
    description = "Test de refus"
    entreprise = "TestCorp"
    ville = "Lyon"
    dateDebut = "2026-07-01"
    dateFin = "2026-09-30"
    etudiantId = $STUDENT_ID
    filiereId = $FILIERE_ID
} | ConvertTo-Json

$stage2Response = Invoke-RestMethod -Method POST `
    -Uri "$BASE_URL/api/stages" `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $TOKEN_STUDENT" } `
    -Body $stageBody2

$STAGE_ID_2 = $stage2Response.id
Write-Success "2e stage créé (ID=$STAGE_ID_2)"

Write-Host "📤 Soumission du 2e stage..."
Invoke-RestMethod -Method PUT `
    -Uri "$BASE_URL/api/stages/$STAGE_ID_2/submit" `
    -Headers @{ Authorization = "Bearer $TOKEN_STUDENT" } | Out-Null
Write-Success "2e stage soumis"

Write-Host "❌ Refus du 2e stage (REFUSE)..."
$refuseResponse = Invoke-RestMethod -Method PUT `
    -Uri "$BASE_URL/api/stages/$STAGE_ID_2/refuse?commentaire=Sujet%20non%20approprié" `
    -Headers @{ Authorization = "Bearer $TOKEN_TEACHER" }
Write-Success "Stage refusé (Nouvel état: $($refuseResponse.etat), Commentaire: $($refuseResponse.commentaire))"

# ========================================
# 5. RECHERCHE et FILTRES
# ========================================
Write-Title "5. RECHERCHE et FILTRES"

Write-Host "🔍 Stages par étudiant..."
$stagesByStudent = Invoke-RestMethod `
    -Uri "$BASE_URL/api/stages/etudiant/$STUDENT_ID" `
    -Headers @{ Authorization = "Bearer $TOKEN_STUDENT" }
Write-Success "Nombre de stages: $($stagesByStudent.Count)"

Write-Host "🔍 Stages par état (VALIDE)..."
$stagesByState = Invoke-RestMethod `
    -Uri "$BASE_URL/api/stages/search/etat?etat=VALIDE" `
    -Headers @{ Authorization = "Bearer $TOKEN_ADMIN" }
Write-Success "Stages VALIDE: $($stagesByState.Count)"

Write-Host "🔍 Stages par entreprise..."
$stagesByCompany = Invoke-RestMethod `
    -Uri "$BASE_URL/api/stages/search/entreprise?entreprise=TechCorp" `
    -Headers @{ Authorization = "Bearer $TOKEN_ADMIN" }
Write-Success "Stages TechCorp: $($stagesByCompany.Count)"

# ========================================
# 6. STATISTIQUES
# ========================================
Write-Title "6. STATISTIQUES"

Write-Host "📊 Résumé des statistiques..."
$stats = Invoke-RestMethod `
    -Uri "$BASE_URL/api/stats/summary" `
    -Headers @{ Authorization = "Bearer $TOKEN_ADMIN" }

Write-Host @"
$GREEN
Total Stages: $($stats.totalStages)
États:
  - BROUILLON: $($stats.etatCounts.BROUILLON)
  - EN_ATTENTE: $($stats.etatCounts.EN_ATTENTE_VALIDATION)
  - VALIDE: $($stats.etatCounts.VALIDE)
  - REFUSE: $($stats.etatCounts.REFUSE)
$RESET
"@
Write-Success "Statistiques récupérées"

# ========================================
# 7. FICHIERS
# ========================================
Write-Title "7. FICHIERS - Upload/Download"

Write-Host "📄 Création d'un fichier test..."
$testFilePath = "rapport_test.txt"
@"
Rapport de Stage - Test
Auteur: Alice
Date: $(Get-Date)
"@ | Out-File -FilePath $testFilePath -Encoding UTF8
Write-Success "Fichier créé: $testFilePath"

Write-Host "📤 Upload du rapport..."
$fileContent = Get-Content $testFilePath -Raw
$uploadResponse = Invoke-RestMethod -Method POST `
    -Uri "$BASE_URL/api/files/upload?stageId=$STAGE_ID" `
    -Headers @{ Authorization = "Bearer $TOKEN_STUDENT" } `
    -Form @{ file = Get-Item $testFilePath }
Write-Success "Rapport uploadé"

# ========================================
# RÉSUMÉ FINAL
# ========================================
Write-Title "RÉSUMÉ FINAL"

Write-Host @"
$GREEN
✅ Tous les tests sont terminés avec succès!

Utilisateurs créés:
  - Étudiant: $EMAIL_STUDENT (ID=$STUDENT_ID)
  - Enseignant: $EMAIL_TEACHER (ID=$TEACHER_ID)
  - Admin: $EMAIL_ADMIN (ID=$ADMIN_ID)

Filière créée:
  - $($filiereResponse.nom) (ID=$FILIERE_ID, Niveau=$($filiereResponse.niveau))

Stages créés:
  - Stage 1: VALIDE
  - Stage 2: REFUSE

Statistiques:
  - Total: $($stats.totalStages) stages
  - VALIDE: $($stats.etatCounts.VALIDE)
  - REFUSE: $($stats.etatCounts.REFUSE)

$RESET
"@

Write-Host "$GREEN" + "╔════════════════════════════════════════╗"
Write-Host "║      ✓ Tests réussis avec succès!     ║"
Write-Host "╚════════════════════════════════════════╝" + "$RESET"

# Cleanup
Remove-Item $testFilePath -Force
```

---

## 🚀 Utilisation du script

### Étape 1 : Sauvegarder le script

Créez un fichier `test-api.ps1` et copiez-y le contenu ci-dessus.

### Étape 2 : Autoriser l'exécution

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Étape 3 : Exécuter le script

```powershell
cd C:\Users\pc\IdeaProjects\authentification
.\test-api.ps1
```

---

## 📊 Output attendu

```
╔════════════════════════════════════════╗
║   Gestion des Stages - Test Script    ║
╚════════════════════════════════════════╝

=== 1. AUTHENTIFICATION - Inscription ===

📝 Inscription Étudiant...
✓ Étudiant créé: ID=1, Email=alice20260112101530@example.com

📝 Inscription Enseignant...
✓ Enseignant créé: ID=2, Email=prof20260112101531@example.com

📝 Inscription Admin...
✓ Admin créé: ID=3, Email=admin20260112101532@example.com

=== 2. AUTHENTIFICATION - Connexion ===

🔐 Test de connexion...
✓ Connexion réussie pour alice20260112101530@example.com

... (suite)

╔════════════════════════════════════════╗
║      ✓ Tests réussis avec succès!     ║
╚════════════════════════════════════════╝
```

---

**Status** : ✅ Script de test PowerShell prêt à l'emploi !

