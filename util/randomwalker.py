#!/usr/bin/env python2
import sys
sys.path.append("../api/python")

import json
import skyport

from twisted.internet import reactor
from twisted.internet.protocol import ClientFactory
from twisted.protocols.basic import LineReceiver

name = "randomwalker"
protocol_version = 1

class SkyportConnection(LineReceiver):
    receiver = None
    transmitter = None
    
    def lineReceived(self, msg):
        receiver.parse_line(msg)

    def connectionMade(self):
        receiver = skyport.SkyportReceiver()
        transmitter = skyport.SkyportTransmitter(self.sendLine)
        transmitter.sendHandshake(name)

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
    main()
