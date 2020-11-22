package accesodatos;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XPathQueryService;

import javax.swing.*;
import java.util.ArrayList;

public class Comedor extends JFrame {
  private JPanel Principal;
  private JTextField jtCliente;
  private JComboBox<String> cbCamareros;
  private JTextField tjtFecha;
  private JComboBox<Object> cbPrimero;
  private JComboBox<Object> cbSegundo;
  private JComboBox<Object> cbPostre;
  private JLabel lImporte;
  private JPanel jpBotonera;
  private JButton bGuardar;
  private JButton bLimpiar;

  private static float precioPrimero = 0;
  private static float precioSegundo = 0;
  private static float precioPostre = 0;

  public Comedor() {

    add(Principal);

    setTitle("Comedor");
    setResizable(false);
    setSize(520, 300);


    ArrayList<String> camareros = new ArrayList<>() {{
      add("Juanito");
      add("Merche");
    }};

    cbCamareros.addItem(camareros.get(0));
    cbCamareros.addItem(camareros.get(1));

    for (Object primero : Main.primerosArray) {
      cbPrimero.addItem(primero);
    }

    for (Object segundo : Main.segundosArray) {
      cbSegundo.addItem(segundo);
    }

    for (Object postre : Main.primerosArray) {
      cbPostre.addItem(postre);
    }

    bGuardar.addActionListener(e -> {

      String nombre = jtCliente.getText();
      String fecha = tjtFecha.getText();
      String camarero = camareros.get(cbCamareros.getSelectedIndex());
      String primero = (String) Main.primerosArray[cbPrimero.getSelectedIndex()];
      String segundo = (String) Main.segundosArray[cbSegundo.getSelectedIndex()];
      String postre = (String) Main.postresArray[cbPostre.getSelectedIndex()];


      insertaFactura(nombre, fecha, camarero, primero, segundo, postre,
          String.valueOf(Main.primeros.get(primero) + Main.segundos.get(segundo) + Main.postres.get(postre)));
    });

    cbPrimero.addActionListener(e -> {

      precioPrimero = Main.primeros.get((String) Main.primerosArray[cbPrimero.getSelectedIndex()]);

      lImporte.setText(String.valueOf(precioPrimero + precioSegundo + precioPostre));
    });

    cbSegundo.addActionListener(e -> {

      precioSegundo = Main.segundos.get((String) Main.segundosArray[cbSegundo.getSelectedIndex()]);

      lImporte.setText(String.valueOf(precioPrimero + precioSegundo + precioPostre));

    });
    cbPostre.addActionListener(e -> {

      precioPostre = Main.postres.get((String) Main.postresArray[cbPostre.getSelectedIndex()]);

      lImporte.setText(String.valueOf(precioPrimero + precioSegundo + precioPostre));


    });
  }

  //update insert <Factura><Fecha>03/12/2020</Fecha></Factura> into /Comandas/Cliente[@nombre = "Juan"]
  //para insertar una factura aun cliente concreto

  private void insertaFactura(String nombre, String fecha, String camarero, String primero, String segundo, String postre, String importe) {

    //Caso concreto: sabemos cuáles son los nodos
    String nuevaFactura =
        "<Cliente nombre = \"" + nombre + "\">" +
            "<Factura>" +
            "<Fecha>" + fecha + "</Fecha>" +
            "<Camarero>" + camarero + "</Camarero>" +
            "<Primero>" + primero + "</Primero>" +
            "<Segundo>" + segundo + "</Segundo>" +
            "<Postre>" + postre + "</Postre>" +
            "<Importe>" + importe + "</Importe>"
            + "</Factura>" +
            "</Cliente>";

    if (Utilidades.conectar() != null) {
      try {
        XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");
        System.out.printf("Inserto: %s \n", nuevaFactura);
        //Consulta para insertar --> update insert ... into
        ResourceSet result = servicio.query("update insert " + nuevaFactura + " into /Comandas");
        Utilidades.col.close(); //borramos
        System.out.println("Dep insertado.");
      } catch (Exception e) {
        System.out.println("Error al insertar empleado.");
        e.printStackTrace();
      }
    } else {
      System.out.println("Error en la conexión. Comprueba datos.");
    }

  }
}
