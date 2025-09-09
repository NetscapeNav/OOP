rm -rf build
mkdir -p build/classes
mkdir -p build/docs
javac -d build/classes src/main/java/org/example/*.java
javadoc -d build/docs -sourcepath src/main/java org.example
jar --create --file=build/heapsort.jar --main-class=org.example.Main -C build/classes .
java -jar build/heapsort.jar
