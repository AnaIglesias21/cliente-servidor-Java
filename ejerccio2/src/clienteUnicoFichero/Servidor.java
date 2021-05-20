package clienteUnicoFichero;

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
public class Servidor {

    public static void main(String[] args) {
        MarcoServidor marcoServidor = new MarcoServidor();
        marcoServidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}

class MarcoServidor extends JFrame{

    private JTextArea areaTexto;
    private DataInputStream flujo_entrada = null;
    private DataOutputStream flujo_salida = null;
    private boolean encontrado;
    private File f;
    private Socket socket;
    
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
        //añadimos el jscrollpane al frame
        add(scroll);
        setVisible(true);  
         
        try {
            ServerSocket servidor = new ServerSocket(1500);
            areaTexto.append("Servidor iniciado\n");
            //acepto un cliente
            socket = servidor.accept();
            areaTexto.append("Conectado cliente\n");
            //creamos los fujos de entrada y salida
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
            while(!encontrado){
                 
                //leemos y visualizamos contenido del flujo
                String ficheroBuscado = flujo_entrada.readUTF();
                areaTexto.append(ficheroBuscado+"\n");
                
                //creo un objeto tipo file con el nombre del fichero
                f = new File(ficheroBuscado);
                flujo_salida = new DataOutputStream(socket.getOutputStream());
                //comprobamos que el fichero existe
                if (f.exists()){
                    
                    areaTexto.append("Fichero "+ficheroBuscado+" encontrado. Enviando fichero ...");
                   
                    //mandamos un mensaje al cliente el mensaje de que se ha encontrado el archivo
                    flujo_salida.writeUTF("El fichero "+ficheroBuscado+" se ha encontrado");
                    //necesitamos conocer la longuitud del fichero en bytes
                    byte [] longFichero = new byte[(int)ficheroBuscado.length()];
                    
                    //necesitamos un flujo de lectura
                    FileInputStream flujoLectura = new FileInputStream(ficheroBuscado);
                    //necesitamos un lector de ficheros
                    BufferedInputStream bf = new BufferedInputStream(flujoLectura);
                    //creamos el flujo para enviar el fichero
                    OutputStream os = socket.getOutputStream();
                    //leemos el fichero 
                    int n;
                    while ((n = bf.read(longFichero)) != -1){
                        
                        os.write(longFichero,0,n);
                        //nos aseguramos que se escribe todo lo qque hay en buffer
                        os.flush();
                    }
                    
                    encontrado = true;
                   
                }else{
                    
                    //si el fichero no se encuentra se envía un mensaje al cliente
                    flujo_salida.writeUTF("El fichero "+ficheroBuscado+" no se ha encontrado");
                }
                
                if (encontrado){
                    //cerramos flujos
                    socket.close();
                    flujo_entrada.close();
                    flujo_salida.close();
                    //cerramos la aplicación servidor
                    System.exit(0);
                   
                }
                
            }  
            
        } catch (IOException ex) {
           ex.printStackTrace();
        }  
    }
    
   
}