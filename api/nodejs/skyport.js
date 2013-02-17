var net = require('net');

module.exports = {
    SkyportConnection:SkyportConnection
};

function SkyportConnection(host, port){
    this.host = host;
    this.port = port;
    this.dispatchtable = {};
    this.data = "";
}

SkyportConnection.prototype = {
    on: function(signal, handler){
	this.dispatchtable[signal] = handler;
    },
    _process_packet: function(obj){
	if(obj["error"]){
	    this.dispatchtable['error'](obj["error"]);
	    return;
	}
	// error, endturn
	switch(obj["message"]){
	case "connect":
	    this.dispatchtable['handshake']();
	    break;
	case "gamestate":
	    if(obj["turn"] == 0){
		this.dispatchtable['gamestart'](obj["map"], obj["players"]);
	    }
	    else {
		this.dispatchtable['gamestate'](obj["turn"], obj["map"], obj["players"]);
	    }
	    break;
	case "action":
	    action_type = obj["type"];
	    action_from = obj["from"];
	    delete obj["type"];
	    delete obj["from"];
	    this.dispatchtable['action'](obj["type"], obj["from"], obj);
	    break;
	case "endturn":
	    this.dispatchtable['endturn']();
	    break;
	default:
	    console.log("unknown message: '" + obj["message"] + "'");
	}
    },
    _process_data: function(data, local){
	local.data += data;
	local._try_to_read_line();
    },
    _process_line: function(line){
	try {
	    obj = JSON.parse(line);
	    this._process_packet(obj);
	}
	catch (e) {
	    console.log("JSON Error: '" + e + "'");
	}
    },
    _try_to_read_line: function(){
	if(this.data.indexOf('\n') !=-1 ) {
	    var linebuffer = this.data.split('\n');
	    this.data = linebuffer.pop();
	    linebuffer.forEach(function(line) {
		if(line != ""){
		    this._process_line(line);
		}
            }, this);
	}
    },
    _send_packet: function(obj){
	this.sock.write(JSON.stringify(obj) + "\n");
    },
    
    // Public API
    connect: function(){
	this.data = "";
	var local = this;
	this.sock = net.createConnection(this.port, this.host);
	this.sock.on('connect', function(){local.dispatchtable['connection']();});
	this.sock.on('data', function(data){local._process_data(data, local);});
    },
    send_handshake: function(name){
	this._send_packet({"message":"connect", "revision":1, "name":name});
    },
    send_loadout: function(primary_weapon, secondary_weapon){
	this._send_packet({"message":"loadout", "primary-weapon":primary_weapon,
			   "secondary-weapon":secondary_weapon});
    },
    move: function(direction){
	this._send_packet({"message":"action", "type":"move", "direction":direction});
    }
};
