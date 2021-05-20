package ejercicio02SinVentanas;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author AnaIglesias
 */
public class ClienteSinVentanas extends Thread{
    
    private static int nCliente;
    private Socket socket;
    private DataInputStream flujo_entrada;
    private DataOutputStream flujo_salida;

    @Override
    public void run() {
        String ficheroABuscar;
        boolean encontrado = false;
        Scanner entrada = new Scanner(System.in);
        
        try {
            // creamos un socket para enviar el nombre del fichero al servidor
            socket = new Socket("localhost",1500);
            
            //creamos los flujos de entrada y salida
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
            //recibimos el número de Cliente
            nCliente = flujo_entrada.readInt();
            System.out.println("Cliente "+nCliente+" conectado. Esperando petición...");
            
            do{
                
                System.out.print("Nombre del fichero: ");
                ficheroABuscar = entrada.nextLine();
                
                if (!ficheroABuscar.isEmpty()){    
                    
                    //mandamos por el flujo de salida  el nombre del fichero
                    flujo_salida.writeUTF(ficheroABuscar);
                    System.out.println(ficheroABuscar);
                    //recibimos la respuesta del servidor
                    encontrado = flujo_entrada.readBoolean();
                    
                    if (encontrado){
                        
                        System.out.println("Fichero encontrado. Recibiendo fichero...");
                        //vemos cuál es la longitud del fichero
                        byte [] longFichero = new byte[(int)ficheroABuscar.length()];
                        
                        String nombreFichero = obtenerNombreFichero(ficheroABuscar);
                        String extensionFichero = obtenerExtensionFichero(ficheroABuscar);
                        
                       //creamos una copia del fichero para ver que se envía
                        File fileCopia = new File(nombreFichero+"Copia"+extensionFichero);
                        
                        //cramos un buffer para leer el contenido del fichero
                        BufferedInputStream fis = new BufferedInputStream(socket.getInputStream());
                        FileOutputStream fos = new FileOutputStream(fileCopia);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        int n;
                        
                        while((n = fis.read(longFichero)) != -1){
                            bos.write(longFichero,0, n);
                            bos.flush();
                        }
                        
                        bos.close();
                        fis.close();
                        
                        /*leemos el fichero que se acaba de enviar y 
                          visualizamos su contenido en el textArea. Para ello
                          creamos un buffer de lectura 
                        */  
                        if (extensionFichero.equals(".jpg")){
                            System.out.println("El fichero es una imagen, no se puede visualizar.");
                        }else{  
                            //creamos un buffer para leer el fichero copia 
                            BufferedReader br = new BufferedReader(new FileReader(fileCopia));
                            String linea;
                            while((linea=br.readLine())!=null){
                 
                                System.out.println(linea);;
                           
                            }
                            //cerramos el buffer
                            br.close();
                        }   
                        
                    }else{
                        System.out.println("Fichero no encontrado");
                    }
                    
                }else{
                    System.out.println("\nDebe introducir el fichero a buscar");
                    
                }
                
            }while(!encontrado);
            
            //cerramos los flujos y el socket
            System.out.println("Fin de la ejecución para el cliente "+nCliente);
            flujo_entrada.close();
            flujo_salida.close();
            socket.close();
            System.out.println("Cliente "+nCliente+" desconectado");
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private static String obtenerNombreFichero(String text) {
        String nombre = "";
        for (int i=0;i<text.length();i++){
            if(text.charAt(i)!= '.'){
                nombre = nombre + text.charAt(i);
            }else{
                i = text.length();
        
            }
        }
            
        return nombre;
    }

    private static String obtenerExtensionFichero(String text) {
        String extension = "";
        boolean ext = false;
        for (int i=0;i<text.length();i++){
            if (text.charAt(i)=='.'){
                ext = true;
            }
            if (ext){
                extension += text.charAt(i);
            }
        }
        return extension;
    }
    
    public static void main(String[] args) {
        ClienteSinVentanas cliente = new ClienteSinVentanas();
        cliente.start();
    }
    
}
