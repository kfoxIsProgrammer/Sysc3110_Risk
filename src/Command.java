/**
 * Command container which contains all vital information about the action to be performed.
 *
 * @author Omar Hashmi
 * @version 10.24.2020
 */
public class Command {
    /** The type of command*/
    public CommandCode commandCode;
    /** The source country the command will act on*/
    public String countrySrc;
    /** The destination country the command will act on*/
    public String countryDst;
    /** The number of troops involved in the operation*/
    public int numTroops;

    /**
     * Packages all command information into a Command object
     *
     * @param commandCode The type of command
     * @param countrySrc The source country the command will act on
     * @param countryDst The destination country the command will act on
     * @param numTroops The number of troops involved in the operation
     */
    Command(CommandCode commandCode, String countrySrc, String countryDst, int numTroops){
        this.commandCode=commandCode;
        this.countrySrc=countrySrc;
        this.countryDst=countryDst;
        this.numTroops=numTroops;
    }
}