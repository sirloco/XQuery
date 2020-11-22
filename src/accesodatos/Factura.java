package accesodatos;

import java.io.Serializable;
import java.util.List;

public class Factura implements Serializable {

    private String nombre;


    private List<Tiket> lineas;


    public Factura(String nombre, List<Tiket> lineas) {
        this.nombre = nombre;
        this.lineas = lineas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
