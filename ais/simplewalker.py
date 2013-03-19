#!/usr/bin/env python2
import sys
import random
import socket
from socket import timeout
import json
sys.path.append("../api/python/")
import skyport

assert(len(sys.argv) == 2)

# We open the socket manually
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect(('127.0.0.1', 54321))

inputbuf = "" # All received data goes in here
weapons_chosen = [] # remember the weapons we chose in the loadout

def read_packet(): # this AI takes a single-thread blocking-I/O approach
    global inputbuf
    try:
        ret = sock.recv(1)
        inputbuf += ret
        if not ret:
            print("Disconnected!")
            sys.exit(1)
    except socket.timeout as e:
        print("timeout!")
        return None
    except socket.error as e:
        print("error: %s" % e)
        sys.exit(1)
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

def shoot_mortar_in_random_direction():
    # Randomly performs invalid shots.
    # [-4, 4] x [-4, 4]
    j = random.randrange(-4, 5)
    k = random.randrange(-4, 5)
    if j == 0 and k == 0:
        j = 2 # don't hit ourselves -- we don't care about bias.
        k = 2
    transmitter.attack_mortar(j, k) # coordinates relative to us

def upgrade_random_weapon():
    #transmitter.upgrade("laser")
    transmitter.upgrade(random.choice(weapons_chosen))
    
def shoot_laser_in_random_direction():
    # requires you to select the laser as weapon, obviously
    direction = random.choice(["up", "down", "left-down", "left-up", "right-down", "right-up"])
    print("shooting %s-wards." % direction)
    transmitter.attack_laser(direction)

def shoot_droid_in_random_directions():
    directions = []
    for x in range(0, 8):
        directions.append(random.choice(["up", "down", "left-down", "left-up", "right-down", "right-up"]))
    print("shooting droid in sequence %r" % directions)
    transmitter.attack_droid(directions)
    
def send_line(line): # sends a line to the socket
    print("sending: '%s'" % line)
    if sock.sendall(line + "\n") != None:
        print("Error sending data!")

def got_handshake():
    print("got handshake!")

def got_error(errmsg):
    print("Error: '%s'" % errmsg)

def got_gamestate(turn, map_obj, player_list):
    if player_list[0]["name"] == sys.argv[1]: # its our turn
        do_random_move()
        do_random_move()
        #transmitter.mine()
        random.choice([transmitter.mine, shoot_mortar_in_random_direction,
                       shoot_laser_in_random_direction, shoot_droid_in_random_directions,
                       transmitter.mine, upgrade_random_weapon])()
        

def got_gamestart(turn, map_obj, player_list):
    weapons = ["mortar", "droid", "laser"]
    primary_weapon = random.choice(weapons)
    weapons.remove(primary_weapon)
    secondary_weapon = random.choice(weapons)
    print("chose loadout: %s and %s" % (primary_weapon, secondary_weapon))
    global weapons_chosen
    weapons_chosen = [primary_weapon, secondary_weapon]
    transmitter.send_loadout(primary_weapon, secondary_weapon)

def got_action(action_type, who, rest_data):
    print("got action!")

def got_endturn():
    print("got endturn!")

    
receiver = skyport.SkyportReceiver()
transmitter = skyport.SkyportTransmitter(send_line)
# the SkyportTransmitter doesn't do networking on its own
# so you have to provide it with a send_line function that
# it can use to send data to the socket


# Register functions as callback, so that
# SkyportReceiver can call them when something happens
receiver.handler_handshake_successful = got_handshake
receiver.handler_error = got_error
receiver.handler_gamestate = got_gamestate
receiver.handler_gamestart = got_gamestart
receiver.handler_action = got_action
receiver.handler_endturn = got_endturn

# send the initial handshake
transmitter.send_handshake(sys.argv[1])

while True:
    line = read_packet() # try to read a line from the socket
    if line != None:
        print("got line: '%r'" % line)
        receiver.parse_line(line) # hand the line to SkyportReceiver to process

