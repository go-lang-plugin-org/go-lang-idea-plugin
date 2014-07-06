rm -rf bin/proguard
java -Xms528m -Xmx592m -jar lib/proguard.jar @intellij-go.pro 
java -Xms528m -Xmx592m -jar lib/proguard.jar @jps-plugin.pro 
cd bin/proguard
mkdir -p Go/lib/jps
cp intellij-go.jar Go/lib/
cp jps-plugin.jar Go/lib/jps/
zip Go.zip -r Go
rm -rf Go intellij-go.jar jps-plugin.jar