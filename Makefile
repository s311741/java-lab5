CLASSPATH = .
JAVAC = javac
JAVA = java

PACKAGE = storage

FILES_JAVA := $(shell find . -type f -name '*.java')
FILES_CLASS := $(FILES_JAVA:%.java=%.class)

all: $(FILES_CLASS)

run: all
	$(JAVA) -classpath $(CLASSPATH) $(PACKAGE)/Main

%.class: %.java
	$(JAVAC) -classpath $(CLASSPATH) $<

