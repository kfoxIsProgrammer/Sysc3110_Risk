import java.awt.*;

public class PlayerHuman extends Player{
    protected PlayerHuman(String name, Color color, int armiesToAllocate, int playerId,Map map) {
        super(name, color, false,playerId, map);
        this.troopsToDeploy =armiesToAllocate;
    }
}
