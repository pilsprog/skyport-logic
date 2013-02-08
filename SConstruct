env = Environment()
env.Append(JAVACFLAGS="-cp bin:bin/json -Xlint:all")

JSON = Environment().Java("bin/json", "json")
Exceptions = env.Java("bin", "exceptions")
Libs = env.Java("bin", "libs")
Main = env.Java("bin", "Main.java")

Depends(Libs, Exceptions)
Depends(Libs, JSON)
Depends(Main, Libs)

Default(Main)
Clean(JSON, "bin/json/org/json/JSONObject$1.class")
