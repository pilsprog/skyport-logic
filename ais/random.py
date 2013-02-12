import sys, random, threadng, socket
sys.path.append("../api/python") #We need to add an extra folder
#for python to search in if we want to use the API
import skyport

class SkyNection():
"""
Handler for the connection to the Skyport server. This is a
reactive handler and may be unsuited for those who want to create
a more dynamic AI.

@author Emil Hatlelid
"""
	def __init__():
		print("SkN> Starting up SkyNection...")
		self.receiver = skyport.SkyportReceiver()
		self.sender = skyport.SkyportTransmitter(sendToSocket)
		receiver.cb_handshake_successful = handshakeSuccessful
		self.AI = Fishy_AI()
		sender.sendHandshake(AI.NAME)
		receiver.parseLine()
	def sendToSocket(data):
		print(data)
	def handshakeSuccessful():
		print("SkN> Handshake successful!")
	#receiver.parseLine("{\"message\":\"connect\", \"status\":true}")

class Fishy_AI(threading.Thread):
"""
FISHY_AI
	This is a reactive test-AI for Skyport, written in
	Python. It walks around the map randomly, firing its
	weapons at any unsuspecting victim in its sights.
	The purpose of this AI is to demonstrate some
	possibilities of the Python API and of the game.

	@author Emil Hatlelid
"""
	def __init__(self):
		print("FAI> Initializing Fishy_AI...")
		self.NAME = "Fishy_AI"
		self.threadID = self.NAME
		threading.Thread.__init__(self)
	def run(self):
		print("FAI> Starting Fishy_AI...")
	def parseGameState():
		
	def rndMove():
		direction = random.choice(["up","down","left-down","left-up","right-down","right-up"])
		print ("AI> Moving in %s direction"%(direction)
		Server.sender.sendMove(direction)
	def isVoid(direction):
	def chooseLoadout():
		weapons = ("mortar", "laser", "droid")
		return weapons.pop(randrange(3)),weapons.pop(randrange(2))
	
