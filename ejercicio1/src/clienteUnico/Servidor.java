package clienteUnico;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author anaig
 */
public class Servidor{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MarcoServidor miMarco = new MarcoServidor();
        miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
}
/*Para que la aplicación esté a la escucha permanentemente debe implementar
  la interfaz Runnable 
*/ 
class MarcoServidor extends JFrame{
    
    private JTextArea areaTexto;
    private DataInputStream flujo_entrada = null;
    private DataOutputStream flujo_salida = null;
    private int numero;
    private boolean encontrado;
    
    public MarcoServidor(){
        setSize(300,200);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(" - SERVIDOR - ");
        
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);
        //hacemos que el JscrollPane se desplace automaticamente según se va llenando
        DefaultCaret crList = (DefaultCaret) areaTexto.getCaret();
        crList.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        add(scroll);
        setVisible(true);
        
        try {
            ServerSocket servidor = new ServerSocket(2000);
            areaTexto.append("Servidor iniciado\n");
            int aleatorio = (int)(Math.random()*100);
            
            areaTexto.append("Número generado: "+aleatorio+"\n");
            boolean encontrado=false;
            //acepto un cliente
            Socket socket = servidor.accept();
            areaTexto.append("Conectado cliente\n");
            //creamos los fujos de entrada y salida
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
            while(!encontrado){
                numero = flujo_entrada.readInt();
                areaTexto.append("Número recibido del cliente: "+numero+"\n");
                if (numero<aleatorio){
                    flujo_salida.writeUTF("El número enviado es menor");
                }else if (numero>aleatorio){
                    flujo_salida.writeUTF("El número enviado es mayor");
                    }else{
                      encontrado = true;
                      flujo_salida.writeUTF("¡¡¡ Has Acertado !!!");
                     
                      areaTexto.append("Cliente desconectado\n");
                      
                    }
            }
            
            //cerramos el socket
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }

}
