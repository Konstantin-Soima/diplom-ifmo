package worker;
import common.Ads;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class Finder {

    private String urlCatalog;
    private String[] keywords;
    private List<Ads> listAds = new ArrayList<>();
    //Чтобы обойти защиту от парсинга Авито необходимо использовать selenium иначе далее мы не сможем работать с обьявлением
    WebDriver driver;

    public Finder(String urlCatalog, String[] keywords) {
        //Выбор браузера - дефолтный для ОС
        String currentOs = System.getProperty("os.name");
        if (currentOs.startsWith("Windows")){
            System.out.println("Начало установки");
            ChromeDriverManager.chromedriver().setup();
            System.out.println("Конец установки");
            //WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
        else {
            driver = new SafariDriver();
        }
        this.urlCatalog = urlCatalog;
        this.keywords = keywords;
    }

    public void start(){


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
                listAds.add(getPageContent("https://avito.ru"+element.attr("href")));
                //<h3 class="snippet-title" data-marker="item-title" data-shape="default"><a class="snippet-link" itemprop="url" href="/murino/muzykalnye_instrumenty/pocked_pod_line_6_1936728915?slocation=107621" target="_blank" title="Pocked POD Line 6 в Мурино">Pocked POD Line 6</a></h3>
            }
        }
        driver.close();
       // driver.quit();
    }

    public Ads getPageContent(String url){
        Ads ads = new Ads();
        driver.get(url);
        String pageResult = driver.getPageSource();
        Document document = Jsoup.parse(pageResult);
        Elements elements = document.select("div.title-info-main h1.title-info-title span.title-info-title-text");
        System.out.println(elements.size());
        System.out.println(document.body());
        String adsTitle = elements.first().html();
        ads.setName(adsTitle);
        elements = document.select("div.item-description div.item-description-text p");
        String adsDescription = elements.first().html();
        ads.setContent(adsDescription);
        ads.setLink(url);
        System.out.println(ads);
        return ads;
    }

}
