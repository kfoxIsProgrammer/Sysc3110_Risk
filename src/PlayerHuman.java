import java.awt.*;

public class PlayerHuman extends Player{
    protected PlayerHuman(String name, Color color, int armiesToAllocate, int playerId) {
        super(name, color, false,playerId);
        this.troopsToDeploy =armiesToAllocate;
    }
}
