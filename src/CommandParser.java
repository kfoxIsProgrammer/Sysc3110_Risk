import java.util.ArrayList;
import java.util.Scanner;

/**
 * Prompts the user for input, then parses the input into a Command object for the RiskModel to use
 *
 * @author Omar Hashmi
 * @version 10.24.2020
 */
public class CommandParser {
    /** List of Country objects*/
    private ArrayList<Country> countries;
    /** List of the Country names*/
    private ArrayList<String> countryNames;

    /**
     * Initializes the command parser and stores relevant information in the object
     *
     * @param countries A list of countries
     */
    CommandParser(ArrayList<Country> countries){
        this.countries=countries;
        this.countryNames=new ArrayList<>();

        for(Country country : this.countries){
            this.countryNames.add(country.getName());
        }
    }

    /**
     * Prints a list of all countries and their army sizes
     */
    private void showFullMap(){
        System.out.printf("Here are all the countries and their army sizes:\n");
        System.out.printf("<-------------------------------------------------------------->\n");
        for(Country country : this.countries){
            System.out.printf("\t%s:\t\t%d\n",country.getName(),country.getArmy());
        }
        System.out.printf("<-------------------------------------------------------------->\n");
    }

    /**
     * Prints a list of the countries owned by the player and the size of its neighbors armies
     *
     * @param player The player who's turn it is
     */
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

    /**
     * Prints a list of the neighbors of a country and their army sizes
     *
     * @param player The player who's turn it is
     * @param country The country the player is focused on
     */
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

    /**
     * Checks if a string contains an integer
     *
     * @param s The string to be checked
     * @return true if the string is an int, false if it is not
     */
    private boolean isInt(String s) {
        for(int i=0; i<s.length(); i++) {
            if(Character.digit(s.charAt(i),10) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds and returns a country object when given it's name
     *
     * @param countryName The name of the country to be found
     * @return A country object that corresponds to the name given
     */
    private Country stringToCountry(String countryName){
        for(Country country : this.countries){
            if(country.getName().equals(countryName)){
                return country;
            }
        }
        return null;
    }

    /**
     * Gets input from the user and converts it into a Command object
     *
     * @return A command object that reflects the user's input
     */
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
            if ("skip".equals(input[0])) {
                commandCode = CommandCode.SKIP;
            } else if ("back".equals(input[0])) {
                commandCode = CommandCode._BACK;
            } else if ("help".equals(input[0])) {
                commandCode = CommandCode._HELP;
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

    /**
     * Asks the player what they want to do during the deploy phase then returns their command
     *
     * @return A Command object containing the users move
     */
    public Command Deploy(){
        return getInput();
    }
    /**
     * Asks the player what they want to do during the attack phase then returns their command
     *
     * @return A Command object containing the users move
     */
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
    /**
     * Asks the player what they want to do during the fortify phase then returns their command
     *
     * @return A Command object containing the users move
     */
    public Command Fortify(){
        return getInput();
    }

    /**
     * Give the outcome of a battle to the parser
     * @param outcome the object containing battle outcomes
     */
    public void sendBattleObject(BattleObject outcome);

    /**
     * Get the number of units to send back once an attacker wins
     * @return the number of units between 0 - finalAttackers -1
     */
    public int getNumberOfUnitsToSendAfterAttackerWin();



}