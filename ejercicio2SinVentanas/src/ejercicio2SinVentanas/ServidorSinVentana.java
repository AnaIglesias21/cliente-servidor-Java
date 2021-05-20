package ejercicio2SinVentanas;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AnaIglesias
 */
public class ServidorSinVentana {

    public static void main(String[] args) {
        ServerSocket servidor;
        Socket socket;
        DataInputStream flujo_entrada;
        DataOutputStream flujo_salida;
        boolean encontrado = false;
        
        try {
            //iniciamos el servidor
            servidor = new ServerSocket(1500);
            System.out.println("Servidor iniciado...");
            
            //aceptamos el cliente
            socket = servidor.accept();
            System.out.println("Cliente conectado. Esperando petición...");
            
            //creamos los fujos de entrada y salida
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
            while (!encontrado){
                //leemos y visualizamos contenido del flujo
                String ficheroBuscado = flujo_entrada.readUTF();
                
                System.out.println("Buscando fichero "+ficheroBuscado);
                //creo un objeto tipo file con el nombre del fichero
                File f = new File(ficheroBuscado);
                
                if (f.exists()){
                    //notificamos al cliente que se ha encontrado el fichero
                    flujo_salida.writeBoolean(true);
                    
                    System.out.println("Fichero "+ficheroBuscado+" encontrado. Enviando al cliente...");
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
                    System.out.println("Fichero enviado con éxito");
                    
                }else{
                    System.out.println("Fichero "+ficheroBuscado+" no encontrado");
                    //si el fichero no se encuentra se envía un mensaje al cliente
                    flujo_salida.writeBoolean(false);
                }
                
            }
            
            //cerramos flujos, socket y servidor
            flujo_salida.close();
            flujo_entrada.close();
            socket.close();
            servidor.close();
            
            System.out.println("Fin de ejecución. Servidor desconectado");
            
        } catch (IOException ex) {
            Logger.getLogger(ServidorSinVentana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
