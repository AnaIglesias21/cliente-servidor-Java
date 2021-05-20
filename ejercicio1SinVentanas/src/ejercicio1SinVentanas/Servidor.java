package ejercicio1SinVentanas;

import java.io.*;
import java.net.*;

/**
 *
 * @author AnaIglesias
 */
public class Servidor {
    
    public static void main(String[] args) {
        int nCliente = 0;
        ServerSocket servidor;
        Socket socket;
       
        try {
            servidor = new ServerSocket(2000);
            System.out.println("Servidor iniciado...");
            
            while(true){
                //aceptamos un cliente
                socket = servidor.accept();
                
                //informar del número de cliente conectado
                nCliente++;
                System.out.println("Cliente "+nCliente+" conectado...");
                
                //atendemos los clientes
                ServidorHilos atenderCliente = new ServidorHilos(nCliente,socket);
                atenderCliente.start();
                
            }
            
        } catch (IOException ex) {
            System.out.println("Error al iniciar el servidor");
        }
    }
    
}
