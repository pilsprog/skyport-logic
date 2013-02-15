import sys
import random
import socket
import json
sys.path.append("../api/python/")
import skyport

assert(len(sys.argv) == 2)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect(('127.0.0.1', 54321))

inputbuf = ""

def read_packet():
    global inputbuf
    inputbuf += sock.recv(1024*4)
    try:
        characters_to_read = inputbuf.index("\n")
        line = inputbuf[0:characters_to_read] # removing the newline
        inputbuf = inputbuf[characters_to_read+1:len(inputbuf)]
        return line
    except ValueError as e:
        return None

def do_random_move():
    direction = random.choice(["up", "down", "left-down", "left-up", "right-down", "right-up"])
    print("moving %s-wards." % direction)
    transmitter.sendMove(direction)

def send_packet(line):
    send_line(json.dumps(line))

def send_line(line):
    sock.send(line + "\n")

def got_handshake():
    print("got handshake!")

def got_error(errmsg):
    print("Error: '%s'" % errmsg)

def got_gamestate(turn, map_obj, player_list):
    if player_list[0]["name"] == sys.argv[1]:
        for x in range(0, 3):
            do_random_move()

def got_gamestart(turn, map_obj, player_list):
    transmitter.sendLoadout("droid", "mortar")

def got_action(action_type, who, rest_data):
    print("got action!")

def got_endturn():
    print("got endturn!")

    
receiver = skyport.SkyportReceiver()
transmitter = skyport.SkyportTransmitter(send_line)

receiver.cb_handshake_successful = got_handshake
receiver.cb_error = got_error
receiver.cb_gamestate = got_gamestate
receiver.cb_gamestart = got_gamestart
receiver.cb_action = got_action
receiver.cb_endturn = got_endturn

transmitter.sendHandshake(sys.argv[1])

while True:
    line = read_packet()
    if line != None:
        print("got line: '%s'" % line)
        receiver.parseLine(line)

