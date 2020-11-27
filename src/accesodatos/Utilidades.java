package accesodatos;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import java.io.File;

public class Utilidades {

    static org.xmldb.api.base.Collection col = null;
    static String driver = "org.exist.xmldb.DatabaseImpl"; //Driver para eXist
    static String URI = "xmldb:exist://localhost:8083/exist/xmlrpc/db/ColeccionPruebas"; //URI colección
    static String usu = "admin"; //Usuario
    static String usuPwd = "admin"; //Clave

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
                res = (XMLResource)col.createResource("Comedor.xml", "XMLResource");

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
