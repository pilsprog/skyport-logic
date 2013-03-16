using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Sockets;
using fastJSON;
using System.Reflection;
using System.Web;

namespace Skyport
{
    public class SkyportConnection
    {
        SkyportObserver observer;
        string host;
        int port;
        TcpClient client;
        StreamReader reader;
        StreamWriter writer;
        public SkyportConnection(string HostName, int PortNumber, SkyportObserver ObserverArg) {
            observer = ObserverArg;
            host = HostName;
            port = PortNumber;
        }
        private void Send(object obj) {
            string str = fastJSON.JSON.Instance.ToJSON(obj,
                 new fastJSON.JSONParameters { EnableAnonymousTypes = true });
            writer.WriteLine(str);
        }
        private string ReadDataFromSocket(){
            string line = reader.ReadLine();
            return line;
        }
        private void Parse(Dictionary<string, object> obj) {
            object messageType;
            if (obj.TryGetValue("error", out messageType)) {
                observer.OnError((string)messageType);
                return;
            }
            if(!obj.TryGetValue("message", out messageType)){
                Console.WriteLine("Error: JSON packet has no 'message' key");
                return;
            }
            Console.WriteLine("got: " + (string)messageType);
            switch ((string)messageType) { 
                case "connect":
                    observer.OnHandshakeSuccessful();
                    break;
                case "gamestate":
                    ParseGamestate(obj);
                    break;
                case "action":
                    ParseAction(obj);
                    break;
                case "endturn":
                    observer.OnEndturn();
                    break;
                default:
                    Console.WriteLine("Got unknown message: " + messageType);
                    break;
            }
        }
        void ParseGamestate(Dictionary<string, object> obj)
        {
            object turnNumber;
            object map;
            object players;
            obj.TryGetValue("turn", out turnNumber);
            obj.TryGetValue("map", out map);
            obj.TryGetValue("players", out players);
            if ((long)turnNumber == 0)
            {
                observer.OnGamestart(new Map(map));
            }
            else {
                List<object> playerArr = (List<object>)players;
                PlayerData[] playerDataList = new PlayerData[playerArr.Count];
                for (int i = 0; i < playerArr.Count; i++)
                {
                    playerDataList[i] = new PlayerData(playerArr[i]);
                }
                observer.OnGamestate((long)turnNumber, new Map(map), playerDataList);
            }
        }
        void ParseAction(Dictionary<string, object> obj)
        {
            object player;
            object action;
            object type;
            obj.TryGetValue("from", out player);
            obj.TryGetValue("type", out type);
            obj.Remove("message");
            obj.Remove("from");
            obj.Remove("type");
            Dictionary<string, string> parameters = new Dictionary<string, string>();
            observer.OnAction((string)player, (string)type, obj);
        }

        /**
         * Public API
         */
        public void Run() {
            try
            {
                client = new TcpClient(host, port);
                Stream s = client.GetStream();
                reader = new StreamReader(s);
                writer = new StreamWriter(s);
                writer.AutoFlush = true;
                observer.OnConnectionEstablished(this);
            }
            catch (Exception e) {
                Console.WriteLine("Error establishing connection: " + e.Message);
            }
            while (true) {
                string data = ReadDataFromSocket();
                if(data == null){
                    break;
                }
                data.TrimEnd('\r', '\n');
                object newobj;
                try
                {
                    newobj = fastJSON.JSON.Instance.Parse(data);
                }
                catch (Exception e) {
                    Console.WriteLine("Warning: Invalid JSON packet received: '" + data + "':" + e.Message);
                    return;
                }
                Dictionary<string, object> newdict = (Dictionary<string, object>)newobj;
                Parse(newdict);
            }
        }
        public void SendHandshake(string AIName) {
            Send(new {message = "connect", revision = 1, name = AIName});
        }
        public void SendLoadout(string PrimaryWeapon, string SecondaryWeapon) {
            Dictionary<string, string> newdict = new Dictionary<string, string>();
            newdict.Add("message", "loadout");
            newdict.Add("primary-weapon", PrimaryWeapon);
            newdict.Add("secondary-weapon", SecondaryWeapon);
            Send(newdict);
        }
        public void SendMove(string whereto) {
            Send(new { message = "action", type = "move", direction = whereto });
        }
        public void AttackLaser(string whereto) {
            Send(new { message = "action", type = "laser", direction = whereto });
        }
        public void AttackMortar(long j, long k) {
            Send(new { message = "action", type = "mortar", direction = j + "," + k });
        }
        public void AttackDroid(string[] CommandSequence) {
            Send(new { message = "action", type = "droid", sequence = CommandSequence });
        }
        public void Mine() {
            Send(new { message = "action", type = "mine"});
        }
        public void Upgrade(string WeaponToUpgrade) {
            Send(new { message = "action", type = "upgrade", weapon = WeaponToUpgrade });
        }
    }
    public class PlayerData {
        public string name;
        public long j;
        public long k;
        public long score;
        public long health;
        public string PrimaryWeapon;
        public string SecondaryWeapon;
        public long PrimaryWeaponLevel;
        public long SecondaryWeaponLevel;
        public PlayerData(object obj) {
            var newdict = (Dictionary<string, object>)obj;
            object primaryObj, secondaryObj, primaryStringObj, secondaryStringObj,
                primaryLevelObj, secondaryLevelObj, positionStringObj, nameStringObj, scoreObj, healthObj;
            newdict.TryGetValue("primary-weapon", out primaryObj);
            newdict.TryGetValue("secondary-weapon", out secondaryObj);
            var primaryDict = (Dictionary<string, object>)primaryObj;
            var secondaryDict = (Dictionary<string, object>)secondaryObj;
            primaryDict.TryGetValue("name", out primaryStringObj);
            primaryDict.TryGetValue("level", out primaryLevelObj);
            secondaryDict.TryGetValue("name", out secondaryStringObj);
            secondaryDict.TryGetValue("level", out secondaryLevelObj);
            newdict.TryGetValue("name", out nameStringObj);
            newdict.TryGetValue("health", out healthObj);
            newdict.TryGetValue("score", out scoreObj);
            newdict.TryGetValue("position", out positionStringObj);
            string coordString = (string)positionStringObj;
            string[] coords = coordString.Split(',');
            j = Int64.Parse(coords[0]);
            k = Int64.Parse(coords[1]);
            name = (string)nameStringObj;
            score = (long)scoreObj;
            health = (long)healthObj;
            PrimaryWeapon = (string)primaryStringObj;
            SecondaryWeapon = (string)secondaryStringObj;
            PrimaryWeaponLevel = (long)primaryLevelObj;
            SecondaryWeaponLevel = (long)secondaryLevelObj;
        }
    }
    public class Map {
        public Map(object obj) {
            var map = (Dictionary<string, object>)obj;
            object jLength, kLength, data;
            map.TryGetValue("j-length", out jLength);
            map.TryGetValue("k-length", out kLength);
            map.TryGetValue("data", out data);
            string[,] mapArray = new string[(long)jLength, (long)kLength];
            var outer = (List<object>)data;
            for (int j = 0; j < outer.Count; j++) {
                var inner = (List<object>)(outer[j]);
                for (int k = 0; k < inner.Count; k++) {
                    Console.Write(inner[k] + ", ");
                }
                Console.WriteLine("");
            }
        }
    }
}
