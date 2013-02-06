SKYPORT AI KONKURRANSE PROTOKOLL REV.1 NORSK
======================================

SAMMENDRAG
--------
Dette dokumentet beskriver protokollen som **AI'en bruker til å kommunisere med serveren.** 
Revisjonen som blir beskrevet i dette dokumentet er **rev1**. 
Linjer som starter med **">"** beskriver meldinger som sendes **fra** serveren til AI-klienten (inn-data), 
mens linjer som starter med **"<"** beskriver meldinger som sendes **til** serveren (ut-data). 
Legg også merke til at meldingene beskrevet i dette dokumentet er delt opp i flere linjer og forklart med kommentarer. 
Når meldinger skal sendes til serveren må de sendes som en linje, uten kommentarer.

TRANSPORT
--------
Transport protokollen som brukes i dette systemet er **linje-basert TCP**. 
Serveren aksepterer linjeslutt i både UNIX-format (\n) og Windows-format (\r\n).

CODEC & FORMAT
--------------
Alle overføringene bruker et **linje-basert JSON format**. 
JSON ble valgt for å unngå at deltakerene skulle måtte lage sin egen parser for et egendefinert format. 
**JSON er et enkelt, text-basert format med et javascript-inspirert syntax.** 
Les om JSON her: http://json.org/. 
Nederst på siden finner du også en liste med eksisterende JSON parsere for flere programmeringsspråk. 
Denne protokollen bruker ingen spesielle JSON funksjoner; som delvis JSON parsing og spesielle tegn/unicode. 
Ulempen er at det kan være en fordel å velge en raskere JSON parser.

TILKOBLING
---------
Et håndtrykk (tilkoblings forespørsel) blir sent av AI'en til serveren for å etablere tilkoblingen.
**Dette skal sendes umiddelbart** etter forbindelsen er opprettet. 
Hvis AI'en ikke sender et håndtrykk vil serveren avslutte forbindelsen etter 10 sekunder.

Handshake sent by the AI to establish the connection.

    < {"message":"connect",
    <  "revision":REVISION, //Protokoll revisjon settes til integer, feks. 1
    <  "name":NAME //Navnet til AI'en. String med mindre enn 16 tegn
    < }
    
Hvis håndtrykket godkjennes svarer serveren med:
    
    > {"message":"connect",
    >  "status":true
    > }
    
Hvis ikke vil serveren sende en feilmelding.



SPILLSTART
---------
Før spillet starter sender serveren en **initial gamestate** (status ved oppstart) til alle AI'er, 
med **TURN-NUMBER = 0**. Denne spillstatusen ser ellers **nøyaktig lik normale spillstatus**, 
men skal ikke svares av AI'ene. Serveren vil avvise alle svar. 
**10 sekunder etter oppstartsstatus ble sendt starter spillet.** 
Formålet er å gi alle klienter tid til å initialisere og prosessere brettet, 
ressurser og start-posisjonene til datastrukturer.

SPILLSTATUS
---------
Spillstatus (gamestate) blir sendt av serveren til individuelle AI'er **hver runde**. 
Du kan bruke all informasjon i denne pakken til din fordel. 
Hvis det er din tur har du **3 sekunder på å svare med 3 handlingspakker**. 
Hvis du sender noen pakker etter de 3 sekundene har utgått vil de bli avvist. 
Dette betyr at du kan feks sende en handling etter 1s, 
andre handling etter 2s (1s etter den første) og en tredje etter 3s, 
men den siste handlingen vil sannsynligvis nå serveren etter 3s-grensen og vil bli avvist. 
De første to handlingene vil fortsatt bli gjennomført for deg. 
    
    >  "turn": TURN-NUMBER,  //Rundenr, som starter på 1
    >  "map": MAP-OBJECT,         //Objekt som beskriver alle fliser. Se Kart-objekt (MAP-OBJECT) for detaljer
    >  "players":[PLAYER1, PLAYER2, ...] //Roterende liste med AI'er i spillet. I dette eksempelet er det PLAYER1 sin tur
    > }

Kart-objekt (MAP-OBJECT)
-----------------------
     J-koordinat                       K-koordinat
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

Kart-objektet ser slik ut:
	     
	
    > {"j-length": MAP-WIDTH-IN-J-DIRECTION // Størrelsen på kartet i J-koordinatet
    >  "k-length": MAP-WIDTH-IN-K-DIRECTION // Størrelsen på kartet i K-koordinatet
    >  "data": [ // Kart-dataen, en J-kolonne om gangen
    >          [TILE(0,0), TILE(0,1), TILE(0,2), ...],
    >          [TILE(1,0), TILE(1,1), TILE(1,2), ...],
    >          [TILE(2,0), TILE(2,1), TILE(2,2), ...]]
    > }

`TILE(j, k)` er rute-typen til ruten ved koordinatene (j, k).
`TILE(j, k)` er en streng av en av følgende typer:
* "G" -- "GRASS" (Gress)
* "V" -- "VOID" (Tom rute)
* "S" -- "SPAWN" (Startsted)
* "E" -- "EXPLODIUM"
* "R" -- "RUBIDIUM"
* "C" -- "SCRAP" (Skrap)
* "O" -- "ROCK" (Stein)

Se docs/GAME.md dokumentet for en beskrivelse av hvordan rutetypene oppfører seg.
'Tile(j,k)' notasjonen som blir brukt her indikerer at ruten har posisjonen (j,k),
dette er ikke en del av den egentlige protokollen.
    
### EKSEMPEL:

    > {"data": [                 // Kart-dataen, en J-kolonne om gangen
    >          ["G", "E", "S"],  // første J-kolonne
    >          ["G", "R", "V"],  // andre J-kolonne
    >          ["C", "G", "G"]]  // tredje J-kolonne
    > }
    
utgjør dette kartet:

     J-koordinat                       K-koordinat
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

SPILLER
------
    > {"name":"players-name",
    >  "primary-weapon":"laser-1",    // "laser", "mortar", "droid", nummeret indikerer nivå (1,2 eller 3)
    >  "secondary-weapon":"mortar-1", // samme som forrige linje
    >  "health":20,		      // int fra 1 til 100
    >  "score":120,		      // int fra 1 til ?
    >  "position":"j,k"}              // posisjon i j/k koordinater (globalt)
    

KOMPLETT EKSEMPEL
----------------

TODO

HANDLINGER (AI)
-----------

Handlinger AI'en kan utføre.

    > {"message":"action"
    >  "type":TYPE,
    >  ...
    > }

Følgende handlinger er for øyeblikket gyldige:

### BEVEGELSE/TAKTISK:
    
**Flytt** spilleren en rute:

    > {"message":"action", "type":"move",
    >  "direction":"up" // kan være "up" (opp), "down" (ned), "right-up" (opp-høyre), "right-down" (ned-høyre), "left-up" (venstre-opp), "left-down" (venstre-ned)
    > }

**Stå over** turen:

    > {"message":"action", "type":"pass"}

**Oppgrader** ett våpen:
    
    > {"message":"action", "type":"upgrade", "weapon":"mortar"} // kan være "mortar", "laser" eller "droid"

**Hent ressurser** fra ruten:

    > {"message":"action", "type":"mine"}
    
### ANGREP/OFFENSIV:
    
Skyt **laseren**:
    
    > {"message":"action", "type":"laser",
    >  "direction":"up", // kan være "up", "down", "right-up", "right-down", "left-up", "left-down"
    > }

Skyt **mortar**:
    
    > {"message":"action", "type":"mortar",
    >  "coordinates":"3,2" // relative J/K koordinatene fra spillerens posisjon
    > }

aktiver **droiden**:
    
    > {"message":"action", "type":"droid",
    >  "sequence":["up", "rightUp", "rightDown", "down"] // bevegelses-sekvens
    > }

HANDLINGER (Server)
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
    <  "from":"username" // user who shot the mortar
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
    >  "map": {"j-length": 5
    >          "k-length": 5
    >          "data": [
    >                  ["R", "G", "V", "G", "V"],
    >                  ["G", "E", "G", "G", "S"],
    > 		       ["G", "G", "O", "G", "G"],
    >		       ["S", "G", "G", "C", "G"]
    >                  ["V", "G", "V", "G", "G"]]
    >         },
    >  "players":[
    >             {"name":"playerA", "primary-weapon":"laser-1",
    >               "secondary-weapon":"mortar-1", "health":100,
    >               "score":0, "position":"4,0"},
    >		  {"name":"you", "primary-weapon":"laser-1",
    >              "secondary-weapon":"droid-1", "health":100,
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
