import sys, random, threading, socket
from math import *
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
	def __init__(self, INPUT_AI):
		"""
			@param INPUT_AI:
			@rtype: OBJECT
		"""
		print("SkN> Initializing SkyNection...\nSkN> Loading AI...")
		self.AI = INPUT_AI
		print("SkN> Initializing socket")
		self.s = socket.socket()
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
		print("SkN> Starting SkyNection...")
		self.s.connect(raw_input("SkN> Port: "), raw_input("SkN> Host: "))
		sender.sendHandshake(AI.NAME)
		self.running = true
		self.commands=()
		lock = threading.Lock()
		threading.thread.start_new_thread(readFromSocket, lock)
		while self.running:
			lock.acquire()
			try:
				if self.commands.len()>0:
					receiver.parseLine(self.commands.pop(0))
			finally:
				lock.release()
		print("SkN> Closing connection")
		self.s.close()
	def readFromSocket(lock):
		while self.running:
			lock.acquire()
			try:
				self.commands.append(self.s.recv())
			finally:
				lock.release()
	def sendToSocket(data):
		print(data)
	def handshakeSuccessful():
		print("SkN> Handshake successful!")
	def gotError(e):
		print("SkN> SERVERERROR:\n", e)

class WONDERFUL_AI():
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
	intruders = ("STOP, CRIMINAL SCUM!", "OH SNAP", "Freeze, sucka", "You just made a terrible decision...","Holy mother of blueberries", "Well, excuuuuse you!", "gurl puhleeeease", "Imma get you, teletubby!", "I pity the fool who steppes in my turf", "Worst ninja ever.", "Jinkies!", "FUS-RO-DAH!", "EXTERMINATE")
	def __init__(self):
		print("AI> Initializing WONDERFUL_AI...")
		self.NAME = "WONDERFUL_AI"
		skyportRelay = SkyNection(self)
		print("AI> %, you are, and so am I... lets GO!\nAI> %s" % (raw_input("Are you ready to begin?"), random.choice(catchphrases)))
		skyportRelay.begin()
	def doSomething(i):
		i -= 1
		enemy=getClosestEnemy().json_packet["position"]
		if inRange("mortar", 1, enemy):
			print("AI> ", random.choice(intruders))
			skyportRelay.sender.attackMortar(enemy)
		elif inRange("laser", 1, enemy) or getDirection(enemy) != "NON_LINEAR":
			print("AI> ", random.choice(intruders))
			skyportRelay.sender.attackLaser(getDirection(enemy))
		elif i>=0:
			rndMove()
			doSomething(i)
	def inRange(weapon, level, target):
		rangeDict = {"mortar":(2,3,4), "laser":(5,6,7), "droid":(3,4,5)}
		return getDistance(target, self) <= rangeDict[weapon][level]
	def getClosestEnemy():
		closest = player[0]
		distance = getDistance(closest.json_packet["position"], self.location)
		for player in self.players:
			if getDistance(player.json_packet["position"], self.location) < distance:
				closest = player
		return closest
	def getDistance(pos1, pos2):
		return sqrt((pos2[0] - pos1[0])**2 + (pos2[1] - pos1[1])**2)
	def parseGameState(turn, map, players):
		self.map = map
		self.players = players
		if players.pop(0).json_packet["name"]==self.NAME:
			doSomething(3)
	def parseGameStart(turn, map, players):
		self.map = map
		self.players = players
		skyportRelay.sender.sendLoadout("laser", "mortar")
	def parseAction(actionType, who, restData):
		pass
	def parseEndturn():
		pass
	def rndMove():
		skyportRelay.sender.sendMove(random.choice(["up","down","left-down","left-up","right-down","right-up"]))
	def getDirection(j,k):
		if j == self.location[0]:
			if k>self.location[1]: return "right-down"
			else:
				return "left-up"
		elif k==self.location[1]:
			if j>self.location[0]:
				return "left-down"
			else:
				return "right-up"
		elif((j-self.location[0])==(k-self.location[1])):
			if j>self.location[0]:
				return "down"
			else:
				return "up"
		else:
			return "NON_LINEAR"
	def isVoid(j,k):
		return "V"==self.map.json_packet["data"][j][k]
	def isRock(j,k):
		return "O"==self.map.json_packet["data"][j][k]
