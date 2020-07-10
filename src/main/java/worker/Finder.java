package worker;
import common.Ads;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.safari.SafariDriver;
import org.jsoup.Jsoup;
import org.openqa.selenium.safari.SafariOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void start() throws InterruptedException {
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
                Ads ads = getPageContent("https://avito.ru"+element.attr("href"));
                if (!ads.isEmpty()) {
                    listAds.add(ads);
                    //TODO: ДОбавить в бд
                }
                //<h3 class="snippet-title" data-marker="item-title" data-shape="default"><a class="snippet-link" itemprop="url" href="/murino/muzykalnye_instrumenty/pocked_pod_line_6_1936728915?slocation=107621" target="_blank" title="Pocked POD Line 6 в Мурино">Pocked POD Line 6</a></h3>
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
            elements = document.select("div.item-description div.item-description-text p"); //текст обьявления
            String adsDescription = elements.first().html();
            ads.setContent(adsDescription);
            ads.setLink(url); //ссылка на обьявление
            //ссылка на продавца
            elements = document.select("div.seller-info-name.js-seller-info-name a"); //текст обьявления
            String adsContactLink = elements.first().attr("href");
            ads.setContactLink(adsContactLink);
            //имя продавца
                    String adsContactName = elements.first().html();
                    ads.setContactName(adsContactName);
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
                ads.setPhoneImg(adsPhone);
            }
            System.out.println(ads);
            Thread.sleep(500000);
            driver.navigate().back();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Не удалось получить обьявление");
        }

        return ads;
    }

}
