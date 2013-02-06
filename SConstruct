env = Environment()
env.Append(JAVACFLAGS="-cp bin:bin/json")

JSON = env.Java("bin/json", "json")
Tile = env.Java("bin", "logic/Tile.java")
WorldParser = env.Java("bin", "world/WorldParser.java")
AIConnection = env.Java("bin", "net/AIConnection.java")
Acceptor = env.Java("bin", "net/Acceptor.java")
GameThread = env.Java("bin", "logic/GameThread.java")
Main = env.Java("bin", "Main.java")

# Necessary requirements
Depends(Main, JSON)
Depends(Main, WorldParser)
Depends(Main, AIConnection)
Depends(Main, Acceptor)
Depends(Main, GameThread)
Depends(WorldParser, Tile)

Default(Main)
