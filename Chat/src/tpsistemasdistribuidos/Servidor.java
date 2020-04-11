package tpsistemasdistribuidos;

import javax.swing.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoServidor mimarco = new MarcoServidor();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class MarcoServidor extends JFrame implements Runnable {

    private JTextArea areatexto;

    public MarcoServidor() {

        setBounds(1200, 300, 280, 350);

        JPanel milamina = new JPanel();

        milamina.setLayout(new BorderLayout());

        areatexto = new JTextArea();

        milamina.add(areatexto, BorderLayout.CENTER);

        add(milamina);

        setVisible(true);

        Thread mihilo = new Thread(this);

        mihilo.start();

    }

    @Override
    public void run() {

        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            //System.out.println("Estoy a la escucha");
            ServerSocket servidor = new ServerSocket(9999);
            
            String nick, ip, mensaje;
            
            PaqueteEnvio paquete_recibido;
            
            while (true) {//se pone a la escucha el servidor
                
                Socket misocket = servidor.accept();
                
                ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());
                
                paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();
                
                //almacenamos la informacion recibida desde un cliente
                nick = paquete_recibido.getNick();  
                ip = paquete_recibido.getIP();
                mensaje = paquete_recibido.getMensaje();
                
                
                
                //DataInputStream flujo_entrada = new DataInputStream(misocket.getInputStream());

                //String mensaje_texto = flujo_entrada.readUTF();
                
                if (!mensaje.equals("online")) {

	                areatexto.append(nick + ": " + mensaje + " para " + ip + "\n");
	                
	                Socket enviaDestinatario = new Socket(ip, 9090);
	                
	                ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
	                
	                paqueteReenvio.writeObject(paquete_recibido);
	                
	                paqueteReenvio.close();
	                
	                enviaDestinatario.close();
	
	                misocket.close();
                }else {
                	
                    //------------DETECTA ONLINE--------------
                    
                    InetAddress localizacion = misocket.getInetAddress();
                    
                    String ipRemota = localizacion.getHostAddress();
                    
                    System.out.println("Online " + ipRemota);
                    
                    //------------------------------------------
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);//excepcion del metodo readObject()
        }

    }
}