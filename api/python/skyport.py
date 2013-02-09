import json
class SkyportReceiver:
    cb_handshake_successful = None
    cb_error = None
    cb_gamestate = None
    cb_gamestart = None
    cb_action = None
    cb_endturn = None
    
    def __init__(self):
        pass

    def parseLine(self, line):
        try:
            json_line = json.loads(line)
            self._parseJsonPacket(json_line)
        except ValueError as e:
            print("Error decoding JSON packet: %s" % e)
        except AttributeError as e:
            print("Invalid message: %s" % e)

    def _parseJsonPacket(self, json_packet):
        if json_packet["message"] == "connect":
            self.cb_handshake_successful()
        elif json_packet["message"] == "gamestate":
            if json_packet["turn"] == 0:
                self.cb_gamestart(json_packet["turn"], json_packet["map"], json_packet["players"])
            else:
                self.cb_gamestate(json_packet["turn"], json_packet["map"], json_packet["players"])    
        elif json_packet["message"] == "action":
            self.cb_action()
        elif json_packet["message"] == "endturn":
            self.cb_endturn()
        else:
            print("unknown message type: '%s'" % json_packet["message"])
        
class SkyportTransmitter:
    cb_send = None
    def __init__(self, send_function):
        self.cb_send = send_function
    def sendLoadout(self, primary_weapon, secondary_weapon):
        self.cb_send(json.dumps({"message":"loadout", "primary-weapon": primary_weapon, 
                                 "secondary-weapon": secondary_weapon}))
    def sendHandshake(self, name):
        self.cb_send(json.dumps({"message":"connect", "revision": 1, "name": name}))
    
    def sendMove(self, whereto):
        self.cb_send(json.dumps({"not":"implemented"}))
