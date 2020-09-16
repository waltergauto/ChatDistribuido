import java.io.*;
import java.net.*;
import java.util.*;


public class server{
    ArrayList clientOutputStreams;
    ArrayList clientsOnline;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSOcket) {
            try {
                sock = clientSOcket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);

            } catch (Exception ex) { ex.printStackTrace(); }
        }

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    tellEveryone(message, sock);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        new server().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList();
        clientsOnline = new ArrayList();
        try {
            ServerSocket serverSock = new ServerSocket(5000);
            while(true) {
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                clientsOnline.add(clientSocket);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void tellEveryone(String message, Socket sender) {
        /*Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) { ex.printStackTrace(); }
        }*/
        if(message.equals("1")){
            try{
                PrintWriter writer = (PrintWriter) clientOutputStreams.get(clientsOnline.indexOf(sender));
                Iterator it = clientsOnline.iterator();
                while(it.hasNext()){
                    Socket a = (Socket) it.next();
                    InetAddress b = a.getInetAddress();
                    writer.println(b.getHostAddress());
                    writer.flush();
                }
            }catch(Exception ex) {ex.printStackTrace();}
        }
    }
}
