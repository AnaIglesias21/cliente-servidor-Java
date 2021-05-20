package clienteUnico;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

/**
 * Clase donde el usuario introduce un número y lo envía a un servidor 
 * para comprobar que coincide o no con el generado por el servidor
 * 
 * @author anaig
 */
public class Cliente {
    
    public Cliente(int n_cliente){
        this.n_cliente = n_cliente;
    }

    public int getN_cliente() {
        return n_cliente;
    }
    
    

    public static void main(String[] args) {
       MarcoCliente miMarco = new MarcoCliente();
       miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private int n_cliente;
    
}


//Esta clase crea el marco visualizar el comportamiento del cliente
class MarcoCliente extends JFrame{
    
    private Socket socket=null;
    private DataOutputStream flujo_salida=null;
    private DataInputStream  flujo_entrada=null;
    
    public MarcoCliente(){
        setSize(300,200);
        setLocationRelativeTo(null);
        setTitle(" - CLIENTE - ");
        setResizable(false);
        
        try {
            //para enviar el número al servidor debo crear un socket
            socket = new Socket("localhost",2000);
            //creamos los flujos para comunicarnos con el servidor
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            //creamos un flujo de entrada
            flujo_entrada = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(MarcoCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LaminaCliente lamina = new LaminaCliente(socket,flujo_salida,flujo_entrada);
        add(lamina);
        
        
        setVisible(true);
    }
}

/*Esta clase contine los campos para recoger el número y visualizar la
  respuesta del servidor
*/
class LaminaCliente extends JPanel{
    
    public LaminaCliente(Socket socket,DataOutputStream flujo_salida,DataInputStream flujo_entrada){
        this.socket = socket;
        this.flujo_salida = flujo_salida;
        this.flujo_entrada = flujo_entrada;
        
        setLayout(new BorderLayout());
        
        JPanel datos = new JPanel();
        
        lb_numero = new JLabel("Adivina número: ");
        datos.add(lb_numero);
        
        txt_numero = new JTextField(3);
        datos.add(txt_numero);
        
        enviar = new JButton("Comprobar");
        EnviaNumero accion = new EnviaNumero();
        enviar.addActionListener(accion);
        datos.add(enviar);
        
        JPanel respuesta = new JPanel();
        respuesta.setLayout(new FlowLayout());
        lb_respuesta = new JLabel();
       
        respuesta.add(lb_respuesta);
        
        add(datos,BorderLayout.NORTH);
        add(respuesta, BorderLayout.CENTER);
     
    }

    /*Esta clase es la que crea el socket y los flujos para mantener
      la comunicación con el servidor
    */
    private class EnviaNumero implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            try {
                if (validarNumero(txt_numero.getText())){
                    int numero = Integer.parseInt(txt_numero.getText());
                   
                    //mandamos por el flujo el número
                    flujo_salida.writeInt(numero);
                    //leemos el flujo y visualizamos la respuesta
                    String respuesta = flujo_entrada.readUTF();
                    lb_respuesta.setText(respuesta);
                    
                    //si la respuesta es ¡¡¡ Has Acertado !!! inhabilitamos el botón comparar
                    if( respuesta.equals("¡¡¡ Has Acertado !!!")){
                        
                       //cerramos los flujos
                        flujo_salida.close();
                        flujo_entrada.close();
                        //cerramos el socket
                        socket.close();
                        
                        //deshabilitamos el botone enviar
                        enviar.setEnabled(false);
                    
                    } 
                    
                    
                }else{
                    JOptionPane.showMessageDialog(null, "Error. Dato no numérico", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
         
        }
        
    }
    
    private boolean validarNumero(String cadena){
        int nlong = 0;
        boolean esNumero = true;
        while (nlong<cadena.length() && esNumero){
            if (!Character.isDigit(cadena.charAt(nlong)))
                return false;
            nlong++;
        }
        
        return true;
    }
    
    private JLabel lb_numero, lb_respuesta;
    private JTextField txt_numero;
    private JButton enviar;
    private DataOutputStream flujo_salida=null;
    private DataInputStream flujo_entrada=null;
    private Socket socket;
   
}