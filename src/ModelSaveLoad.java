import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ModelSaveLoad {
    class PlayerData {
        public String name;
        public Object[] countryIDs;
        public int[] countryTroops;
        public boolean isAi;

        public PlayerData(String name, Object[] countryIDs, int[] countryTroops, boolean isAi) {
            this.name = name;
            this.countryIDs = countryIDs;
            this.countryTroops = countryTroops;
            this.isAi = isAi;
        }
    }
    public PlayerData[] players;




    public void modelSave(RiskModel model){
        players = new PlayerData[model.players.length];

        for (int x = 0; x < players.length; x ++){
            int [] troops = new int [model.players[x].countryIndexes.size()];
            for(int i = 0; i < troops.length; i++){ troops[i] = model.getCountries()[model.players[x].countryIndexes.get(i)].getArmy();}
            players[x] = new PlayerData(model.players[x].name, model.players[x].countryIndexes.toArray(), troops, model.players[x].isAI);
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

}
