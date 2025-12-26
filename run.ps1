# =====================================================
# ECONEXION BACKEND - Script de Ejecución (Windows)
# Servicio de lógica de negocio que conecta a Supabase
# =====================================================

Write-Host "🚀 Iniciando Econexion Backend Service..." -ForegroundColor Green
Write-Host ""

# =====================================================
# VARIABLES DE ENTORNO - SUPABASE
# =====================================================

# Password de Supabase (obtén esto en Supabase Dashboard > Project Settings > Database)
$env:SUPABASE_PASSWORD = "TU_PASSWORD_DE_SUPABASE_AQUI"

# URL de conexión a Supabase
$env:SUPABASE_URL = "jdbc:postgresql://db.kmkrcsoznbqzfzjhrrvc.supabase.co:5432/postgres"
$env:SUPABASE_USERNAME = "postgres"

# =====================================================
# VARIABLES DE ENTORNO - JWT Y SEGURIDAD
# =====================================================

$env:JWT_SECRET = "en_produccion_usa_un_secret_mucho_mas_largo_y_seguro_123456"

# =====================================================
# VARIABLES DE ENTORNO - GOOGLE OAUTH (Opcional)
# =====================================================

$env:GOOGLE_CLIENT_ID = "32986221627-1qg0bor77ng5mf5e1kp584t4k5epbe03.apps.googleusercontent.com"
$env:GOOGLE_CLIENT_SECRET = "TU_GOOGLE_CLIENT_SECRET_AQUI"
$env:GOOGLE_ANDROID_CLIENT_ID = "32986221627-59jc0c9vja9cl7i4150r3ufud38d09pb.apps.googleusercontent.com"

# =====================================================
# CONFIGURACIÓN DE SPRING BOOT
# =====================================================

# Profile a usar: dev, prod, default
$PROFILE = "dev"

# Puerto del servidor (debe coincidir con application.yml)
$env:SERVER_PORT = "35002"

# =====================================================
# VERIFICACIÓN PREVIA
# =====================================================

Write-Host "📋 Configuración:" -ForegroundColor Cyan
Write-Host "  - Profile: $PROFILE"
Write-Host "  - Puerto: $env:SERVER_PORT"
Write-Host "  - Base de datos: Supabase (PostgreSQL)"
Write-Host "  - URL: $env:SUPABASE_URL"
Write-Host ""

# Verificar que Maven está instalado
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "❌ ERROR: Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "   Descarga Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Verificar que existe el pom.xml
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ ERROR: No se encuentra pom.xml en el directorio actual" -ForegroundColor Red
    Write-Host "   Asegúrate de ejecutar este script desde la raíz del proyecto" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Verificaciones completadas" -ForegroundColor Green
Write-Host ""

# =====================================================
# COMPILAR Y EJECUTAR
# =====================================================

Write-Host "🔨 Compilando proyecto..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al compilar el proyecto" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "▶️  Iniciando aplicación con profile: $PROFILE" -ForegroundColor Green
Write-Host "🌐 La API estará disponible en: http://localhost:$env:SERVER_PORT" -ForegroundColor Cyan
Write-Host "📊 Health check: http://localhost:$env:SERVER_PORT/actuator/health" -ForegroundColor Cyan
Write-Host ""
Write-Host "💡 Presiona Ctrl+C para detener el servidor" -ForegroundColor Yellow
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor DarkGray
Write-Host ""

# Ejecutar Spring Boot
mvn spring-boot:run -D"spring-boot.run.profiles=$PROFILE"   