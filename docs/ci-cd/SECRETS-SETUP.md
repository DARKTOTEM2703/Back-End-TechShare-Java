# GitHub Actions Secrets Configuration Helper

## 📋 Quick Setup Script

Este script ayuda a crear los secretos necesarios en GitHub de forma segura.

### Requisitos:

- CLI de GitHub: https://cli.github.com/
- Permisos de administrador en el repositorio

### Ejecutar:

```bash
# 1. Login en GitHub CLI
gh auth login

# 2. Navega al repositorio
cd Back-End-TechShare-Java

# 3. Agregar secretos (ejecuta cada comando por separado)

# Secretos obligatorios para Docker Push (automático con GITHUB_TOKEN):
# No requiere configuración adicional — usa el token de GitHub Actions

# Secretos opcionales para Staging Deployment:
gh secret set STAGING_HOST --body "staging.techshare.com"
gh secret set STAGING_USER --body "deploy_user"
gh secret set STAGING_SSH_KEY --body "$(cat ~/.ssh/id_rsa)"

# Secretos para notificaciones (opcional):
gh secret set SLACK_WEBHOOK --body "https://hooks.slack.com/services/YOUR_WEBHOOK"

# 4. Verificar secretos agregados:
gh secret list
```

---

## 🔑 Generar JWT Secret (production-safe)

```bash
# Generar una clave segura de 256 bits (Base64):
openssl rand -base64 32

# Ejemplo salida:
# UxgmwdNrTQPwlF3PyK6mLMZusftOEM2hqK/VPuoDHj0=

# Copiar ese valor y agregarlo como secreto:
gh secret set JWT_SECRET --body "UxgmwdNrTQPwlF3PyK6mLMZusftOEM2hqK/VPuoDHj0="
```

---

## 🐳 Generar Token GHCR (GitHub Container Registry)

1. Ve a https://github.com/settings/tokens/new
2. Selecciona `write:packages` y `read:packages`
3. Genera el token
4. Copia el token y agrégalo:

```bash
gh secret set GHCR_TOKEN --body "ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

O usa GITHUB_TOKEN (automático en CI):

```yaml
# En el workflow, usa:
with:
  username: ${{ github.actor }}
  password: ${{ secrets.GITHUB_TOKEN }}
```

---

## 📦 Secretos para Database & Services

```bash
# Base de datos (production):
gh secret set SPRING_DATASOURCE_URL --body "jdbc:mysql://prod-db:3306/techshare_db?ssl=true&serverTimezone=UTC"
gh secret set SPRING_DATASOURCE_USERNAME --body "techshare_prod_user"
gh secret set SPRING_DATASOURCE_PASSWORD --body "$(openssl rand -base64 32)"

# Email (si usas variable ENV):
gh secret set MAIL_USERNAME --body "noreply@techshare.com"
gh secret set MAIL_PASSWORD --body "app_password_de_gmail"

# Redis (si lo usas):
gh secret set REDIS_PASSWORD --body "$(openssl rand -base64 32)"

# CORS allowed origins (production):
gh secret set CORS_ALLOWED_ORIGINS --body "https://app.techshare.com,https://www.techshare.com"
```

---

## 🔒 Vault Integration (Advanced)

Para máxima seguridad, integra con GitHub Secrets Manager o HashiCorp Vault:

```yaml
# Ejemplo en workflow:
env:
  JWT_SECRET: ${{ secrets.JWT_SECRET }}
  SPRING_DATASOURCE_PASSWORD: ${{ secrets.SPRING_DATASOURCE_PASSWORD }}
```

Luego en `.env.production`:

```bash
# En server antes de deploy:
export JWT_SECRET=$(aws secretsmanager get-secret-value --secret-id jwt_secret | jq -r .SecretString)
```

---

## ✅ Checklist de Seguridad

- [ ] JWT_SECRET generado con `openssl rand -base64 32`
- [ ] Nunca commitear secretos en código
- [ ] Usar `gh secret` o GitHub UI para configurar
- [ ] Rotar secretos cada 90 días
- [ ] Usar SSH keys sin passphrase para CI/CD
- [ ] Limitar scope de tokens (mínimo requerido)
- [ ] Habilitar branch protection + required checks
- [ ] Auditar acceso a secretos en GitHub
- [ ] Encriptar secretos en transit (TLS)
- [ ] No loguear valores de secretos en logs públicos

---

## 🧪 Verificar Que CI/CD Funciona

### Paso 1: Hacer un push a dev

```bash
git add .github/workflows/ci-cd.yml .github/CI-CD-SETUP.md
git commit -m "ci: add github actions ci/cd pipeline"
git push origin dev
```

### Paso 2: Monitorear en GitHub

1. Ve a `https://github.com/RafaPacheco2003/Back-End-TechShare-Java/actions`
2. Deberías ver un nuevo workflow "TechShare Backend CI/CD" corriendo
3. Espera a que complete (5-10 minutos)
4. Verifica que:
   - ✅ Build & Test pasó (383 tests)
   - ✅ Code Quality Analysis completó
   - ✅ Docker Build & Push (si configuraste registry)

### Paso 3: Verificar Docker Image (si pusheó)

```bash
docker pull ghcr.io/RafaPacheco2003/Back-End-TechShare-Java/techshare-backend:dev
docker run -p 8080:8080 ghcr.io/RafaPacheco2003/Back-End-TechShare-Java/techshare-backend:dev
```

---

## 🚨 Troubleshooting

### "Error: permission denied to GHCR"

→ Verifica que GITHUB_TOKEN tenga `write:packages`

### "Docker build context not found"

→ Revisa que `context: Back-End-TechShare-Java` es correcto en workflow

### "JDK 17 not found"

→ Usa `actions/setup-java@v4` (versión correcta)

### Tests fallan solo en CI

→ Verifica variables ENV, conexión BD, permisos de archivo

---

Generated: 2025-12-22
