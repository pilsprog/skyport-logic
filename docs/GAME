SYNOPSIS
    This file describes the game rules as implemented by the server.
    

MAP
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
	      
    Positions are always written as [J, K], i.e. the J-coordinate comes first,
    the K-coordinate second. For instance, you may send the server a message
    such as "go to [1,0]" and then "fire mortar [2,2]". The server would then
    first move you to the tile [1, 0], and then perform your mortar shot,
    assuming it is in range.
    There are seven different types of tiles, as described in the next section.

TILES
    ACCESSIBLE TILES (These tiles can be moved onto)
        GRASS     - normal grassland. No effect. May come in various forms and colors, but
    	            robots don't care about aesthetics.
        EXPLODIUM - If you stand on this tile, you can use one action or more to mine
		    explodium. An explodium tile has 2 explodium, which you mine at a rate of
		    one explodium per action. After it is depleted, it turns into grass.
	RUBIDIUM - If you stand on this tile, you can use one action or more to mine
		    rubidium. An rubidium tile has 2 rubidium, which you mine at a rate of
		    one rubidium per action. After it is depleted, it turns into grass.
	SCRAP - If you stand on this tile, you can use one action or more to mine
		    scrap. An scrap tile has 2 scrap, which you mine at a rate of
		    one scrap per action. After it is depleted, it turns into grass.
    INACCESSIBLE TILES (These tiles cannot be moved onto, but the server may place you on them.)
    	VOID      - No tile at all.	
        SPAWN     - protected spawn area. Once you move away from it, you can't re-enter.
        ROCK      - A rock is blocking the way.

TURNS
    Each round, each player gets three turns. In a turn, he may move to any
    adjacent tile, covering a maximal distance of three tiles per turn. A
    player may chose to use an action for something else, such as firing a
    weapon or simply waiting. Firing a weapon immediately ends the turn, but
    any left-over actions give your attack an extra damage bonus.
    Waiting incurrs a small point/health penality, so that inactive AIs will
    get kicked out after a while.
    After a players turn is over, the gamestate is sent to all clients, and
    the next player may make his turn.
		    
WEAPONS
    There are three basic weapons, the laser, the mortar and the battle droid.
    Each weapon may be upgraded three times, to increase its range and damage,
    see the next sections.

MORTAR
    The mortar is the easiest to use; it simply has a radius/range, and it
    hit any tile inside that range. An alternative interpretation (resulting
    in the same tiles) is to say "the mortar can move a fixed number of steps
    in any direction. Any tile it can reach in that number of steps, is in
    range". The mortar is unaffected by gaps and rocks, but shooting at them
    has no effect. The mortar carries a small explosive load, and hence has
    a very weak AoE bonus damage at a radius of one tile.
    Range at level 1: 2 tiles
    Range at level 2: 3 tiles
    Range at level 3: 4 tiles
    See range-mortar.png for a visualization.
    
LASER
    The laser has the longest range, and shoots straight. That means there
    are tiles it cannot reach, without moving into a different position first.
    The laser can shoot over gaps, but not through rocks.
    Range at level 1: 5 tiles
    Range at level 2: 6 tiles
    Range at level 3: 7 tiles
    See range-laser.png for a visualization.
    
DROID
    The droid requires the player to send a list of directional steps.
    It has a fixed number of steps it can walk, after which it will explode,
    whether it has reached its target or not. The droid cannot walk over gaps
    or rocks, so it has to navigate around them. The droid carries a big
    explosive payload and induces a massive AoE damage bonus, at a radius of
    one tile.
    Range at level 1: 3 steps
    Range at level 2: 4 steps
    Range at level 3: 5 steps
    See range-droid.png for a visualization.

       
RESOURCES
    There are three basic types of resources: rubidium, explosium and scrap.
    rubidium is used to upgrade lasers, explosium is used to upgrade the
    mortar, and scrap is used to upgrade the battle droid.
    Resources are obtained by standing on a resource-tile and using an action
    to mine the resource. If a player has collected enough of one type of resource,
    he can use a turn to upgrade his weapon to the next-highest tier. Once a weapon has
    reached level 3, it cannot be upgraded anymore.

    
LOADOUT
    Upon gamestart, the AI can chose two weapons to use for the entire game.
    Any combinations of weapons can be chosen. Some maps may contain an un-
    even distribution of resources, having more resources of one type on one
    end, or lacking a resource alltogether. It is hence probably wise to
    scan the map thoroughly before chosing a loadout.

    
STARTING
    Upon gamestart, each AI is set to a protected starting-tile. A starting-
    tile can be moved off of, but cannot be moved onto. Hence only the server
    may place a player on a starting tile.
    Attacking a player on a starting tile incurs damage to the player
    performing the attack. A player on a starting-tile may not perform any
    moves other than moving away from the starting tile. Standing on the
    starting-tile incurs a health/point penality.
    
ACTIONS
    Each round, each client gets three actions to use. An offensive move
    immediately terminates the players turn, and uses up the remaining
    moves (if any) for a damage bonus. 
    
MOVEMENT
    An action may be used for moving in one of the six cardinal directions.
    Only grass-tiles may be moved onto, attempting to move onto any other
    tile is an invalid move and hence discarded by the server.
    
POINTS
    Killing another player awards you with a certain amount of points. You
    may lose varying amounts of points by performing certain actions that
    incur a penality, such as not moving a turn and standing on a spawn
    tile beyond the initial round. After a fixed amount of time has passed,
    the round ends, and the player with the highest score wins.

DESTRUCTION
    Each player starts with a certain amount of health. Health does not
    replenish, but upon destruction, a player may respawn from his spawn-
    point and continue playing without losing their upgraded weapons.
    Respawning takes one full turn.
    
SUDDEN DEATH/CRUMBLING WORLD
    may be implemented? Where do the spawn-points go? what about players that
    fall off the world? Should this be a core mechanic?

ARITHMETIC & STATS
    Upgrading a weapon from lvl 1 to lvl 2: 4 resources
    Upgrading a weapon from lvl 2 to lvl 3: 5 resources
    hp = -dmg
    Player at full health: 100hp
    laser lvl 1: 16dmg
    laser lvl 2: 18dmg
    laser lvl 3: 22dmg
    mortar lvl 1: 20dmg
    mortar lvl 2: 20dmg
    mortar lvl 3: 25dmg
    mortar AoE damage: 2dmg
    droid lvl 1: 22dmg
    droid lvl 2: 24dmg
    droid lvl 3: 26dmg
    droid AoE damage: 10dmg
    player_damage = weapon_damage
        + AoE_damage + unused_turns*(0.2*weapon_damage) + unused_turns*(0.2*AoE_damage)
    