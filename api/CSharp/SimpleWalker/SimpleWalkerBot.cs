using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Skyport;

namespace SimpleWalker
{
    class SimpleWalkerBot: SkyportObserver
    {
        public string BotName = "csharpwalker";
        SkyportConnection connection;
        static Random rnd = new Random();
        string primary;
        string secondary;
        public void OnConnectionEstablished(SkyportConnection connectionArg) {
            connection = connectionArg;
            Console.WriteLine("Connection established");
            connection.SendHandshake(BotName);
        }
        private T RandomChoice<T>(T[] list)
        {
            return list[rnd.Next(list.Count())];
        }
        public void OnHandshakeSuccessful() {
            Console.WriteLine("Handshake successful");
        }
        public void OnEndturn() {
            Console.WriteLine("Endturn!");
        }
        public void OnAction(string player, string type, Dictionary<string, object> parameters) {
            Console.WriteLine("Got Action from " + player + " of type " + type);
            foreach (KeyValuePair<string, object> pair in parameters)
            {
                try
                {
                    Console.WriteLine("parameter: {0}: {1}", pair.Key, (string)pair.Value);
                }
                catch(InvalidCastException e){} // value wasn't a string (happens for droids -- it's a list)
            }
        }
        public void OnGamestart(Map MapData) {
            Console.WriteLine("Got Gamestart!");
            primary = RandomChoice(new string[] { "mortar", "laser", "droid" });
            do {
                secondary = RandomChoice(new string[] { "mortar", "laser", "droid" });
            } while (primary.Equals(secondary));
            connection.SendLoadout(primary, secondary);
        }
        public void OnGamestate(long TurnNumber, Map MapData, PlayerData[] players) {
            foreach(PlayerData player in players){
                /*Console.WriteLine(
                    "player {0} has {1} life, {2} score, is at position {3},{4}, has a {5} lvl {6} and a {7} lvl {8}",
                    player.name, player.health, player.score, player.j, player.k, player.PrimaryWeapon, player.PrimaryWeaponLevel,
                    player.SecondaryWeapon, player.SecondaryWeaponLevel);*/
            }
            if (players[0].name.Equals(BotName)) {
                connection.SendMove(RandomChoice(new string[] { "up", "down", "right-up", "right-down", "left-up", "left-down" }));
                connection.SendMove(RandomChoice(new string[] { "up", "down", "right-up", "right-down", "left-up", "left-down" }));
                // mortar, laser, droid, mine, upgrade
                switch (rnd.Next(5)) { 
                    case 0:
                        RandomMortar();
                        break;
                    case 1:
                        RandomLaser();
                        break;
                    case 2:
                        RandomDroid();
                        break;
                    case 3:
                        Console.WriteLine("RANDOM MINE");
                        connection.Mine();
                        break;
                    case 4:
                        Console.WriteLine("RANDOM UPGRADE");
                        connection.Upgrade(RandomChoice(new string[]{primary, secondary}));
                        break;
                }
            }
        }
        public void OnError(string errormessage) {
            Console.WriteLine("Got error: " + errormessage);
        }
        public void RandomMortar() {
            long j, k;
            j = RandomChoice(new int[]{-4, -3, -2, -1, 1, 2, 3, 4});
            k = RandomChoice(new int[]{-4, -3, -2, -1, 1, 2, 3, 4});
            connection.AttackMortar(j, k);
            Console.WriteLine("RANDOM MORTAR");
        }
        public void RandomDroid() {
            var list = new List<string>();
            for (int i = 0; i < 5; i++) {
                list.Add(RandomChoice(new string[]{"up", "down", "right-up", "right-down", "left-up", "left-down"}));
            }
            connection.AttackDroid(list.ToArray());
            Console.WriteLine("RANDOM DROID");
        }
        public void RandomLaser() {
            connection.AttackLaser(RandomChoice(new string[]{"up", "down", "right-up", "right-down", "left-up", "left-down"}));
            Console.WriteLine("RANDOM LASER");
        }
    }
}
