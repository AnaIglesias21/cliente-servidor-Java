package ejercicio2.servidor;

import java.net.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author AnaIglesias
 */
public class VentanaServidor extends JFrame{
    private JTextArea areaTexto;
    
    public VentanaServidor(){
        setSize(350,400);
        setTitle("Busca Fichero. Servidor");
        setLocationRelativeTo(null);
        setResizable(false);
        
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);
        //hacemos que el JscrollPane se desplace automaticamente según se va llenando
        DefaultCaret crList = (DefaultCaret) areaTexto.getCaret();
        crList.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        add(scroll);
        
        setVisible(true);
    }
        
    public JTextArea getAreaTexto() {
        return areaTexto;
    }
    
    public static void main(String[] args) {
        
        int nCliente=0;
        
        try{
            //visualizamos el frame del servidor
            VentanaServidor vs = new VentanaServidor();
            vs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //establecemos el servidor
            ServerSocket servidor = new ServerSocket(1500);
            vs.areaTexto.append("Servidor iniciado ...\n");
            
            while(true){
                //aceptamos un nuevo Cliente
                Socket socket = servidor.accept();
                nCliente++;
                vs.areaTexto.append("Cliente "+nCliente+" conectado\n");
                ServidorHilo atenderCliente = new ServidorHilo(socket,nCliente,vs);
                atenderCliente.start();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }    
    }
    
}
