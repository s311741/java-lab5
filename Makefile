CLASSPATH := .:./postgresql-42.2.21.jar:./json-java.jar

JAVAC := javac
JAVA := java
JAR := jar

PORT := 13666
HOST := pg
DBNAME := studs

FILES_CLASS_COMMON := $(wildcard "storage/*.class" "storage/cmd/*.class")
FILES_CLASS_CLIENT := $(FILES_CLASS_COMMON) $(wildcard "storage/client/*.class")
FILES_CLASS_SERVER := $(FILES_CLASS_COMMON) $(wildcard "storage/server/*.class")
FILES_JAVA_ALL := $(shell find storage -type f -name '*.java')

all: client.jar server.jar

server:
	@echo "Running server.jar"
	$(JAVA) -jar server.jar $(PORT) $(HOST) $(DBNAME)

client:
	@echo "Running client.jar"
	@$(JAVA) -jar client.jar localhost:$(PORT)

all_classes: $(FILES_JAVA_ALL)
	@echo "Compiling java files"
	@$(JAVAC) -classpath $(CLASSPATH) $(FILES_JAVA_ALL)

client.jar: all_classes
	@echo "Building $@"
	@$(JAR) -cfm $@ "MANIFEST_CLIENT.MF" $(FILES_CLASS_CLIENT)
server.jar: all_classes
	@echo "Building $@"
	@$(JAR) -cfm $@ "MANIFEST_SERVER.MF" $(FILES_CLASS_SERVER)