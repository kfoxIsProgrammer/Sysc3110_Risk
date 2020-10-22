import java.util.ArrayList;

public class RiskModel {
    public static void main(String[] args) {
        CommandParser cp = new CommandParser();
        Country country = new Country("Canada");
        ArrayList<Country> countrylist = new ArrayList<>();
        countrylist.add(country);
        Continent con = new Continent("North America", countrylist,69);
        Player pl = new Player("Kevin", 0);
        System.out.println(cp);
        System.out.println(con);
        System.out.println(country);
        System.out.println(pl);
    }
}
