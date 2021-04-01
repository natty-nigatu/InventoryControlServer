package sync;

import file.FileServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    static ServerSocket server;
    static final int port = 5060;

    //thread pool for connection, track list of clients
    static ExecutorService pool = Executors.newFixedThreadPool(50);
    static ArrayList<ClientHandler> clients = new ArrayList<>();


    public static void main(String[] args) {

        FileServer fileServer = new FileServer();
        fileServer.start();

        System.out.println("         [Inventory Control Server]");

        try {
            //create server
            server = new ServerSocket(port);
            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                    " [SERVER] Server started");

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            //always waiting for connections
            while (true) {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [SERVER] Waiting for connection ...");
                //wait and accept
                Socket socket = server.accept();
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [SERVER] Client accepted.");

                //change to client handler
                ClientHandler client = new ClientHandler(socket);

                clients.add(client);
                pool.execute(client);
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [SERVER] Connected to client " + socket.getRemoteSocketAddress().toString().substring(1));

            }
        } catch (Exception e) {

        }
    }
}
