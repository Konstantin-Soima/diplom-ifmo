package worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Category;
import org.jsoup.Jsoup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class FinderThread extends Thread{
    @Override
    public void run() {
        Thread.currentThread().setName("SearchThread");
        System.out.println("Поток запущен"+Thread.currentThread().getName());
        //2. получаем из настроек мониторимый товар
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("find.properties")) {
            properties.load(input);
            String title = properties.getProperty("title");
            if (properties.getProperty("keywords") == null || properties.getProperty("keywords").isEmpty()){
                System.out.println("Нет ключевых слов для поиска");
                return;
            }
            String[] keyword = properties.getProperty("keywords").split(",");

            String group = properties.getProperty("category");
            String categoryListJson = Jsoup.connect("http://188.242.232.214:8080/categoryByIds/" + group).ignoreContentType(true).execute().body();
            ObjectMapper objectMapper = new ObjectMapper();

            List<Category> listCategory = objectMapper.readValue(categoryListJson, new TypeReference<List<Category>>() {
            });

            DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ld = LocalDate.parse(properties.getProperty("datestart"), DATEFORMATTER);

            //3. Создаём поисковик для ключевого слова в каждой группу
            try {
                for (Category groupName : listCategory) {
                    Finder testFind = new Finder(groupName.getLink(), keyword);
                    testFind.start();
                }
                //5. таймер раз в час
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
