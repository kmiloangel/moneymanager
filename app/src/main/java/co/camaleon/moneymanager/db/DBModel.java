package co.camaleon.moneymanager.db;

import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by Usuario on 31/03/2015.
 */
abstract class DBModel {
    private ArrayList<DBField> dbFields;
    private String tableName;

    public ArrayList<DBField> getDbFields() {
        return dbFields;
    }

    public void setDbFields(ArrayList<DBField> dbFields) {
        this.dbFields = dbFields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

class DBField {
    private String name;
    private String type;
    private int length = 0;
    private boolean primaryKey = false;

    public DBField(String name, String type, int length, boolean primaryKey){
        setName(name, primaryKey);
        setType(type);
        setLength(length);
        setPrimaryKey(primaryKey);
    }

    public DBField(String name, String type){
        setName(name, primaryKey);
        setType(type);
    }

    public DBField(String name, String type, int length){
        setName(name, primaryKey);
        setType(type);
        setLength(length);
    }

    public String getName() {
        return name;
    }

    public void setName(String name, boolean primaryKey) {
        this.name = !primaryKey ? name : BaseColumns._ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}