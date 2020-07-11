package worker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Ads;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.jsoup.Jsoup;
import org.openqa.selenium.safari.SafariOptions;

import java.io.IOException;
import java.util.*;

public class Finder {

    private String urlCatalog;
    private String[] keywords;
    private List<Ads> listAds = new ArrayList<>();
    private Date dateLastAds = new Date(); //TODO: сверь формат даты
    //Чтобы обойти защиту от парсинга Авито необходимо использовать selenium иначе далее мы не сможем работать с обьявлением
    WebDriver driver;

    public Finder(String urlCatalog, String[] keywords) {
        //Выбор браузера - дефолтный для ОС
        String currentOs = System.getProperty("os.name");
        if (!currentOs.startsWith("Windows")){
            System.out.println("Начало установки");
            ChromeDriverManager.chromedriver().setup();
            System.out.println("Конец установки");
            driver = new ChromeDriver();
        }
        else {
            SafariOptions options = new SafariOptions();
            options.setCapability("browserSize", "1690x1000");
            driver = new SafariDriver();
        }
        this.urlCatalog = urlCatalog;
        this.keywords = keywords;
    }

    public void start() throws InterruptedException, IOException {
        //первый проход - использование поисковика авито
        for (String keyword: keywords) {
            driver.get(urlCatalog+"?q="+keyword);
            String navigateResult = driver.getPageSource();
            Document document = Jsoup.parse(navigateResult);
            //Получение ссылок из заголовков обьявлений
            Elements elements = document.select("h3.snippet-title a.snippet-link");
            for(Element element: elements) {
                System.out.println("Получена ссылка:");
                System.out.println(element.attr("href"));
                System.out.println(element.html());
                //System.out.println(element.absUrl("href").toString());
                Thread.sleep(5000);
                //TODO: роверка есть ли в базе ссылка
                System.out.println("GET from "+"http://188.242.232.214:8080/adsByUrl?url=" + "https://avito.ru" + element.attr("href"));
                String adsJson = Jsoup.connect("http://188.242.232.214:8080/adsByUrl?url=" + "https://avito.ru" +  element.attr("href")).ignoreContentType(true).execute().body();
                System.out.println(adsJson);
                Ads ads =new Ads();
                if (!"null".equals(adsJson)) {
                    ObjectMapper objectMapperAds = new ObjectMapper();
                    Ads resultAds = objectMapperAds.readValue(adsJson, new TypeReference<Ads>() {
                    });
                    if(!resultAds.isEmpty()){
                        ads=resultAds;
                    }
                    System.out.println("ПРИСВОЕННО ЗНАЧЕНИЕ ИЗ БАЗЫ");
                }
                else {
                    ads = getPageContent("https://avito.ru" + element.attr("href"));
                }
                if (!ads.isEmpty()) {
                    listAds.add(ads);
                    ObjectMapper objectMapper = new ObjectMapper();
                    /*String jsonProfile = objectMapper.writeValueAsString(ads.getProfile());
                    System.out.println(jsonProfile);
                    Document resultProfile =  Jsoup.connect("http://188.242.232.214:8080/addProfile")
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .followRedirects(true)
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 AppleWebKit/537.36 (KHTML," +
                                    " like Gecko) Chrome/45.0.2454.4 Safari/537.36")
                            .requestBody(jsonProfile)
                            .post();
                    System.out.println(resultProfile.toString());*/
                    String jsonAd = objectMapper.writeValueAsString(ads);
                    System.out.println(jsonAd);
                    Document result =  Jsoup.connect("http://188.242.232.214:8080/addAds")
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .followRedirects(true)
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 AppleWebKit/537.36 (KHTML," +
                                    " like Gecko) Chrome/45.0.2454.4 Safari/537.36")
                            .requestBody(jsonAd)
                            .post();

                    System.out.println(result.toString());

                }
            }
        }
        driver.close();
       // driver.quit();
    }

    public Ads getPageContent(String url) {
        Ads ads = new Ads();
        try { //визуально убидел
            driver.get(url);
            String pageResult = driver.getPageSource();
            Document document = Jsoup.parse(pageResult);
            Elements elements = document.select("div.title-info-main h1.title-info-title span.title-info-title-text"); //Заголовок
            System.out.println(elements.size());
            //System.out.println(document.body());
            String adsTitle = elements.first().html();
            ads.setName(adsTitle);
            //Текст обьявления
            elements = document.select("div.item-description div.item-description-text p"); //текст обьявления
            String adsDescription = elements.first().html();
            ads.setContent(adsDescription);
            ads.setLink(url); //ссылка на обьявление
            //ссылка на продавца
            elements = document.select("div.seller-info-name.js-seller-info-name a"); //текст обьявления
            String adsContactLink = elements.first().attr("href");
            //Profile contact = new Profile();
            ads.setProfileLink(adsContactLink);
            //имя продавца
            String adsContactName = elements.first().html();
            ads.setProfileName(adsContactName);
            //Дата обьявления
            elements = document.select("div.title-info-metadata-item-redesign"); //текст обьявления
            //String adsDate = elements.first().html().replace();
            //Calendar cal = Calendar.getInstance();
            //SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            //cal.setTime(sdf.parse("Mon Mar 14 16:02:37 GMT 2011"));
            //ads.setDate(cal);
            //Клик телефона
            Thread.sleep(4000);
            driver.findElement(By.xpath("//button[@data-marker='item-phone-button/card']")).click();//data-marker="item-phone-button/card"
            //телефон
            Thread.sleep(4000);//Анимация
            pageResult = driver.getPageSource();
            document = Jsoup.parse(pageResult);
            elements = document.select("img.button-content-phone_size_l-1O5VB");
            if (elements.size()>0) { //Есть обьявления без номеров и оно может быть искомым
                String adsPhone = elements.first().attr("src");
                ads.setPhone(adsPhone);
            }
            System.out.println(ads);
            Thread.sleep(5000);
            driver.navigate().back();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Не удалось получить обьявление");
        }

        return ads;
    }

}
