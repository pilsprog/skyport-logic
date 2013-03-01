#!/usr/bin/env python2
import sys
import random
import socket
from socket import timeout
import json
sys.path.append("../api/python/")
import skyport

assert(len(sys.argv) == 2)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect(('127.0.0.1', 54321))
#sock.setblocking(0)
#sockfile = sock.makefile()

inputbuf = ""

def read_packet():
    global inputbuf
    try:
        ret = sock.recv(1)
        inputbuf += ret
        if not ret:
            print("Disconnected!")
            sys.exit(1)
    except socket.timeout as e:
        print("TO!")
        return None
    except socket.error as e:
        print("foo")
        return None
    try:
        characters_to_read = inputbuf.index("\n")
        line = inputbuf[0:characters_to_read] # removing the newline
        inputbuf = inputbuf[characters_to_read+1:len(inputbuf)]
        return line
    except ValueError as f:
        return None

def do_random_move():
    direction = random.choice(["up", "down", "left-down", "left-up", "right-down", "right-up"])
    print("moving %s-wards." % direction)
    transmitter.send_move(direction)

def shoot_laser_in_random_direction():
    # requires you to select the laser as weapon, obviously
    direction = random.choice(["up", "down", "left-down", "left-up", "right-down", "right-up"])
    print("shooting %s-wards." % direction)
    transmitter.attack_laser(direction)

def shoot_droid_in_random_directions():
    directions = []
    for x in range(0, 4):
        directions.append(random.choice(["up", "down", "left-down", "left-up", "right-down", "right-up", "lol"]))
    print("shooting droid in sequence %r" % directions)
    transmitter.attack_droid(directions)
    
def send_line(line):
    print("sending: '%s'" % line)
    if sock.sendall(line + "\n") != None:
        print("Error sending data!")

def got_handshake():
    print("got handshake!")

def got_error(errmsg):
    print("Error: '%s'" % errmsg)

def got_gamestate(turn, map_obj, player_list):
    if player_list[0]["name"] == sys.argv[1]:
        do_random_move()
        do_random_move()
        random.choice([shoot_laser_in_random_direction, shoot_droid_in_random_directions])()

def got_gamestart(turn, map_obj, player_list):
    # weapons = ["mortar", "droid"]
    primary_weapon = "laser"
    secondary_weapon = "droid"
    # #weapons.remove(primary_weapon)
    # secondary_weapon = random.choice(weapons)
    print("chose loadout: %s and %s" % (primary_weapon, secondary_weapon))
    transmitter.send_loadout(primary_weapon, secondary_weapon)

def got_action(action_type, who, rest_data):
    print("got action!")

def got_endturn():
    print("got endturn!")

    
receiver = skyport.SkyportReceiver()
transmitter = skyport.SkyportTransmitter(send_line)

receiver.handler_handshake_successful = got_handshake
receiver.handler_error = got_error
receiver.handler_gamestate = got_gamestate
receiver.handler_gamestart = got_gamestart
receiver.handler_action = got_action
receiver.handler_endturn = got_endturn

transmitter.send_handshake(sys.argv[1])

while True:
    line = read_packet()
    if line != None:
        print("got line: '%r'" % line)
        receiver.parse_line(line)

