#!/usr/bin/env python
import os, sys, socket, re
from math import *
	
def SkyportWorld(world):
"""
@param world:
@rtype:
@return:
"""
	
s = socket.socket()
s.connect(raw_input("port: "), raw_input("host: "))
#port = raw_input("port: ")
#host = raw_input("host: ")
#s.bind (port, host)

AI = SmellsFishy_AI()

s.close()