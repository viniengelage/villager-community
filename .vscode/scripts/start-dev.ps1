param()

# start-dev.ps1
# Inicia em background o gradle -t classes (watch) e o runClient com JDWP
# O script retorna somente depois que a porta 5005 estiver aberta (ou timeout)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$workspaceRoot = Resolve-Path (Join-Path $scriptDir "..\..")
$workspaceRoot = $workspaceRoot.Path

$gradlew = Join-Path $workspaceRoot 'gradlew.bat'

Write-Host "Workspace root: $workspaceRoot"

if (-not (Test-Path $gradlew)) {
  Write-Error "gradlew.bat not found at $gradlew"
  exit 1
}

Write-Host "Starting gradle continuous compile (classes)..."
Start-Process -FilePath $gradlew -ArgumentList '-t','classes' -WorkingDirectory $workspaceRoot -WindowStyle Minimized

Start-Sleep -Seconds 1

Write-Host "Starting gradle runClient with JDWP (debug)..."
$runArgs = @('runClient','--no-daemon','-Dorg.gradle.jvmargs=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005')
Start-Process -FilePath $gradlew -ArgumentList $runArgs -WorkingDirectory $workspaceRoot -WindowStyle Normal

Write-Host "Waiting for JDWP to listen on localhost:5005 (timeout 120s)..."

$timeout = 120
$elapsed = 0
$found = $false
while ($elapsed -lt $timeout) {
  try {
    $res = Test-NetConnection -ComputerName 'localhost' -Port 5005 -WarningAction SilentlyContinue
    if ($res -and $res.TcpTestSucceeded) {
      Write-Host "Port 5005 is listening. Ready to attach."
      $found = $true
      break
    }
  } catch { }
  Start-Sleep -Seconds 1
  $elapsed += 1
}

if (-not $found) {
  Write-Error "Timed out waiting for JDWP on port 5005"
  exit 2
}

exit 0
