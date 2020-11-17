package accesodatos;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        crearXml();
    }

    /**
     * Crea el fichero XML
     */
    private static void crearXml() {

        List<Factura> facturas = new ArrayList<>();
        List<LineaTiket> lineas = new ArrayList<>();

        lineas.add(new LineaTiket(105, "03/02/2020", "Juanito", "Lentejas", "Guiso", "Goxua"));
        lineas.add(new LineaTiket(15, "04/02/2020", "Juanito", "Fideos", "Lubina", "Galletas"));

        facturas.add(new Factura("pedro",lineas));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document document = implementation.createDocument(null, "Comandas", null);
            document.setXmlVersion("1.0");

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

            Source source = new DOMSource(document);
            Result result = new StreamResult(new java.io.File(".\\Archivos\\Empleados.xml"));
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
}

