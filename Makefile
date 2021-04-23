CLASSPATH = .:./json-java.jar

JAVAC = javac
JAVA = java
JAR = jar

FILES_JAVA := $(shell find storage -type f -name '*.java')

all: all_classes

run: all r
r:
	@$(JAVA) -jar app.jar db.json

all_classes: $(FILES_JAVA)
	@$(JAVAC) -classpath $(CLASSPATH) $(FILES_JAVA)
	@$(JAR) -cfm "app.jar" "MANIFEST.MF" storage/*.class
