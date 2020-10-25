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
    public Country countrySrc;
    /** The destination country the command will act on*/
    public Country countryDst;
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
    Command(CommandCode commandCode, Country countrySrc, Country countryDst, int numTroops){
        this.commandCode=commandCode;
        this.countrySrc=countrySrc;
        this.countryDst=countryDst;
        this.numTroops=numTroops;
    }
    public void print(){
        if(commandCode!=null)
            System.out.printf("Enum: %s, Src: %s, Dst: %s, Num: %d\n",commandCode.toString(),countrySrc,countryDst,numTroops);
        else
            System.out.printf("fuck");
    }
}