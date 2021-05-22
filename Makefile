CLASSPATH = .:./json-java.jar

JAVAC = javac
JAVA = java
JAR = jar

FILES_CLASS_COMMON := $(wildcard "storage/*.class" "storage/cmd/*.class")
FILES_CLASS_CLIENT := $(FILES_CLASS_COMMON) $(wildcard "storage/client/*.class")
FILES_CLASS_SERVER := $(FILES_CLASS_COMMON) $(wildcard "storage/server/*.class")
FILES_JAVA_ALL := $(shell find storage -type f -name '*.java')

all: client.jar server.jar

server: server.jar
	@echo "Running $^"
	@$(JAVA) -jar $^ 13666
client: client.jar
	@echo "Running $^"
	@$(JAVA) -jar $^ localhost:13666

all_classes: $(FILES_JAVA_ALL)
	@echo "Compiling java files"
	@$(JAVAC) -classpath $(CLASSPATH) $(FILES_JAVA_ALL)

client.jar: all_classes
	@echo "Building $@"
	@$(JAR) -cfm $@ "MANIFEST_CLIENT.MF" $(FILES_CLASS_CLIENT)
server.jar: all_classes
	@echo "Building $@"
	@$(JAR) -cfm $@ "MANIFEST_SERVER.MF" $(FILES_CLASS_SERVER)