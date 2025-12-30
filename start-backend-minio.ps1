# Detener Java existente
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue

# Limpiar logs
Remove-Item -Path logs\backend*.log -Force -ErrorAction SilentlyContinue

# Configurar todas las variables de entorno
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3307/techshare_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:SPRING_DATASOURCE_USERNAME = 'root'
$env:SPRING_DATASOURCE_PASSWORD = 'root'
$env:MINIO_ENDPOINT = 'http://localhost:9000'
$env:MINIO_ACCESS_KEY = 'minioadmin'
$env:MINIO_SECRET_KEY = 'minioadmin'
$env:MINIO_BUCKET_NAME = 'techshare'
$env:APP_STORAGE_TYPE = 'minio'

Write-Host "🚀 Iniciando backend con perfil MinIO..." -ForegroundColor Cyan

# Iniciar JAR (bloquea el terminal)
java -jar target/techmate-0.0.1-SNAPSHOT.jar --spring.profiles.active=minio
