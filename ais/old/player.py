#!/usr/bin/env python
import sys
import time
from twisted.internet.protocol import Protocol, Factory
from twisted.internet import reactor

FH = None
INFILE = ""
DATA = []
SEGMENTED_DATA = []
PORT = 54331
CURRENT_REPLAY_INDEX = 0

import json
def print_turn(packet):
    json_obj = json.loads(packet)
    if json_obj["message"] == "gamestate":
        print("turn nr. %d" % json_obj["turn"])

def is_endaction(string):
    if string == "{\"message\":\"endactions\"}\n" or string == "{\"message\":\"connect\",\"status\":true}\n":
        return True
    else:
        return False

def is_ready(string):
    return string == "{\"message\":\"ready\"}\n"

def create_segments():
    for line in DATA:
        SEGMENTED_DATA.append(line)
        if is_endaction(line):
            SEGMENTED_DATA.append(None)
    if SEGMENTED_DATA[-1] != None:
        SEGMENTED_DATA.append(None)
    
class Spammer(Protocol):
    def sendLines(self):
        global CURRENT_REPLAY_INDEX
        global SEGMENTED_DATA
        print_turn(SEGMENTED_DATA[CURRENT_REPLAY_INDEX])
        self.transport.write(SEGMENTED_DATA[CURRENT_REPLAY_INDEX])
        CURRENT_REPLAY_INDEX += 1
        if SEGMENTED_DATA[CURRENT_REPLAY_INDEX] == None:
            CURRENT_REPLAY_INDEX += 1
            return
        else:
            reactor.callLater(0.1, self.sendLines)
    
    def connectionMade(self):
        print("got connection")

    def dataReceived(self, data):
        if CURRENT_REPLAY_INDEX == 0:
            self.sendLines()
            reactor.callLater(1.0, self.sendLines)
        if is_ready(data):
            self.sendLines()
        else:
            print("got data: '%s'" % data)

def main():
    assert(len(sys.argv) == 2)
    INFILE = sys.argv[1]
    FH = open(INFILE, "r")
    print("reading...")
    for line in FH:
        DATA.append(line)
    print("read %d lines of data." % len(DATA))
    print("segmenting...")
    create_segments()
    print("Listening on port %d..." % PORT)
    f = Factory()
    f.protocol = Spammer
    reactor.listenTCP(PORT, f)
    reactor.run()

if __name__ == '__main__':
    main()

