@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

set PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=true

echo [1/3] compile (no framework) ...
call mvn -q clean test-compile -DskipTests
if errorlevel 1 goto fail

echo [2/3] copy deps ...
call mvn -q dependency:copy-dependencies ^
          -DincludeScope=compile ^
          -DincludeScope=test ^
          -DoutputDirectory=target\dependency
if errorlevel 1 goto fail

set "CP=target\classes;target\test-classes"
for %%J in (target\dependency\*.jar) do (
  set "CP=!CP!;%%~fJ"
)

echo [3/3] run TestRunner %1 ...
java -ea -cp "!CP!" api.tests.TestRunner %1
goto :eof

:fail
echo BUILD FAILED
exit /b 1