APK_PATH=$1
rm app-debug.apk
cp $APK_PATH ./app-debug.apk
unzip -j app-debug.apk classes.dex -d ./appDebugDex/
java -jar baksmali-2.2.0.jar disassemble ./appDebugDex/classes.dex -o appDebug
rm -rf out/com/bryan
mv appDebug/com/bryan out/com