package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 8090;
        try {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Сервер запущен...");
                while (true){
                    //Для ответа каждому клиенту создаём отдельный поток
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new Worker(clientSocket)).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}