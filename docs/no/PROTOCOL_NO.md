#SKYPORT AI KONKURRANSE PROTOKOLL REV.1 NORSK
***

##SAMMENDRAG##
Dette dokumentet beskriver protokollen som **AI'en bruker til å kommunisere med serveren.** 
Revisjonen som blir beskrevet i dette dokumentet er **rev1**. 
Linjer som starter med **">"** beskriver meldinger som sendes **fra** serveren til AI-klienten (inn-data), 
mens linjer som starter med **"<"** beskriver meldinger som sendes **til** serveren (ut-data). 
Legg også merke til at meldingene beskrevet i dette dokumentet er delt opp i flere linjer og forklart med kommentarer. 
Når meldinger skal sendes til serveren må de sendes som en linje, uten kommentarer.

##TRANSPORT##
Transport protokollen som brukes i dette systemet er **linje-basert TCP**. 
Serveren aksepterer linjeslutt i både UNIX-format (\n) og Windows-format (\r\n).

##CODEC & FORMAT##
Alle overføringene bruker et **linje-basert JSON format**. 
JSON ble valgt for å unngå at deltakerene skulle måtte lage sin egen parser for et egendefinert format. 
**JSON er et enkelt, text-basert format med et javascript-inspirert syntax.** 
Les om JSON her: http://json.org/. 
Nederst på siden finner du også en liste med eksisterende JSON parsere for flere programmeringsspråk. 
Denne protokollen bruker ingen spesielle JSON funksjoner; som delvis JSON parsing og spesielle tegn/unicode. 
Ulempen er at det kan være en fordel å velge en raskere JSON parser.

##TILKOBLING##
Et håndtrykk (tilkoblings forespørsel) blir sent av AI'en til serveren for å etablere tilkoblingen.
**Dette skal sendes umiddelbart** etter forbindelsen er opprettet. 
Hvis AI'en ikke sender et håndtrykk vil serveren avslutte forbindelsen etter 10 sekunder.

Håndtrykk sendt av AI for å etablere tilkoblingen til serveren.

    < {"message":"connect",
    <  "revision":REVISION, //Protokoll revisjon settes til integer, feks. 1
    <  "name":NAME //Navnet til AI'en. String med mindre enn 16 tegn
    < }
    
Hvis håndtrykket godkjennes svarer serveren med:
    
    > {"message":"connect",
    >  "status":true
    > }
    
Hvis ikke vil serveren sende en feilmelding.



##SPILLSTART##
Før spillet starter sender serveren en **initial gamestate** (status ved oppstart) til alle AI'er, 
med **TURN-NUMBER = 0** og tom players array. Denne spillstatusen ser ellers **nøyaktig lik normale spillstatus**, 
men skal ikke svares av AI'ene. Serveren vil avvise alle svar.
Etter at server-operatøren trykker på en knapp vil en ENDTURN (tur-slutt) pakke bli sendt og
spillet vil begynne. Formålet er å gi alle klienter tid til å initialisere og prosessere brettet,
ressurser og start-posisjonene til datastrukturer.


##VÅPENVALG##
AI'en må selv velge hvilke våpen den skal bruke i spillet. Dette gjøres etter oppstarts-meldingen
er sendt (initial gamestate) og før spillet starter. Hvis en AI ikke har sendt sitt våpenvalg før
første spillstatus sendes fra serveren; vil AI'en bli **frakoblet fra serveren**.

    < {"message":"loadout",
    <  "primary-weapon":"laser",    // Kan være "laser", "mortar", "droid"
    <  "secondary-weapon":"mortar", // ditto
    < }


##SPILLSTATUS##
Spillstatus (gamestate) blir sendt av serveren til individuelle AI'er **hver runde**. 
Du kan bruke all informasjon i denne pakken til din fordel. 
Hvis det er din tur har du **3 sekunder på å svare med 3 handlingspakker**. 
Hvis du sender noen pakker etter de 3 sekundene har utgått vil de bli avvist. 
Dette betyr at du kan feks sende en handling etter 1s, 
andre handling etter 2s (1s etter den første) og en tredje etter 3s, 
men den siste handlingen vil sannsynligvis nå serveren etter 3s-grensen og vil bli avvist. 
De første to handlingene vil fortsatt bli gjennomført for deg. Etter turen er ferdig vil det bli
sendt en ENDTURN pakke.    
  
    > {"message":"gamestate",
    >  "turn": TURN-NUMBER,  //Rundenr, som starter på 1
    >  "map": MAP-OBJECT,         //Objekt som beskriver alle fliser. Se Kart-objekt (MAP-OBJECT) for detaljer
    >  "players":[PLAYER1, PLAYER2, ...] //Roterende liste med AI'er i spillet. I dette eksempelet er det PLAYER1 sin tur
    > }


##KART-OBJEKT (MAP-OBJECT)##
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
    >  "data": [ // Kart-dataen, en J-"kolonne" om gangen
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

##SPILLER##
    > {"name":"players-name",
    >  "primary-weapon":
    >    {"name":"laser", "level":1},    // "laser", "mortar", "droid", nummeret indikerer nivå (1,2 eller 3)
    >  "secondary-weapon":"mortar-1", // samme som forrige linje
    >  "health":20,		      // int fra 0 til 100
    >  "score":120,		      // positivt tall (eller 0)
    >  "position":"j,k"}              // posisjon i j/k koordinater (globalt)  

##TURSLUTT (ENDTURN)
Etter at en tur er over (tre-sekunders-fristen er over) sendes det en "ENDTURN" melding. Det er
ikke nødvendig å svare denne meldingen. Formålet er å si ifra til AI'en at turen er ferdig.
En ENDTURN pakke ser slik ut:
	> {"message":"endturn"}

##KOMPLETT EKSEMPEL##

TODO

##HANDLINGER (AI)##

Handlinger AI'en kan utføre.

    > {"message":"action"
    >  "type":TYPE,
    >  ...
    > }

Følgende handlinger er gyldige:

### BEVEGELSE/TAKTISK:
    
**Flytt** spilleren en rute:

    > {"message":"action", "type":"move",
    >  "direction":"up" // kan være "up" (opp), "down" (ned), "right-up" (opp-høyre), "right-down" (ned-høyre), "left-up" (venstre-opp), "left-down" (venstre-ned)
    > }

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

##HANDLINGER (Server)##
Handlinger som AI'ene utfører må bli validert av serveren, før de blir kringkastet
på nytt til alle AI'er. For praktiske årsaker inkluderer vi et "from" (fra) felt.

### EKSEMPLER
    
**Flytt** til en rute:

    < {"message":"action", "type":"move",
    <  "direction":"up", // Kan være "up", "down", "right-up", "right-down", "left-up", "left-down"
    <  "from":"username" // AI'en som utførte handlingen
    < }
    
**Oppgradere** et våpen:
    
    < {"message":"action", "type":"upgrade", "weapon":"mortar",
    <  "from":"username" // AI'en som utførte handlingen
    < }
        

##FEILMELDINGER##   
Hvis det oppstår en feil, enten på grunn av en ugyldig kommando eller en ugyldig handling,
vil serveren sende en feilmelding i form av et feilmeldings-objekt. Hvis feilen er alvorlig
kan serveren frakoble AI'en som forårsaket den.

Eksempel:

    < {"error":"You need to send a handshake first"}

Feilmeldingene er ikke maskinlesbare og er hovedsakling ment for menneskelig feilsøking.
Derfor er ikke feilmeldingene dokumentert og kan bli endret i utviklingen av systemet.
En AI burde aldri være avhengig av feilmeldinger fra serveren.

##DØD##
Når en spiller dør, blir hans helsepoeng satt til 0, men han vil forbli på spillbrettet.
En spiller som har 0 helsepoeng lever ikke, og vil bli satt tilbake til hans startrute
i løpet av neste runde. En spiller med 0 helsepoeng kan ikke har en tur (servern vil fylle
opp helsepoengene til 100 før spillern får en tur), og kan blir ignorert. Å skade en dø spiller
vil ikke gir deg noe poeng, eller har noe som helst effekt ellers.

##FRAKOBLING##

En AI som ble frakoblet, vil forbli i spillet, og fortsette å samle inn straffpoeng for å ikke
gjøre noe. Å drepe en frakoblet AI vil gir deg vanlig mengde skade- og bonus-poeng. Om du blir
frakoblet fra servern, kan du ikke forbinde deg tilbake til den, så du burde passe på å ikke
frakoble under noen omstendigheter.

##EKSEMPEL ØKTER##
Disse eksempel øktene demonstrer kommunikasjonen mellom en AI og serveren i en 1-mot-1 kamp
på et simplifisert kart.

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
    >               "secondary-weapon": {"name": "mortar", "level":1},
    >               "health":100, "score":0, "position":"4,0"},
    >		  {"name":"you", "primary-weapon":{"name":"laser", "level": 1},
    >              "secondary-weapon":{"name":"droid", "level":1},
    >              "health":100, "score":0, "position":"0,4"},
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
