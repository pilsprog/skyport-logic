import os
import glob
import re

env = Environment()
env.Replace(JAVAVERSION = env['javaver'])

# Workaround for windows
newpath = os.environ.get('PATH')
env.Append(ENV = { 'PATH' : newpath })

env.Append(JAVACFLAGS="-cp bin:bin/json -Xlint:all")

JSON = Environment().Java("bin/json", "json")
Libs = env.Java("bin", "libs")
Main = env.Java("bin", "Main.java")

Depends(Libs, JSON)
Depends(Main, Libs)

Default(Main)
Clean(JSON, "bin/json/org/json/JSONObject$1.class")
