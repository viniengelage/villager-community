@echo off
REM Script para definir JAVA_HOME e executar Gradle
REM Usando Java 8 temporariamente - instale Java 21 para produção
set JAVA_HOME=C:\Java\jdk1.8.0_392
echo JAVA_HOME definido como: %JAVA_HOME%
gradlew.bat %*