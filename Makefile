COMPILER=javac
BINDIR=bin
CFLAGS=-cp netty/netty.jar:$(BINDIR) -d $(BINDIR)

default:
	@$(MAKE) $(BINDIR)/Main.class

$(BINDIR)/Main.class: $(BINDIR)/Acceptor.class Makefile
	$(COMPILER) $(CFLAGS) Main.java
# These two always need to be compiled in one go...
$(BINDIR)/Acceptor.class $(BINDIR)/AIClientHandler.class: net/Acceptor.java net/AIClientHandler.java
	$(COMPILER) $(CFLAGS) net/Acceptor.java net/AIClientHandler.java
$(BINDIR)/World.class: world/World.java
	$(COMPILER) $(CFLAGS) $<

# Some cleanup & convenience stuff
clean:
	$(RM) bin/*.class $(BINDIR)/Main.class
.PHONY: clean
