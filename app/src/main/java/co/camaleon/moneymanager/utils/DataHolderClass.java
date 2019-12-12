package co.camaleon.moneymanager.utils;

/**
 * Created by kmilo on 22-Sep-17.
 * MANAGES GLOBAL VARIABLES
 */

public class DataHolderClass {
    private static DataHolderClass dataObject = null;

    private DataHolderClass() {
        // left blank intentionally
    }

    public static DataHolderClass getInstance() {
        if (dataObject == null)
            dataObject = new DataHolderClass();
        return dataObject;
    }

    private int movimientoCurrentPage;

    public int getMovimientoCurrentPage() {
        return movimientoCurrentPage;
    }

    public void setMovimientoCurrentPage(int movimientoCurrentPage) {
        this.movimientoCurrentPage = movimientoCurrentPage;
    }
}