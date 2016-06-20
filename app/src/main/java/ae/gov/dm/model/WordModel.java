package ae.gov.dm.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model Class for Words
 */

@DatabaseTable(tableName = "dictionary")
public class WordModel {

    @DatabaseField
    private String letter;

    @DatabaseField
    private String word;

    @DatabaseField
    private String meaning;

    @DatabaseField
    private String search_word;

    @DatabaseField(id=true)
    private String record_id;

    @DatabaseField
    private boolean isFavorite;

    @DatabaseField
    private  String has_audio;


    //@DatabaseField(generatedId = true)
   // private int id;


    public WordModel() {
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public String toString() {
        return "WordModel{" +
                "word='" + word + '\'' +
                ", meaning='" + meaning + '\'' +
                '}';
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getSearch_word() {
        return search_word;
    }

    public void setSearch_word(String search_word) {
        this.search_word = search_word;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getHas_audio() {
        return has_audio;
    }

    public void setHas_audio(String has_audio) {
        this.has_audio = has_audio;
    }
}
