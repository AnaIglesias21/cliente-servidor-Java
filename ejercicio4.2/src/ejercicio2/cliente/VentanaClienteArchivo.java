package ejercicio2.cliente;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author AnaIglesias
 */
public class VentanaClienteArchivo extends JFrame implements Runnable{
    private JPanel panelPrincipal;
    private JLabel lb_fichero, lb_mensaje;
    private JTextField txt_fichero;
    private JTextArea contenido;
    private JButton btn_buscar;
    private JScrollPane scroll;
    private Socket socket;
    private File fileCopia;
    private String idCliente;
    private DataInputStream flujo_entrada;
    private DataOutputStream flujo_salida;
    
    public VentanaClienteArchivo(){
        
        setSize(350,250);
       // setTitle("Cliente "+idCliente);
        setLocationRelativeTo(null);
        setResizable(false);
        
        panelPrincipal = new JPanel(new BorderLayout());
        
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
        
        
        contenido = new JTextArea();
        contenido.setEditable(false);
        scroll = new JScrollPane(contenido);
        //hacemos que el JscrollPane se desplace automaticamente según se va llenando
        DefaultCaret crList = (DefaultCaret) contenido.getCaret();
        crList.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
       
        linea.add(fila1);
        linea.add(fila2);
     
        panelPrincipal.add(linea,BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        
        add(panelPrincipal);
        
        setVisible(true);
        
    }
    private class BuscarFichero implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            lb_mensaje.setText("");
            
            try {
               
                //mandamos por el flujo de salida  el nombre del fichero
                flujo_salida.writeUTF(txt_fichero.getText());
                //recibimos la respuesta del servidor
                String mensaje = flujo_entrada.readUTF();
                  
                if (!mensaje.contains("no")){
                    lb_mensaje.setText(mensaje);
                    
                    //creamos una copia del fichero para ver que se envía
                    String nombreFichero = obtenerNombreFichero(txt_fichero.getText());
                    String extensionFichero = obtenerExtendsioFeichero(txt_fichero.getText());
                    idCliente = flujo_entrada.readUTF();
                    fileCopia = new File(nombreFichero+"Copia"+idCliente+extensionFichero);
                    
                    int longitud = flujo_entrada.readInt();
                    //vemos cuál es la longuitud del fichero
                    byte [] longFichero = new byte[longitud];
                    
                    //creamos los flujos para leer un fichero y escribir la copia
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
                    lb_mensaje.setText("Contenido del fichero "+txt_fichero.getText());
                        
                    if (extensionFichero.equals(".jpg")){
                        contenido.setVisible(false);
                        
                        JLabel etiquetaImagen = new JLabel();
                        Icon imagen = new ImageIcon(nombreFichero+"Copia"+idCliente+extensionFichero);
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
                    
                    //deshabiliymos el botón buscar
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

        private String obtenerExtendsioFeichero(String text) {
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
    public static void main(String[] args) {
        VentanaClienteArchivo ventana = new VentanaClienteArchivo();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new Thread(ventana).start();
    }

    @Override
    public void run() {
        try {
            // creamos un socket para enviar el nombre del fichero al servidor
            socket = new Socket("localhost",1500);
            /*creamos un stream de salida para pasar al servidor
              el nombre del fichero a buscar
            */    
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            flujo_entrada = new DataInputStream(socket.getInputStream());
            
            //recogemos el número de cliente conectado y visualizamos el título de la ventana
            this.setTitle("Cliente "+flujo_entrada.readInt()+". Busca Fichero.");
            
        }catch(Exception ex){
            ex.printStackTrace();
        }        
    }
    
}
