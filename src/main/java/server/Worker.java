package server;

import common.Ads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Worker implements Runnable {
    private Socket socket ;
    private ObjectInputStream inputStream;

    public Worker(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            while (true){
                try{
                    //Если есть в базе - отдать пользователю, если нет и переданно полное - добавить
                    Ads ads = (Ads) inputStream.readObject();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
