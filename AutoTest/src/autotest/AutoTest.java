package autotest;

import autotest.Common.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.read.biff.BiffException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class AutoTest  {
    private static String WEBDRIVER_IE = "webdriver.ie.driver";
    private static String WEBDRIVER_CHROME = "webdriver.chrome.driver";
    private static Properties properties;
    private static final String CLASS_NAME = MSOSUtils.class.getName(); 
    private static final Logger LOGGER = autotest.Common.Utils.getLogger(CLASS_NAME); 
    
    public static void main(String[] args) throws IOException, BiffException {
        properties = Utils.getProperties();
        System.setProperty(WEBDRIVER_IE, properties.getProperty(WEBDRIVER_IE) ); 
        System.setProperty(WEBDRIVER_CHROME, properties.getProperty(WEBDRIVER_CHROME) ); 

        /*Todo
         * 
         * Добавить проверку доступности полей
         * 
         * Проверка доступности кнопок
         * 
         * 
         * Логирование в html
         * 
        */

        //WebDriver driver = new ChromeDriver();
        WebDriver driver = new InternetExplorerDriver();
        //WebDriver driver = new FirefoxDriver();

        //не следует использовать неявное ожидание
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.MILLISECONDS);
        
        MSOSUtils utils = null;
        try{
            utils = new MSOSUtils(driver);
            utils.execTestFromXLS("main.xls",0);
        }
        catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            LOGGER.severe(exceptionAsString);
            //e.printStackTrace();
            if (utils != null) {
                utils.screenshot("ERR");
            }
        }
        finally {
            //Close the browser
            driver.quit();
        }
        
    }
}
