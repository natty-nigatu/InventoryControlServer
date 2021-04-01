package sync;

import java.io.DataOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler extends Thread {

    Socket socket;
    DataOutputStream out;

    @Override
    public void run() {
        try {
            //this class handles output streams
            out = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // create object for listener
        ClientListener listener = new ClientListener(socket, this);
        listener.start();
    }

    public void broadcast(String msg) {
        for (ClientHandler client : Server.clients) {
            if(client == this) {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [SERVER] Skipped Source");
                continue;
            }

            client.send(msg);
        }
    }

    public void send(String msg) {

        try {
            out.writeUTF(msg);
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                    " [SERVER] Sent to " + socket.getRemoteSocketAddress().toString().substring(1));
        } catch (Exception e) {
            //e.printStackTrace();
            /*Server.clients.remove(this);
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                    " [SERVER] Connection lost with " + socket.getRemoteSocketAddress().toString().substring(1));

            try {
                socket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }


             */
        }
    }

    public ClientHandler(Socket socket) {
        //get socket
        this.socket = socket;
    }

}
