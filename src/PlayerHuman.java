import java.awt.*;

public class PlayerHuman extends Player{
    protected PlayerHuman(String name, Color color, int armiesToAllocate) {
        super(name, color, false);
        this.armiesToAllocate=armiesToAllocate;
    }
}
