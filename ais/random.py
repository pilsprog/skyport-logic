"""
FISHY_AI
	This is a test-AI for Skyport, written in Python. It
	walks around the map randomly, firing its weapons at
	any unsuspecting victim in its sights.
	The purpose of this AI is to demonstrate some
	possibilities of the Python API

	Written by Emil Hatlelid
"""

import sys, random, thread
sys.path.append("../api/python")
import skyport

NAME = "Fishy_AI"

class SkyNection():
	def 
	def sendToSocket(data):
		print(data)
	def handshakeSuccessful():
		print("handshake successful!")

	receiver = skyport.SkyportReceiver()
	receiver.cb_handshake_successful = handshakeSuccessful
	sender = skyport.SkyportTransmitter(sendToSocket)
	receiver.parseLine("{\"message\":\"connect\", \"status\":true}")
	sender.sendHandshake("lol")
