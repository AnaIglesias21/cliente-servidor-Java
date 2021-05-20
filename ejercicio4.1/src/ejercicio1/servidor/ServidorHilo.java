package ejercicio1.servidor;

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
    
    @Override
    public void run(){
        try{
           //enviamos al cliente el id Cliente para visualizarlo en la ventana
           flujo_salida.writeInt(idCliente); 
           int aleatorio = (int)(Math.random()*100);
           JTextArea area=vs.getAreaTexto();
           area.append("Número aleatorio generado para cliente "+idCliente+": "+aleatorio+"\n");
           
           boolean acertado = false;
           while(!acertado){
               int n=flujo_entrada.readInt();
               area.append("Número enviado por el cliente "+idCliente+": "+n+"\n");
               
               if (n<aleatorio){
                   flujo_salida.writeUTF("es menor");
               }else if(n>aleatorio){
                   flujo_salida.writeUTF("es mayor");
               }else{
                   
                   flujo_salida.writeUTF("¡¡¡ Has Acertado !!!");
                   acertado=true;
               }
           }
           area.append("Cliente "+idCliente+ " desconectado.¡Ha acertado!\n");
           flujo_entrada.close();
           flujo_salida.close();
           socket.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
}
