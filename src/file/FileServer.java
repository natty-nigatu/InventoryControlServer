package file;

import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer extends Thread{

    static ServerSocket server;
    static final int port = 5065;

    //thread pool for connection, track list of clients
    static ExecutorService pool = Executors.newFixedThreadPool(50);
    static ArrayList<FileClientHandler> clients = new ArrayList<>();


    public void run() {

        try {
            //create server
            server = new ServerSocket(port);

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            //always waiting for connections
            while (true) {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER] Waiting for connection ...");
                //wait and accept
                Socket socket = server.accept();
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER] Client accepted.");

                //change to client handler
                FileClientHandler client = new FileClientHandler(socket);

                clients.add(client);
                pool.execute(client);
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER] Connected to client " + socket.getRemoteSocketAddress().toString().substring(1));

            }
        } catch (Exception e) {

        }
    }
}
