package amada.ramsatna.model.configs;

/**
 * Model for Random Object of the JSON Response
 */
public class Random {

    private String id;

    private String word;

    private String meaning;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
