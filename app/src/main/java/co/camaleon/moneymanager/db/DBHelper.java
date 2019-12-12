package co.camaleon.moneymanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Usuario on 16/03/2015.
 */
class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = DBHelper.class.getSimpleName();

    public static final String DB_NAME = "moneymanager.db";
    public static final int DB_VERSION = 3;

    private ArrayList<DBModel> dbModels = new ArrayList<DBModel>();

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);

        dbModels.add(new MovimientoDao());
        dbModels.add(new CategoriaDao());
        dbModels.add(new CuentaDao());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.e(TAG, "ON CREATE SQL");

        for(DBModel dbModel : dbModels){
            String sqlCreate = getCreateTableSql(dbModel);
            db.execSQL(sqlCreate);
            Log.e(TAG, "onCreated SQL " + sqlCreate);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(DBModel dbModel : dbModels){
            db.execSQL("DROP TABLE IF EXISTS " + dbModel.getTableName());
        }

        Log.e(TAG, "onUpdated");

        onCreate(db);
    }

    private String getCreateTableSql(DBModel dbModel){
        StringBuilder sql  = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS `").append(dbModel.getTableName()).append("` (");

        ArrayList<DBField> dbFields = dbModel.getDbFields();

        //Log.e(TAG, "TAMANOOOOOOOOOOOOO " + dbFields.size());

        for (DBField field : dbFields) {
            sql.append("`" + field.getName() + "`").append(" ").append(field.getType());

            if(field.getLength() > 0){
                sql.append("(" + field.getLength() + ")");
            }

            if(field.isPrimaryKey()){
                sql.append(" primary key");
            }
            sql.append(",");
        }

        sql.deleteCharAt(sql.length() - 1).append(")");

        Log.e(TAG, "CREATE SQL " + sql.toString());

        return sql.toString();
    }
}
