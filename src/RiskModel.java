public class RiskModel {
    public static void main(String[] args) {
        CommandParser cp = new CommandParser();
        Continent con = new Continent();
        Country country = new Country("Bolivia");
        Player pl = new Player("Kevin", 0);
        System.out.println(cp);
        System.out.println(con);
        System.out.println(country);
        System.out.println(pl);
    }
}
