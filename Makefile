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

jar:
	cd bin
	jar cfe skyport-server.jar Main *
	cp skyport-server.jar ..
	cd ..

.PHONY: json
