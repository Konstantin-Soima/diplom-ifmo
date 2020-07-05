package worker;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {

        //1. создаём иконку в трее
        TrayIcon trayIcon = null;
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
                    WorkWindow newWindow = new WorkWindow();

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
            }
            else {
                image = Toolkit.getDefaultToolkit().getImage("sources/tenor.gif"); //иконка в трей
                trayIcon = new TrayIcon(image, "Поиск сомнительных обьявлений", popup);
            }
            trayIcon.addActionListener(listenerAdd);
            trayIcon.addActionListener(listenerWatch);
            trayIcon.addActionListener(listenerExit);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
        } else {

        }
        //2. получаем из настроек мониторимый товар
        String[] keyword = {"pod","line6"};
        String[] groups = {"https://www.avito.ru/sankt-peterburg/muzykalnye_instrumenty/dlya_studii_i_kontsertov-ASgBAgICAUTEAsgK"};

        //3. Создаём поисковик для ключевого слова в каждой группу
        for (String groupName: groups) {
            Finder testFind = new Finder(groupName,keyword);
            testFind.start();
        }
        //5. таймер раз в час

    }

}
