package accesodatos;

import java.io.Serializable;
import java.util.List;

public class Factura implements Serializable {

    private final String nombre;


    public Factura(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }


}
