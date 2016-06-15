package amada.ramsatna.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model class for Version
 */
@DatabaseTable(tableName = "version")
public class Version {

    @DatabaseField
    private int size;


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
