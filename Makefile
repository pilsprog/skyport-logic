PLATFORM := linux
COMPILER=javac
INTERPRETER=java

ifeq ($(PLATFORM),linux)

CFLAGS=-cp ../lwjgl/jar/lwjgl.jar:../PNGDecoder/PNGDecoder.jar:.
RUNFLAGS=-cp ../lwjgl/jar/lwjgl.jar:../PNGDecoder/PNGDecoder.jar:.
LD_LIBRARY_PATH=../lwjgl/native/linux/
DELETE=rm

endif
ifeq ($(PLATFORM),windows)

CFLAGS=-cp ..\lwjgl\jar\lwjgl.jar;..\PNGDecoder\PNGDecoder.jar;.
RUNFLAGS=-cp ..\lwjgl\jar\lwjgl.jar;..\PNGDecoder\PNGDecoder.jar;.
LD_LIBRARY_PATH=..\lwjgl\native\windows\
DELETE=erase

endif

default: Main.class Makefile
run: default
	@LD_LIBRARY_PATH=$(LD_LIBRARY_PATH) $(INTERPRETER) $(RUNFLAGS) Main

clean:
	$(DELETE) *.class

Main.class: World.class Engine.class Main.java Makefile
	$(COMPILER) $(CFLAGS) Main.java

World.class: World.java
	$(COMPILER) $(CFLAGS) World.java

Engine.class: World.class Graphics.class Conf.class Engine.java
	$(COMPILER) $(CFLAGS) Engine.java

Graphics.class: Texture.class Graphics.java
	$(COMPILER) $(CFLAGS) Graphics.java

Conf.class: Conf.java
	$(COMPILER) $(CFLAGS) Conf.java

Texture.class: Texture.java
	$(COMPILER) $(CFLAGS) Texture.java

Tile.class: Texture.class Tile.java
	$(COMPILER) $(CFLAGS) Tile.java
