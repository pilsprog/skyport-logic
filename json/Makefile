include ../commons.mk
BINDIR:=../bin/
TARGET:=$(BINDIR)/org/json/JSONWriter.class
SOURCE:=JSONWriter.java

default: $(TARGET)

$(TARGET): $(SOURCE)
	@$(JC) $(JAVAFLAGS)

.PHONY: clean $(TARGET)
