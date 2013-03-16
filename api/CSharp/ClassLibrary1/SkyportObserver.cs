using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Skyport
{
    public interface SkyportObserver
    {

        /**
         * Called when the SkyportConnection
         * is first established. You can keep the
         * object reference for later calls.
         */
        void OnConnectionEstablished(SkyportConnection connection);

        /**
        * Called when the server accepted
        * your handshake.
        */
        void OnHandshakeSuccessful();


        /**
         * Called whenever the server sent
         * an error.
         *
         * @param errorMessage  The error message as sring
         */
        void OnError(string errorMessage);


        /**
         * Called when the initial gamestate
         * (called GAMESTART) with round number
         * 0 arrives.
         *
         * @param mapObject    Contains all information
         * about the board, resources etc.
         */
        void OnGamestart(Map mapObject);


        /**
         * Called whenever a gamestate arrives.
         *
         * @param turnNumber The number of the current turn 
         *
         * @param mapObject Contains all information
         * about the board, resources etc.
         *
         * @param playersOnBattlefield  Contains a list
         * of PlayerData objects that contain
         * information about the players in the game.
         */
        void OnGamestate(long turnNumber, Map mapObject, PlayerData[] playersOnBattlefield);


        /**
         * Called whenever a player performed an action.
         *
         * @param performingPlayerName The name of the player who performed the action
         *
         * @param type the type of the action (e.g. "laser", "move", ...)
         * 
         * @param arguments the rest of the arguments of the action as dictionary
         */
        void OnAction(string performingPlayerName, string type, Dictionary<string, object> arguments);


        /**
         * Called whenever a turn has ended.
         */
        void OnEndturn();
    }
}
