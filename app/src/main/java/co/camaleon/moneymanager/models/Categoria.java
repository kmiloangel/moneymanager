package co.camaleon.moneymanager.models;

/**
 * Created by Usuario on 10/04/2015.
 */
public class Categoria {
    private int id;
    private String nombre;
    private int presupuesto;
    private int usos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(int presupuesto) {
        this.presupuesto = presupuesto;
    }

    public int getUsos() {
        return usos;
    }

    public void setUsos(int usos) {
        this.usos = usos;
    }

    public String toString()
    {
        return nombre;
    }
}
