import sys, random, threadng, socket
sys.path.append("../api/python") #We need to add an extra folder
#for python to search in if we want to use the API
import skyport

class SkyNection():
"""
	Handler for the connection to the Skyport server.
	Usable as API for VERY inexperienced users.
	This is a reactive handler and may be unsuited for those
	who want to create a more complex and/or dynamic AI.
	
	Requires skyport python API to run.

	@author Emil Hatlelid
"""
	AI = None
	def __init__(self, INPUT_AI):
		print("SkN> Initializing SkyNection...")
		print("SkN> Loading AI...")
		self.AI = INPUT_AI
		print("SkN> Binding receiver and transmitter functions...")
		self.receiver = skyport.SkyportReceiver()
		self.sender = skyport.SkyportTransmitter(sendToSocket)
		receiver.cb_handshake_successful = handshakeSuccessful
		receiver.cb_error = gotError
		receiver.cb_gamestate = AI.parseGameState
		receiver.cb_gamestart = AI.parseGameStart
		receiver.cb_action = AI.parseAction
		receiver.cb_endturn = AI.parseEndturn
	def begin(self):
		print("Starting SkyNection...")
		sender.sendHandshake(AI.NAME)
		running = true
		while(running):
			receiver.parseLine()
	def sendToSocket(data):
		print(data)
	def handshakeSuccessful():
		print("SkN> Handshake successful!")
	def gotError(e):
		print("SkN> SERVERERROR:\n", e)
	#receiver.parseLine("{\"message\":\"connect\", \"status\":true}")

class WONDERFUL_AI(threading.Thread):
"""
	WONDERFUL_AI
	This is a reactive test-AI for Skyport, written in
	Python. It walks around the map randomly, firing its
	weapons at any unsuspecting victim in its sights.
	The purpose of this AI is to demonstrate some
	possibilities of the Python API and of the game.

	@author Emil Hatlelid
"""
	skyportRelay, NAME, map, players = None
	location = (0,0)
	catchphrases = ("Its time to kick ass and chew bubblegum... and Im all outta gum", "Better not take another arrow to my knee...", "Wonderful.")
	intruders = ("STOP, CRIMINAL SCUM!", "OH SNAP", "Freeze, sucka", "You just made a terrible decision...","Holy mother of blueberries", "Well, excuuuuse you!", "girl puhleeeeaseh", "Imma get you, teletubby!", "I pity the fool who stepped in my turf", "Worst ninja ever.", "Jinkies!", "FUS-RO-DAH!"i, "EXTERMINATE")
	def __init__(self):
		print("AI> Initializing WONDERFUL_AI...")
		self.NAME = "WONDERFUL_AI"
		skyportRelay = SkyNection(self)
		print("AI> %s, you are, and so am I... lets GO!" % (raw_input("Are you ready to begin?")))
		print("AI> ", random.choice(catchphrases))
		skyportRelay.begin()
	def doSomething():
		enemy=getEnemy()
		
	def getEnemy():
		closest = player[0]
		for each player in self.players:
			#if(player.json_packet["position"]
	def parseGameState(turn, map, players):
		self.map = map
		self.players = players
		if players.pop(0).json_packet["name"]==self.NAME:
			doSomething()
	def parseGameStart(turn, map, players):
		self.map = map
		self.players = players
		skyportRelay.sender.sendLoadout(chooseLoadout())
	def parseAction(actionType, who, restData):
		pass
	def parseEndturn():
		pass
	def rndMove():
		direction = random.choice(["up","down","left-down","left-up","right-down","right-up"])
		print ("AI> Moving in %s direction"%(direction)
		skyportRelay.sender.sendMove(direction)
	def getDirection(j,k):
		"""
		Doesn't work too good with non-linear coordinates.
		"""
		if(j==self.location[0]):
			if(k>self.location[1]) return "right-down"
			else return "left-up"
		else if(k==self.location[1]):
			if(j>self.location[0]) return "left-down"
			else return "right-up"
		else if((j-self.location[0])==(k-self.location[1])):
			if(j>self.location[0]) return "down"
			else return "up"
		else return "NON_LINEAR"
	def isVoid(j,k):
		return "V"==self.map.json_packet["data"][j][k]
	def isRock(j,k):
		return "O"==self.map.json_packet["data"][j][k]
	def chooseLoadout():
		weapons = ("mortar", "laser", "droid")
		return weapons.pop(randrange(len(weapons))), weapons.pop(randrange(len(weapons)))
