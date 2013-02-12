PLATFORM=linux

ifdef SystemRoot
   PLATFORM=windows
else
   PLATFORM=unix
endif


ifeq ($(PLATFORM),windows)
   path=$(subst /,\,$1)
else
   path=$(1)
endif

ifeq ($(PLATFORM),windows)
    RM=del $(1)
else
    RM=rm $(1)
endif
