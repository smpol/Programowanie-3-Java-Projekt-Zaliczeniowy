package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8080);
            while(true) {
                Socket socket = server.accept();
                Operacje watek = new Operacje(socket);
                watek.start();
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}