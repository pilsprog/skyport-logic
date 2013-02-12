include commons.mk
BINDIR=bin
JSONDIR=json

default: json



#@$(MAKE) $(BINDIR)

json:
	$(MAKE) -C $(JSONDIR) COMMONS=$(call path,../commons.mk)
	exit

#clean:
#	#@$(MAKE) $(BINDIR) clean
#	#@$(MAKE) $(BINDIR) clean

.PHONY: json
