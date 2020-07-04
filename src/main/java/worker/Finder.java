package worker;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

public class Finder {

    private String urlCatalog;
    private String[] keywords;

    public Finder(String urlCatalog, String[] keywords) {
        this.urlCatalog = urlCatalog;
        this.keywords = keywords;
    }

    public void start(){

        WebDriver driver=new SafariDriver();
        //первый проход - использование поисковика авито
        for (String keyword: keywords) {
            driver.get(urlCatalog+"?q="+keyword);
            String navigateResult = driver.getPageSource();
            System.out.println(navigateResult);
            //<h3 class="snippet-title" data-marker="item-title" data-shape="default"><a class="snippet-link" itemprop="url" href="/murino/muzykalnye_instrumenty/pocked_pod_line_6_1936728915?slocation=107621" target="_blank" title="Pocked POD Line 6 в Мурино">Pocked POD Line 6</a></h3>

        }
        driver.close();
       // driver.quit();
    }

}
