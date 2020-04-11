package tpsistemasdistribuidos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import javax.swing.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoCliente mimarco = new MarcoCliente();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}

class MarcoCliente extends JFrame {

    public MarcoCliente() {

        setBounds(600, 300, 280, 350);

        LaminaMarcoCliente milamina = new LaminaMarcoCliente();

        add(milamina);

        setVisible(true);
        
        addWindowListener(new EnvioOnline());//se dispara el evento de ventana cuando un cliente se conecta a la aplicacion y envia unos datos al servidor automaticamente
        
    }

}

//evento de ventana para enviar al servidor el estado activo de un cliente
class EnvioOnline extends WindowAdapter {
	
	public void windowOpened(WindowEvent e) {
		
		try {
			
			Socket misocket = new Socket("192.168.100.77", 9999);
			
			PaqueteEnvio datos = new PaqueteEnvio();
			
			datos.setMensaje("online");
			
			ObjectOutputStream paquete_datos_inicial = new ObjectOutputStream(misocket.getOutputStream());
			
			paquete_datos_inicial.writeObject(datos);
			
			misocket.close();
						
		}catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
}

class LaminaMarcoCliente extends JPanel implements Runnable{//interfaz

    private JTextField campo1;//campo donde se escribe el texto a enviar
    private JLabel nick; //campo donde se visualizará el nick name del usuario
    private JComboBox ip; //campo donde se visualizará la direccion ip del cliente con el que se está llevando a cabo la conversacion
    private JTextArea campochat;//area de chat
    private JButton miboton; //boton para enviar mensajes

    public LaminaMarcoCliente() {
    	
    	String nick_usuario = JOptionPane.showInputDialog("Ingrese su Ni:");
    	
    	JLabel n_nick = new JLabel("Nick:");
    	add(n_nick);

        nick = new JLabel();
        nick.setText(nick_usuario);
        add(nick);

        JLabel texto = new JLabel("Online:");
        add(texto);

        ip = new JComboBox();//configuramos el cuadro de texto para que aparezca a la izquierda 
        ip.addItem("Usuario1");
        ip.addItem("Usuario2");
        ip.addItem("Usuario3");
        //ip.addItem("192.168.100.46");//ip de la vm Xubuntu
        //ip.addItem("");//ip de la vm Ubuntu Mate
        
        add(ip); //agregamos el cuadro de texto a la lamina(interfaz)

        campochat = new JTextArea(12, 20);//lugar de colocacion del area de texto, las coordenadas son 12 y 20
        add(campochat);//se agrega el campo de texto a la lamina(interfaz)

        campo1 = new JTextField(20); //area donde se escribirá el mensaje a enviar
        add(campo1); //se agrega el campo de texto a la lamina(interfaz)

        miboton = new JButton("Enviar"); //boton para enviar el texto escrito

        EnviaTexto mievento = new EnviaTexto();

        miboton.addActionListener(mievento);
        add(miboton);//se grega el boton a la lamina(interfaz)
        
        Thread mihilo = new Thread(this);
        
        mihilo.start();

    }

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        try{
            
            ServerSocket servidor_cliente = new ServerSocket(9090);
            
            Socket cliente;
            
            PaqueteEnvio paqueteRecibido;
            
            while(true){//el cliente se pone a la escucha
            
                cliente = servidor_cliente.accept();//acepta las conexiones entrantes
                
                ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
                
                paqueteRecibido = (PaqueteEnvio) flujoentrada.readObject();
                
                campochat.append(paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje() + "\n");
            
            }
            
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    
    }

    private class EnviaTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                //System.out.println(campo1.getText());
                
                campochat.append("\n" + campo1.getText());
                
                Socket misocket = new Socket("192.168.100.77", 9090);

                PaqueteEnvio datos = new PaqueteEnvio();

                datos.setNick(nick.getText());//almacenamos el texto escrito en el cuadro de texto para el nick name
                datos.setIP(ip.getSelectedItem().toString());//almacenamos el texto escrito en el cuadro de texto para la ip
                datos.setMensaje(campo1.getText());

                ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());//se crea un objeto para cargarlo posteriormente de los datos a ser enviados

                paquete_datos.writeObject(datos); //se cargan los datos del objeto PaqueteEnvio

                misocket.close(); //se cierra el socket

                //Debemos enviar el objeto datos al servidor ya que él actuará de intermediario entre un cliente y otro
                //DataOutputStream flujo_salida = new DataOutputStream(misocket.getOutputStream());
                //flujo_salida.writeUTF(campo1.getText());
                //flujo_salida.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                Logger.getLogger(LaminaMarcoCliente.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}

class PaqueteEnvio implements Serializable { //serializamos la clase (puede ser convertido a bytes) para que una instancia de ésta pueda viajar por la red

    private String nick, ip, mensaje;

    //Metodos getter y setter para los atributos de la clase PaqueteEnvio
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
