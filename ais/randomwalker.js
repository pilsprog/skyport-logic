var skyport = require('../api/nodejs/skyport.js');
if(process.argv.length != 3){
    console.log("Usage: node randomwalker.js name_of_the_bot");
    process.exit();
}

var myname = process.argv[2];
function got_connection(){
    console.log("got connection, sending handshake...");
    connection.send_handshake(myname);
}
function got_handshake(){console.log("got handshake");}
function got_gamestart(map, players){
    console.log("got gamestart");
    connection.send_loadout("laser", "mortar");
}
function got_gamestate(turn_number, map, players){
    console.log("got gamestate");
    if(players[0]["name"] == myname){
	console.log("my turn!");
	directions = ["up", "down", "left-up", "left-down", "right-up", "right-down"];
	connection.move(directions[Math.floor(Math.random() * directions.length)]);
	connection.move(directions[Math.floor(Math.random() * directions.length)]);
	connection.attack_laser(directions[Math.floor(Math.random() * directions.length)]);
    }
}
function got_action(type, from, rest){console.log("got action");}
function got_error(message){console.log("got error: '" + message + "'");}
function got_endturn(){console.log("got endturn");}

connection = new skyport.SkyportConnection("localhost", 54321);
connection.on('connection', got_connection);
connection.on('handshake', got_handshake);
connection.on('gamestart', got_gamestart);
connection.on('gamestate', got_gamestate);
connection.on('action', got_action);
connection.on('error', got_error);
connection.on('endturn', got_endturn);
connection.connect();

