package file;

import data.ImageFilePacket;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FileClientHandler extends Thread {

    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    @Override
    public void run() {
        try {
            //init streams
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        listener();
    }

    public void listener() {
        while (true) {
            try {

                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER] Waiting for file request");
                ImageFilePacket imageFilePacket = (ImageFilePacket) in.readObject();
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER][" + socket.getRemoteSocketAddress().toString().substring(1) + "] request received");

                out.writeObject(process(imageFilePacket));
                out.flush();

                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER][" + socket.getRemoteSocketAddress().toString().substring(1) + "] Response sent");

            } catch (SocketException se) {
                break;

            } catch (EOFException ee) {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [SERVER][" + socket.getRemoteSocketAddress().toString().substring(1) + "] Connection lost");
                try{
                    socket.close();
                    in.close();
                    out.close();
                } catch (Exception e){}
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Object process(ImageFilePacket imageFilePacket) {
        //set dir
        String dir = "src/assets/images/";

        //read data in packet

        switch (imageFilePacket.getType()) {
            case "send":
                File file = new File(dir + imageFilePacket.getName());
                ImageFilePacket img;
                //create packet and return
                if(file.exists())
                    img = new ImageFilePacket(file, imageFilePacket.getName(), "response");
                else
                    img = new ImageFilePacket(null, "", "response");

                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER] requested: send");
                return img;

            case "save":
                Path path = Paths.get(dir + imageFilePacket.getName());
                try {
                    Files.write(path, imageFilePacket.getImage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER] requested: add");
                return new ImageFilePacket(null, null, "success");

            case "delete":
                File fileDel = new File(dir + imageFilePacket.getName());
                if(fileDel.exists())
                    fileDel.delete();
                else
                    return new ImageFilePacket(null, null, "file not found");
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                        " [FILE SERVER] requested: delete");
                return new ImageFilePacket(null, null, "success");
        }



        return null;
    }

    public FileClientHandler(Socket socket) {
        //get socket
        this.socket = socket;
    }

}
