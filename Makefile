include commons.mk
BINDIR=bin
JSONDIR=json

default:
	echo $(call path,bin/foo/bar)

#@$(MAKE) $(BINDIR)

#json:
#	#@$(MAKE) $(JSONDIR)

#clean:
#	#@$(MAKE) $(BINDIR) clean
#	#@$(MAKE) $(BINDIR) clean

#.PHONY: clean
