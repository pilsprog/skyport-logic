#SKYPORT AI COMPETITION PROTOCOL REV. 1
***

NOTE
----

If you have obtained this file through downloading the SDK, make sure you have
the latest versions. New versions of the SDK may be continuously released up
until the competition. You can read a brand-new, in-development version of this
document online at

https://github.com/Amadiro/Skyport-logic/blob/master/docs/en/PROTOCOL.md

SYNOPSIS
--------
This file describes the protocol **used by the AIs to communicate with the
Server.** The revision described in this protocol is **rev1**.
Note that lines prefixed with **">"** describe **"incoming"** data, that is, data
sent from the server to the AI client, while lines prefixed with **"<"**
describe **"outgoing"** data, that is, data the AI client sends to the server.
Note also that messages in this document are broken up into several lines
for clarity, and are adorned with explanatory comments. however, when they
are sent to the server, they need to be all on one single line without any
comments.

TRANSPORT
--------
The transport protocol used is **line-based TCP**. The server accepts UNIX-style
line-endings (\n) and windows-style line-endings (\r\n).
When reading from the socket, make sure that you **don't limit the length of
your lines**, as some packets may end up rather large.

CODEC & FORMAT
--------------
Codec used for all transmissions is a **line-based JSON format**. This has been
chosen to avoid the trouble of making a separate format and requiring the
AIs to implement a parser for it. **JSON is a simple, text-based format with
a syntax inspired by javascript**.
Read about JSON here: http://json.org/
At the bottom of the page, you will find a list of existing JSON parsers for
various languages. This protocol does not use any special JSON features, does
not require you to have capabilities like partial JSON parsing, and does not
require you to deal with special characters/unicode. However, chosing a JSON
parser that is reasonably fast may give you an advantage.

HANDSHAKE
---------
Handshake sent by the AI to establish the connection.
**Sent immediately upon connecting.** If no handshake is sent after 10 seconds,
the server will drop the connection.

    < {"message":"connect",
    <  "revision":REVISION,  // The protocol revision as integer, i.e. 1
    <  "name":NAME // The name of your AI. String with less than 16 letters.
    < }
    
If the handshake was successful, the server answers with
    
    > {"message":"connect",
    >  "status":true
    > }
    
Otherwise it will send an error.


GAMESTART
---------
Before the game starts, the server sends an **initial gamestate** to all AIs, with
the **TURN-NUMBER = 0**. This gamestate looks otherwise **exactly like a normal gamestate**,
but should not be replied to. The server will reject all replies.
After the operator presses a button, an ENDTURN packet is sent, and the actual gameplay starts**.
The intent is to give all clients time to initialize and process the board, resources &
starting-positions into datastructures.

LOADOUT
-------
This is the loadout used by the AI to chose the weapons.

    < {"message":"loadout",
    <  "primary-weapon":"laser",    // can be "laser", "mortar", "droid"
    <  "secondary-weapon":"mortar", // ditto
    < }

The weapon loadout should be sent to the server by the AI after the GAMESTART
packet was sent, and before the first GAMESTATE packet is sent. If no loadout
is sent before the first GAMESTATE is sent, you are kicked off the server.

GAMESTATE
---------
Gamestate sent by the server to each of the AIs **every round**. You may use
any information contained in this packet to your advantage however you like.
If it is your turn (you are the first player in the rotating "players" list,)
**you have 3 seconds to reply with 3 actions packets.** Any actions you send
after the 3 seconds are over are discarded. This means for instance that you
could send one action at 1s, another action at 2s, and the third action at 3s,
but the third action will likely arrive at the server-end after the cutoff
and will be discarded. The first two actions will still be carried out for you.
After a turn is over, the ENDTURN packet is sent.

    > {"message":"gamestate",
    >  "turn": TURN-NUMBER,  // turn-number starting at 1, i.e. this would be the TURN-NUMBERth turn.
    >  "map": MAP-OBJECT,         // object describing all tiles. See MAP-OBJECT below for details.
    >  "players":[PLAYER1, PLAYER2, ...] // rotating list of AIs in the game. This turn is PLAYER1s.
    > }

    
MAP-OBJECT
----------
     J-coordinate                      K-coordinate
      \                               /
       \                             /
        \                _____      /
         \         <0>  /     \  <0>
          \            /       \
           \     ,----(   0,0   )----.
            <1> /      \       /      \ <1>
         _____ /  1,0   \_____/  0,1   \_____
    <2> /      \        /     \        /     \  <2>
       /        \      /       \      /       \
      (   2,0    )----(   1,1   )----(   0,2   )
       \        /      \       /      \       /
        \_____ /  2,1   \_____/  1,2   \_____/
               \        /     \        /
                \      /       \      /
                 `----(   2,2   )----'
               .       \       /      .
              .         \_____/        .
             .                          .

The map-object looks as follows:
	     
	
    > {"j-length": MAP-WIDTH-IN-J-DIRECTION // size of the map in the J-direction
    >  "k-length": MAP-WIDTH-IN-K-DIRECTION // size of the map in the K-direction
    >  "data": [ // the map data, one J-column at a time
    >          [TILE(0,0), TILE(0,1), TILE(0,2), ...],
    >          [TILE(1,0), TILE(1,1), TILE(1,2), ...],
    >          [TILE(2,0), TILE(2,1), TILE(2,2), ...]]
    > }

`TILE(j, k)` is the tile-type at coordinate (j, k).
`TILE(j, k)` is simply a string of one of the following types:
* "G" -- "GRASS"
* "V" -- "VOID"
* "S" -- "SPAWN"
* "E" -- "EXPLOSIUM"
* "R" -- "RUBIDIUM"
* "C" -- "SCRAP"
* "O" -- "ROCK"

See the docs/GAME.md file for a description of how each of these behaves.
The `TILE(j,k)` notation used here is simply to indicate that this tile
is at position (j,k), the (j,k) is not part of the actual protocol.
    
### EXAMPLE:

    > {"data": [                 // the map data, one J-column at a time
    >          ["G", "E", "S"],  // first J-column
    >          ["G", "R", "V"],  // second J-column
    >          ["C", "G", "G"]]  // third J-column
    > }
    
"extracts" to

     J-coordinate                      K-coordinate
      \                               /
       \                             /
        \                _____      /
         \         <0>  /     \  <0>
          \            /       \
           \     ,----(    G    )----.
            <1> /      \       /      \ <1>
         _____ /   G    \_____/   E    \_____
    <2> /      \        /     \        /     \  <2>
       /        \      /       \      /       \
      (    C     )----(    R    )----(    S    )
       \        /      \       /      \       /
        \_____ /   G    \_____/   V    \_____/
               \        /     \        /
                \      /       \      /
                 `----(    G    )----'
               .       \       /      .
              .         \_____/        .
             .                          .

PLAYER
------
    > {"name":"players-name",
    >  "primary-weapon":
    >    {"name":"laser", "level":1}, // "laser", "mortar", "droid", the number is the tier (1,2 or 3)
    >  "secondary-weapon":
    >    {"name":"mortar", "level":1},// ditto
    >  "health":20,		      // int from 1 to 100
    >  "score":120,		      // int from 1 to ?
    >  "position":"j,k"}              // position in j/k coordinates (global)
    
ENDTURN
-------
After a turn is over (the three-seconds deadline is over), a ENDTURN packet is sent. It is not
really necessary to react to the ENDTURN message in any way, it's merely a convenience message
so that all AIs know that the deadline for sending actions by the active player is over.

    > {"message":"endturn"}

    
COMPLETE EXAMPLE
----------------

TODO

ACTIONS (AI)
-----------
Actions that can be taken by the AI.

    > {"message":"action"
    >  "type":TYPE,
    >  ...
    > }

The following actions are currently valid:

### MOVEMENT/TACTICAL:
    
**Move** a tile:

    > {"message":"action", "type":"move",
    >  "direction":"up" // can be "up", "down", "right-up", "right-down", "left-up", "left-down"
    > }

**Forfeit** the turn:

    > {"message":"action", "type":"pass"}

**Upgrade** a weapon:
    
    > {"message":"action", "type":"upgrade", "weapon":"mortar"} // can be "mortar", "laser" or "droid"

**Mine** the current tile:

    > {"message":"action", "type":"mine"}
    
### ATTACK/OFFENSIVE:
    
Shoot the **laser**:
    
    > {"message":"action", "type":"laser",
    >  "direction":"up", // can be "up", "down", "right-up", "right-down", "left-up", "left-down"
    > }

Shoot the **mortar**:
    
    > {"message":"action", "type":"mortar",
    >  "coordinates":"3,2" // relative J,K coordinates from the players position
    > }

Launch the **droid**:
    
    > {"message":"action", "type":"droid",
    >  "sequence":["up", "rightUp", "rightDown", "down"] // sequence of directions
    > }

ACTIONS (Server)
----------------
Actions that are taken by the AI are validated by the server,
and then re-broadcasted to all AIs. For convenience, a "from" field is attached.

### EXAMPLES
    
**Moved** a tile:

    < {"message":"action", "type":"move",
    <  "direction":"up", // can be "up", "down", "right-up", "right-down", "left-up", "left-down"
    <  "from":"username" // user who performed the move
    < }
    
**Upgraded** a weapon:
    
    < {"message":"action", "type":"upgrade", "weapon":"mortar",
    <  "from":"username" // user who upgraded the mortar
    < }
        

ERRORS
------    
If the server encounters an error, either due to an invalid protocol command, or due to
an invalid move, it will send back an error object. The server may disconnect the AI that
provoked the error, depending on the severity.

Example:

    < {"error":"You need to send a handshake first"}

Error messages are not machine-readable and mainly meant for human debugging. Hence
the exact error-messages are not documented and may change. An AI should never rely
on some behaviour that provokes an error from the server.


EXAMPLE SESSIONS
----------------
The following example session demonstrates the communication between an AI and
the server in a one-on-one match on a very simple map.

    < {"message":"connect", "revision":1, "name":"you"}

    > {"message":"connect", "status":true}

    > {"message":"gamestate",
    >  "turn": 0,
    >  "map": {"j-length": 5,
    >          "k-length": 5,
    >          "data": [
    >                  ["R", "G", "V", "G", "V"],
    >                  ["G", "E", "G", "G", "S"],
    > 		       ["G", "G", "O", "G", "G"],
    >		       ["S", "G", "G", "C", "G"]
    >                  ["V", "G", "V", "G", "G"]]
    >         },
    >  "players":[
    >             {"name":"playerA", "primary-weapon":{"name":"laser", "level":1},
    >               "secondary-weapon":{"name":"laser", "level":1}, "health":100,
    >               "score":0, "position":"4,0"},
    >		  {"name":"you", "primary-weapon":{"name":"laser", "level":1},
    >              "secondary-weapon":{"name":"droid", "level":1}, "health":100,
    >		   "score":0, "position":"0,4"},
    >		 ]
    > }

                 __
              __/R \__
           __/G \__/G \__
        __/G \__/E \__/V \__
     __/S \__/G \__/G \__/G \__
    /V \__/G \__/O \__/G \__/V \
    \*_/G \__/G \__/G \__/S \*_/
       \__/V \__/C \__/G \__/
          \__/G \__/G \__/
             \__/G \__/
                \__/

		
