package co.camaleon.moneymanager.models;

import java.util.ArrayList;

/**
 * Created by Usuario on 10/04/2015.
 */
public class Cuenta {
    private int id;
    private String nombre;
    private String descripcion;
    private String color;
    private int saldo;
    private int usos;

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

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

    public String toString() { return nombre; }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getUsos() {
        return usos;
    }

    public void setUsos(int usos) {
        this.usos = usos;
    }

    public String getCuentaColor(){
        ArrayList<String> colors = new ArrayList<String>();

        //Log.e("CUENTA COLOR", this.getId()+ "");

        colors.add("#f44336");
        colors.add("#e91e63");
        colors.add("#9c27b0");
        colors.add("#673ab7");
        colors.add("#3f51b5");
        colors.add("#2196f3");
        colors.add("#03a9f4");
        colors.add("#00bcd4");
        colors.add("#009688");
        colors.add("#4caf50");
        colors.add("#8bc34a");
        colors.add("#cddc39");
        colors.add("#ffeb3b");
        colors.add("#ffc107");
        colors.add("#ff9800");
        colors.add("#ff5722");
        colors.add("#795548");
        colors.add("#9e9e9e");
        colors.add("#607d8b");

        //Log.e("CUENTA", (1 % 18) + " x " + (19 % 18) + "");
        //Log.e("CUENTA", (2%18) + " x " + (3%18) + "");
        //Log.e("CUENTA", (4%18) + " x " + (5%18) + "");
        //Log.e("CUENTA", (20%18) + " x " + (32%18) + "");
        //Log.e("CUENTA", (66%18) + " x " + (88%18) + "");

        int pos = this.getId() % colors.size();

        return colors.get(pos);
    }
}