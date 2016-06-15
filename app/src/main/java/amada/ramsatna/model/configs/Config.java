package amada.ramsatna.model.configs;

/**
 * Model class for Config JSON Result from the API
 */
public class Config {

    private String res;

    private Random random;

    private Params params;

    private String total_in_dictionary;

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getTotal_in_dictionary() {
        return total_in_dictionary;
    }

    public void setTotal_in_dictionary(String total_in_dictionary) {
        this.total_in_dictionary = total_in_dictionary;
    }
}
