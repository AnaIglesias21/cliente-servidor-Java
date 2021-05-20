package ejercicio01VariosClientes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AnaIglesias
 */
class ServidorHilos extends Thread{
    
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
        int numBuscado;
        try {
            //enviamos al cliente el número de  Cliente
            flujo_salida.writeInt(nCliente);
            //generamos el número aleatorio para el cliente conectado al servidor
            int aleatorio = (int)(Math.random()*100);
            //visualizamos el número aleatorio
            System.out.println("Número aleatorio para el cliente "+nCliente+": "+aleatorio);
            
            //leemos el número introducido por el cliente
            numBuscado = flujo_entrada.readInt();
            
            while(aleatorio!=numBuscado){
                
                System.out.println("Número enviado por el cliente "+nCliente+": "+numBuscado);
                
                if (numBuscado > aleatorio){
                    flujo_salida.writeUTF("EL número a adivinar es menor al introducido");
                }else{
                    flujo_salida.writeUTF("EL número a adivinar es mayor al introducido");
                }
                
                numBuscado = flujo_entrada.readInt();
            }
            
            System.out.println("El cliente "+nCliente+" ha adivinado el número");
            flujo_salida.writeUTF("¡¡¡ Has Acertado !!!");
            System.out.println("Ejecución finalizada para el cliente "+nCliente);
            //cerramos los flujos y el socket
            flujo_entrada.close();
            flujo_salida.close();
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
