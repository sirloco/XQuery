package accesodatos;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static org.xmldb.api.base.Collection col = null;
    static String driver = "org.exist.xmldb.DatabaseImpl"; //Driver para eXist
    static String URI = "xmldb:exist://localhost:8083/exist/xmlrpc/db/ColeccionPruebas"; //URI colección
    static String usu = "admin"; //Usuario
    static String usuPwd = "admin"; //Clave


    public static void main(String[] args) {

        crearXml();
        cargar_en_coleccion();
    }

    /**
     * Crea el fichero XML
     */
    private static void crearXml() {

        List<Factura> facturas = new ArrayList<>();
        List<LineaTiket> lineas = new ArrayList<>();


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document document = implementation.createDocument(null, "Comandas", null);
            document.setXmlVersion("1.0");

            lineas.add(new LineaTiket(105, "03/02/2020", "Juanito", "Lentejas", "Guiso", "Goxua"));
            lineas.add(new LineaTiket(15, "04/02/2020", "Juanito", "Fideos", "Lubina", "Galletas"));
            facturas.add(new Factura("pedro",lineas));

            crearCliente(facturas,lineas,document);

            lineas.clear();
            facturas.clear();

            lineas.add(new LineaTiket(75, "05/02/2020", "Merche", "Trucha marinada", "Sopa de huevo", "Trufas de cava"));
            lineas.add(new LineaTiket(12, "04/05/2020", "Juanito", "Judías verdes", "Bonito marinado", "Crema catalana"));
            lineas.add(new LineaTiket(200, "07/01/2020", "Juanito", "Berenjenas agridulces", "Lomo de cerdo", "Brownie de chocolate"));
            lineas.add(new LineaTiket(80, "09/03/2020", "Juanito", "Lentejas con calabaza", "Fajitas de ternera", "Tiramisú"));
            facturas.add(new Factura("Juan",lineas));

            crearCliente(facturas,lineas,document);

            lineas.clear();
            facturas.clear();

            lineas.add(new LineaTiket(90, "09/03/2020", "Juanito", "Entrecot", "Arroz tres delicias", "mochis"));
            facturas.add(new Factura("Andres",lineas));

            crearCliente(facturas,lineas,document);

            lineas.clear();
            facturas.clear();

            lineas.add(new LineaTiket(11, "03/02/2020", "Merche", "Lentejas", "Guiso", "Goxua"));
            lineas.add(new LineaTiket(15, "04/02/2020", "Merche", "Fideos", "Lubina", "Galletas"));
            facturas.add(new Factura("Rosa",lineas));

            crearCliente(facturas,lineas,document);



            Source source = new DOMSource(document);
            Result result = new StreamResult(new java.io.File(".\\Archivos\\Comedor.xml"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            //Result console = new StreamResult(System.out);
            //transformer.transform(source, console);

        } catch (ParserConfigurationException | TransformerConfigurationException ignored) {
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    static void CrearElemento(String datoEmple, String valor, Element raiz, Document document) {
        Element elem = document.createElement(datoEmple); //creamos hijo

        Text text = document.createTextNode(valor); //damos valor
        raiz.appendChild(elem); //pegamos el elemento hijo a la raiz
        elem.appendChild(text); //pegamos el valor
    }

    static void crearCliente(List<Factura> facturas, List<LineaTiket> lineas, Document document){
        for (Factura factura : facturas) {

            //Crea y añade el nodo
            Element cliente = document.createElement("Cliente"); //nodo cliente

            cliente.setAttribute("nombre", factura.getNombre());

            document.getDocumentElement().appendChild(cliente); //lo añade a la raíz del documento


            for (LineaTiket linea : lineas) {

                Element tiket = document.createElement("Factura");


                document.getDocumentElement().appendChild(tiket);

                cliente.appendChild(tiket);

                   /* Text text = document.createTextNode("Fecha"); //damos valor
                    cliente.appendChild(tiket); //pegamos el elemento hijo a la raiz
                    tiket.appendChild(text); //pegamos el valor*/

                //se añaden los hijos al nodo raiz
                CrearElemento("Fecha", linea.getFecha(), tiket, document);
                CrearElemento("Camarero", linea.getCamarero(), tiket, document);
                CrearElemento("Primero", linea.getPrimero(), tiket, document);
                CrearElemento("Segundo", linea.getSegundo(), tiket, document);
                CrearElemento("Postre", linea.getPostre(), tiket, document);
                CrearElemento("Importe", String.valueOf(linea.getImporte()), tiket, document);
            }

        }

    }

    public static void cargar_en_coleccion() {
        //Devuelve true si el dep existe
        if (conectar() != null) {
            try {
                System.out.print("Conecta");
                // Inicializamos el recurso
                XMLResource res;

                // Creamos el recurso -> recibe 2 parámetros tipo String:
                // s: nombre.xml (si lo dejamos null, pondrá un nombre aleatorio)
                // s1: tipo recurso (en este caso, siempre será XMLResource)
                res = (XMLResource)col.createResource("dep_cargado.xml", "XMLResource");

                // Elegimos el fichero .xml que queremos añadir a la colección
                File f = new File(".\\Archivos\\Comedor.xml");

                // Fijamos como contenido ese archivo .xml elegido
                res.setContent(f);
                col.storeResource(res); // lo añadimos a la colección

                // Listamos la colección para ver que en efecto se ha añadido
                for (String colRe: col.listResources())
                    System.out.println(colRe);

                col.close();
            } catch (Exception e) {
                System.out.println("Error al consultar.");
                // e.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión. Comprueba datos.");
        }
    }// FIN cargar_en_coleccion()


    public static Collection conectar() {

        try {
            Class cl = Class.forName(driver); //Cargar del driver
            Database database = (Database) cl.newInstance(); //Instancia de la BD
            DatabaseManager.registerDatabase(database); //Registro del driver
            col = DatabaseManager.getCollection(URI, usu, usuPwd);
            return col;
        } catch (XMLDBException e) {
            System.out.println("Error al inicializar la BD eXist.");
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error en el driver.");
            //e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("Error al instanciar la BD.");
            //e.printStackTrace();
        }
        return null;
    }
}

