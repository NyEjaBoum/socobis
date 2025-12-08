## Build & Run (Docker + JDK8 + Ant)

### Prérequis
- Docker Desktop (Windows) actif
- PowerShell

### Étapes
1. Construire les images (WildFly + application, Oracle XE):
```powershell
docker compose build
```
2. Démarrer les conteneurs:
```powershell
docker compose up -d
```
3. Importer la base de données (dump `.dmp`):
```powershell
cd socobis-prod/socobis-prod
./scripts/import-db.ps1 -DumpPath "..\socobis_20251107\socobis_20251107.dmp"
```
	- Si le dump est DataPump (expdp), ajoutez `-DataPump`.
4. Vérifier le schéma:
```powershell
docker exec -it oracle11g bash -c "sqlplus socobisprod/socobisprod@XE <<EOF\nSELECT COUNT(*) FROM user_tables;\nEXIT;\nEOF"
```
5. Accès application: http://localhost:8080

### Configuration JDBC
Fichier `socobis-ejb/src/java/apj.properties` :
```
apj.connection.url=jdbc:oracle:thin:@oracle:1521:XE
apj.connection.user=socobisprod
apj.connection.password=socobisprod
```
Alternative service name: `jdbc:oracle:thin:@//oracle:1521/XE`

### Re-build rapide après modifications Java
```powershell
docker compose build socobis
docker compose up -d socobis
```

### Dépannage
| Problème | Action |
|----------|--------|
| Erreur ORA-01017 | Vérifier user/password dans `apj.properties` et que l'utilisateur existe |
| WildFly ne déploie pas | Consulter `docker logs socobis-app --tail 200` |
| Connexion refusée | Vérifier port 1521 exposé et `lsnrctl status` dans le conteneur Oracle |
| Classes manquantes | S'assurer que `lib/` est copié et que `ant` compile sans erreurs |

---



## Ancien README (template GitLab)



CREATE USER socobis IDENTIFIED BY socobis;
GRANT CONNECT, RESOURCE, IMP_FULL_DATABASE TO socobis;
ALTER USER socobis QUOTA UNLIMITED ON USERS;



impdp socobis/socobis@XE DIRECTORY=DATA_PUMP_DIR DUMPFILE=socobis_20251107.dmp LOGFILE=import.log

Copy-Item "C:\Users\NyEja\Documents\itu\S5\MrTahina\socobis\socobis_20251107\socobis_20251107.dmp" "E:\app\NyEja\admin\DBCOURS\dpdump\"

imp socobis/socobis@localhost:1521/DBCOURS.UNEPH.HT file="C:\Users\NyEja\Documents\itu\S5\MrTahina\socobis\socobis_20251107\socobis_20251107.dmp" log="C:\Users\NyEja\Documents\itu\S5\MrTahina\socobis\socobis_20251107\import.log" full=y


CREATE USER socobis IDENTIFIED BY socobis;
GRANT CONNECT, RESOURCE, IMP_FULL_DATABASE TO socobis;
ALTER USER socobis QUOTA UNLIMITED ON USERS;