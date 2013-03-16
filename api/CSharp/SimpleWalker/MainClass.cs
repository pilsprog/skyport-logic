using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Skyport;

namespace SimpleWalker
{
    class MainClass
    {
        static void Main(string[] args)
        {
            SimpleWalkerBot bot = new SimpleWalkerBot();
            SkyportConnection conn = new SkyportConnection("localhost", 54321, bot);
            conn.Run();

            Console.WriteLine("finished");
            Console.ReadKey();
        }
    }
}
