# 🚀 Migration vers ADMIN/SOUS_ADMIN - Guide complet

## ✅ Avant de migrer

Vérifiez que:
- [ ] MySQL est en cours d'exécution
- [ ] Compilation réussie (`mvn clean compile`)
- [ ] 5 minutes disponibles

---

## 🎯 3 étapes pour migrer

### Étape 1: Supprimer l'ancienne BD + en créer une nouvelle

```bash
mysql -u root -p -e "DROP DATABASE authdb; CREATE DATABASE authdb CHARACTER SET utf8mb4;"
```

**Résultat attendu:**
```
Query OK (pas d'erreur)
```

---

### Étape 2: Démarrer l'application (migrations auto!)

```bash
cd C:\Users\pc\IdeaProjects\authentification
mvn spring-boot:run
```

**Attendez ces messages:**
```
INFO ... Flyway : Successfully applied 5 migrations
INFO ... Flyway : Migrating schema to version 1 - Create Users Table
INFO ... Flyway : Migrating schema to version 2 - Create Filieres Table
INFO ... Flyway : Migrating schema to version 3 - Create Stages Table
INFO ... Flyway : Migrating schema to version 4 - Add ImportLog Table
INFO ... Flyway : Migrating schema to version 5 - Add SOUS_ADMIN Role
Tomcat started on port(s): 8081 ✓
```

---

### Étape 3: Vérifier (dans un autre terminal)

```bash
mysql -u root authdb -e "SHOW TABLES; SELECT COUNT(*) FROM flyway_schema_history;"
```

**Résultat attendu:**
```
+-----------------------+
| Tables_in_authdb      |
+-----------------------+
| flyway_schema_history |
| filieres              |
| import_logs           |  ← NEW!
| stages                |
| users                 |
+-----------------------+
5 rows in set (count = 5)
```

✅ **Migration réussie!**

---

## 🆘 Dépannage

### Erreur: "Cannot drop database authdb"
**Solution:** Arrêter l'app d'abord (Ctrl+C)

### Erreur: "Access denied for user 'root'"
**Solution:** Vérifier votre mot de passe MySQL

### Tables manquantes
**Solution:** Vérifier les logs pour les erreurs Flyway

---

## 📊 Après migration, vous aurez:

```
✅ 5 tables:
   - users (avec SOUS_ADMIN)
   - filieres
   - stages
   - import_logs (NEW!)
   - flyway_schema_history

✅ 5 migrations appliquées:
   - V1: Users
   - V2: Filieres
   - V3: Stages
   - V4: ImportLog
   - V5: SOUS_ADMIN Role

✅ API prête sur http://localhost:8081
```

---

## 🎯 Résumé des commandes

```bash
# 1. Supprimer et créer BD
mysql -u root -p -e "DROP DATABASE authdb; CREATE DATABASE authdb CHARACTER SET utf8mb4;"

# 2. Démarrer
cd C:\Users\pc\IdeaProjects\authentification && mvn spring-boot:run

# 3. Vérifier (autre terminal)
mysql -u root authdb -e "SHOW TABLES;"
```

**Total: ~2 minutes ⚡**

---

## ✅ Status

```
✅ Toutes les migrations prêtes
✅ Entité ImportLog créée
✅ Service d'import créé
✅ Endpoints prêts
✅ Compilation réussie
✅ Prêt à migrer!
```

---

## 🚀 Allez-y!

Exécutez les 3 commandes et vous serez prêt! 🎉

