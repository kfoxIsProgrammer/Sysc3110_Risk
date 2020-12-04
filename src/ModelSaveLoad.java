import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

public class ModelSaveLoad {
    class PlayerData {
        public String name;
        public int[] countryIDs;
        public int[] countryTroops;
        public boolean isAi;
        public ActionContext actionContext;
        int troopsToDeploy;

        public PlayerData(String name, int[] countryIDs, int[] countryTroops, boolean isAi, ActionContext actionContext,int troopsToDeploy) {
            this.name = name;
            this.countryIDs = countryIDs;
            this.countryTroops = countryTroops;
            this.isAi = isAi;
            this.actionContext = actionContext;
            this.troopsToDeploy = troopsToDeploy;
        }
    }
    public PlayerData[] players;




    public void modelSave(RiskModel model){
        players = new PlayerData[model.players.length];

        for (int x = 0; x < players.length; x ++){
            int [] indexes = new int[model.players[x].countryIndexes.size()];
            int [] troops = new int [model.players[x].countryIndexes.size()];
            for(int i = 0; i < troops.length; i++){ troops[i] = model.getCountries()[model.players[x].countryIndexes.get(i)].getArmy();}
            for(int i = 0; i < indexes.length; i++ ){indexes[i] = model.players[x].countryIndexes.get(i);};

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
    public RiskModel modelLoad(){
        RiskModel importedModel = new RiskModel();
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
            importedModel.actionContext =players[players.length-1].actionContext;
            importedModel.players = new Player[players.length];
            importedModel.numPlayers = players.length;
            for (int x = 0; x < players.length; x++){

                if (players[x].isAi) {
                    importedModel.players[x] = new PlayerAI(players[x].name, playerColors[x],0, x, importedModel.map);
                    numAi ++;
                }
                else{
                    importedModel.players[x] = new PlayerHuman(players[x].name, playerColors[x],0, x, importedModel.map);
                    numHumans ++;
                }
                importedModel.players[x].troopsToDeploy = players[x].troopsToDeploy;
                for(int i = 0; i < players[x].countryIDs.length; i++){
                    importedModel.players[x].countryIndexes.add((Integer) players[x].countryIDs[i]);
                    importedModel.getCountries()[players[x].countryIDs[i]].setArmy(players[x].countryTroops[i]);
                    importedModel.getCountries()[players[x].countryIDs[i]].setOwner(importedModel.players[x]);
                }

            }
            importedModel.numHumans = numHumans;
            importedModel.numAI = numAi;
            for(Player player: importedModel.players){
                if (player.playerId == importedModel.actionContext.getPlayerId()){
                    importedModel.actionContext.setPlayer(player);
                }
            }
            return importedModel;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;


    }

}
