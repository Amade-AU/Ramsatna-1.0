package amada.ramsatna.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Hamza on 21/04/2016.
 */
@DatabaseTable(tableName = "favorites")
public class Favorites implements Serializable {

    @DatabaseField(id = true)
    private String record_id;

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

}
