CLASSPATH = .:./json-java.jar

JAVAC = javac
JAVA = java
JAR = jar

FILES_JAVA := $(shell find storage -type f -name '*.java')

all: all_classes

run: all
	@$(JAVA) -jar app.jar
r:
	@$(JAVA) -jar app.jar

all_classes: $(FILES_JAVA)
	@$(JAVAC) -classpath $(CLASSPATH) $(FILES_JAVA)
	@$(JAR) -cfm "app.jar" "MANIFEST.MF" storage/*.class
