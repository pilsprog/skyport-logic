include commons.mk
BINDIR=bin
JSONDIR=json
LIBDIR=libs

default: json

json:
	$(MAKE) -C $(JSONDIR)

lib:
	$(MAKE) -C $(LIBDIR)

main:
	$(JC) $(JAVAFLAGS) Main.java

clean:
	rm -rf bin/*
	rm skyport-server.jar

.PHONY: json
