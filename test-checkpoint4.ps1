#!/usr/bin/env powershell

Write-Host "Setting up environment..."
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "Checking Docker status..."
$dockerStatus = docker ps 2>&1
Write-Host $dockerStatus

Write-Host "Building project..."
Set-Location "C:\Users\Konrad\projects\edfms\backend"
mvn clean install -q -DskipTests
Write-Host "Build completed!"

Write-Host "Starting reservation-service on port 8081..."
Set-Location services\reservation-service
Start-Process -FilePath "powershell" -ArgumentList "-Command", "mvn spring-boot:run -Dspring-boot.run.profiles=local" -WindowStyle Minimized

Write-Host "Waiting for service to start..."
Start-Sleep -Seconds 10

Write-Host "Testing endpoint /api/test..."
try {
    $result = Invoke-WebRequest -Uri "http://localhost:8081/api/test" -UseBasicParsing
    Write-Host "Response: $($result.Content)"
    Write-Host "Status Code: $($result.StatusCode)"
} catch {
    Write-Host "ERROR: $($_)"
}

