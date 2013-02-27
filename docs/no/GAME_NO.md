
#AI KONKURRANSE (tidl. "Hardcore Programming")
Det er år 2400. Konføderasjonen ligger i ruiner. Alt som
gjenstår av det en gang store imperiet er spredte ruiner av
de intergalaktiske havnene; imperiets gamle "Skyports".

Meteor regn herjer skyportene fra den asurblåe stratosfæren.
Samtidig gjør stammene seg klare til et nytt slag på den
svevende slagmarken.

Du har blitt valgt til ansvaret om å skape den perfekte-kamp
roboten til å utslette dine fiender i et hav av laserstråler
og eksplosjoner. Programmer din robot til å oppgradere våpen,
skyte fiender og dominere skyporten.

=======

#SKYPORT REV 1 SPILL REGLER
========================

##SAMMENDRAG

Dette dokumentet tar for seg reglene i spillet, implementert av serveren.


##KART

Kartet er et sekskantet rutenett. Se PROTOCOL_NO/KART-OBJEKT for videre
informasjon. Kartet bruker et koordinatsystem likt det i et vanlig firkantet
rutenett:


     J-Koordinat                       K-Koordinat
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
	      
Rutenes posisjon er alltid skrevet [J,K]; J-koordinatet kommer alltid først og
K-koordinatet sist. For eksempel; en AI vil kanskje sende en melding om å "flytte
til posisjonen 1,0" og så "angrip med mortar (granatkaster) på posisjonen
2,2". Serveren vil da flytte AI'en til posisjon [2,0] og angripe posisjonen
[2,2].  
Du kan bevege deg mellom ruter over grensene mellom rutene, og opp til 3 ruter på
en tur.
Det er syv forskjellige rute typer, dette vil bli forklart nårmere i neste avsnitt.

##RUTER

###FARBARE RUTER (ruter du kan flytte til)
* **GRESS**	- Gressrute. Ingen spesiell effekt. Kan komme i flere farger og
former, men roboter er ikke opptatt av estetikk.  
* **EXPLOSIUM**	- Hvis du står på denne ruten kan du bruke en eller flere 
handlinger på å utvinne explosium ressurs, 1 ressurs per handling. Hver explosium
 rute har maksimum 2 tilgjengelige ressurser. Etter at en rute er oppbrukt vil
 den forvandles til en gress rute.  vis du er it is depleted, it turns into
 grass.
* **RUBIDIUM**	- Hvis du står på denne ruten kan du bruke en eller flere 
handlinger på å utvinne rubidium ressurs, 1 ressurs per handling. Hver rubidium 
rute har maksimum 2 tilgjengelige ressurser. Etter at en rute er oppbrukt vil den
forvandles til en gress rute.
* **SKRAP**	- Hvis du står på denne ruten kan du bruke en eller flere 
handlinger på å utvinne metall, 1 ressurs per handling. Hver skrap rute har
maksimum 2 tilgjengelige ressurser. Etter at en rute er oppbrukt vil den 
forvandles til en gress rute.

###UFARBARE RUTER
* **VOID**(TOM)	- Tomme ruter.
* **SPAWN**(START)	- Beskyttet startområde. Det vil ikke være mulig å gå 
tilbake til disse rutene etter at du har gått av dem.
* **ROCK**(STEIN)	- En stein som stenger for veien.

##TUR
Hver runde får hver AI tre handlinger. En AI kan bruke handlingene til å flytte
seg (1 rute per handling), skyte ett våpen, vente eller utvinne ressurser. å 
skyte et våpen vil avslutte runden og bruke de resterende handlingene på skuddet.
De ekstra handlingene vil øke skaden som skuddet gjør. Venting vil gi en liten 
poeng- og helse- straff, dette betyr også at inaktive AI'er vil bli kastet ut 
etter en stund.
Etter at AI'ens runde er over; vil en spillstatus bli sendt til alle AI'er, og 
det vil være neste AI sin tur.
		
##VÅPEN
Det er tre forskjellige våpen i spillet; laser, granatkaster og kamp-droider. 
Hvert våpen kan bli oppgradert tre ganger for å øke rekkevidden og skaden.

###GRANATKASTER (Mortar)
Granatkasteren er det enkleste våpenet å bruke; den har en radius/rekkevidde, og
kan treffe alle ruter innen denne rekkevidden. Du kan også tenke deg at skuddet
kan "gå" et antall steg over alle typer ruter, i alle retninger. Granatkasteren
blir ikke påvirket av steiner eller tomme ruter, men det har heller ingen effekt å
skyte på dem. Skuddet (granaten) fra granatkasteren har en liten, eksplosiv ladning
og vil eksplodere ved sammenstøt, derfor har granatkasteren også en liten AoE 
(områdeskade) en rute rundt målet (1 rute).
* Rekkevidde ved nivå 1: 2 ruter
* Rekkevidde ved nivå 2: 3 ruter
* Rekkevidde ved nivå 3: 4 ruter

Se bildet under for illustrasjon:

![range of the mortar](../range-mortar.png)

###LASER
Laseren er våpenet med den lengste rekkevidden, men den kan bare skyte i rette
linjer, våpenet pekes altså i en retning og fyres av. Dette betyr også at det er
flere områder som man ikke kan treffe uten å bevege seg til en annen posisjon.
Laseren kan skyte over tomme ruter, men ikke gjennom steiner.
* Rekkevidde ved nivå 1: 5 ruter
* Rekkevidde ved nivå 2: 6 ruter
* Rekkevidde ved nivå 3: 7 ruter

Se bildet under for illustrasjon:

![range of the laser](../range-laser.png)

###KAMP-DROIDER
Kamp-droidene krever at AI'en sender en serie med retninger som viser hvilke steg 
de skal ta. En kamp-droide har et begrenset antall steg den kan ta, etter den har 
gått det antallet steg vil den eksplodere, uansett om den har fullført alle stegene
eller ikke. Droider kan ikke gå over tomme ruter eller gjennom stein, derfor må de 
navigeres rundt dem. Droider som møter tomme ruter eller steiner vil eksplodere.
Droider har en stor ksplosiv ladning og vil forårsake stor AoE (områdeskade) skade
rundt målet (1 rute).
* Rekkevidde ved nivå: 3 steg
* Rekkevidde ved nivå: 4 steg
* Rekkevidde ved nivå: 5 steg

Se bildet under for illustrasjon:

![range of the droid](../range-droid.png)

   
##RESURSER
Det er tre forskjellige typer ressurser i Skyport: rubidium, explosium og 
skrap-metall. Rubidium blir brukt til å oppgradere lasere, explosium; granatkastere
 og skrap-metall; kamp-droider.
Du kan utvinne ressurser ved å stå på en russurs-rute og bruke en handling på å
utvinne ressursen. Hvis en AI har samlet nok ressurser av en type vil det være
mulig å oppgradere det tilsvarende våpenet til det neste nivået. Hvert våpen kan 
ikke bli oppgradert forbi nivå 3.


##VÅPENVALG
Ved spillstart vil AI'ene velge hvilke to våpen de skal bruke for resten av
spillet. Alle kombinasjoner av de tre våpene er tilgjengelige. Noen kart kan 
inneholde en ujevnt distribuert mengde av ressurser, som kan påvirke hvor nyttig
hvert våpen er. Det er derfor lurt å gå nøye gjennom kartet får våpenvalget sendes


##SPILLSTART
Ved spillstart vil hver AI bli satt på hver sin **"STARTPOSISJON"-rute** (SPAWN) 
eller "start-ruter". Det vil ikke være mulig å gå tilbake på start-ruten etter at
man har flyttet seg av den. Derfor er det bare serveren som kan flytte AI'er til 
start-rutene. Et angrep mot en AI på en startrute vil *returnere skaden* til 
angriperen i stedet for målet. En AI som står på en startrute kan ikke utføre
andre handlinger enn å bevege seg av ruten. å vente på en startrute vil også føre
til poeng- og helse-straffer.

##HANDLINGER
Hver runde får hver AI muligheten til å utføre tre handlinger. En angreps-handling
vil avslutte turen og bruke de resterende handlingene til å styrke angrepet.

##BEVEGELSE
En handling kan bli brukt til å flytte AI'en fra en rute til en av de tilstøtende 
rutene; en AI kan altså bare bevege seg en rute om gangen og bare i seks retninger.
AI'er kan heller ikke bevege seg til stein eller tomme ruter.
Alle ugyldige bevegelser vil bli forkastet av serveren.

##POENG
å **skade en motstander** gir deg poeng tilsvarende skaden du påførte.
Hvis du **dreper en motstander** gir det en ekstra 20-poengs bonus.

Hvis du **dør vil du miste 80 poeng**.
Du kan også miste poeng på andre måter, som for eksempel å bruke handlinger på å
vente eller å stå på en startrute etter den første runden.

Ved spillets slutt vil den med den høyeste poengsummen vinne.

##ØDELEGGELSE
Hver spiller starter med en fast mengde helsepoeng. Helse vil ikke regenerere 
(fylle seg opp igjen). Hvis en AI blir ødelagt (går tom for helsepoeng); vil AI'en 
starte fra startruten igjen, uten å miste eventuelle oppgraderinger. AI'en må stå
over en hel runde etter å ha blitt ødelagt.


ARITHMETIC & STATS
##ARITMETIKK OG STATISTIKK
* Oppgradering av et våpen fra nivå 1 til 2 krever: 4 ressurser
* Oppgradering av et våpen fra nivå 2 til 3 krever: 5 ressurser
* Helsepoeng (hp) = -skade (-dmg)
* Startsum for helsepoeng: 100hp
* Laser skader, ved nivå 1: 16 dmg
* Laser skader, ved nivå 2: 18 dmg
* Laser skader, ved nivå 3: 22 dmg
* Granatkaster skader, ved nivå 1: 20 dmg
* Granatkaster skader, ved nivå 2: 20 dmg
* Granatkaster skader, ved nivå 3: 25 dmg
* Granatkasters AoE skader: 2 dmg
* Kamp-droider skader, ved nivå 1: 22 dmg
* Kamp-droider skader, ved nivå 2: 24 dmg
* Kamp-droider skader, ved nivå 3: 26 dmg
* Kamp-droiders AoE skader: 10 dmg
* Utregning av skade:
	player_damage = weapon_damage + AoE_damage + unused_turns * (0.2 * weapon_damage) + unused_turns * (0.2 * AoE_damage)
