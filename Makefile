COMPILER=javac
BD=bin
CFLAGS= -cp "$(BD):$(BD)/json/" -d $(BD) -Xlint:all 

default:
	@$(MAKE) $(BD)/Main.class
$(BD)/Main.class: $(BD)/Acceptor.class $(BD)/GameThread.class $(BD)/WorldParser.class Main.java Makefile
	$(COMPILER) $(CFLAGS) Main.java

## Networking
$(BD)/Acceptor.class: $(BD)/AIConnection.class $(BD)/AIClientHandler.class $(BD)/GraphicsClientHandler.class $(BD)/GraphicsConnection.class net/Acceptor.java
	$(COMPILER) $(CFLAGS) net/Acceptor.java
$(BD)/AIClientHandler.class: json net/AIClientHandler.java
	$(COMPILER) $(CFLAGS) net/AIClientHandler.java
$(BD)/AIConnection.class: $(BD)/StatefulProtocolDecoder.class net/AIConnection.java
	$(COMPILER) $(CFLAGS) net/AIConnection.java
$(BD)/GraphicsClientHandler.class: $(BD)/GraphicsConnection.class json net/GraphicsClientHandler.java
	$(COMPILER) $(CFLAGS) net/GraphicsClientHandler.java
$(BD)/GraphicsConnection.class: $(BD)/StatefulProtocolDecoder.class net/GraphicsConnection.java
	$(COMPILER) $(CFLAGS) net/GraphicsConnection.java
$(BD)/StatefulProtocolDecoder.class: json $(BD)/ProtocolException.class net/StatefulProtocolDecoder.java
	$(COMPILER) $(CFLAGS) net/StatefulProtocolDecoder.java
$(BD)/ProtocolException.class: net/ProtocolException.java
	$(COMPILER) $(CFLAGS) net/ProtocolException.java
$(BD)/WorldParser.class: world/WorldParser.java
	$(COMPILER) $(CFLAGS) world/WorldParser.java

## Logic
$(BD)/GameThread.class: $(BD)/GameState.class logic/GameThread.java
	$(COMPILER) $(CFLAGS) logic/GameThread.java
$(BD)/GameState.class: $(BD)/Player.class $(BD)/Action.class logic/GameState.java
	$(COMPILER) $(CFLAGS) logic/GameState.java
$(BD)/Player.class: logic/Player.java
	$(COMPILER) $(CFLAGS) logic/Player.java
$(BD)/Action.class: logic/Action.java
	$(COMPILER) $(CFLAGS) logic/Action.java
## World

## JSON
json $(BD)/json/JSONWriter.class: json/JSONWriter.java
	$(COMPILER) -d $(BD)/json json/*.java

# Some cleanup & convenience stuff
clean:
	$(RM) bin/*.class
	$(RM) -r bin/json/org

jar:
	jar -c -e Main 

.PHONY: clean
