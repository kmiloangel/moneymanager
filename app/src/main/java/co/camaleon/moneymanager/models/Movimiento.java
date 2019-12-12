package co.camaleon.moneymanager.models;

/**
 * Created by Usuario on 23/03/2015.
 */
public class Movimiento{
    private int id;
    private int cuenta_id;
    private int categoria_id;
    private boolean egreso;
    private String descripcion;
    private String fecha;
    private float valor;

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCuenta_id() {
        return cuenta_id;
    }

    public void setCuenta_id(int cuenta_id) {
        this.cuenta_id = cuenta_id;
    }

    public int getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(int categoria_id) {
        this.categoria_id = categoria_id;
    }

    public boolean isEgreso() {
        return egreso;
    }

    public void setEgreso(boolean egreso) {
        this.egreso = egreso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


}
