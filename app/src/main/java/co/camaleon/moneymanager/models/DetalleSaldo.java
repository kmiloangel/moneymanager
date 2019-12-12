package co.camaleon.moneymanager.models;

/**
 * Created by Usuario on 20/04/2015.
 */
public class DetalleSaldo {
    private String nombre;
    private int saldo;
    private int presupuesto;

    public int getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(int presupuesto) {
        this.presupuesto = presupuesto;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
