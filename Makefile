COMPILER=javac
BINDIR=bin
PLATFORM:=linux
CFLAGS=-cp $(BINDIR) -d $(BINDIR) -Xlint:all 

default:
	@$(MAKE) $(BINDIR)/Main.class
$(BINDIR)/Main.class: $(BINDIR)/Acceptor.class $(BINDIR)/GameThread.class Main.java Makefile
	$(COMPILER) $(CFLAGS) Main.java
$(BINDIR)/Acceptor.class: $(BINDIR)/AIConnection.class $(BINDIR)/AIClientHandler.class net/Acceptor.java
	$(COMPILER) $(CFLAGS) net/Acceptor.java
$(BINDIR)/AIClientHandler.class: net/AIClientHandler.java
	$(COMPILER) $(CFLAGS) net/AIClientHandler.java
$(BINDIR)/AIConnection.class: net/AIConnection.java
	$(COMPILER) $(CFLAGS) net/AIConnection.java
$(BINDIR)/GameThread.class: logic/GameThread.java
	$(COMPILER) $(CFLAGS) logic/GameThread.java

# Some cleanup & convenience stuff
clean:
	$(RM) bin/*.class $(BINDIR)/Main.class
.PHONY: clean
