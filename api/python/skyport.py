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

    def parse_line(self, line):
        print("parsing line: %s" % line)
        try:
            json_line = json.loads(line)
            self._parse_json_packet(json_line)
        except ValueError as e:
            print("Error decoding JSON packet: '%s'" % e)

    def _parse_json_packet(self, json_packet):
        pass
        
class SkyportTransmitter:
    cb_send = None
    def __init__(self, send_function):
        self.cb_send = send_function
        
    def sendHandshake(self, name):
        self.cb_send(json.dumps({"message":"connect", "revision": 1, "name": name}))
    
    def sendMove(self, whereto):
        self.cb_send(json.dumps({"not":"implemented"}))
