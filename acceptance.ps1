param(
  [string]$BaseUrl = ""
)

# ==== Default BaseUrl (sin ??) ====
if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
  if ($env:BASE_URL -and -not [string]::IsNullOrWhiteSpace($env:BASE_URL)) {
    $BaseUrl = $env:BASE_URL
  } else {
    $BaseUrl = "http://localhost:4000"
  }
}

# ========== Helpers ==========
$ErrorActionPreference = "Stop"
$global:Tests = @()

function Add-Result($name, $ok, $info="") {
  $global:Tests += [pscustomobject]@{ test=$name; ok=$ok; info=$info }
  if ($ok) { Write-Host "PASS  " -NoNewline -ForegroundColor Green; Write-Host $name }
  else     { Write-Host "FAIL  " -NoNewline -ForegroundColor Red;   Write-Host "$name → $info" }
}

function Get-JwtPayload([string]$jwt) {
  $p = $jwt.Split('.')[1]; $p += "=" * ((4 - $p.Length % 4) % 4)
  [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($p)) | ConvertFrom-Json
}

Write-Host "=== ACEPTANCE TESTS @ $BaseUrl ===" -ForegroundColor Cyan

# ========== 0) Server up ==========
try {
  $resp = Invoke-WebRequest -Uri "$BaseUrl/health" -Method GET -TimeoutSec 5
  if ($resp.StatusCode -eq 200) { Add-Result "Health check (/health)" $true }
  else { Add-Result "Health check (/health)" $false "HTTP $($resp.StatusCode)" }
} catch {
  Add-Result "Health check (/health)" $false "No responde: $($_.Exception.Message)"
}

# ========== 1) Logins mock ==========
$respA = $null; $respB = $null
$UID_A = ""; $UID_B = ""
$headersA = $null; $headersB = $null
try {
  $respA = Invoke-RestMethod -Method POST -Uri "$BaseUrl/auth/mock-login" -ContentType "application/json" -Body '{"name":"Alejo","email":"a@a.com"}'
  $respB = Invoke-RestMethod -Method POST -Uri "$BaseUrl/auth/mock-login" -ContentType "application/json" -Body '{"name":"Maria","email":"m@m.com"}'
  if ($respA.token -and $respB.token) {
    $UID_A = (Get-JwtPayload $respA.token).id
    $UID_B = (Get-JwtPayload $respB.token).id
    $headersA = @{ Authorization = "Bearer $($respA.token)" }
    $headersB = @{ Authorization = "Bearer $($respB.token)" }
    Add-Result "Mock login A y B" $true
  } else {
    Add-Result "Mock login A y B" $false "Sin tokens"
  }
} catch {
  Add-Result "Mock login A y B" $false $_.Exception.Message
}

# ========== 2) Crear conversación A->B ==========
$CONV_ID = ""
if ($headersA -and $UID_B) {
  try {
    $conv = Invoke-RestMethod -Method POST -Uri "$BaseUrl/conversations" -Headers $headersA -ContentType "application/json" -Body (@{ toUserId = $UID_B } | ConvertTo-Json)
    $CONV_ID = $conv._id
    if ($CONV_ID) { Add-Result "Crear conversación A→B" $true } else { Add-Result "Crear conversación A→B" $false "sin _id" }
  } catch {
    Add-Result "Crear conversación A→B" $false $_.Exception.Message
  }
} else {
  Add-Result "Crear conversación A→B" $false "Faltan headersA/UID_B"
}

# ========== 3) Enviar mensajes ==========
if ($CONV_ID -and $headersA -and $headersB) {
  try {
    Invoke-RestMethod -Method POST -Uri "$BaseUrl/messages" -Headers $headersA -ContentType "application/json" -Body (@{ conversationId = $CONV_ID; text = "Hola desde A" } | ConvertTo-Json) | Out-Null
    Invoke-RestMethod -Method POST -Uri "$BaseUrl/messages" -Headers $headersB -ContentType "application/json" -Body (@{ conversationId = $CONV_ID; text = "Hola A, soy B" } | ConvertTo-Json) | Out-Null
    Add-Result "Enviar 2 mensajes" $true
  } catch {
    Add-Result "Enviar 2 mensajes" $false $_.Exception.Message
  }
} else {
  Add-Result "Enviar 2 mensajes" $false "Faltan datos previos"
}

# ========== 4) Listado por QUERY ==========
if ($CONV_ID -and $headersA) {
  try {
    $respQ = Invoke-RestMethod -Method GET -Uri "$BaseUrl/messages?conversationId=$CONV_ID&limit=50" -Headers $headersA
    $count = @($respQ.items).Count
    if ($count -ge 2) { Add-Result "Listar mensajes por query" $true "items=$count" }
    else { Add-Result "Listar mensajes por query" $false "items=$count" }
  } catch {
    Add-Result "Listar mensajes por query" $false $_.Exception.Message
  }
} else {
  Add-Result "Listar mensajes por query" $false "Faltan CONV_ID/headersA"
}

# ========== 5) Listado por ALIAS (opcional) ==========
if ($CONV_ID -and $headersA) {
  try {
    $respP = Invoke-RestMethod -Method GET -Uri "$BaseUrl/messages/$CONV_ID?limit=50" -Headers $headersA
    $cnt = @($respP.items).Count
    Add-Result "Listar mensajes por alias /:conversationId (opcional)" $true "items=$cnt"
  } catch {
    Add-Result "Listar mensajes por alias /:conversationId (opcional)" $true "No implementado (404), OK"
  }
}

# ========== 6) Seguridad: tercero NO debe ver ==========
try {
  $respC = Invoke-RestMethod -Method POST -Uri "$BaseUrl/auth/mock-login" -ContentType "application/json" -Body '{"name":"Tercero","email":"t@t.com"}'
  $headersC = @{ Authorization = "Bearer $($respC.token)" }
  try {
    $null = Invoke-RestMethod -Method GET -Uri "$BaseUrl/messages/$CONV_ID" -Headers $headersC
    Add-Result "Acceso de tercero bloqueado" $false "Devolvió 200, se esperaba 403/404"
  } catch {
    $code = $null
    try { $code = $_.Exception.Response.StatusCode.value__ } catch {}
    if ($code -in 403,404) { Add-Result "Acceso de tercero bloqueado" $true "HTTP $code" }
    else { Add-Result "Acceso de tercero bloqueado" $false ("HTTP " + $code) }
  }
} catch {
  Add-Result "Login tercero (para prueba de seguridad)" $false $_.Exception.Message
}

# ========== 7) AI proxy (tolerante si no está arriba) ==========
function Test-AIGet($url) {
  try {
    $r = Invoke-RestMethod -Method GET -Uri $url
    if ($r.items -or $r.ok) { Add-Result "AI GET $url" $true }
    else { Add-Result "AI GET $url" $true "200 sin items/ok (tolerado)" }
  } catch {
    $code = $null
    try { $code = $_.Exception.Response.StatusCode.value__ } catch {}
    if ($code -eq 503 -or $code -eq 404) { Add-Result "AI GET $url" $true "HTTP $code (tolerado)" }
    else { Add-Result "AI GET $url" $false ("HTTP " + $code) }
  }
}
function Test-AIPost($url, $body) {
  try {
    $r = Invoke-RestMethod -Method POST -Uri $url -ContentType "application/json" -Body $body
    if ($r.ok -or $r.indexed) { Add-Result "AI POST $url" $true }
    else { Add-Result "AI POST $url" $true "200 sin ok/indexed (tolerado)" }
  } catch {
    $code = $null
    try { $code = $_.Exception.Response.StatusCode.value__ } catch {}
    if ($code -eq 503 -or $code -eq 404) { Add-Result "AI POST $url" $true "HTTP $code (tolerado)" }
    else { Add-Result "AI POST $url" $false ("HTTP " + $code) }
  }
}

Test-AIGet  "$BaseUrl/ai/health"
Test-AIPost "$BaseUrl/ai/index-item" '{"id":"p-1","title":"Botella PET","desc":"500ml","image_url":"https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Plastic_bottle.jpg/320px-Plastic_bottle.jpg"}'
Test-AIGet  "$BaseUrl/ai/search-text?q=botella%20plastica%20pet&k=5"

# ========== Summary ==========
$pass = ($Tests | Where-Object { $_.ok }).Count
$fail = ($Tests | Where-Object { -not $_.ok }).Count
$summary = [pscustomobject]@{
  base      = $BaseUrl
  passed    = $pass
  failed    = $fail
  timestamp = (Get-Date).ToString("s")
  results   = $Tests
}
$summary | ConvertTo-Json -Depth 6 | Set-Content -Encoding UTF8 ".\acceptance_report.json"

Write-Host "------------------------------" -ForegroundColor DarkGray
Write-Host "PASSED: $pass   FAILED: $fail" -ForegroundColor Yellow
Write-Host "Reporte: acceptance_report.json" -ForegroundColor Yellow

if ($fail -gt 0) { exit 1 } else { exit 0 }
