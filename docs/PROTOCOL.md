SKYPORT AI COMPETITION PROTOCOL REV. 1
======================================

SYNOPSIS
========
This file describes the protocol used by the AIs to communicate with the
Server. The revision described in this protocol is rev1.
Note that lines prefixed with ">" describe "incoming" data, that is, data
sent from the server to the AI client, while lines prefixed with "<"
describe "outgoing" data, that is, data the AI client sends to the server.
Note also that messages in this document are broken up into several lines
for clarity, and are adorned with explanatory comments. however, when they
are sent to the server, they need to be all on one single line without any
comments.

TRANSPORT
========
The transport protocol used is line-based TCP. The server accepts UNIX-style
line-endings (\n) and windows-style line-endings (\r\n).
When reading from the socket, make sure that you don't limit the length of
your lines, as some packets may end up rather large.

CODEC & FORMAT
==============
Codec used for all transmissions is a line-based JSON format. This has been
chosen to avoid the trouble of making a separate format and requiring the
AIs to implement a parser for it. JSON is a simple, text-based format with
a syntax inspired by javascript.
Read about JSON here: http://json.org/
At the bottom of the page, you will find a list of existing JSON parsers for
various languages. This protocol does not use any special JSON features, does
not require you to have capabilities like partial JSON parsing, and does not
require you to deal with special characters/unicode. However, chosing a JSON
parser that is reasonably fast may give you an advantage.

HANDSHAKE
=========
Handshake sent by the AI to establish the connection.
Sent immediately upon connecting. If no handshake is sent after 10 seconds,
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
=========
Before the game starts, the server sends an initial gamestate to all AIs, with
the TURN-NUMBER = 0. This gamestate should not be replied to, and the server
rejects all replies. 10 seconds after the GAMESTART was sent, the actual
gameplay starts. The intent is to give all clients time to initialize and process
the board, resources & starting-positions into datastructures.

GAMESTATE
=========
Gamestate sent by the server to each of the AIs every round. You may use
any information contained in this packet to your advantage however you like.
After you have received the GAMESTATE packet, you have 3 seconds to reply
with 3 actions. If your reply is late, your actions will be discarded and
you forfeit your turn.
    > {"message":"gamestate",
    "gamestate": TURN-NUMBER,
    // turn-number starting to count at 1, i.e. this would be the TURN-NUMBERth turn.
    >  "map": MAP-OBJECT,
    // mapobject describing all tiles. See MAP-OBJECT below for its detailed structure.
    >  "entities": ENTITY-OBJECT,
    // entity-object describing the position of all entities on the map.
    >  "players":[PLAYER1, PLAYER2, ...]
    // players active in the game. The first player in the list, is the one to act this turn.
    > }

MAP-OBJECT
==========
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

	
    > {"j-length": MAP-WIDTH-IN-J-DIRECTION // size of the map in the J-direction
    >  "k-length": MAP-WIDTH-IN-K-DIRECTION // size of the map in the K-direction
    >  "data": [ // the map data, one J-column at a time
    >          [TILE(0,0), TILE(0,1), TILE(0,2), ...],
    >          [TILE(1,0), TILE(1,1), TILE(1,2), ...],
    >          [TILE(2,0), TILE(2,1), TILE(2,2), ...]]
    > }
    TILE(j, k) is the tile-type at coordinate (j, k).
    TILE(j, k) is simply a string of one of the following types:
    "G" -- "GRASS"
    "V" -- "VOID"
    "S" -- "SPAWN"
    "E" -- "EXPLODIUM"
    "R" -- "RUBIDIUM"
    "C" -- "SCRAP"
    "O" -- "ROCK"
    See the docs/GAME file for a description of how each of these behaves.
    The TILE(j,k) notation used here is simply to indicate that this tile
    is at position (j,k), the (j,k) is not part of the actual protocol.
    
    EXAMPLE:
    >  "data": [                // the map data, one J-column at a time
    >          ["G", "E", "S"], // first J-column
    >          ["G", "R", "V"], // second J-column
    >          ["C", "G", "G"]] // third J-column
    > }
    extracts to

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
    
	
	
ACTIONS (AI)
    Actions that can be taken by the AI.
    > {"message":"action"
    >  "type":TYPE,
    >  ...
    > }
    The following actions are currently valid:

    MOVEMENT/TACTICAL:
    
    Move a tile:
    > {"message":"action", "type":"move",
    >  "direction":"up" // can be "up", "down", "right-up", "right-down", "left-up", "left-down"
    > }

    Forfeit the turn:
    > {"message":"action", "type":"pass"}

    Upgrade a weapon:
    > {"message":"action", "type":"upgrade", "weapon":"mortar"} // can be "mortar", "laser" or "droid"
    
    Shoot the laser:
    > {"message":"action", "type":"laser",
    >  "direction":"up", // can be "up", "down", "right-up", "right-down", "left-up", "left-down"
    > }

    Shoot the mortar:
    > {"message":"action", "type":"mortar",
    >  "coordinates":"3,2" // relative J,K coordinates from the players position
    > }

    Launch the droid:
    > {"message":"action", "type":"droid",
    >  "sequence":["up", "rightUp", "rightDown", "down"] // sequence of directions
    > }

ACTIONS (Server)
    Actions that are taken by the AI are validated by the server,
    and then re-broadcasted to all AIs. For convenience, a "from" field is attached.
    EXAMPLES:
    
    Move a tile:
    > {"message":"action", "type":"move",
    >  "direction":"up", // can be "up", "down", "right-up", "right-down", "left-up", "left-down"
    >  "from":"username" // user who performed the move
    > }
    
    Upgrade a weapon:
    > {"message":"action", "type":"upgrade", "weapon":"mortar",
    >  "from":"username" // user who shot the mortar
    > }
        

ERRORS
    If the server encounters an error, either due to an invalid protocol command, or due to
    an invalid move, it will send back an error object.
    Example:
        > {"error":"You need to send a hanshake first"}
    Error messages are not machine-readable and mainly meant for human debugging.