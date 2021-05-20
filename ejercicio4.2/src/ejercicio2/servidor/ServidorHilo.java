package ejercicio2.servidor;

import java.io.*;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 *
 * @author AnaIglesias
 */
public class ServidorHilo extends Thread{
    private Socket socket;
    private DataInputStream flujo_entrada;
    private DataOutputStream flujo_salida;
    private int idCliente;
    private VentanaServidor vs;
    
    public ServidorHilo(Socket socket, int idCliente,VentanaServidor vs) {
        
        this.socket = socket;
        this.idCliente = idCliente;
        this.vs=vs;
        
        try{
            //creamos los flujos de entrada y salida de datos
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    public void desconectar(){
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void run(){
        boolean encontrado = false;
        File f;
        String ficheroBuscado;
        try{
            //enviamos al cliente el id Cliente para visualizarlo en la ventana
            flujo_salida.writeInt(idCliente);
            JTextArea area=vs.getAreaTexto();
            
            while(!encontrado){
                
                
                //leemos y visualizamos conetenido del flujo
                ficheroBuscado = flujo_entrada.readUTF();
                area.append("Archivo solicitado por el cliente "+idCliente+": "+ficheroBuscado+"\n");
                
                //creo un objeto tipo file con el nombre del fichero
                f = new File(ficheroBuscado);
                if (f.exists()){
                    flujo_salida.writeUTF("Fichero encontrado. Enviando "+ficheroBuscado+"...");
                    //enviamos el identificador del cliente para crear el fichero copia
                    flujo_salida.writeUTF("Cliente"+idCliente);
                    
                    //necesitamos conocer la longuitud del fichero en bytes
                    byte [] longFichero = new byte[(int)ficheroBuscado.length()];
                    //enviammos al cliente el tamaño del archivo
                    flujo_salida.writeInt(ficheroBuscado.length());
                    //necesitamos un flujo de lectura
                    FileInputStream flujoLectura = new FileInputStream(ficheroBuscado);
                    //necesitamos un lector de ficheros
                    BufferedInputStream bf = new BufferedInputStream(flujoLectura);
                    //creamos el flujo para enviar el fichero
                    OutputStream os = socket.getOutputStream();
                    //leemos el fichero 
                    int n;
                    area.append("Fichero encontrado. Enviando fichero ...\n");
                    //dormimos el hilo para simular el envio del fichero
                    Thread.sleep(3000);
                    while ((n = bf.read(longFichero)) != -1){
                        
                        os.write(longFichero,0,n);
                        //nos aseguramos que se escribe todo lo qque hay en buffer
                        os.flush();
                        
                    }
                    area.append("Fichero "+ficheroBuscado+ " (" +
                            longFichero.length+ " bytes)enviado con éxito al Cliente "+idCliente+"\n");
                    encontrado = true;
                    
                    //cerramos los flujos de transmision del fichero
                    flujoLectura.close();
                    bf.close();
                    os.close();
                }else{
                    //mandamos un mensaje al cliente de que no se ha encontrado el archivo
                    flujo_salida.writeUTF("El fichero "+ficheroBuscado+" no se ha encontrado.");
                }
                
            }  
            //cerramos los flujos de comunicación cliente-servidor
            flujo_entrada.close();
            flujo_salida.close();
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
