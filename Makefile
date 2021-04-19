CLASSPATH = .:./json-java.jar:./app.jar

JAVAC = javac
JAVA = java
JAR = jar

FILES_JAVA := $(shell find storage -type f -name '*.java')

all: all_classes

run: all
	$(JAVA) -classpath $(CLASSPATH) storage/Main

all_classes: $(FILES_JAVA)
	$(JAVAC) -classpath $(CLASSPATH) storage/*.java
