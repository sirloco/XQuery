package accesodatos;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XPathQueryService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;

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
  private JButton jbLimpiar;
  private JButton jbBuscar;
  private JButton jbBorrar;
  private JPanel jpListado;
  private JList<String> jlListado;
  private JLabel lPrecioPrimero;
  private JLabel lPrecioSegundo;
  private JLabel lPrecioPostre;


  public Comedor() {

    add(Principal);

    setTitle("Comedor");
    setResizable(false);
    setSize(550, 600);

    JScrollPane listadoScroll = new JScrollPane();

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

    /////////////////////// Se pone Fecha Actual ///////////////////////
    Calendar f = Calendar.getInstance();
    //Como el mes lo hace mal porque  los meses los hace de enero 0 a diciembre 11 mostraba el mes anterior
    SimpleDateFormat mes = new java.text.SimpleDateFormat("MM");

    tjtFecha.setText(f.get(Calendar.DATE) + "/" + mes.format(f.getTime()) + "/" + f.get(Calendar.YEAR));
    ////////////////////////////////////////////////////////////////////

    //Actualiza el importe total de los platos
    actualizaPrecios();

    bGuardar.addActionListener(e -> {

      String nombre = jtCliente.getText();
      String fecha = tjtFecha.getText();
      String camarero = camareros.get(cbCamareros.getSelectedIndex());
      String primero = (String) Main.primerosArray[cbPrimero.getSelectedIndex()];
      String segundo = (String) Main.segundosArray[cbSegundo.getSelectedIndex()];
      String postre = (String) Main.postresArray[cbPostre.getSelectedIndex()];

      if (nombre.isEmpty() || fecha.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Campos obligatorios fecha y nombre",
            "Error", JOptionPane.INFORMATION_MESSAGE);
      } else {
        insertaFactura(nombre, fecha, camarero, primero, segundo, postre,
            String.valueOf(Main.primeros.get(primero) + Main.segundos.get(segundo) + Main.postres.get(postre)));

        buscaCliente(nombre);

      }
    });


    //Actualizar precios al cambiar opcion en el combobox
    cbPrimero.addActionListener(e -> actualizaPrecios());

    cbSegundo.addActionListener(e -> actualizaPrecios());

    cbPostre.addActionListener(e -> actualizaPrecios());

    jbBuscar.addActionListener(e -> buscaCliente(jtCliente.getText()));

    jbLimpiar.addActionListener(e -> actualizaTikect(jtCliente.getText(),tjtFecha.getText()));

    //Borra el tiket del cliente escrito en la fecha descrita
    jbBorrar.addActionListener(e -> {
      borraTiket(jtCliente.getText(), tjtFecha.getText());
      buscaCliente(jtCliente.getText());
    });

  }

  private void actualizaTikect(String cliente, String fecha) {


  }

  private void borraTiket(String cliente, String fecha) {

    String consulta = "/Comandas/Cliente[@nombre=\"" + cliente + "\"]/Factura[fecha=\"" + fecha + "\"]";
    if (Utilidades.conectar() != null) {

      try {
        System.out.printf("Borro el tiket de : %s\n", cliente);
        XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");


        //Preparamos la consulta esta devuelve el nombre del cliente si existe
        ResourceSet resul = servicio.query(consulta);

        // recorrer los datos del recurso.
        ResourceIterator i = resul.getIterator();

        if (!i.hasMoreResources()) {

          System.out.println("No existe el Tiket");

        } else {

          ResourceSet result = servicio.query("update delete " + consulta);

          System.out.println(result);
          Utilidades.col.close();
          System.out.println("Tiket borrado.");

        }

      } catch (Exception e) {
        System.out.println("Error al borrar.");
        e.printStackTrace();
      }

    } else {
      System.out.println("Error en la conexión. Comprueba datos.");
    }

  }

  private void buscaCliente(String nombre) {

    if (Utilidades.conectar() != null) {
      try {
        XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

        //Preparamos la consulta esta devuelve el nombre del cliente si existe
        ResourceSet resul = servicio.query("data(/Comandas/Cliente[@nombre = \"" + nombre + "\"]/@nombre)");

        // recorrer los datos del recurso.
        ResourceIterator i = resul.getIterator();

        if (!i.hasMoreResources()) {

          System.out.println("No existe el cliente");

        } else {

          //El cliente ya existe
          //Preparamos la consulta esta devuelve el nombre del cliente si existe
          resul = servicio.query("/Comandas/Cliente[@nombre = \"" + nombre + "\"]/Factura");

          // recorrer los datos del recurso.
          i = resul.getIterator();

          //Se crea la lista de tikets del cliente
          List<Tiket> tikets = new ArrayList<>();

          //Se recorren todos los tikets del cliente
          while (i.hasMoreResources()) {

            //Se obtiene cada  nodo que corresponde con cada tiket
            Resource r = i.nextResource();

            //Se añaden a una lista de objetos tiket para poder tratarlo mejor
            tikets.add(creaObjeto(r.getContent().toString()));

          }

          muestraTikets(tikets, nombre);

          System.out.println("Tikets mostrados par el cliente " + nombre);

        }
        Utilidades.col.close(); //borramos
        System.out.println("Cliente Encontrado.");
      } catch (Exception e) {
        System.out.println("Error al Buscar cliente.");
        e.printStackTrace();
      }
    } else {
      System.out.println("Error en la conexión. Comprueba datos.");
    }


  }

  private void muestraTikets(List<Tiket> tikets, String nombre) {

    DefaultListModel<String> modelo = new DefaultListModel<>();


    modelo.addElement(nombre);
    modelo.addElement("===========================");
    modelo.addElement("");
    modelo.addElement("");

    for (Tiket tiket : tikets) {

      modelo.addElement("----------------------------------------------");
      modelo.addElement("Fecha: " + tiket.getFecha());
      modelo.addElement("Camarero: " + tiket.getCamarero());
      modelo.addElement("Primero: " + tiket.getPrimero());
      modelo.addElement("Segundo: " + tiket.getSegundo());
      modelo.addElement("Postre: " + tiket.getPostre());
      modelo.addElement("Total: " + tiket.getImporte());
    }
    modelo.addElement("----------------------------------------------");

    modelo.addElement("");


    jlListado.setModel(modelo);
  }

  private void actualizaPrecios() {
    float precioPrimero = Main.primeros.get((String) Main.primerosArray[cbPrimero.getSelectedIndex()]);
    float precioSegundo = Main.segundos.get((String) Main.segundosArray[cbSegundo.getSelectedIndex()]);
    float precioPostre = Main.postres.get((String) Main.postresArray[cbPostre.getSelectedIndex()]);
    lPrecioPrimero.setText(precioPrimero + " €");
    lPrecioSegundo.setText(precioSegundo + " €");
    lPrecioPostre.setText(precioPostre + " €");
    lImporte.setText((precioPrimero + precioSegundo + precioPostre) + " €");
  }

  private void insertaFactura(String nombre, String fecha, String camarero, String primero, String
      segundo, String postre, String importe) {

    String nuevaFactura = "<Factura>" +
        "<fecha>" + fecha + "</fecha>" +
        "<camarero>" + camarero + "</camarero>" +
        "<primero>" + primero + "</primero>" +
        "<segundo>" + segundo + "</segundo>" +
        "<postre>" + postre + "</postre>" +
        "<importe>" + importe + "</importe>"
        + "</Factura>";

    if (Utilidades.conectar() != null) {
      try {
        XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

        //Preparamos la consulta esta devuelve el nombre del cliente si existe
        ResourceSet resul = servicio.query("data(/Comandas/Cliente[@nombre = \"" + nombre + "\"]/@nombre)");

        // recorrer los datos del recurso.
        ResourceIterator i = resul.getIterator();


        if (!i.hasMoreResources()) {

          //Caso concreto: no existe el cliente y se le añade la etiqueta cliente para hacer uno nuevo
          nuevaFactura = "<Cliente nombre = \"" + nombre + "\">" + nuevaFactura + "</Cliente>";

          //LA CONSULTA NO DEVUELVE NADA el cliente no existe todavia insertamos a nivel de comandas
          servicio.query("update insert " + nuevaFactura + " into /Comandas");

        } else {
          //El cliente ya existe

          //Caso concreto: el cliente ya existe no agregamos la etiqueta cliente lo agregamos a nivel del cliente
          servicio.query("update insert " + nuevaFactura + " into /Comandas/Cliente[@nombre = \"" + nombre + "\"]");


          Resource r = i.nextResource();
          System.out.println("--------------------------------------------");
          System.out.println((String) r.getContent());
        }

        Utilidades.col.close(); //borramos
        System.out.println("Tickeet  insertado.");
      } catch (Exception e) {
        System.out.println("Error al insertar empleado.");
        e.printStackTrace();
      }
    } else {
      System.out.println("Error en la conexión. Comprueba datos.");
    }

  }

  /**
   * Crea objetos a partir de un nodo de xml
   *
   * @param xml String que devuelve la consulta
   * @return devuelve un objeto
   */
  public Tiket creaObjeto(String xml) {

    XStream xstream = new XStream(new DomDriver());
    xstream.alias("Factura", Tiket.class);
    Tiket t = (Tiket) xstream.fromXML(xml);

    return t;
  }


}
