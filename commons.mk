ifdef SystemRoot
   PLATFORM=windows
else
   PLATFORM=unix
endif


ifeq ($(PLATFORM),windows)
    path=$(subst :,;,$1)
else
    path=$(1)
endif

JC:=javac
JAVAFLAGS=-g -cp $(call path,"$(BINDIR):$(BINDIR)/org/") -d $(BINDIR) -Xlint:all

