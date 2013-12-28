package skyport.message;

import java.util.ArrayList;
import java.util.List;

import skyport.game.GameMap;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.Tile;
import skyport.game.weapon.Weapon;

public class GameStateMessage extends Message {
    @SuppressWarnings("unused")
    private int turn;
    @SuppressWarnings("unused")
    private GameMap map;
    private List<PlayerMessage> players;

    private class PlayerMessage {
        @SuppressWarnings("unused")
        String name;
        @SuppressWarnings("unused")
        Point position;
        @SuppressWarnings("unused")
        int health;
        @SuppressWarnings("unused")
        int score;
        @SuppressWarnings("unused")
        Weapon primaryWeapon;
        @SuppressWarnings("unused")
        Weapon secondaryWeapon;

        public PlayerMessage(String name, Tile position, int health, int score, Weapon primaryWeapon, Weapon secondaryWeapon) {
            this.name = name;
            this.position = position.coords;
            this.health = health;
            this.score = score;
            this.primaryWeapon = primaryWeapon;
            this.secondaryWeapon = secondaryWeapon;
        }
    }

    public GameStateMessage(int turn, GameMap map, List<Player> players) {
        this.message = "gamestate";
        this.turn = turn;
        this.map = map;
        this.players = new ArrayList<>();
        for (Player player : players) {
            PlayerMessage p = new PlayerMessage(player.getName(), player.position, player.health, player.score, player.primaryWeapon, player.secondaryWeapon);
            this.players.add(p);
        }
    }
}
