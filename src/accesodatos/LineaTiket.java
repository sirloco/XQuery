package accesodatos;

import java.io.Serializable;

public class LineaTiket implements Serializable {

    private float importe;
    private String fecha;
    private String camarero;
    private String primero;
    private String segundo;
    private String postre;

    public LineaTiket(float importe, String fecha, String camarero, String primero, String segundo, String postre) {
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

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCamarero() {
        return camarero;
    }

    public void setCamarero(String camarero) {
        this.camarero = camarero;
    }

    public String getPrimero() {
        return primero;
    }

    public void setPrimero(String primero) {
        this.primero = primero;
    }

    public String getSegundo() {
        return segundo;
    }

    public void setSegundo(String segundo) {
        this.segundo = segundo;
    }

    public String getPostre() {
        return postre;
    }

    public void setPostre(String postre) {
        this.postre = postre;
    }
}
