var skyport = require('../api/nodejs/skyport.js');
if(process.argv.length != 3){
    console.log("Usage: node randomwalker.js name_of_the_bot");
    process.exit();
}

var myname = process.argv[2];
var myweapons = [];

function randomchoice(list){
    return list[Math.floor(Math.random() * list.length)];
}

function got_connection(){
    console.log("got connection, sending handshake...");
    connection.send_handshake(myname);
}
function got_handshake(){console.log("got handshake");}
function got_gamestart(map, players){
    // randomly chose some weapons to use
    console.log("got gamestart");
    available_weapons = ["laser", "mortar", "droid"];
    primary = randomchoice(available_weapons);
    available_weapons = available_weapons.filter(function(e){return e !== primary});
    secondary = randomchoice(available_weapons);
    connection.send_loadout(primary, secondary);
    myweapons = [primary, secondary];
}
function got_gamestate(turn_number, map, players){
    console.log("got gamestate");
    if(players[0]["name"] == myname){ // its our turn
	console.log("my turn!");
	random_move();
	random_move();
	// randomly shoot one of the weapons, upgrade or mine the tile
	randomchoice([random_laser, random_mortar, random_droid, upgrade, mine])();
    }
}

function upgrade(){
    // randomly upgrade one of our weapons
    connection.upgrade(randomchoice(myweapons));
}
function mine(){connection.mine();}

function random_move(){
    directions = ["up", "down", "left-up", "left-down", "right-up", "right-down"];
    connection.move(randomchoice(directions));
}

function random_laser(){
    console.log("Shooting the laser");
    directions = ["up", "down", "left-up", "left-down", "right-up", "right-down"];
    connection.attack_laser(randomchoice(directions));
}
function random_mortar(){
    console.log("Shooting the mortar");
    // [-4, 4] x [-4, 4] area
    var j = Math.floor(Math.random()*9) - 4;
    var k = Math.floor(Math.random()*9) - 4;
    if(j == 0 && k == 0){ // don't hit yourself
	j = 2; // unless you enjoy that kind of thing, that is
	k = 2;
    }
    connection.attack_mortar(j, k); // j,k coordinates relative to our position
}
function random_droid(){
    console.log("Shooting the droid");
    var commands = [];
    for(i = 0; i < 7; i++){
	commands.push(randomchoice(["up", "down", "left-up", "left-down", "right-up", "right-down"]));
    }
    connection.attack_droid(commands);
}

function got_action(type, from, rest){console.log("got action");}
function got_error(message){console.log("got error: '" + message + "'");}
function got_endturn(){console.log("got endturn");}

// Establish the connection
connection = new skyport.SkyportConnection("localhost", 54321);

// Register these callbacks. SkyportConnection will call the
// provided callback function when something of interest happens
connection.on('connection', got_connection);
connection.on('handshake', got_handshake);
connection.on('gamestart', got_gamestart);
connection.on('gamestate', got_gamestate);
connection.on('action', got_action);
connection.on('error', got_error);
connection.on('endturn', got_endturn);
connection.connect();

