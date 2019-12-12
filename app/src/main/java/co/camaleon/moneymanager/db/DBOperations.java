package co.camaleon.moneymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.StringBuilderPrinter;

import java.util.ArrayList;

import co.camaleon.moneymanager.models.Categoria;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.models.DetalleSaldo;
import co.camaleon.moneymanager.models.Movimiento;

/**
 * Created by Usuario on 16/03/2015.
 */
public class DBOperations {
    public static final String TAG = DBOperations.class.getSimpleName();

    private DBHelper dbHelper;
    //private SQLiteDatabase dataBase;

    public DBOperations(Context context){
        dbHelper = new DBHelper(context);
        //dataBase = dbHelper.getWritableDatabase();
    }

    public int insertOrIgnore(ContentValues values, DBModel dbModel){
        Log.d(TAG, "insertOrIgnore on: " + values + " " + dbModel.getTableName());

        int inserted_id = -1;

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        try {
            inserted_id = (int) dataBase.insertWithOnConflict(dbModel.getTableName(), null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }finally {
            dataBase.close();
        }

        return inserted_id;
    }

    public void update(ContentValues values, DBModel dbModel, int regId){
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        try {
            dataBase.update(dbModel.getTableName(), values, BaseColumns._ID + " = ?", new String[]{String.valueOf(regId)});
        }finally {
            dataBase.close();
        }
    }

    public boolean delete( DBModel dbModel, int id)
    {
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        boolean result = false;
        try {
            result = dataBase.delete(dbModel.getTableName(), BaseColumns._ID + "=" + id, null) > 0;
        }finally{
            dataBase.close();
        }
        return result;
    }

    public ArrayList<Movimiento> getMovimientosList(String where, int numberOfItems, int pageNumber) {
        return getMovimientosListPrivate(where, numberOfItems, pageNumber);
    }

    public ArrayList<Movimiento> getMovimientosList() {
        return getMovimientosListPrivate(null, 0, 0);
    }

    private ArrayList<Movimiento> getMovimientosListPrivate(String where, int numberOfItems, int pageNumber) {
        ArrayList<Movimiento> list = new ArrayList<Movimiento>();
        DBModel dbModel = new MovimientoDao();

        String limit = "";
        if(numberOfItems != 0 && pageNumber != 0){
            limit = this.getLimit(numberOfItems, pageNumber);
        }

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {

            Cursor cursor;
            if (where == null) {
                Log.e(TAG, limit);
                cursor = dataBase.query(dbModel.getTableName(), null, null, null, null, null, "fecha DESC, " + BaseColumns._ID + " DESC", limit);
            } else {
                if (limit != "") limit = " LIMIT " + limit;

                Log.e(TAG, "movimientos where select * from " + dbModel.getTableName() + " where " + where + limit);

                cursor = dataBase.rawQuery("SELECT * FROM " + dbModel.getTableName() + " WHERE " + where + " ORDER BY fecha DESC, " + BaseColumns._ID + " DESC " + limit, null);
            }
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {

                    Movimiento mov = new Movimiento();
                    mov.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                    mov.setFecha(cursor.getString(cursor.getColumnIndex("fecha")));
                    mov.setEgreso(cursor.getInt(cursor.getColumnIndex("egreso")) == 1);
                    mov.setCuenta_id(cursor.getInt(cursor.getColumnIndex("cuenta_id")));
                    mov.setCategoria_id(cursor.getInt(cursor.getColumnIndex("categoria_id")));
                    mov.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
                    mov.setValor(cursor.getInt(cursor.getColumnIndex("valor")));

                    list.add(mov);

                    cursor.moveToNext();
                }

            }
        }finally {
            dataBase.close();
        }
        //Log.e(TAG, "SIZEEEE" + list.size());
        return list;
    }

    public Movimiento getMovimientoById(int id) {

        ArrayList<Movimiento> list = getMovimientosList(BaseColumns._ID + " = " + id, 0, 0);

        if(list.size() > 0){
            return list.get(0);
        }
        return new Movimiento();
    }

    public String getMovimientoFilterWhere(int categoria_id, int cuenta_id, boolean egresos, boolean ingresos, String fechaDesde, String fechaHasta, String valorDesde, String valorHasta, String desc){
        StringBuilder where = new StringBuilder();

        where.append("1=1 ");

        if(categoria_id != -1){
            where.append(" AND categoria_id = " + categoria_id);
        }

        if(cuenta_id != -1){
            where.append(" AND cuenta_id = " + cuenta_id);
        }

        if(egresos && !ingresos){
            where.append(" AND egreso = '1'");
        }

        if(ingresos && !egresos){
            where.append(" AND egreso = '0'");
        }

        if(fechaDesde.length() > 0){
            where.append(" AND fecha >= '" + fechaDesde + "'");
        }

        if(fechaHasta.length() > 0){
            where.append(" AND fecha <= '" + fechaHasta + " 23:59:59'");
        }

        if(valorDesde.length() > 0){
            where.append(" AND valor >= " + valorDesde);
        }

        if(valorHasta.length() > 0){
            where.append(" AND valor <= " + valorHasta);
        }

        if(desc.length() > 0){
            where.append(" AND descripcion like '%" + desc + "%'");
        }

        return where.toString();
    }

    public ArrayList<Categoria> getCategoriasList(String where) {
        return getCategoriasListPrivate(where);
    }

    public ArrayList<Categoria> getCategoriasList() {
        return getCategoriasListPrivate(null);
    }

    private ArrayList<Categoria> getCategoriasListPrivate(String where) {
        ArrayList<Categoria> list = new ArrayList<Categoria>();
        DBModel dbModel = new CategoriaDao();

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        try {
            Cursor cursor;
            if (where == null) {
                cursor = dataBase.query(dbModel.getTableName(), null, null, null, null, null, "usos DESC,nombre ASC");
            } else {
                cursor = dataBase.rawQuery("SELECT * FROM " + dbModel.getTableName() + " WHERE " + where, null);
            }
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {

                    Categoria o = new Categoria();
                    o.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                    o.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                    o.setPresupuesto(cursor.getInt(cursor.getColumnIndex("presupuesto")));
                    o.setUsos(cursor.getInt(cursor.getColumnIndex("usos")));

                    list.add(o);

                    cursor.moveToNext();
                }

            }
        }finally {
            dataBase.close();
        }
        return list;
    }

    public Categoria getCategoriaById(int id) {

        ArrayList<Categoria> list = getCategoriasList(BaseColumns._ID + " = " + id);

        if(list.size() > 0){
            return list.get(0);
        }
        return new Categoria();
    }

    public ArrayList<Cuenta> getCuentasList(String where) {
        return getCuentasListPrivate(where, false);
    }

    public ArrayList<Cuenta> getCuentasList(boolean withSaldo) {
        return getCuentasListPrivate(null, withSaldo);
    }

    private ArrayList<Cuenta> getCuentasListPrivate(String where, boolean withSaldo) {
        ArrayList<Cuenta> list = new ArrayList<Cuenta>();
        DBModel dbModel = new CuentaDao();
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            Cursor cursor;
            if (where == null) {
                cursor = dataBase.query(dbModel.getTableName(), null, null, null, null, null, "usos DESC,nombre ASC");
            } else {
                cursor = dataBase.rawQuery("SELECT * FROM " + dbModel.getTableName() + " WHERE " + where, null);
            }

            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {

                    Cuenta o = new Cuenta();
                    o.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                    o.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                    o.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
                    o.setColor(o.getCuentaColor());
                    o.setUsos(cursor.getInt(cursor.getColumnIndex("usos")));

                    if (withSaldo) {
                        o.setSaldo(getSaldoActual(o.getId()));
                    } else {
                        o.setSaldo(0);
                    }

                    list.add(o);

                    cursor.moveToNext();
                }

            }
        }finally {
            dataBase.close();
        }
        return list;
    }

    public Cuenta getCuentaById(int id) {

        ArrayList<Cuenta> list = getCuentasList(BaseColumns._ID + " = " + id);

        if(list.size() > 0){
            return list.get(0);
        }
        return new Cuenta();
    }

    public int getSaldoActual() {
        int saldo = 0;
        MovimientoDao dao = new MovimientoDao();

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            Cursor cursor = dataBase.rawQuery(
                    "SELECT " +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE egreso = '0') - " +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE egreso = '1') AS saldo", null);


            if (cursor.moveToFirst()) {
                saldo = cursor.getInt(cursor.getColumnIndex("saldo"));
            }
        }finally {
            dataBase.close();
        }

        return saldo;
    }

    public int getSaldoActual(int categoria_id, String fecha_inicio, String fecha_fin){
        int saldo = 0;
        MovimientoDao dao = new MovimientoDao();

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            Cursor cursor = dataBase.rawQuery(
                    "SELECT " +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE egreso = '0' AND categoria_id = ? and fecha >= ? and fecha <= ?) - " +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE egreso = '1' AND categoria_id = ? and fecha >= ? and fecha <= ?) AS saldo", new String[]{
                            String.valueOf(categoria_id), fecha_inicio, fecha_fin,
                            String.valueOf(categoria_id), fecha_inicio, fecha_fin
                    });

            //Log.e(TAG, fecha_inicio + " "+ fecha_fin);

            if (cursor.moveToFirst()) {
                saldo = cursor.getInt(cursor.getColumnIndex("saldo"));
            }
        }finally {
            dataBase.close();
        }

        return saldo;
    }

    public int getSaldoActual(int cuenta_id){
        int saldo = 0;
        MovimientoDao dao = new MovimientoDao();

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            Cursor cursor = dataBase.rawQuery(
                    "SELECT " +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE egreso = '0' AND cuenta_id = ?) - " +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE egreso = '1' AND cuenta_id = ?) AS saldo", new String[]{
                            String.valueOf(cuenta_id), String.valueOf(cuenta_id)
                    });

            //Log.e(TAG, cuenta_id + "");

            if (cursor.moveToFirst()) {
                saldo = cursor.getInt(cursor.getColumnIndex("saldo"));
            }
        }finally {
            dataBase.close();
        }

        return saldo;
    }

    public String getFechaUltimoMovimiento() {
        String fecha = "";
        MovimientoDao dao = new MovimientoDao();
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            Cursor cursor = dataBase.rawQuery("SELECT fecha FROM " + dao.getTableName() + " ORDER BY fecha DESC LIMIT 1", null);

            if (cursor.moveToFirst()) {
                fecha = cursor.getString(cursor.getColumnIndex("fecha"));
            }
        }finally {
            dataBase.close();
        }

        return fecha;
    }

    public ArrayList<DetalleSaldo> getDetalleSaldo(String dateFrom, String dateEnd){
        ArrayList<DetalleSaldo> detalle = new ArrayList<DetalleSaldo>();

        MovimientoDao dao = new MovimientoDao();

        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            Cursor cursor = dataBase.rawQuery(
                    "SELECT nombre, presupuesto, " +
                            "(" +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE fecha >= '" + dateFrom + "' and fecha <= '" + dateEnd + "' and egreso = '1' AND categoria_id = categoria." + BaseColumns._ID + ") - " +
                            "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE fecha >= '" + dateFrom + "' and fecha <= '" + dateEnd + "' and egreso = '0' AND categoria_id = categoria." + BaseColumns._ID + ") " +
                            ") AS saldo " +
                            "FROM categoria " +
                            "GROUP BY categoria." + BaseColumns._ID + " ORDER BY categoria.nombre ASC,categoria.usos DESC", null);

            Log.e("SALDO", "SELECT nombre, presupuesto, " +
                    "(" +
                    "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE fecha >= '" + dateFrom + "' and fecha <= '" + dateEnd + "' and egreso = '1' AND categoria_id = categoria." + BaseColumns._ID + ") - " +
                    "(SELECT COALESCE(SUM(valor), 0) FROM " + dao.getTableName() + " WHERE fecha >= '" + dateFrom + "' and fecha <= '" + dateEnd + "' and egreso = '0' AND categoria_id = categoria." + BaseColumns._ID + ") " +
                    ") AS saldo " +
                    "FROM categoria " +
                    "GROUP BY categoria." + BaseColumns._ID + " ORDER BY categoria.nombre ASC,categoria.usos DESC");

            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {

                    DetalleSaldo ds = new DetalleSaldo();
                    ds.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                    ds.setSaldo(cursor.getInt(cursor.getColumnIndex("saldo")));
                    ds.setPresupuesto(cursor.getInt(cursor.getColumnIndex("presupuesto")));

                    detalle.add(ds);

                    cursor.moveToNext();
                }
            }
        }finally {
            dataBase.close();
        }

        return detalle;
    }

    public static int saveNewCuenta(DBOperations dbOperations, String nombre, String descripcion, Cuenta cuenta){
        ContentValues values = new ContentValues();

        values.clear();
        values.put("nombre",        nombre);
        values.put("descripcion",   descripcion);

        if(cuenta != null) {
            dbOperations.update(values, new CuentaDao(), cuenta.getId());
            return cuenta.getId();
        }else{
            return dbOperations.insertOrIgnore(values, new CuentaDao());
        }
    }

    public static int saveNewCategoria(DBOperations dbOperations, String nombre, String presupuesto,  Categoria categoria){
        ContentValues values = new ContentValues();

        values.clear();
        values.put("nombre", nombre);
        values.put("presupuesto", presupuesto);

        if (categoria != null) {
            dbOperations.update(values, new CategoriaDao(), categoria.getId());

            return categoria.getId();
        } else {
            return dbOperations.insertOrIgnore(values, new CategoriaDao());
        }
    }

    public void updateField(DBModel dbModel, String field, String value, int id){
        ContentValues values = new ContentValues();

        values.clear();
        values.put(field, value);

        this.update(values, dbModel, id);
    }

    private boolean existsColumnInTable(String inTable, String columnToCheck) {
        Cursor mCursor = null;
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            // Query 1 row
            mCursor = dataBase.rawQuery("SELECT * FROM " + inTable + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
            dataBase.close();
        }
    }

    public void updateDB(){
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        try {
            if (!existsColumnInTable("cuenta", "usos")) {
                dataBase.execSQL("alter table cuenta add column usos integer");
            }

            if (!existsColumnInTable("categoria", "usos")) {
                dataBase.execSQL("alter table categoria add column usos integer");
            }
        }finally {
            dataBase.close();
        }
    }

    public String getLimit(int numberOfItems, int page){
        int hasta = numberOfItems;
        int desde = (hasta * page) - hasta;
        return desde + ", " + hasta;
    }

    /*public void fixMovimientoDates(){
        ArrayList<Movimiento> movimientos = getMovimientosList();

        for(Movimiento mov : movimientos){
            String[] parts = mov.getFecha().split("-");

            parts[1] = Utils.fillCeros(Integer.parseInt(parts[1]), 2);
            parts[2] = Utils.fillCeros(Integer.parseInt(parts[2]), 2);

            String fecha = Utils.implode("-", parts);

            dataBase.execSQL("update movimiento set fecha = '" + fecha + "' where _id = " + mov.getId());
        }
    }*/
}
