#!/usr/bin/env python2

################## randomwalker.py ####################
# This is a test-AI for skyport, written in python    #
# & twisted. It randomly walks around on the board.   #
# This AI mainly demonstrates the use of the skyport  #
# python API.                                         #
# Note that the API is "framework-agnostic", so there #
# is no need to actually use twisted -- you can just  #
# use threads and blocking i/o if you want to. All    #
# you need to provide the skyport API with is a       #
# function it can use to send data to the socket,     #
# and you need to call receiver.parse_line() whenever #
# you receive data on the socket.                     #
#                                                     #
# Written by Jonathan Ringstad                        #
#######################################################

import sys
import json
import random

from twisted.internet import reactor
from twisted.internet.protocol import ClientFactory
from twisted.protocols.basic import LineReceiver

sys.path.append("../api/python") # add the folder with the API to pythons path
import skyport # the API.

# The name for our "AI"
NAME = "randomwalker"

class SkyportConnection(LineReceiver): # twisted-specific things
    receiver = None     # the SkyportReceiver object
    transmitter = None  # the SkyportTransmitter object
    delimiter = "\n"    # need to set this so that twisted parses the line endings correctly

    def connectionMade(self): # twisted has successfully established the connection for us
        # instantiate the receiver
        self.receiver = skyport.SkyportReceiver()
        # Set up callbacks for the receiver
        # self.gotHandshake is called when the handshake is completed successfully
        self.receiver.cb_handshake_successful = self.gotHandshake
        # self.gotError is called whenever the server replies with an error packet
        self.receiver.cb_error = self.gotError
        # self.gotGamestate is called whenever the server sends the gamestate (not gamestart!)
        self.receiver.cb_gamestate = self.gotGamestate
        # self.gameStart is called when the server sends the initial gamestate (gamestart)
        self.receiver.cb_gamestart = self.gotGamestart
        # self.gotAction is called when the server re-broadcasts an action someone has taken
        self.receiver.cb_action = self.gotAction
        # self.gotEndturn is called when the server announces a turn has ended (3 seconds)
        self.receiver.cb_endturn = self.gotEndturn

        # instantiate the transmitter. It will use the self.sendLine() function to send things.
        self.transmitter = skyport.SkyportTransmitter(self.sendLine)
        # the transmitter only needs to know what function to call
        # to actually send the data to the socket (self.sendLine)
        # send the initial handshake
        self.transmitter.sendHandshake(NAME)
        
    def lineReceived(self, line):
        # simply send the received line to the SkyportReceiver.
        # it will then accordingly call the appropriate callbacks registered.
        self.receiver.parseLine(line)
        
    def gotHandshake(self):
        print("AI got handshake!")
        
    def gotError(self, errormessage):
        print("AI got error: %s" % errormessage)
        
    def gotGamestate(self, turnNumber, mapObject, playerList):
        print("AI got gamestate!")
        if playerList[0]["name"] == NAME:
            print("thinking...")
            for x in range(0, 3):
                reactor.callLater(x/2.0, self.doRandomMovement)

    def doRandomMovement(self):
        direction = random.choice(["up", "down", "left-down", "left-up", "right-down", "right-up"])
        print("moving %s-wards." % direction)
        self.transmitter.sendMove(direction);
        
    def gotGamestart(self, turnNumber, mapObject, playerList):
        self.transmitter.sendLoadout("droid", "mortar")
        print("AI got gamestart!")
        
    def gotAction(self, actionType, who, restData):
        print("AI got action: %s" % actionType)
        
    def gotEndturn(self):
        print("AI got endturn!")

class SkyportConnectionFactory(ClientFactory):
    protocol = SkyportConnection
    def clientConnectionFailed(self, connector, reason):
        print 'connection failed:', reason.getErrorMessage()
        reactor.stop()

    def clientConnectionLost(self, connector, reason):
        print 'connection lost:', reason.getErrorMessage()
        reactor.stop()

def main():
    factory = SkyportConnectionFactory()
    reactor.connectTCP('localhost', 54321, factory)
    reactor.run()

if __name__ == '__main__':
    assert(len(sys.argv) == 2)
    NAME = sys.argv[1]
    main()
