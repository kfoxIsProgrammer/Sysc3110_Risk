import java.awt.*;

/**
 * Player class for human risk players
 * Extends the Player Abstract class
 * @author Omar Hashmi
 * @version 12-09-2020
 */
public class PlayerHuman extends Player{
    /**
     * 5 param constructor
     * @param name the name of the player
     * @param color the color of the player
     * @param armiesToAllocate the armies to deploy
     * @param playerId the id of the player
     * @param map the map of the current game
     */
    protected PlayerHuman(String name, Color color, int armiesToAllocate, int playerId,Map map) {
        super(name, color, false,playerId, map);
        this.troopsToDeploy =armiesToAllocate;
    }
}
