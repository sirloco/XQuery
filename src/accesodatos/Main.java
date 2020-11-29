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
import java.util.*;

public class Main {

  //Se crea el menu contenido en Maps asociando el plato al precio Key Value
  static Map<String, Float> primeros = Map.of(
      "Sopa de huevo", 7f,
      "Judías verdes", 12.3f,
      "Berenjenas agridulces", 15.5f,
      "Lentejas con calabaza", 8f

  );

  static Map<String, Float> segundos = Map.of(
      "Trucha marinada", 25.2f,
      "Bonito marinado", 32.3f,
      "Fajitas de ternera", 18.5f,
      "Lomo de cerdo", 29.7f

  );

  static Map<String, Float> postres = Map.of(
      "Trufas de cava", 25.2f,
      "Crema catalana", 42.3f,
      "Brownie de chocolate", 5.5f,
      "Goxua", 39.7f

  );

  //Se transforma en un array de objetos con el objetivo de sacar un valor aleatorio de las keys del map
  static Object[] primerosArray = primeros.keySet().toArray();
  static Object[] segundosArray = segundos.keySet().toArray();
  static Object[] postresArray = postres.keySet().toArray();


  public static void main(String[] args) {

    crearXml();
    Utilidades.cargar_en_coleccion();

    Comedor comedor = new Comedor();
    comedor.setLocationRelativeTo(null);
    comedor.setVisible(true);
  }

  /**
   * Crea el fichero XML
   */
  private static void crearXml() {

    //Lista con el contenido de los nodos de cada factura de cada cliente
    List<Factura> facturas = new ArrayList<>();
    //Lista con las lineas de cada factura
    List<Tiket> lineas = new ArrayList<>();


    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      DOMImplementation implementation = builder.getDOMImplementation();
      Document document = implementation.createDocument(null, "Comandas", null);
      document.setXmlVersion("1.0");


      //Se crean los tikects pedro tendra tres tikects
      creaTikect(1,lineas, "03/02/2020", "Juanito");
      creaTikect(2,lineas, "04/02/2020", "Merche");
      creaTikect(3,lineas, "05/02/2020", "Juanito");

      facturas.add(new Factura("pedro"));
      crearCliente(facturas, lineas, document);

      lineas.clear();
      facturas.clear();

      creaTikect(4,lineas, "03/02/2020", "Juanito");
      creaTikect(5,lineas, "04/02/2020", "Juanito");
      creaTikect(6,lineas, "05/02/2020", "Juanito");
      creaTikect(7,lineas, "06/02/2020", "Juanito");
      creaTikect(8,lineas, "07/02/2020", "Merche");
      creaTikect(9,lineas, "08/02/2020", "Juanito");

      facturas.add(new Factura("Juan"));
      crearCliente(facturas, lineas, document);

      lineas.clear();
      facturas.clear();

      creaTikect(10,lineas, "10/02/2020", "Juanito");

      facturas.add(new Factura("Andres"));
      crearCliente(facturas, lineas, document);

      lineas.clear();
      facturas.clear();

      creaTikect(11,lineas, "03/02/2020", "Merche");
      creaTikect(12,lineas, "04/02/2020", "Juanito");

      facturas.add(new Factura("Rosa"));
      crearCliente(facturas, lineas, document);


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

  /**
   * @param lineas Contiene lo que seria el contenido de un ticket
   * @param fecha  Contiene la fecha del tiket
   */
  private static void creaTikect(int id, List<Tiket> lineas, String fecha, String camarero) {

    String primero = (String) primerosArray[new Random().nextInt(primerosArray.length)];
    String segundo = (String) segundosArray[new Random().nextInt(segundosArray.length)];
    String postre = (String) postresArray[new Random().nextInt(postresArray.length)];

    lineas.add(
        new Tiket(id,primeros.get(primero) + segundos.get(segundo) + postres.get(postre),
            fecha, camarero, String.valueOf(primero),
            String.valueOf(segundo), String.valueOf(postre)));

  }

  static void crearCliente(List<Factura> facturas, List<Tiket> lineas, Document document) {
    for (Factura factura : facturas) {

      //Crea y añade el nodo
      Element cliente = document.createElement("Cliente"); //nodo cliente

      cliente.setAttribute("nombre", factura.getNombre());

      document.getDocumentElement().appendChild(cliente); //lo añade a la raíz del documento


      for (Tiket linea : lineas) {

        Element tiket = document.createElement("Factura");


        document.getDocumentElement().appendChild(tiket);

        cliente.appendChild(tiket);

                   /* Text text = document.createTextNode("Fecha"); //damos valor
                    cliente.appendChild(tiket); //pegamos el elemento hijo a la raiz
                    tiket.appendChild(text); //pegamos el valor*/

        //se añaden los hijos al nodo raiz
        CrearElemento("id", String.valueOf(linea.getId()), tiket, document);
        CrearElemento("fecha", linea.getFecha(), tiket, document);
        CrearElemento("camarero", linea.getCamarero(), tiket, document);
        CrearElemento("primero", linea.getPrimero(), tiket, document);
        CrearElemento("segundo", linea.getSegundo(), tiket, document);
        CrearElemento("postre", linea.getPostre(), tiket, document);
        CrearElemento("importe", String.valueOf(linea.getImporte()), tiket, document);
      }

    }

  }

  static void CrearElemento(String datoEmple, String valor, Element raiz, Document document) {
    Element elem = document.createElement(datoEmple); //creamos hijo

    Text text = document.createTextNode(valor); //damos valor
    raiz.appendChild(elem); //pegamos el elemento hijo a la raiz
    elem.appendChild(text); //pegamos el valor
  }


}

