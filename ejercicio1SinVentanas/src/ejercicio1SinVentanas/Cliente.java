package ejercicio1SinVentanas;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AnaIglesias
 */
public class Cliente extends Thread{
    
    private static int nCliente;
    private Socket socket;
    private DataInputStream flujo_entrada;
    private DataOutputStream flujo_salida; 

    @Override
    public void run() {
        Scanner entrada = new Scanner(System.in);
        int numBuscar;
        String respuesta;
        
        try {
            //creamos el socket
            socket = new Socket("localhost",2000);
            
            //creamos los flujos de entrada y salida
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
            //recibimos el número de Cliente
            nCliente = flujo_entrada.readInt();
            System.out.println("Cliente "+nCliente+" conectado...");
            
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
            System.out.println("Fin de la ejecución para el cliente "+nCliente);
            flujo_entrada.close();
            flujo_salida.close();
            socket.close();
            System.out.println("Cliente "+nCliente+" desconectado");
            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.start();
    }
    
}
