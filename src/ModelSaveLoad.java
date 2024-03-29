import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;

/**
 * ModelSaveLoad class responsible for exporting and importing the model using json
 * @author Dimitry Koutchine
 * @version 12.04.2020
 */

public class ModelSaveLoad extends RiskModel{
    /**
     * Inner class modelData that stores all the needed info from model.
     */
    static class ModelData {
        private final ActionContext ac;
        private final PlayerData[] players;
        private final String fileName;
        public ModelData(ActionContext ac, PlayerData[] playerData, String fileName){
            this.ac = ac;
            this.players = playerData;
            this.fileName = fileName;
        }
    }
    /**
     * inner Class for saving individual player data.
     */
    static class PlayerData {
        public String name;
        public int[] countryIDs;
        public int[] countryTroops;
        public boolean isAi;
        int troopsToDeploy;

        /**
         * Constructor for PlayerData
         * @param name name of player
         * @param countryIDs array of country ids of owned countries
         * @param countryTroops array of troops stationed in country
         * @param isAi bool that determines if player is ai or not
         * @param troopsToDeploy troops player has to deploy
         */
        public PlayerData(String name, int[] countryIDs, int[] countryTroops, boolean isAi, int troopsToDeploy) {
            this.name = name;
            this.countryIDs = countryIDs;
            this.countryTroops = countryTroops;
            this.isAi = isAi;
            this.troopsToDeploy = troopsToDeploy;
        }
    }
    /**
     * modelSave extracts all the needed information from given model and converts to json format and writes to file
     * @param model The game model
     */
    public static boolean Save(RiskModel model, String filename){
        for(Player player: model.players){
            if(player == model.ac.getPlayer()){
                model.ac.setPlayerIndex(player.playerId);
            }
        }
        PlayerData[] players;
        ModelData modelData;
        players = new PlayerData[model.players.length];
        String filePath = "saves/";

        for (int x = 0; x < players.length; x ++){
            int [] indexes = new int[model.players[x].countryIndexes.size()];
            int [] troops = new int [model.players[x].countryIndexes.size()];
            for(int i = 0; i < troops.length; i++){
                troops[i] = model.getCountries()[model.players[x].countryIndexes.get(i)].getArmy();
                indexes[i] = model.players[x].countryIndexes.get(i);
            }
            players[x] = new PlayerData(model.players[x].name, indexes, troops, model.players[x].isAI, model.players[x].troopsToDeploy);
        }
        modelData = new ModelData(model.ac,players,model.map.getFilename());

        try {
            File file = new File(filePath);
            if(file.mkdir()){
                System.out.printf("Directory made\n");
            }
            Writer writer = new FileWriter(filePath+filename+".RiskGame");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(modelData,writer);
            writer.flush();
            writer.close();
            return  true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * reads file and converts the json data gathered into a model
     */
    public static void Load(RiskModel riskModel, String filename){
        ModelData modelData;
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
            Reader reader = new FileReader(filename);
            modelData = new Gson().fromJson(reader, ModelData.class);
            reader.close();
            riskModel.ac=modelData.ac;
            Map.Import(modelData.fileName);
            riskModel.players = new Player[modelData.players.length];
            riskModel.numPlayers = modelData.players.length;
            for (int x = 0; x < modelData.players.length; x++){

                if (modelData.players[x].isAi) {
                    riskModel.players[x] = new PlayerAI(modelData.players[x].name, playerColors[x],0, x, riskModel.map);
                    numAi ++;
                }
                else{
                    riskModel.players[x] = new PlayerHuman(modelData.players[x].name, playerColors[x],0, x, riskModel.map);
                    numHumans ++;
                }
                riskModel.players[x].troopsToDeploy = modelData.players[x].troopsToDeploy;
                for(int i = 0; i < modelData.players[x].countryIDs.length; i++){
                    riskModel.players[x].countryIndexes.add(modelData.players[x].countryIDs[i]);
                    riskModel.getCountries()[modelData.players[x].countryIDs[i]].setArmy(modelData.players[x].countryTroops[i]);
                    riskModel.getCountries()[modelData.players[x].countryIDs[i]].setOwner(riskModel.players[x]);
                }

            }
            riskModel.numHumans = numHumans;
            riskModel.numAI = numAi;
            for(Player player: riskModel.players){
                if (player.playerId == riskModel.ac.getPlayerIndex()){
                    riskModel.ac.setPlayer(player);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * a method that returns every save file in the saves directory
     * @return returns an array of file names
     */
    public static String[] getSaves(){
        String[] saves;
        File savesDirectory = new File("saves/");
        saves = savesDirectory.list();
        return saves;
    }

}
