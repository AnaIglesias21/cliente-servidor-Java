/*
 * Servidor que genera un número aleatorio y espera que el cliente acierte cual
 * es el número. Recibe el número del cliente y le indica si el número es
 * mayor, menor o igual  al generado. 
 */
package ejercicio1SinVentanas;

import java.io.*;
import java.net.*;

/**
 *
 * @author AnaIglesias
 */
public class Servidor {
    
    public static void main(String[] args) {
        
        ServerSocket servidor;
        Socket socket;
        DataInputStream flujo_entrada;
        DataOutputStream flujo_salida;
        int numero, numBuscado = 0;
        
        try {
            //iniciamos el servidor
            servidor = new ServerSocket(2000);
            System.out.println("Servidor iniciado...");
            //aceptamos un cliente
            socket = servidor.accept();
            //indicamos que se ha conectado un cliente
            System.out.println("Cliente conectado. Generando número aleatorio");
            //creamos los flujos
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            //generamos el número aleatorio
            numero = (int)(Math.random()*100);
            System.out.println("Número generado: "+numero); 
            //leemos el número introducido por el cliente
            numBuscado = flujo_entrada.readInt();
            while(numero!=numBuscado){
                System.out.println("Número enviado por el cliente: "+numBuscado);
                if (numBuscado > numero){
                    flujo_salida.writeUTF("EL número a adivinar es menor al introducido");
                }else{
                    flujo_salida.writeUTF("EL número a adivinar es mayor al introducido");
                }
                numBuscado = flujo_entrada.readInt();
            }
            System.out.println("El cliente ha adivinado el número");
            flujo_salida.writeUTF("¡¡¡ Has Acertado !!!");
            System.out.println("Ejecución finalizada. Servidor desconectado.");
            //cerramos los flujos y el socket
            flujo_entrada.close();
            flujo_salida.close();
            socket.close();
            
            
        } catch (IOException ex) {
            System.out.println("Error al iniciar el servidor");
        }
        
        
        
    }
    
}
