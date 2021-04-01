package sync;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientListener extends Thread{

    Socket socket;
    DataInputStream in;
    ClientHandler client;

    @Override
    public void run() {

        try {
            //create input stream
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        receive();
    }

    public void receive() {

        try {

            //always listening
            while (true) {
                //read data
                String msg = in.readUTF();

                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [CLIENT][" + socket.getRemoteSocketAddress().toString().substring(1) + "] Broadcast Message");

                //send to other devices
                client.broadcast(msg);

            }

        } catch (Exception e) {
            //Server.clients.remove(this);

        }
    }

    public ClientListener(Socket socket, ClientHandler client) {
        //et socket and parent client
        this.socket = socket;
        this.client = client;
    }

}
