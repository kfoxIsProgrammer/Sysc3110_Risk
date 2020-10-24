import java.util.ArrayList;
import java.util.Scanner;

public class CommandParser {
    private ArrayList<Country> countries;
    private ArrayList<String> countryNames;

    CommandParser(ArrayList<Country> countries){
        this.countries=countries;
        this.countryNames=new ArrayList<>();

        for(Country country : this.countries){
            this.countryNames.add(country.getName());
        }
    }

    private void showFullMap(){
        System.out.printf("Here are all the countries and their army sizes:\n");
        System.out.printf("<-------------------------------------------------------------->\n");
        for(Country country : this.countries){
            System.out.printf("\t%s:\t\t%d\n",country.getName(),country.getArmy());
        }
        System.out.printf("<-------------------------------------------------------------->\n");
    }
    private void showFocusMap(Player player){
        System.out.printf("Here are all your countries, their army sizes, and neighboring army sizes:\n");
        System.out.printf("<-------------------------------------------------------------->\n");
        for(Country country : this.countries){
            System.out.printf("\t%s:\t\t%d\n\t\t",country.getName(),country.getArmy());
            for(Country neighbor : country.getAdjancentCountries()){
                if(!neighbor.getOwner().equals(player)){
                    System.out.printf("%d ",neighbor.getArmy());
                }
            }
            System.out.printf("\n\n");
        }
        System.out.printf("<-------------------------------------------------------------->\n");
    }
    private void showNeighbors(Player player, Country country){
        System.out.printf("You have %d troops at %s",country.getArmy(),country.getName());
        System.out.printf("Your neighbors are:\n");
        for(Country neighbor : country.getAdjancentCountries()){
            if(!neighbor.getOwner().equals(player)){
                System.out.printf("\t%s: %d",neighbor.getName(),neighbor.getArmy());
            }
        }
        System.out.printf("\n\n");
    }
    private boolean isInt(String s) {
        for(int i=0; i<s.length(); i++) {
            if(Character.digit(s.charAt(i),10) < 0) {
                return false;
            }
        }
        return true;
    }
    private Country stringToCountry(String countryName){
        for(Country country : this.countries){
            if(country.getName().equals(countryName)){
                return country;
            }
        }
        return null;
    }
    private Command getInput(){
        CommandCode commandCode=null;
        String countrySrc=null;
        String countryDst=null;
        int numTroops=0;

        Scanner scanner=new Scanner(System.in);
        String[] input=scanner.nextLine().split(" ");

        /*1
        *   skip
        *   back
        *   help
        *
        * 2
        *   focus [country]
        *   attack [country]
        *
        * 3
        *   Show Full Map
        *   Show Focus Map
        *
        * 4
        *   Send [numTroops] to [country]
        *
        * 6
        *   transfer [numTroops] from [countrySrc] to [countryDst]
        *
        */

        for(String word : input){
            word=word.toLowerCase();
        }

        if(input.length==1){
            if(input[0].equals("skip")){
                commandCode=CommandCode.SKIP;
            }
            else if(input[0].equals("back")){
                commandCode=CommandCode._BACK;
            }
            else if(input[0].equals("help")){
                commandCode=CommandCode._HELP;
            }
        }
        else if(input.length==2){
            if(input[0].equals("focus")&&
               this.countryNames.contains(input[1])){
                commandCode=CommandCode._FOCUS;
                countrySrc=input[1];
            }
            else if(input[0].equals("attack")&&
                     this.countryNames.contains(input[1])){
                commandCode=CommandCode.ATTACK;
                countrySrc=input[1];
            }
        }
        else if(input.length==3){
            if(input[0].equals("show")&&
               input[1].equals("full")&&
               input[2].equals("map")){
                commandCode=CommandCode._FULL_MAP;
            }
            else if(input[0].equals("show")&&
                     input[1].equals("focus")&&
                     input[2].equals("map")) {
                commandCode = CommandCode._FOCUS_MAP;
            }
        }
        else if(input.length==4){
            if(input[0].equals("send")&&
               this.isInt(input[1])&&
               input[2].equals("to")&&
                    this.countryNames.contains(input[3])){
                commandCode=CommandCode.DEPLOY;
                numTroops=Integer.parseInt(input[1]);
                countryDst=input[3];
            }
        }
        else if(input.length==6){
            if(input[0].equals("transfer")&&
               this.isInt(input[1])&&
               input[2].equals("from")&&
               this.countryNames.contains(input[3])&&
               input[4].equals("to")&&
               this.countryNames.contains(input[5])){
                commandCode=CommandCode.FORTIFY;
                numTroops=Integer.parseInt(input[1]);
                countrySrc=input[3];
                countryDst=input[5];
            }
        }
        return new Command(commandCode,countrySrc,countryDst,numTroops);
    }
    private void infoDisplay(){

    }
    public Command Deploy(){
        return getInput();
    }
    public Command Attack(Player player){
        Command command;

        this.showFocusMap(player);
        System.out.printf("What do you want to do?\n\n");
        command = getInput();

        while(true){
            if(command.commandCode==CommandCode.SKIP){
                break;
            }
            else if(command.commandCode==CommandCode._FOCUS){
                Country country=this.stringToCountry(command.countrySrc);

                this.showNeighbors(player,country);

                System.out.printf("What do you want to do?\n\n");
                command=getInput();

                if(command.commandCode==CommandCode._BACK){
                    System.out.printf("Back at the attack menu. What do you want to do?\n\n");
                    command=getInput();
                }
                else if(command.commandCode==CommandCode.ATTACK){
                    break;
                }
                else{
                    System.out.printf("That command is invalid. For a list of commands type 'help'. What do you want to do?\n\n");
                    command=getInput();
                }
            }
            else{
                System.out.printf("That command is invalid. For a list of commands type 'help'. What do you want to do?\n\n");
                command=getInput();
            }
        }

        return command;
    }
    public Command Fortify(){
        return getInput();
    }
}