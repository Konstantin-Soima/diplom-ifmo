package worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Category;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jsoup.Jsoup;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class Main {
    public static TrayIcon trayIcon = null;
    public static void main(String[] args) {

        //1. создаём иконку в трее
        if (SystemTray.isSupported()) { //Проверка наличия трея (в линуксе с Deepin, LXDM может и не быть)
            SystemTray tray = SystemTray.getSystemTray();

            //События для трея
            ActionListener listenerExit = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };
            ActionListener listenerWatch = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //TODO: просмотр совпадений
                }
            };
            ActionListener listenerAdd = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Открыть форму добавление поиска
                    FXMLLoader fxmlLoader = new FXMLLoader();

                    Platform.startup(() -> //Инициализация платформы JAVA FX
                    {
                        try {
                            fxmlLoader.setLocation(getClass().getResource("/WorkWindow.fxml"));
                            Parent root = fxmlLoader.load();
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                            System.out.println("закончился показ окна");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

                }
            };
            PopupMenu popup = new PopupMenu();
            MenuItem addItem = new MenuItem("Добавить");
            addItem.addActionListener(listenerAdd);
            popup.add(addItem);
            MenuItem watchItem = new MenuItem("Просмотр совпадений");
            addItem.addActionListener(listenerWatch);
            popup.add(watchItem);
            MenuItem exitItem = new MenuItem("Выход");
            exitItem.addActionListener(listenerExit);
            popup.add(exitItem);
            String currentOs = System.getProperty("os.name");
            Image image;
            if (currentOs.startsWith("Windows")) {
                Image trayIconImage = Toolkit.getDefaultToolkit().getImage("sources/find.png");
                int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
                trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "Поиск сомнительных обьявлений", popup);
            } else {
                image = Toolkit.getDefaultToolkit().getImage("sources/tenor.gif"); //иконка в трей
                trayIcon = new TrayIcon(image, "Поиск сомнительных обьявлений", popup);
            }
            trayIcon.addActionListener(listenerAdd);
            trayIcon.addActionListener(listenerWatch);
            trayIcon.addActionListener(listenerExit);

            try {
                tray.add(trayIcon);

                trayIcon.displayMessage("Приветствую!", "Это приложение для поиска потерянных/украденых вещей на авито", TrayIcon.MessageType.INFO);

            } catch (AWTException e) {
                System.err.println(e);
            }
        } else {
            System.out.println("Запуск невозможен, нет системного трея/доступа к нему");
        }
        //Таймер каждые 5 минут проверка новых обьявлений
        while (true){
            try {
                FinderThread finderThread = new FinderThread();
                System.out.println("Запуск потока поисковика...");
                finderThread.start();
                Thread.sleep(60000);
                finderThread.join();//При маленьком лимите лучше дождаться окончания потока
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

}
