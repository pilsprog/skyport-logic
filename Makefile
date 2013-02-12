include commons.mk
BINDIR=bin
JSONDIR=json

default: json

json:
	$(MAKE) -C $(JSONDIR) COMMONS=$(call path,../commons.mk)
	exit


