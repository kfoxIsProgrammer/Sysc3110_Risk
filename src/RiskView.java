public interface RiskView {
    void update(ActionContext ac);
    void log(String message);
    void updateSaveFileList(String[] saves);
    void updateMapFileList(String[] maps);
    void updateMap(Map map);
}
