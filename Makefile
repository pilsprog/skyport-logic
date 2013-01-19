COMPILER=javac
INTERPRETER=java
BINDIR=bin

CFLAGS=-d $(BINDIR)
RUNFLAGS=-cp bin

SOURCES=$(wildcard world/*.java logic/*.java net/*.java)
CLASSFILES=$(patsubst %.java, $(BINDIR)/%.class, $(notdir $(SOURCES)))

default: $(CLASSFILES)
	@$(MAKE) $(BINDIR)/Main.class

$(BINDIR)/Main.class: Makefile
	$(COMPILER) $(CFLAGS) Main.java

$(BINDIR)/%.class: logic/%.java
	$(COMPILER) $(CFLAGS) $<
$(BINDIR)/%.class: world/%.java
	$(COMPILER) $(CFLAGS) $<
$(BINDIR)/%.class: net/%.java
	$(COMPILER) $(CFLAGS) $< 

# Some cleanup & convenience stuff
run:
	$(INTERPRETER) $(RUNFLAGS) Main
clean:
	$(RM) $(CLASSFILES) $(BINDIR)/Main.class
.PHONY: run
.PHONY: clean
