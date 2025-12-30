# Load .env (project root) and export to process env, then start JAR
$envPath = Join-Path (Get-Location).Path "..\.env"
if (-Not (Test-Path $envPath)) {
  Write-Error "Cannot find .env at $envPath"
  exit 1
}
Get-Content $envPath | ForEach-Object {
  $line = $_.Trim()
  if ($line -and -not ($line.StartsWith('#')) -and ($line -match '=')) {
    $parts = $line -split '=',2
    $name = $parts[0].Trim()
    $value = $parts[1].Trim()
    [System.Environment]::SetEnvironmentVariable($name, $value, 'Process')
  }
}
# Force profile and storage selection for this run
[System.Environment]::SetEnvironmentVariable('SPRING_PROFILES_ACTIVE','minio','Process')
[System.Environment]::SetEnvironmentVariable('APP_STORAGE_TYPE','minio','Process')
# Ensure DB points to docker-exposed port
[System.Environment]::SetEnvironmentVariable('SPRING_DATASOURCE_URL','jdbc:mysql://localhost:3307/techshare_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC','Process')

Write-Host "Starting techmate JAR with profile: $env:SPRING_PROFILES_ACTIVE"
Start-Process -FilePath 'java' -ArgumentList '-jar','target/techmate-0.0.1-SNAPSHOT.jar' -WorkingDirectory (Get-Location) -NoNewWindow -PassThru | Out-Null
Start-Sleep -Seconds 8
try {
  $resp = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -UseBasicParsing -TimeoutSec 10
  Write-Host "HEALTH: $($resp.StatusCode)"
} catch {
  Write-Host "HEALTH: FAIL - $_"
}
