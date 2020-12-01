package accesodatos;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XPathQueryService;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

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
    private JTextField jtId;

    //Se establece el nombre del registro
    static Logger logger = Logger.getLogger("MiLog");

    //Se establece la ubicacion del registro true para añadir nuevos no sobreescribir
    static FileHandler fh;

    static {
        try {
            fh = new FileHandler(".\\Archivos\\log.xml", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Comedor() {

        //Establecer si se muestran por pantalla los registros
        logger.setUseParentHandlers(false);

        //Se establece el formato del archivo
        XMLFormatter formatter = new XMLFormatter();

        //Se aplica el formato
        fh.setFormatter(formatter);

        //Se establece el nivel de seguridad
        logger.setLevel(Level.ALL);

        //Se añade al handler
        logger.addHandler(fh);

        add(Principal);

        setTitle("Comedor");
        setResizable(false);
        setSize(580, 600);

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

        for (Object postre : Main.postresArray) {
            cbPostre.addItem(postre);
        }

        jtId.setText(idDisponible());

        /////////////////////// Se pone Fecha Actual ///////////////////////
        Calendar f = Calendar.getInstance();
        //Como el mes lo hace mal porque  los meses los hace de enero 0 a diciembre 11 mostraba el mes anterior
        SimpleDateFormat mes = new java.text.SimpleDateFormat("MM");

        tjtFecha.setText(f.get(Calendar.DATE) + "/" + mes.format(f.getTime()) + "/" + f.get(Calendar.YEAR));
        ////////////////////////////////////////////////////////////////////

        //Actualiza el importe total de los platos
        actualizaPrecios();

        ///////////////////////////////////////////EXPORTAR MENU////////////////////////////////////////////////
        exportaMenu();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Boton Guardar
        bGuardar.addActionListener(e -> {

            String id = jtId.getText();
            String nombre = jtCliente.getText();
            String fecha = tjtFecha.getText();
            String camarero = camareros.get(cbCamareros.getSelectedIndex());
            String primero = (String) Main.primerosArray[cbPrimero.getSelectedIndex()];
            String segundo = (String) Main.segundosArray[cbSegundo.getSelectedIndex()];
            String postre = (String) Main.postresArray[cbPostre.getSelectedIndex()];

            boolean falloEnteros = false;
            boolean idRepetido = false;
            try {

                Integer.parseInt(id);

                idRepetido = buscaIdTiket(id);

            } catch (NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
                falloEnteros = true;
            }

            if (nombre.isEmpty() || fecha.isEmpty() || id.isEmpty()) {

                JOptionPane.showMessageDialog(null, "Campos obligatorios fecha y nombre e id",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

            } else if (falloEnteros) {

                JOptionPane.showMessageDialog(null, "Solo numeros enteros ne id",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

            } else if (idRepetido) {

                JOptionPane.showMessageDialog(null, "id Repetido elige otro",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

            } else {
                insertaFactura(id, nombre, fecha, camarero, primero, segundo, postre,
                        String.valueOf(Main.primeros.get(primero) + Main.segundos.get(segundo) + Main.postres.get(postre)));

                buscaCliente(nombre, fecha, id);

            }
        });

        //Boton actualizar
        jbLimpiar.addActionListener(e -> {

            String id = jtId.getText();
            String nombre = jtCliente.getText();
            String fecha = tjtFecha.getText();
            String camarero = camareros.get(cbCamareros.getSelectedIndex());
            String primero = (String) Main.primerosArray[cbPrimero.getSelectedIndex()];
            String segundo = (String) Main.segundosArray[cbSegundo.getSelectedIndex()];
            String postre = (String) Main.postresArray[cbPostre.getSelectedIndex()];

            boolean existe = buscaIdTiket(id);

            if (nombre.isEmpty()) {

                JOptionPane.showMessageDialog(null, "Campos obligatorios fecha y nombre",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

            } else if (!existe) {

                JOptionPane.showMessageDialog(null, "id no existe en la base de datos",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

            } else {

                actualizaTikect(id, nombre, fecha, camarero, primero, segundo, postre,
                        String.valueOf(Main.primeros.get(primero) + Main.segundos.get(segundo) + Main.postres.get(postre)));

                buscaCliente(nombre, fecha, id);

            }

        });

        //Actualizar precios al cambiar opcion en el combobox
        cbPrimero.addActionListener(e -> actualizaPrecios());

        cbSegundo.addActionListener(e -> actualizaPrecios());

        cbPostre.addActionListener(e -> actualizaPrecios());

        jbBuscar.addActionListener(e -> buscaCliente(jtCliente.getText(), tjtFecha.getText(), jtId.getText()));

        //Borra el tiket del cliente escrito en la fecha descrita
        jbBorrar.addActionListener(e -> {

            if (jtCliente.getText().isEmpty() && jtId.getText().isEmpty())
                JOptionPane.showMessageDialog(null, "Campo obligatorio nombre o id",
                        "Error", JOptionPane.INFORMATION_MESSAGE);
            else {
                borraTiket(jtCliente.getText(), tjtFecha.getText(), jtId.getText());
                buscaCliente(jtCliente.getText(), tjtFecha.getText(), jtId.getText());
            }
        });

    }

    private void exportaMenu() {


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document doc = implementation.createDocument(null, "Menu", null);
            doc.setXmlVersion("1.0");


            Main.primeros.forEach((k, v) -> {

                Element prim = doc.createElement("Primero"); //nodo Primeros
                doc.getDocumentElement().appendChild(prim);


                Element nombre = doc.createElement("Nombre");
                Text plato = doc.createTextNode(k);

                nombre.appendChild(plato);
                prim.appendChild(nombre);


                Element valor = doc.createElement("Precio");
                Text precio = doc.createTextNode(String.valueOf(v));

                valor.appendChild(precio);
                prim.appendChild(valor);

            });


            Main.segundos.forEach((k, v) -> {

                Element segundo = doc.createElement("Segundo"); //nodo Primeros
                doc.getDocumentElement().appendChild(segundo);

                Element nombre = doc.createElement("Nombre");
                Text plato = doc.createTextNode(k);

                nombre.appendChild(plato);
                segundo.appendChild(nombre);

                Element valor = doc.createElement("Precio");
                Text precio = doc.createTextNode(String.valueOf(v));

                valor.appendChild(precio);
                segundo.appendChild(valor);

            });

            Main.postres.forEach((k, v) -> {

                Element postre = doc.createElement("Postre"); //nodo Primeros
                doc.getDocumentElement().appendChild(postre);

                Element nombre = doc.createElement("Nombre");
                Text plato = doc.createTextNode(k);

                nombre.appendChild(plato);
                postre.appendChild(nombre);

                Element valor = doc.createElement("Precio");
                Text precio = doc.createTextNode(String.valueOf(v));

                valor.appendChild(precio);
                postre.appendChild(valor);

            });

            Source source = new DOMSource(doc);
            Result result = new StreamResult(new java.io.File(".\\Archivos\\Menu.xml"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerConfigurationException ignored) {
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    private boolean buscaIdTiket(String id) {

        boolean ocupado = false;

        if (Utilidades.conectar() != null) {
            try {
                XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

                //Preparamos la consulta esta devuelve el nombre del cliente si existe
                ResourceSet resul = servicio.query("/Comandas/Cliente/Factura[id=" + id + "]");

                // recorrer los datos del recurso.
                ResourceIterator i = resul.getIterator();

                if (!i.hasMoreResources()) {

                    System.out.println("No existe el id");
                    JOptionPane.showMessageDialog(null, "id que quieres buscar no existe",
                            "Error", JOptionPane.INFORMATION_MESSAGE);


                } else {

                    ocupado = true;
                }

                Utilidades.col.close();

            } catch (Exception e) {
                System.out.println("Error al Buscar cliente.");

                JOptionPane.showMessageDialog(null, "Error al buscar el cliente",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión. Comprueba datos.");

            JOptionPane.showMessageDialog(null, "Error al intentar conectar a la base de datos",
                    "Error", JOptionPane.INFORMATION_MESSAGE);

        }

        return ocupado;
    }

    private String idDisponible() {

        String identificador = "";

        if (Utilidades.conectar() != null) {
            try {
                XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

                //Preparamos la consulta esta devuelve el id
                ResourceSet id = servicio.query("max(/Comandas/Cliente/Factura/id)");

                Resource r = id.getIterator().nextResource();

                identificador = r.getContent().toString();

                identificador = String.valueOf(Integer.parseInt(identificador) + 1);

                Utilidades.col.close();

            } catch (Exception e) {
                System.out.println("Error al Buscar cliente.");

                JOptionPane.showMessageDialog(null, "Error al intentar encontrar siguiente id disponible",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión. Comprueba datos.");

            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos",
                    "Error", JOptionPane.INFORMATION_MESSAGE);

        }
        return identificador;
    }

    private void actualizaTikect(String id, String cliente, String fecha, String camarero, String primero, String
            segundo, String postre, String importe) {

        String tiket = "<Factura>" +
                "<id>" + id + "</id>" +
                "<fecha>" + fecha + "</fecha>" +
                "<camarero>" + camarero + "</camarero>" +
                "<primero>" + primero + "</primero>" +
                "<segundo>" + segundo + "</segundo>" +
                "<postre>" + postre + "</postre>" +
                "<importe>" + importe + "</importe>"
                + "</Factura>";

        //SE inicializa la consulta solo con el nombre de cliente de primeras
        String consulta = "/Comandas/Cliente[@nombre=\"" + cliente + "\"]";

        //Luego en funcion de si se ha puesto la fecha o no se añade el id
        consulta += !fecha.isEmpty() ? "/Factura[fecha=\"" + fecha + "\"][id=\"" + id + "\"]" : "/Factura[id=\"" + id + "\"]";

        if (Utilidades.conectar() != null) {

            try {

                XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

                //Preparamos la consulta esta devuelve el nombre del cliente si existe
                ResourceSet resul = servicio.query(consulta);

                // recorrer los datos del recurso.
                ResourceIterator i = resul.getIterator();

                if (!i.hasMoreResources()) {

                    System.out.println("No existe el Tiket");

                    JOptionPane.showMessageDialog(null, "El tiket que quieres actualizar no existe",
                            "Error", JOptionPane.INFORMATION_MESSAGE);


                } else {

                    ResourceSet result = servicio.query("update replace " + consulta + " with " + tiket);

                    Utilidades.col.close();
                    System.out.println("Tiket actualizado.");

                    JOptionPane.showMessageDialog(null, "Tiket actualizado",
                            "Info", JOptionPane.INFORMATION_MESSAGE);


                }

            } catch (Exception e) {
                System.out.println("Error al borrar.");

                JOptionPane.showMessageDialog(null, "Error al intentar borrar el tiket",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }

        } else {
            System.out.println("Error en la conexión. Comprueba datos.");

            JOptionPane.showMessageDialog(null, "Problema al intentar conectar con la base de datos",
                    "Error", JOptionPane.INFORMATION_MESSAGE);

        }


    }

    private void borraTiket(String cliente, String fecha, String id) {

        /////////////////////////// construyo la jodida consulta ////////////////////////////////

        String consulta = "/Comandas/Cliente";

        if (!cliente.isEmpty())
            consulta += "[@nombre=\"" + cliente + "\"]";

        if (!fecha.isEmpty())
            consulta += "/Factura[fecha=\"" + fecha + "\"]";

        if (!id.isEmpty() && !fecha.isEmpty())
            consulta += "[id = " + id + "]";
        else if (!id.isEmpty())
            consulta += "/Factura[id = \"" + id + "\"]";

        ////////////////////////////////////////////////////////////////////////////////////////////

        System.out.println(consulta);
        if (Utilidades.conectar() != null) {

            try {
                System.out.printf("Borro el tiket de : %s\n", cliente);
                XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

                //Preparamos la consulta esta devuelve el nombre del cliente si existe
                ResourceSet resul = servicio.query(consulta);

                // recorrer los datos del recurso.
                ResourceIterator i = resul.getIterator();

                if (!i.hasMoreResources()) {

                    JOptionPane.showMessageDialog(null, "No Existe el tiket que quieres actualizar",
                            "Error", JOptionPane.INFORMATION_MESSAGE);

                    System.out.println("No existe el Tiket");

                } else {

                    ResourceSet result = servicio.query("update delete " + consulta);

                    Utilidades.col.close();
                    System.out.println("Tiket borrado.");

                    JOptionPane.showMessageDialog(null, "Tiket  borrado",
                            "Info", JOptionPane.INFORMATION_MESSAGE);


                }

            } catch (Exception e) {
                System.out.println("Error al borrar.");

                JOptionPane.showMessageDialog(null, "No se pudo borrar el tiket que querias",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }

        } else {
            System.out.println("Error en la conexión. Comprueba datos.");

            JOptionPane.showMessageDialog(null, "Error al intentar conectar con la base de datos",
                    "Error", JOptionPane.INFORMATION_MESSAGE);

        }

    }

    private void buscaCliente(String nombre, String fecha, String id) {

        /////////////////////////// construyo la jodida consulta ////////////////////////////////

        String consulta = "/Comandas/Cliente";

        if (!nombre.isEmpty())
            consulta += "[@nombre=\"" + nombre + "\"]/Factura";

        if (!fecha.isEmpty())

            if (nombre.isEmpty())
                consulta += "/Factura[fecha=\"" + fecha + "\"]";
            else
                consulta += "[fecha=\"" + fecha + "\"]";


        if (!id.isEmpty() && !fecha.isEmpty())
            consulta += "[id = \"" + id + "\"]";
        else if (!id.isEmpty())
            consulta += "/Factura[id = \"" + id + "\"]";

        ////////////////////////////////////////////////////////////////////////////////////////////

        if (Utilidades.conectar() != null) {

            try {

                XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

                //Preparamos la consulta esta devuelve el nombre del cliente si existe
                ResourceSet resul = servicio.query(consulta);

                // recorrer los datos del recurso.
                ResourceIterator i = resul.getIterator();

                if (!i.hasMoreResources()) {

                    JOptionPane.showMessageDialog(null, "No encuentro el cliente",
                            "Error", JOptionPane.INFORMATION_MESSAGE);

                    System.out.println("No existe el cliente");

                    JOptionPane.showMessageDialog(null, "El Tiket no existe",
                            "Error", JOptionPane.INFORMATION_MESSAGE);


                } else {

                    //El cliente ya existe
                    //Preparamos la consulta esta devuelve el nombre del cliente si existe
                    resul = servicio.query(consulta);

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

                        logger.info((String) r.getContent());

                    }

                    fh.close();

                    muestraTikets(tikets, nombre);

                }
                Utilidades.col.close(); //borramos


            } catch (Exception e) {
                System.out.println("Error al Buscar cliente.");

                JOptionPane.showMessageDialog(null, "Error al buscar el tiket",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión. Comprueba datos.");

            JOptionPane.showMessageDialog(null, "No se puedo conectar con la base de datos",
                    "Error", JOptionPane.INFORMATION_MESSAGE);

        }


    }

    private void muestraTikets(List<Tiket> tikets, String nombre) {

        DefaultListModel<String> modelo = new DefaultListModel<>();

        if (nombre.isEmpty())
            nombre = traeNombre(tikets.get(0).getId());

        modelo.addElement(nombre);
        modelo.addElement("===========================");
        modelo.addElement("");
        modelo.addElement("");

        for (Tiket tiket : tikets) {

            modelo.addElement("------------------------------");
            modelo.addElement("Id: " + tiket.getId());
            modelo.addElement("Fecha: " + tiket.getFecha());
            modelo.addElement("Camarero: " + tiket.getCamarero());
            modelo.addElement("Primero: " + tiket.getPrimero());
            modelo.addElement("Segundo: " + tiket.getSegundo());
            modelo.addElement("Postre: " + tiket.getPostre());
            modelo.addElement("Total: " + tiket.getImporte());

        }
        modelo.addElement("------------------------------");

        modelo.addElement("");

        generaXml(tikets, nombre);

        jlListado.setModel(modelo);
    }

    private void generaXml(List<Tiket> tikets, String nombre) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document doc = implementation.createDocument(null, "Tikets", null);
            doc.setXmlVersion("1.0");

            List<Factura> fac = new ArrayList<>();

            fac.add(new Factura(nombre));

            Main.crearCliente(fac,tikets,doc);

            Source source = new DOMSource(doc);
            Result result = new StreamResult(new java.io.File(".\\Archivos\\Tikets.xml"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerConfigurationException ignored) {
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    private String traeNombre(int id) {
        String nombre = "";

        if (Utilidades.conectar() != null) {
            try {
                XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");

                //Preparamos la consulta esta devuelve el id
                ResourceSet ide = servicio.query("for $nom in /Comandas/Cliente\n" +
                        "let $nombre:=data($nom/@nombre)\n" +
                        "let $id:=data($nom/Factura/id)\n" +
                        "return if($id=\"" + id + "\") then data(<nombre>{$nombre}</nombre>) else()");

                Resource r = ide.getIterator().nextResource();

                nombre = r.getContent().toString();

                Utilidades.col.close();

            } catch (Exception e) {
                System.out.println("Error al Buscar cliente.");

                JOptionPane.showMessageDialog(null, "Error al intentar localizar el nombre del cliente",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión. Comprueba datos.");

            JOptionPane.showMessageDialog(null, "No se pudo conectar con la base de datos",
                    "Error", JOptionPane.INFORMATION_MESSAGE);

        }

        return nombre;
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

    private void insertaFactura(String id, String nombre, String fecha, String camarero, String primero, String
            segundo, String postre, String importe) {

        String nuevaFactura = "<Factura>" +
                "<id>" + id + "</id>" +
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

                jtId.setText(idDisponible());

                System.out.println("Tickeet  insertado.");

                JOptionPane.showMessageDialog(null, "Tiket guardado a nombre de " + nombre,
                        "Info", JOptionPane.INFORMATION_MESSAGE);


            } catch (Exception e) {
                System.out.println("Error al insertar empleado.");

                JOptionPane.showMessageDialog(null, "no se pudo guardar el tiket",
                        "Error", JOptionPane.INFORMATION_MESSAGE);

                e.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión. Comprueba datos.");

            JOptionPane.showMessageDialog(null, "No se puede conectar con la base de datos",
                    "Error", JOptionPane.INFORMATION_MESSAGE);

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

        return (Tiket) xstream.fromXML(xml);
    }


}
