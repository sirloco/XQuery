package accesodatos;

import java.io.Serializable;

public class Tiket implements Serializable {

    private int id;
    private float importe;
    private String fecha;
    private String camarero;
    private String primero;
    private String segundo;
    private String postre;

    public Tiket(int id, float importe, String fecha, String camarero, String primero, String segundo, String postre) {
        this.id = id;
        this.importe = importe;
        this.fecha = fecha;
        this.camarero = camarero;
        this.primero = primero;
        this.segundo = segundo;
        this.postre = postre;
    }

    public float getImporte() {
        return importe;
    }


    public String getFecha() {
        return fecha;
    }


    public String getCamarero() {
        return camarero;
    }


    public String getPrimero() {
        return primero;
    }


    public String getSegundo() {
        return segundo;
    }


    public String getPostre() {
        return postre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
