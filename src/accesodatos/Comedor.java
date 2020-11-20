package accesodatos;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XPathQueryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Comedor extends JFrame{
    private JPanel Principal;
    private JTextField jtCliente;
    private JComboBox<String> cbCamareros;
    private JTextField tjtFecha;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JLabel lImporte;
    private JPanel jpBotonera;
    private JButton bGuardar;
    private JButton bLimpiar;

    public Comedor() {

        add(Principal);

        setTitle("Comedor");
        setResizable(false);
        setSize(520, 300);


        cbCamareros.addItem("Juanito");
        cbCamareros.addItem("Merche");

        bGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nombre = jtCliente.getText();
                String fecha = tjtFecha.getText();

                insertaFactura("pepe","03/11/2020","juanito","primero","segundo","postre","100");
            }
        });
    }

    //update insert <Factura><Fecha>03/12/2020</Fecha></Factura> into /Comandas/Cliente[@nombre = "Juan"]
    //para insertar una factura aun cliente concreto

    private void insertaFactura(String nombre,String fecha,String camarero,String primero,String segundo,String postre,String importe) {

        //Caso concreto: sabemos cuáles son los nodos
        String nuevaFactura = "<Cliente nombre = "+nombre+ ">" +
                "<Factura>" +
                    "<Fecha>"+fecha+"</Fecha>"+
                    "<Camarero>"+camarero+"</Camarero>"+
                    "<Primero>"+primero+"</Primero>"+
                    "<Segundo>"+segundo+"</Segundo>"+
                    "<Postre>"+postre+"</Postre>"+
                    "<Importe>"+importe+"</Importe>"
                +"</Factura>";

        if (Utilidades.conectar() != null) {
            try {
                XPathQueryService servicio = (XPathQueryService) Utilidades.col.getService("XPathQueryService", "1.0");
                System.out.printf("Inserto: %s \n", nuevaFactura);
                //Consulta para insertar --> update insert ... into
                ResourceSet result = servicio.query("update insert " + nuevaFactura + " into /departamentos");
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
