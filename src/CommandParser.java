import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Prompts the user for input, then parses the input into a Command object for the RiskModel to use
 *
 * @author Omar Hashmi, Kevin Fox
 * @version 10.25.2020
 */
public class CommandParser {
    /** List of Country objects*/
    private ArrayList<Country> countries;
    /** List of the Country names*/
    private ArrayList<String> countryNames;
    /** List of Country names that are more than one word*/
    private String longCountryNames;

    /**
     * Initializes the command parser and stores relevant information in the object
     *
     * @param countries A list of countries
     */
    CommandParser(ArrayList<Country> countries){
        this.countries=countries;
        this.countryNames=new ArrayList<>();
        this.longCountryNames="";

        for(Country country : this.countries) {
            String lowercaseCountryName=country.getName().toLowerCase();
            this.countryNames.add(lowercaseCountryName);

            if(lowercaseCountryName.split(" ").length>1){
                this.longCountryNames+=lowercaseCountryName+",";
            }
        }
    }

    /**
     * Prints a list of all countries and their army sizes
     */
    private void showFullMap(){
        System.out.printf("Here are all the countries and their army sizes:\n");
        System.out.printf("<---------------------------------------------------------------------------------------------->\n");
        for(Country country : this.countries){
            System.out.printf("\t%s:\t\t%d\n",country.getName(),country.getArmy());
        }
        System.out.printf("<---------------------------------------------------------------------------------------------->\n");
    }

    /**
     * Prints a list of the countries owned by the player and the size of its neighbors armies
     *
     * @param player The player who's turn it is
     */
    private void showFocusMap(Player player){
        System.out.printf("Here are all your countries, their army sizes, and neighboring army sizes:\n");
        System.out.printf("<---------------------------------------------------------------------------------------------->\n");
        for(Country country : player.getOwnedCountries().values()){
            System.out.printf("\t%s: %d\n\t\t",country.getName(),country.getArmy());
            for(Country neighbor : country.getAdjacentCountries()){
                if(!neighbor.getOwner().equals(player)){
                    System.out.printf("%d ",neighbor.getArmy());
                }
            }
            System.out.printf("\n");
        }
        System.out.printf("<---------------------------------------------------------------------------------------------->\n");
    }

    /**
     * Prints a list of the neighbors of a country and their army sizes
     *
     * @param player The player who's turn it is
     * @param country The country the player is focused on
     */
    private void showNeighbors(Player player, Country country){
        System.out.printf("You have %d usable troops at %s\n",country.getArmy()-1,country.getName());
        System.out.printf("Your neighbors are:\n");
        for(Country neighbor : country.getAdjacentCountries()){
            if(!neighbor.getOwner().equals(player)){
                System.out.printf("\t%s: %d\n",neighbor.getName(),neighbor.getArmy());
            }
        }
        System.out.printf("\n");
    }

    /**
     * Prints a list of commands
     */
    private void showHelp(){
        System.out.printf("Here is the command list:\n" +
                "<---------------------------------GENERAL---------------------------------->\n" +
                "\tShow Full Map\n" +
                "\t\tDisplay a list of all the countries and the size of their army\n" +
                "\tShow Focus Map\n" +
                "\t\tDisplay a list of your countries, and the army size of neighbors\n" +
                "\tSkip\n" +
                "\t\tEnds the current phase\n" +
                "<-------------------------------ATTACK PHASE------------------------------->\n" +
                "\tFocus [COUNTRY]\n" +
                "\t\tLets you select a country to attack from\n" +
                "\t(When a country is selected) attack [COUNTRY] [NUM TROOPS]\n" +
                "\t\tSend a number of troops to attack a neighboring country\n" +
                "\t(When a country is selected) Back\n" +
                "\t\tDeselects the selected country\n");
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
     * Takes a string of two countries and gets the country object
     * @param s string array
     * @return string Array of country name
     */
    private String[] longCountryToString(String[]  s){
        String processedOutputString="";
        String[] processedOutputArray;
        List<String> longCountryNamesArray= Arrays.asList(this.longCountryNames.split(","));

        for(int i=0;i<s.length;i++){
            if (this.longCountryNames.contains(s[i])){
                for(int j=0;j<longCountryNamesArray.size();j++){
                    String[] splitNames=longCountryNamesArray.get(j).split(" ");
                    if(splitNames.length==2&&
                       s.length-i>=2&&
                       splitNames[0].equals(s[ i ])&&
                       splitNames[1].equals(s[i+1])){
                        processedOutputString+=s[i]+" "+s[i+1]+",";
                        break;
                    }
                    else if(splitNames.length==3&&
                            s.length-i>=3&&
                            splitNames[0].equals(s[ i ])&&
                            splitNames[1].equals(s[i+1])&&
                            splitNames[2].equals(s[i+2])){
                        processedOutputString+=s[i]+" "+s[i+1]+" "+s[i+2]+",";
                        break;
                    }
                }
            }
            else{
                processedOutputString+=s[i]+",";
            }
        }
        processedOutputArray=processedOutputString.split(",");

        return processedOutputArray;
    }

    /**
     * Finds and returns a country object when given it's name
     *
     * @param countryName The name of the country to be found
     * @return A country object that corresponds to the name given
     */
    private Country stringToCountry(String countryName){
        for(Country country : this.countries){
            if(country.getName().toLowerCase().equals(countryName)){
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
    private Command getInput(Command command){


        Scanner scanner=new Scanner(System.in);
        String[] input=longCountryToString(scanner.nextLine().toLowerCase().split(" "));

        if(input.length==1){
            if (input[0].equals("skip")) {
                command.commandCode = CommandCode.SKIP;
            } else if (input[0].equals("back")) {
                command.commandCode = CommandCode._BACK;
            } else if (input[0].equals("help")) {
                command.commandCode = CommandCode._HELP;
            }
        }
        else if(input.length==2){
            if(input[0].equals("focus")&&
               this.countryNames.contains(input[1])){
                command.commandCode=CommandCode._FOCUS;
                command.countrySrc=stringToCountry(input[1]);
            }
        }
        else if(input.length==3){
            if(input[0].equals("show")&&
               input[1].equals("full")&&
               input[2].equals("map")){
                command.commandCode=CommandCode._FULL_MAP;
            }
            else if(input[0].equals("show")&&
                     input[1].equals("focus")&&
                     input[2].equals("map")) {
                command.commandCode = CommandCode._FOCUS_MAP;
            }
            else if(input[0].equals("attack")&&
                    this.countryNames.contains(input[1])&&
                    isInt(input[2])){
                command.commandCode=CommandCode.ATTACK;
                command.countryDst=stringToCountry(input[1]);
                command.numTroops=Integer.parseInt(input[2]);
            }
        }
        else if(input.length==4){
            if(input[0].equals("send")&&
               this.isInt(input[1])&&
               input[2].equals("to")&&
               this.countryNames.contains(input[3])){
               command.commandCode=CommandCode.DEPLOY;
               command.numTroops=Integer.parseInt(input[1]);
               command.countryDst=stringToCountry(input[3]);
            }
        }
        else if(input.length==6){
            if(input[0].equals("transfer")&&
               this.isInt(input[1])&&
               input[2].equals("from")&&
               this.countryNames.contains(input[3])&&
               input[4].equals("to")&&
               this.countryNames.contains(input[5])){
                command.commandCode=CommandCode.FORTIFY;
                command.numTroops=Integer.parseInt(input[1]);
                command.countrySrc=stringToCountry(input[3]);
                command.countryDst=stringToCountry(input[5]);
            }
        }

        //command.print();

        return command;
    }

    /**
     * Checks if the player has called a global command
     *
     * @param command The command to be checked against the global command list
     * @param player The player who's turn it is
     * @return true if an exit condition is met, false otherwise
     */
    private boolean globalCommands(Command command,Player player){
        if(command.commandCode==CommandCode._HELP){
            showHelp();
        }
        else if(command.commandCode==CommandCode._FULL_MAP){
            showFullMap();
        }
        else if(command.commandCode==CommandCode._FOCUS_MAP){
            showFocusMap(player);
        }
        else if(command.commandCode==CommandCode.SKIP){
            return true;
        }
        return false;
    }

    /**
     * Asks the player what they want to do during the deploy phase then returns their command
     *
     * @return A Command object containing the users move
     */
    public Command Deploy(Player player){
        Command command = new Command();

        while(true){
            System.out.printf("%s, what do you want to do?\n\n",player.getName());
            this.showCommands();
            getInput(command);

            if(globalCommands(command, player)){
                break;
            }
            else if(command.commandCode==CommandCode.DEPLOY){
                break;
            }
            else{
                System.out.printf("That command is invalid. For a list of commands type 'help'. ");
            }
        }

        return command;
    }

    /**
     * Helper function to print available commands short hand info
     */
    private void showCommands() {
        System.out.printf("%s",
                "List of Commands\n"+
                "1.Show Full Map\n" +
                "2.Show Focus Map\n" +
                "3.Skip\n" +
                "4.Focus [COUNTRY]\n" +
                        "5.help\n");


    }

    /**
     * Asks the player what they want to do during the attack phase then returns their command
     *
     * @return A Command object containing the users move
     */
    public Command Attack(Player player){
        Command command = new Command();

      // this.showFocusMap(player);
       System.out.println();
       this.showCommands();

        while(true){
            command = new Command();
            System.out.printf("%s, what do you want to do?\n\n",player.getName());
            getInput(command);

            if(globalCommands(command, player)){
                break;
            }
            else if(command.commandCode==CommandCode._FOCUS){
                this.showNeighbors(player,command.countrySrc);

                getInput(command);

                if(command.commandCode==CommandCode._BACK){
                    System.out.printf("Back at the attack menu. \n");
                    continue;
                }
                else if(command.commandCode==CommandCode.ATTACK){
                    break;
                }
            }
            else {
                boolean notCommand = true;
                for(CommandCode commandCode: CommandCode.values()){
                    if(command.commandCode == commandCode){
                        notCommand = false;
                    }
                }
                if(notCommand)
                System.out.printf("That command is invalid. For a list of commands type 'help'. ");
            }
        }

        return command;
    }

    /**
     * Asks the player what they want to do during the fortify phase then returns their command
     *
     * @return A Command object containing the users move
     */
    public Command Fortify(Player player){
        Command command = new Command();

        this.showFocusMap(player);

        while(true){
            System.out.printf("%s, what do you want to do?\n\n",player.getName());
            getInput(command);

            if(globalCommands(command, player)){
                break;
            }
            else if(command.commandCode==CommandCode.FORTIFY){
                break;
            }
            else{
                System.out.printf("That command is invalid. For a list of commands type 'help'. ");
            }
        }

        return command;
    }

    /**
     * Give the outcome of a battle to the parser
     * @param outcome the object containing battle outcomes
     * @return
     */
    public int battleOutcome(BattleContext outcome){

        System.out.println("Country: "+ outcome.getAttackingCountry().getName() +(outcome.didAttackerWin()? " Won": " Lost"));
        System.out.println("Attacker start "+outcome.getAttackingCountry().getName() +" ->" + outcome.getInitialAttackers()+" final-> "+outcome.getFinalAttackingArmy());
        System.out.println("Defender start "+outcome.getDefendingCountry().getName() +" ->" + outcome.getInitialDefenders()+" final-> "+outcome.getFinalDefendingArmy());
        System.out.println();

        if(outcome.didAttackerWin()){

            if(outcome.getFinalAttackingArmy() > 1){
                Scanner scanner=new Scanner(System.in);
                System.out.println("Please Enter how many troops to send back to "+outcome.getAttackingCountry().getName());
                System.out.println("(0 - "+(outcome.getFinalAttackingArmy()-1)+")");
                return scanner.nextInt();
            }
        }
        return 0;
    }

    /**
     * This prints out when a player has met a lost condition
     * @param player the player that has lost
     * @param event why they lost
     */
    public void playerHasLost(Player player, String event){
        System.out.println("Player: "+ player.getName()+" has lost because "+event);
    }

    /**
     * Print final report of the game when it is over
     * @param listOfAllPlayers the list of all players
     * @param winner the PLayer that won
     */
    public void gameIsOver(ArrayList<Player> listOfAllPlayers, Player winner){
        System.out.println("Congratulations "+ winner.getName()+"! You have won!");
        System.out.println("Final Game tallies");

        System.out.printf("%s %17s %18s\n","Player name","#Owned Countries", "Status");

        for(Player player: listOfAllPlayers){
            if(player == winner)
                System.out.printf("%s %17d %18s\n",player.getName(),player.getOwnedCountries().size(), "Won");
            else
                System.out.printf("%s %17d %18s\n",player.getName(),player.getOwnedCountries().size(), "Lost");
        }

    }
}