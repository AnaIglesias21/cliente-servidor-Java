package ejercicio02SinVentanas;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AnaIglesias
 */
public class ServidorHilos extends Thread{
    
    private int nCliente;
    private Socket socket;
    private DataInputStream flujo_entrada;
    private DataOutputStream flujo_salida;
    
    public ServidorHilos(int nCliente, Socket socket) {
        this.nCliente = nCliente;
        this.socket = socket;
        
        try{
            //creamos los flujos de entrada y salida de datos
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }

    @Override
    public void run() {
        boolean encontrado = false;
        
        try {
            //enviamos al cliente el número de  Cliente
            flujo_salida.writeInt(nCliente);
            
            while (!encontrado){
                //leemos y visualizamos contenido del flujo
                String ficheroBuscado = flujo_entrada.readUTF();
                
                System.out.println("Buscando fichero "+ficheroBuscado+" para el cliente "+nCliente);
                //creo un objeto tipo file con el nombre del fichero
                File f = new File(ficheroBuscado);
                
                if (f.exists()){
                    //notificamos al cliente que se ha encontrado el fichero
                    flujo_salida.writeBoolean(true);
                    
                    System.out.println("Fichero "+ficheroBuscado+" encontrado. Enviando al cliente "+nCliente+"...");
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
                    System.out.println("Fichero enviado con éxito al cliente "+nCliente);
                    
                }else{
                    System.out.println("Fichero "+ficheroBuscado+" para el cliente "+nCliente+" no encontrado");
                    //si el fichero no se encuentra se envía un mensaje al cliente
                    flujo_salida.writeBoolean(false);
                }
                
            }
            
        
            //cerramos flujos, socket y servidor
            flujo_salida.close();
            flujo_entrada.close();
            socket.close();
            
            System.out.println("Cliente "+nCliente+" desconectado");
            
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilos.class.getName()).log(Level.SEVERE, null, ex);
        }
            
           
    }
}
