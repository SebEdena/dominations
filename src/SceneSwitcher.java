public class SceneSwitcher {
    private static SceneSwitcher instance;

    private SceneSwitcher() { }

    public SceneSwitcher getInstance() {
        if(instance == null) {
            instance = new SceneSwitcher();
        }
        return instance;
    }


}
