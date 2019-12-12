package co.camaleon.moneymanager.db;

import java.util.ArrayList;

/**
 * Created by Usuario on 10/04/2015.
 */
public class CategoriaDao extends DBModel{
    public CategoriaDao(){
        ArrayList<DBField> fields = new ArrayList<DBField>();

        DBField field = new DBField("id", "integer", 0, true);
        fields.add(field);

        field = new DBField("nombre", "varchar", 255);
        fields.add(field);

        field = new DBField("presupuesto", "integer");
        fields.add(field);

        field = new DBField("usos", "integer");
        fields.add(field);

        setDbFields(fields);
        setTableName("categoria");
    }
}
