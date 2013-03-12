#!/usr/bin/env python2

import sys

from twisted.internet import reactor
from twisted.internet.protocol import ClientFactory
from twisted.protocols.basic import LineReceiver

PASS = "supersecretpassword"
OUTFILE = ""
FH = None

class SkyportConnection(LineReceiver): # twisted-specific things
    receiver = None     # the SkyportReceiver object
    transmitter = None  # the SkyportTransmitter object
    delimiter = "\n"    # need to set this so that twisted parses the line endings correctly

    def connectionMade(self): # twisted has successfully established the connection for us
        print("got connection")
        self.sendLine('{"message":"connect", "revision":1, "password":%s, "laserstyle":"start-stop"}' % PASS)
        
    def lineReceived(self, line):
        print("got line: %s" % line)
        FH.write(line + "\n")
        if line == '{"message":"endactions"}':
            self.sendLine('{"message":"ready"}')
        
class SkyportConnectionFactory(ClientFactory):
    protocol = SkyportConnection
    def clientConnectionFailed(self, connector, reason):
        print 'connection failed:' + reason.getErrorMessage()
        reactor.stop()

    def clientConnectionLost(self, connector, reason):
        print 'connection lost:' + reason.getErrorMessage()
        reactor.stop()

def main():
    factory = SkyportConnectionFactory()
    reactor.connectTCP('localhost', 54331, factory)
    reactor.run()

if __name__ == '__main__':
    assert(len(sys.argv) == 2)
    OUTFILE = sys.argv[1]
    FH = open(OUTFILE, "w")
    main()
