public class Command {
    public CommandCode commandCode;
    public String countrySrc;
    public String countryDst;
    public int numTroops;

    Command(CommandCode commandCode, String countrySrc, String countryDst, int numTroops){
        this.commandCode=commandCode;
        this.countrySrc=countrySrc;
        this.countryDst=countryDst;
        this.numTroops=numTroops;
    }
    Command(CommandCode commandCode, String countrySrc, int numTroops){
        this.commandCode=commandCode;
        this.countrySrc=countrySrc;
        this.countryDst=null;
        this.numTroops=numTroops;
    }
    Command(){
        //DELETE ME
    }
}