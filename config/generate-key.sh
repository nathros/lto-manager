read -p "Enter new key password: " PASS

keytool -genkey -alias alias -keyalg EC -keypass $PASS -keystore mykey.keystore -storepass $PASS

echo storepass=$PASS > keypass.config
echo keypass=$PASS >> keypass.config
