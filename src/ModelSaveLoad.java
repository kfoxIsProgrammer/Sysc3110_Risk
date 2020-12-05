import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
/**
 * ModelSaveLoad class responsible for exporting and importing the model using json
 * @author Dimitry Koutchine
 * @version 12.04.2020
 */

public class ModelSaveLoad {


    /**
     * Inner class PlayerData that stores all the needed info from model.
     */
    static class PlayerData {
        public String name;
        public int[] countryIDs;
        public int[] countryTroops;
        public boolean isAi;
        public ActionContext actionContext;
        int troopsToDeploy;

        /**
         * Constructor for PlayerData
         * @param name name of player
         * @param countryIDs array of country ids of owned countries
         * @param countryTroops array of troops stationed in country
         * @param isAi bool that determines if player is ai or not
         * @param actionContext action context of the game
         * @param troopsToDeploy troops player has to deploy
         */
        public PlayerData(String name, int[] countryIDs, int[] countryTroops, boolean isAi, ActionContext actionContext,int troopsToDeploy) {
            this.name = name;
            this.countryIDs = countryIDs;
            this.countryTroops = countryTroops;
            this.isAi = isAi;
            this.actionContext = actionContext;
            this.troopsToDeploy = troopsToDeploy;
        }
    }



    /**
     * modelSave extracts all the needed information from given model and converts to json format and writes to file
     * @param model The game model
     */
    public static void modelSave(RiskModel model){
        PlayerData[] players;
        players = new PlayerData[model.players.length];

        for (int x = 0; x < players.length; x ++){
            int [] indexes = new int[model.players[x].countryIndexes.size()];
            int [] troops = new int [model.players[x].countryIndexes.size()];
            for(int i = 0; i < troops.length; i++){
                troops[i] = model.getCountries()[model.players[x].countryIndexes.get(i)].getArmy();
                indexes[i] = model.players[x].countryIndexes.get(i);
            }
            players[x] = new PlayerData(model.players[x].name, indexes, troops, model.players[x].isAI, model.actionContext, model.players[x].troopsToDeploy);
        }
        try {
            Writer writer = new FileWriter("Save.txt");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(players,writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * reads file and converts the json data gathered into a model
     * @return the model constructed.
     */
    public static void modelLoad(RiskModel riskModel){
        PlayerData[] players;
        Color[] playerColors={
                new Color(255, 102, 0),
                new Color(81, 119, 241),
                new Color(255, 0  , 0),
                new Color(0  , 255, 0),
                new Color(255, 0, 255),
                new Color(0, 255, 255)
        };


        try {
            int numHumans = 0;
            int numAi = 0;
            Reader reader = new FileReader("Save.txt");
            players = new Gson().fromJson(reader, PlayerData[].class);
            reader.close();
            riskModel.actionContext =players[players.length-1].actionContext;
            riskModel.players = new Player[players.length];
            riskModel.numPlayers = players.length;
            for (int x = 0; x < players.length; x++){

                if (players[x].isAi) {
                    riskModel.players[x] = new PlayerAI(players[x].name, playerColors[x],0, x, riskModel.map);
                    numAi ++;
                }
                else{
                    riskModel.players[x] = new PlayerHuman(players[x].name, playerColors[x],0, x, riskModel.map);
                    numHumans ++;
                }
                riskModel.players[x].troopsToDeploy = players[x].troopsToDeploy;
                for(int i = 0; i < players[x].countryIDs.length; i++){
                    riskModel.players[x].countryIndexes.add((Integer) players[x].countryIDs[i]);
                    riskModel.getCountries()[players[x].countryIDs[i]].setArmy(players[x].countryTroops[i]);
                    riskModel.getCountries()[players[x].countryIDs[i]].setOwner(riskModel.players[x]);
                }

            }
            riskModel.numHumans = numHumans;
            riskModel.numAI = numAi;
            for(Player player: riskModel.players){
                if (player.playerId == riskModel.actionContext.getPlayerId()){
                    riskModel.actionContext.setPlayer(player);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
