SKYPORT REV 1 GAME RULES
========================

NOTE
----

If you have obtained this file through downloading the SDK, make sure you have
the latest versions. New versions of the SDK may be continuously released up
until the competition. You can read a brand-new, in-development version of this
document online at
https://github.com/Amadiro/Skyport-logic/blob/master/docs/en/GAME.md

SYNOPSIS
--------

This file describes the game rules as implemented by the server.


MAP
---

The map is a hexagonal grid of tiles. See PROTOCOL/MAP-OBJECT for an exact description
of the map format used. In short, the tiles have a normal coordinate system
imposed on them:



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
	      
Positions are always written in J,K notation, i.e. the J-coordinate comes first,
the K-coordinate second. For instance, you may send the server a message
such as "go to 1,0" and then "fire mortar at 2,2". The server would then
first move you one tile in the J-direction, and then perform your mortar shot,
assuming it is in range.
You can move from tile to tile in the fashion you would expect (crossing edges),
and you can move up to 3 tiles in one round.
There are seven different types of tiles, as described in the next section.

TILES
-----
### ACCESSIBLE TILES (These tiles can be moved onto)

* **GRASS** - normal grassland. No effect. May come in various forms and colors, but
	  robots don't care about aesthetics.
* **EXPLOSIUM** - If you stand on this tile, you can use one action or more to mine
		explosium. An explosium tile has 2 explosium, which you mine at a rate of
		one explosium per action. After it is depleted, it turns into grass.
* **RUBIDIUM** - If you stand on this tile, you can use one action or more to mine
		rubidium. An rubidium tile has 2 rubidium, which you mine at a rate of
		one rubidium per action. After it is depleted, it turns into grass.
* **SCRAP** - If you stand on this tile, you can use one action or more to mine
		scrap. An scrap tile has 2 scrap, which you mine at a rate of
		one scrap per action. After it is depleted, it turns into grass.
		
### INACCESSIBLE TILES (These tiles cannot be moved onto, but the server may place you on them.)
		
* **VOID**  - No tile at all.	
* **SPAWN** - protected spawn area. Once you move away from it, you can't re-enter. Cannot be attacked, and can't be attacked from.
* **ROCK**  - A rock is blocking the way.

TURNS
-----
Each round, each player gets three turns. In a turn, he may move to any
adjacent tile, covering a maximal distance of three tiles per turn. A
player may chose to use an action for something else, such as firing a
weapon or simply waiting. Firing a weapon immediately ends the turn, but
any left-over actions give your attack an extra damage bonus.
After a players turn is over, the gamestate is sent to all clients, and
the next player may make his turn.
		
WEAPONS
-------
There are three basic weapons, the laser, the mortar and the battle droid.
Each weapon may be upgraded three times, to increase its range and damage,
see the next sections.
Leftover unused actions in your turn (e.g. if you shoot your weapon right
away without moving first) will give your weapon extra bonus damage. See
the end of this document.

MORTAR
------
The mortar is the easiest to use; it simply has a radius/range, and it
hit any tile inside that range. An alternative interpretation (resulting
in the same tiles) is to say "the mortar can move a fixed number of steps
in any direction. Any tile it can reach in that number of steps, is in
range". The mortar is unaffected by gaps and rocks, but shooting at them
has no effect (both void tiles and rocks will simply absorb the damage
completely, and no AoE damage will ocurr). The mortar carries an explosive
load, and hence has a strong AoE bonus damage at a radius of one tile.
Note that you can use the mortar to damage yourself, both with a direct
hit and with AoE damage. You are not awarded any points for damaging or
killing yourself.
* Range at level 1: 2 tiles
* Range at level 2: 3 tiles
* Range at level 3: 4 tiles

See the bottom of this document for damage.

See the following image for a visualization:

![range of the mortar](../range-mortar.png)

LASER
-----
The laser has the longest range, and shoots straight. That means there
are tiles it cannot reach, without moving into a different position first.
The laser can shoot over gaps, but not through rocks.
* Range at level 1: 5 tiles
* Range at level 2: 6 tiles
* Range at level 3: 7 tiles

See the bottom of this document for damage.

See the following image for a visualization:

![range of the laser](../range-laser.png)

DROID
-----
The droid requires the player to send a list of directional steps.
It has a fixed number of steps it can walk, after which it will explode,
whether it has reached its target or not. The droid cannot walk over gaps
or rocks, so it has to navigate around them. The droid carries an
explosive payload and induces an AoE damage bonus, at a radius of
one tile.
Note that you can use the droid to damage yourself, both with a direct
hit and with AoE damage. You are not awarded any points for damaging or
killing yourself.

* Range at level 1: 3 steps
* Range at level 2: 4 steps
* Range at level 3: 5 steps

See the bottom of this document for damage.

See the following image for a visualization:

![range of the droid](../range-droid.png)

   
RESOURCES
---------
There are three basic types of resources: rubidium, explosium and scrap.
rubidium is used to upgrade lasers, explosium is used to upgrade the
mortar, and scrap is used to upgrade the battle droid.
Resources are obtained by standing on a resource-tile and using an action
to mine the resource. If a player has collected enough of one type of resource,
he can use a turn to upgrade his weapon to the next-highest tier. Once a weapon has
reached level 3, it cannot be upgraded anymore.


LOADOUT
-------
Upon gamestart, the AI can chose two weapons to use for the entire game.
Any combinations of weapons can be chosen. Some maps may contain an un-
even distribution of resources, having more resources of one type on one
end, or lacking a resource alltogether. It is hence probably wise to
scan the map thoroughly before chosing a loadout.


STARTING
--------
Upon gamestart, each AI is set to a protected starting-tile. A starting-
tile can be moved off of, but cannot be moved onto. Hence only the server
may place a player on a starting tile.
A player standing on the starting tile, may not attack, and cannot be
attacked. Remaining on the starting tile when the round is over, incurrs
a penality of -10 points.

ACTIONS
-------
Each round, each client gets three actions to use. An offensive move
immediately terminates the players turn, and uses up the remaining
moves (if any) for a damage bonus. 

MOVEMENT
--------
An action may be used for moving in one of the six cardinal directions.
Only accessible tiles may be moved onto, attempting to move onto an
inaccessible tile is an invalid move and hence discarded by the server.

POINTS
------
Damaging a player awards you with the amount of points equivalent to
the damage you inflicted on the player. This means that "overdamage"
is counted, e.g. if your enemy has 1 health point left, and you hit
him for 16 damage, you will be awarded 16 points for the damage inflicted.
Killing another player in addition awards you with a 20 bonus points bounty.
Dying gives you a point penality of -40 points.
Standing on the spawn-tile incurrs a -10 point penality at the end of each
round. To avoid any penalities, move away from the spawn-tile immediately
upon spawning.
Not performing any actions at all during your turn, incurs a -10 point
penality as well. The intention of this rule is to make crashed AIs quickly drop
to the bottom of the score bracket. If you wish to not make a move due
to tactical reasons, you may simply perform a shot in any direction, or
move up a tile and then back down, forfeiting your last action. This will
not incur any penality.

After a fixed amount of time has passed,
the round ends, and the player with the highest score wins.

DESTRUCTION
-----------
Each player starts with a certain amount of health. Health does not
replenish, but upon destruction, a player may respawn from his spawn-
point and continue playing without losing their upgraded weapons.
Respawning takes one full turn, so you have to skip one round. Once
you have died, the server will simply move past you in the player
order, so that you don't get your next turn. The next time after
that, the turn order will be resumed as normal.
Death incurs a -40 points penality.


ARITHMETIC & STATS
------------------
Damage is always rounded to the nearest int, with 0.5 rounding to 1.
* Upgrading a weapon from lvl 1 to lvl 2: 4 resources
* Upgrading a weapon from lvl 2 to lvl 3: 5 resources
* hp = -dmg
* Player at full health: 100hp
* laser lvl 1: 16dmg
* laser lvl 2: 18dmg
* laser lvl 3: 22dmg
* mortar lvl 1: 20dmg
* mortar lvl 2: 20dmg
* mortar lvl 3: 25dmg
* mortar AoE damage: 18dmg
* droid lvl 1: 22dmg
* droid lvl 2: 24dmg
* droid lvl 3: 26dmg
* droid AoE damage: 10dmg
* player_damage = weapon_damage
    + AoE_damage + unused_turns*(0.2*weapon_damage) + unused_turns*(0.2*AoE_damage)
For this last equation, notice that either AoE_damage = 0 or weapon_damage = 0, since a tile cannot be hit
simultaneously by the weapons main damage and its AoE damage.
Example:
Player A hits a tile X with a lvl 3 mortar and 2 unused turns left.
Damage incurred to player on X:	    	       player_damage = 25 + 0 + 2*(0.2*25) + 2*(0.2*0) = 35
Damage incurred to players on adjacent tiles:  player_damage = 0 + 18 + 2*(0.2*0) + 2*(0.2*18) = 25
