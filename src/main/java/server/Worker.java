package server;

import java.net.Socket;

public class Worker implements Runnable {
    private Socket socket ;

    public Worker(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {

    }

}
