include ../commons.mk
#include $(COMMONS)

COMPILER:=javac
BD:=../bin
CFLAGS:= -d $(call path,$(BD))

TARGET:=$(call path,$(BD)/org/json/JSONWriter.class)
SOURCE:=$(call path,JSONWriter.java)

default: $(TARGET)

$(TARGET): $(SOURCE)
	$(COMPILER) -d $(BD) *.java

.PHONY: clean
