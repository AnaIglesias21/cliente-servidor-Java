package clienteUnicoFichero;

import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


/**
 *
 * @author anaig
 */
public class Cliente {

    public static void main(String[] args) {
        MarcoCliente marcoCliente = new MarcoCliente();
        marcoCliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}

class MarcoCliente extends JFrame{
    
    private Socket socket=null;
    private DataOutputStream flujo_salida=null;
    private DataInputStream  flujo_entrada=null;
    
    public MarcoCliente(){
        setSize(350,250);
        setTitle("Busca Fichero. Cliente");
        setLocationRelativeTo(null);
        setResizable(false);
         
        try{
            // creamos un socket para enviar el nombre del fichero al servidor
            socket = new Socket("localhost",1500);
            /*creamos un stream de salida para pasar al servidor
              el nombre del fichero a buscar
            */    
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            flujo_entrada = new DataInputStream(socket.getInputStream());
            
        }catch (IOException ex) {
            Logger.getLogger(MarcoCliente.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        
        LaminaCliente laminaCliente = new LaminaCliente(socket,flujo_salida,flujo_entrada);
        add(laminaCliente);
        
        setVisible(true);
    }
}

class LaminaCliente extends JPanel{
    
    private DataOutputStream flujo_salida=null;
    private DataInputStream flujo_entrada=null;
    private Socket socket;
    
    private JLabel lb_fichero, lb_mensaje;
    private JTextField txt_fichero;
    private JTextArea contenido;
    private JButton btn_buscar;
    private JScrollPane scroll;
    private File fileCopia;
    
    public LaminaCliente(Socket socket, DataOutputStream flujo_salida, DataInputStream flujo_entrada){
        this.socket = socket;
        this.flujo_salida = flujo_salida;
        this.flujo_entrada = flujo_entrada;
        
        setLayout(new BorderLayout());
        
        JPanel linea = new JPanel();
        //linea.setLayout(new FlowLayout());
        linea.setLayout(new GridLayout(2,0));
        
        JPanel fila1 = new JPanel();
        lb_fichero = new JLabel("Fichero: ");
        fila1.add(lb_fichero);
        
        txt_fichero = new JTextField(10);
        fila1.add(txt_fichero);
        
        btn_buscar = new JButton("Buscar");
        BuscarFichero accion = new BuscarFichero();
        btn_buscar.addActionListener(accion);
        fila1.add(btn_buscar);
        
        JPanel fila2 = new JPanel();
        lb_mensaje = new JLabel();
        fila2.add(lb_mensaje);
        
        linea.add(fila1);
        linea.add(fila2);
        
        contenido = new JTextArea();
        contenido.setEditable(false);
        contenido.setBackground(Color.DARK_GRAY);
        contenido.setForeground(Color.LIGHT_GRAY);
      
        scroll = new JScrollPane (contenido,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);        
       
        add(linea, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
    
    private class BuscarFichero implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            lb_mensaje.setText("");
            
            try {
                
                //mandamos por el flujo de salida  el nombre del fichero
                flujo_salida.writeUTF(txt_fichero.getText());
                //creamos un flujo de entrada para recibir la contestación del servidor
                
                String mensaje = flujo_entrada.readUTF();
                  
                if (!mensaje.contains("no")){
                    lb_mensaje.setText("Contenido del fichero "+txt_fichero.getText());
                    
                    //vemos cuál es la longuitud del fichero
                    byte [] longFichero = new byte[(int)txt_fichero.getText().length()];
                    //creamos una copia del fichero para ver que se envía
                    String nombreFichero = obtenerNombreFichero(txt_fichero.getText());
                    String extensionFichero = obtenerExtensionFichero(txt_fichero.getText());
                    fileCopia = new File(nombreFichero+"Copia"+extensionFichero);
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
                        contenido.setVisible(false);
                        
                        JLabel etiquetaImagen = new JLabel();
                        Icon imagen = new ImageIcon(nombreFichero+"Copia"+extensionFichero);
                        etiquetaImagen.setIcon(imagen);
                        scroll.setViewportView(etiquetaImagen);
                    }else{    
                        BufferedReader br = new BufferedReader(new FileReader(fileCopia));
                        String linea;
                        while((linea=br.readLine())!=null){
                 
                            contenido.append(linea+"\n");
                           
                        }
                        //cerramos el buffer
                        br.close();
                        
                        //cerramos los flujos
                        flujo_entrada.close();
                        flujo_salida.close();
                    }
                    
                    //deshabilitamos el botón buscar
                    btn_buscar.setEnabled(false);
                    //cerramos el socket
                    socket.close();
                    
                }else{
                    //mostramos el mensaje de que no se encontró el 
                   //fichero enviado por el serevidor
                    lb_mensaje.setText(mensaje);
                }
                 
            } catch (IOException ex) {
                ex.printStackTrace();
            }
          
        }

        private String obtenerNombreFichero(String text) {
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

        private String obtenerExtensionFichero(String text) {
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
       
    }
    
}