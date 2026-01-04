# GitHub Actions CI/CD Setup Guide

## Overview

Este proyecto tiene configurado un pipeline CI/CD completo con GitHub Actions que:

1. ✅ Compila el código
2. ✅ Ejecuta la suite de tests (383 tests)
3. ✅ Ejecuta análisis de calidad y seguridad
4. ✅ Construye y pushea imagen Docker
5. ✅ Despliega a staging (configurable)
6. ✅ Notifica estado del build

---

## Pipeline Stages

### 1️⃣ Build & Test

- **Trigger**: Push a `dev` o `main`
- **Java**: JDK 17 (Temurin)
- **Steps**:
  - Checkout código
  - Compilar (`./mvnw compile`)
  - Tests (`./mvnw test`)
  - Package JAR (`./mvnw package`)
  - Upload artifacts (7 días)

**Status**: ✅ Listo — sin cambios necesarios

---

### 2️⃣ Code Quality Analysis

- **Trigger**: Después del Build (siempre)
- **Steps**:
  - Dependency security check
  - Compilación para análisis estático

**Opcional**: Integrar con:

- SonarQube: `./mvnw sonar:sonar`
- Checkstyle: `./mvnw checkstyle:check`

**Status**: ✅ Básico configurado

---

### 3️⃣ Docker Build & Push

- **Trigger**: Push a `dev` o `main` (solo en ramas)
- **Registry**: GitHub Container Registry (ghcr.io)
- **Steps**:
  - Setup Docker Buildx
  - Login a ghcr.io
  - Build y push imagen
  - Tags automáticos: `dev`, `main`, SHA commit
  - Cache habilitado (GHA)

**Prerequisitos**:

1. `Back-End-TechShare-Java/Dockerfile` debe existir ✓
2. Token GITHUB_TOKEN se usa automáticamente ✓

**Status**: ✅ Listo para usar

---

### 4️⃣ Deploy to Staging

- **Trigger**: Push a rama `dev`
- **Environment**: staging (requiere aprobación manual)
- **Steps**:
  - Placeholder para deployment real
  - SSH deployment (ejemplo deshabilitado)
  - Health check

**Para activar**:

```bash
# En Settings > Environments > staging, agregar:
# - STAGING_HOST: hostname o IP
# - STAGING_USER: usuario SSH
# - STAGING_SSH_KEY: clave privada SSH
```

**Configuración actual**: Deshabilitada (si: false) — reemplaza con tu logic

**Status**: ⏳ Requiere configuración manual

---

### 5️⃣ Notifications

- **Trigger**: Después de Build & Quality
- **Output**:
  - Build summary en GitHub UI
  - Status message (success/failure)

**Status**: ✅ Automático

---

## 🔐 GitHub Secrets Required

Para que el pipeline funcione completamente, configura estos secretos:

### Obligatorios (Build & Docker):

- **GITHUB_TOKEN**: Automático en GitHub Actions ✓

### Opcionales (Deploy a Staging):

```
STAGING_HOST         = host.example.com
STAGING_USER         = deploy_user
STAGING_SSH_KEY      = -----BEGIN PRIVATE KEY-----...
```

### Recomendados (Seguridad):

```
SLACK_WEBHOOK        = https://hooks.slack.com/services/...
EMAIL_NOTIFY         = devops@company.com
```

### Para tu proyecto específico:

```
# En producción (inyectar en .env durante deploy):
JWT_SECRET           = (generada, nunca commiteada)
SPRING_DATASOURCE_PASSWORD = (from vault/secrets manager)
MAIL_PASSWORD        = (from vault/secrets manager)
REDIS_PASSWORD       = (from vault/secrets manager)
```

---

## ⚙️ Configuración en GitHub

### Step 1: Habilitar GitHub Actions

1. Ve a tu repositorio en GitHub
2. Settings > Actions > General
3. Asegura que "Allow GitHub Actions to create and approve pull requests" esté ✓

### Step 2: Agregar Secretos

1. Settings > Secrets and variables > Actions
2. Click "New repository secret"
3. Ejemplo:
   ```
   Name:  STAGING_HOST
   Value: staging.techshare.com
   ```

### Step 3: Configurar Ambientes (opcional)

1. Settings > Environments
2. Click "New environment" > "staging"
3. Protección (require approval): ✓
4. Agregar secretos específicos del ambiente

### Step 4: Revisar Workflow

1. Actions > TechShare Backend CI/CD
2. Ver logs y status de cada job

---

## 📊 Monitorear el Pipeline

### En GitHub UI:

- **Actions tab**: Ver todos los workflows
- **Commit status**: ✅ o ❌ al lado del commit
- **Branch protections**: Requerir "Passing checks" antes de merge

### Configurar Branch Protection (dev):

1. Settings > Branches > Branch protection rules
2. Add rule para rama `dev`:
   ```
   - Require status checks to pass before merging: ✓
   - Require branches to be up to date: ✓
   - Dismiss stale pull request approvals: ✓
   ```

---

## 🐳 Docker Image Registry

### Acceder a la imagen:

```bash
# Login a ghcr.io
docker login ghcr.io -u <USERNAME> -p <GITHUB_TOKEN>

# Pull imagen
docker pull ghcr.io/RafaPacheco2003/Back-End-TechShare-Java/techshare-backend:dev

# Run local
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/techshare_db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  -e JWT_SECRET=$(openssl rand -base64 32) \
  ghcr.io/RafaPacheco2003/Back-End-TechShare-Java/techshare-backend:dev
```

---

## 🚀 Deployment Strategies

### Option A: Manual Deployment (sin Pipeline)

```bash
# En tu servidor staging:
docker pull ghcr.io/.../techshare-backend:dev
docker-compose up -d
```

### Option B: Automated Deployment (Pipeline)

Descomenta la sección `deploy-staging` y configura secretos SSH.

### Option C: Kubernetes Deployment

```bash
# En lugar de docker-compose, usa:
kubectl set image deployment/techshare-backend \
  techshare-backend=ghcr.io/.../techshare-backend:dev
```

---

## 📋 Checklist de Setup

- [ ] Verificar que `.github/workflows/ci-cd.yml` existe
- [ ] Verificar que `Back-End-TechShare-Java/Dockerfile` existe
- [ ] En GitHub Settings > Secrets, agregar `STAGING_HOST`, `STAGING_USER`, `STAGING_SSH_KEY` (si despliegas)
- [ ] En GitHub Actions, revisar que el workflow tenga permiso para escribir en ghcr.io
- [ ] Hacer un push a `dev` y monitorear primer run en GitHub Actions tab
- [ ] Configurar notificaciones (Slack, Email)
- [ ] Configurar branch protection rules en `dev` y `main`

---

## 🔧 Troubleshooting

### Build falla en CI pero pasa local

- Verifica Java version: `./mvnw --version`
- Limpia Maven cache: `./mvnw clean`
- Revisa logs en GitHub Actions UI

### Docker push falla

- Verifica que GITHUB_TOKEN tiene permiso `write:packages`
- Revisa que el Dockerfile existe: `Back-End-TechShare-Java/Dockerfile`

### Deploy no inicia

- SSH key format debe ser OpenSSH (no PuTTY)
- Host conocido: agrega `known_hosts` en workflow
- Permisos: usuario deploy debe tener acceso a `/opt/techshare`

---

## 📞 References

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Docker Build & Push Action](https://github.com/docker/build-push-action)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)

---

**Status**: ✅ Workflow configurado y listo
**Last Updated**: 2025-12-22
**Maintainer**: DevOps Team
