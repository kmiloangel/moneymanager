package co.camaleon.moneymanager.db;

import java.util.ArrayList;

/**
 * Created by Usuario on 31/03/2015.
 */
public class MovimientoDao extends DBModel{
    public MovimientoDao(){
        ArrayList<DBField> fields = new ArrayList<DBField>();

        DBField field = new DBField("id", "integer", 0, true);
        fields.add(field);

        field = new DBField("cuenta_id", "integer");
        fields.add(field);

        field = new DBField("categoria_id", "integer");
        fields.add(field);

        field = new DBField("egreso", "char", 1);
        fields.add(field);

        field = new DBField("descripcion", "varchar", 255);
        fields.add(field);

        field = new DBField("fecha", "datetime");
        fields.add(field);

        field = new DBField("valor", "integer");
        fields.add(field);

        setDbFields(fields);
        setTableName("movimiento");
    }
}
