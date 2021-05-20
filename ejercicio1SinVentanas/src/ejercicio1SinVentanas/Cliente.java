/*
 * El cliente se conecta al servidor y le va manadando números hasta adivinar el
 * generado por el servidor.
 */
package ejercicio1SinVentanas;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author AnaIglesias
 */
public class Cliente {

    public static void main(String[] args) {
        Socket socket;
        DataInputStream flujo_entrada;
        DataOutputStream flujo_salida;
        
        String respuesta = "";
        Scanner entrada = new Scanner(System.in);
        int numBuscar = 0;
        
        try {
            socket = new Socket("localhost",2000);
            System.out.println("Cliente conectado...");
            //creamos los flujos
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
            do{
                //pedimos el número al cliente
                System.out.print("\nIntroduce el número: ");
                if (entrada.hasNextInt()){
                   numBuscar = entrada.nextInt();
                   //enviamos el número al servidor
                   flujo_salida.writeInt(numBuscar);
                   //recibimos la respueta del servidor
                   respuesta = flujo_entrada.readUTF();
                    System.out.println(respuesta);
                }else{
                    System.out.println("No has introducido un número");
                    entrada.next();
                    respuesta = "";
                }
                
            }while (!respuesta.equalsIgnoreCase("¡¡¡ Has Acertado !!!"));
            
            //cerramos los flujos y el socket
            System.out.println("Fin de la ejecución");
            flujo_entrada.close();
            flujo_salida.close();
            socket.close();
            System.out.println("Cliente desconectado");
        } catch (IOException ex) {
            System.out.println("El cliente no ha podido conectarse");
        }
        
    }
    
    
}
