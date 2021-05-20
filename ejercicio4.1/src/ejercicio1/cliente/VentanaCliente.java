
package ejercicio1.cliente;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author AnaIglesias
 */
public class VentanaCliente extends JFrame implements Runnable{
    private JPanel panelPrincipal;
    private JLabel lb_numero;
    private JTextField txt_numero;
    private JTextArea areaTexto;
    private JButton enviar;
    private static int idCliente;
    private int numero;
    private Socket socket;
    private DataInputStream flujo_entrada;
    private DataOutputStream flujo_salida;
    
    public VentanaCliente(){
        
        setSize(350,250);
        setLocationRelativeTo(null);
        setResizable(false);
        
        panelPrincipal = new JPanel(new BorderLayout());
        
        JPanel datos = new JPanel();
        
        lb_numero = new JLabel("Adivina número: ");
        datos.add(lb_numero);
        
        txt_numero = new JTextField(3);
        datos.add(txt_numero);
        
        enviar = new JButton("Comprobar");
        EnviaNumero accion = new EnviaNumero();
        enviar.addActionListener(accion);
        datos.add(enviar);
        
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);
        //hacemos que el JscrollPane se desplace automaticamente según se va llenando
        DefaultCaret crList = (DefaultCaret) areaTexto.getCaret();
        crList.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
       
        panelPrincipal.add(datos,BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        
        add(panelPrincipal);
        
        setVisible(true);
        
    }

    
    /*Esta clase es la que crea el socket y los flujos para mantener
      la comunicación con el servidor
    */
    private class EnviaNumero implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
                if (validarNumero(txt_numero.getText())){
                    try {
                        int numero = Integer.parseInt(txt_numero.getText());
                        flujo_salida.writeInt(numero);
                        String recibido = flujo_entrada.readUTF();
                        //lb_respuesta.setText(recibido);
                        areaTexto.append(numero+" "+recibido+"\n");
                        txt_numero.setText("");
                        if(recibido.equals("¡¡¡ Has Acertado !!!")){    
                                
                            flujo_entrada.close();
                            flujo_salida.close();
                            socket.close();
                            enviar.setEnabled(false);
                            
                        }    
                        
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    
                }else{
                    txt_numero.setText("");
                    JOptionPane.showMessageDialog(null, "Error. Dato no numérico o campo vacío", "Error", JOptionPane.ERROR_MESSAGE);
                }
           
        }
        
    }
    
    private boolean validarNumero(String cadena){
        int nlong = 0;
        boolean esNumero = true;
        if (cadena.length()!=0){
            while (nlong<cadena.length() && esNumero){
                if (!Character.isDigit(cadena.charAt(nlong)))
                    return false;
                nlong++;
            }
        }else{
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        try{
            //establemcemos la conexión
            socket = new Socket("localhost",2000);
            
            //creamos los flujos de entrada y salida
            flujo_entrada = new DataInputStream(socket.getInputStream());
            flujo_salida = new DataOutputStream(socket.getOutputStream());
            
            //recogemos el número de cliente conectado y visualizamos el título de la ventana
            this.setTitle("Cliente "+flujo_entrada.readInt()+". Adivina Número");
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        
        VentanaCliente ventana = new VentanaCliente();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new Thread(ventana).start();
        
        
    }
    
}
