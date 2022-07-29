@echo off
echo Enter new key password: 
set /p PASS=

keytool -genkey -alias alias -keyalg EC -keypass %PASS% -keystore mykey.keystore -storepass %PASS%

echo storepass=$PASS > keypass.config
echo keypass=$PASS >> keypass.config