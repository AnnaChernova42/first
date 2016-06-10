package autotest;

import autotest.Common.Waiter;
import autotest.Common.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

public class MSOSUtils {
    private WebDriver driver;
    private JavascriptExecutor js;
    //private final Properties properties = Utils.getProperties();
    private static long MAX_WAIT_TIME=40000;
    private static long MAX_CHECK_WAIT_TIME=5000;
    private Map<String, String> parameters = new HashMap<String, String>();
    private StrSubstitutor substitutor = new StrSubstitutor(parameters, "${", "}");
    private static final String CLASS_NAME = MSOSUtils.class.getName(); 
    private static final Logger LOGGER = autotest.Common.Utils.getLogger(CLASS_NAME); 
    
    public MSOSUtils(WebDriver driver) throws FileNotFoundException, IOException{
        this.driver = driver;
        this.js = (JavascriptExecutor)driver;
        initVariables();
    }

    private void initVariables(){
        Date d = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd_HHmmss"); 
        SimpleDateFormat dt2 = new SimpleDateFormat("MM dd HHmmss"); 
        parameters.put("DT", dt.format(d));
        parameters.put("RND_DUL", dt2.format(d));
    }
    
    //Активное ожидание элемента на форме
    public WebElement waitElement(final By by){
        WebElement result = null;
        try{
            //Ожидаем появления элемента 
            Waiter w = new Waiter(MAX_WAIT_TIME);
            while (driver.findElements(by).isEmpty() && !w.isTimeout()){}
            result = driver.findElement(by);
            //System.out.printf("Время ожидания элемента: %s ms\n",System.currentTimeMillis()-d1.getTime());
        } catch (Exception e){
            return result;
        }
        return result;
    }
    
    //Кликнуть на ссылку
    public void clickOnLink(String linkText) throws Exception{
        By by = By.partialLinkText(linkText);
        clickOnElement(by);
    }
    
    //Щелчек на кнопку Input
    public void clickOnButton(String btnName) throws Exception{
        By by = By.xpath("//input[@value='"+btnName+"']");
        clickOnElement(by);
    }
    
    public void clickOnOrganizationMenu(String linkName) throws Exception{
        By by = By.xpath("//div[@class='menu-organization']//li[a='"+linkName+"']");
        clickOnElement(by);
    }
    
    //Кликнуть на элемент
    public void clickOnElement(By by) throws Exception{
        Waiter w = new Waiter(MAX_WAIT_TIME);
        boolean success=false;
        WebElement element;
        while (!w.isTimeout() && !success){
            try{
                element = driver.findElement(by);
                scrollToElement(element);
                element.click();
                success=true;
            } catch (Exception e){
                //LOGGER.info("Не удалость кликнуть на элемент");
                success=false;
            }
        }
        if (w.isTimeout()){
            LOGGER.severe("Так и не удалось кликнуть на элемент '"+by.toString()+"'");
            throw new Exception("Так и не удалось кликнуть на элемент '"+by.toString()+"'");
        }
    }
    
    public void scrollToElement(WebElement element){
        int Y = new Integer(element.getLocation().getY());
        js.executeScript("window.scrollTo(0,"+Y+");");
    }
    
    //Закрыть сообщение об ошибке валидации
    public void closeValidationErrorMessage() throws Exception{
        By by = By.xpath("//div[@id='validationerrors_container']//input[@value='Закрыть']");
        clickOnElement(by);
    }

    //Закрыть сообщение об успешном сохранении
    public void closeSuccessfulOperationMessage() throws Exception{
        try{
            By by = By.xpath("//div[@id='successfulOperation_content_scroller']//input[@value='OK']");
            clickOnElement(by);
        }
        catch (Exception e){
            closeValidationErrorMessage();
            screenshot("ERR");
            throw e;
        }    
    }

    //Закрыть сообщение об успешном сохранении Cотрудника
    public void closeEmployeeSuccessfulOperationMessage() throws Exception{
        try{
            By by = By.xpath("//div[@id='resultPopup_container']//input[@value='ОК']");
            clickOnElement(by);
        }
        catch (Exception e){
            closeValidationErrorMessage();
            screenshot("ERR");
            throw e;
        }    
    }    
    
    //Закрыть сообщение об успешном сохранении Сотрудника
    public void closeSuccessfulEmployeeSaveMessage() throws Exception{
        By by = By.xpath("//div[@id='resultPopup_container']//input[@value='ОК']");
        clickOnElement(by);
    }
    
    //Закрыть диалоговое сообщение "Вы уверены что хотите активировать организацию"
    public void closeActivationDialog() throws Exception{
        By by = By.xpath("//div[@id='activatePopup_container']//input[@value='Да']");
        clickOnElement(by);
    }
    
    //Выбор радиокнопки по атрибуту id
    public void selectRadioButtonByValue(String value) throws Exception{
        By by = By.xpath("//input[@type='radio' and @value='"+value+"']");
        clickOnElement(by);
        //waitElement().sendKeys(" ");
    }
    
    //Выбор радиокнопки по части значения атрибута id
    public void selectRadioButtonByValuePart(String value) throws Exception{
        By by = By.xpath("//input[@type='radio' and contains(@id,'"+value+"')]");
        clickOnElement(by);
        //waitElement(by).sendKeys(" ");
    }

    //Выбор группы организациии
    public void selectOrganizationGroup(String groupId) throws Exception{
        clickOnElement(By.xpath("//input[@id='editOrganization:orgGroupIdHidden']/../input[@value='...']"));
        clickOnElement(By.id("popup_groupsForm:orgGroupPopup_searchBtn"));
        selectRadioButtonByValue(groupId);
        clickOnElement(By.id("popup_groupsForm:popup_selectGroupBtn"));
    }

    //Выбор менеджера клиента организациии
    public void selectOrganizationClientManeger(String managerId) throws Exception{
        clickOnElement(By.xpath("//input[@id='editOrganization:orgClientManagerHidden']/../input[@value='...']"));
        fillField("popup_managersForm:popup_searchMngCode",managerId);
        clickOnElement(By.id("popup_managersForm:orgManagerPopup_searchMngBtn"));
        selectRadioButtonByValue(managerId);
        clickOnElement(By.id("popup_managersForm:popup_selectManagerBtn"));
    }
    
    //Найти организацию по имени
    public void findOrganizationByName(String organizationName) throws Exception{
        clickOnElement(By.partialLinkText("ПОИСК"));
        switchToTab("Поиск организации");
        fillField("organizationSearchForm:radio_parent:0"," ");
        fillField("organizationSearchForm:ui7orgName", organizationName);
        clickOnElement(By.id("organizationSearchForm:ui7search"));
        selectRadioButtonByValuePart("foundOrgForm:foundOrgTable:0");
    }

    //Открыть диалог добавления карты на форме редактирования сотрудника
    public void openCardDialog() throws Exception{
        clickOnElement(By.id("editEmployee:addCardContractBtn"));
    }

    //Нажать сохранить на диалоге добавления карты на форме редактирования сотрудника
    public void clickSaveOnCardDialog() throws Exception{
        clickOnElement(By.id("cardContractPopupForm:addCardContractBtnPopup"));
    }

    //Выполняет поиск организации по имени и открывает первую найденую
    public void openOrganizationByName(String organizationName) throws IOException, Exception{
        findOrganizationByName(organizationName);
        clickOnButton("Параметры организации");        
    }
    
    public void delay(long l){
        try {
            Thread.sleep(l);
        } catch (InterruptedException ex) {
            LOGGER.info(ex.getLocalizedMessage());
        }     
     }
    
    //Проверка существования поля на форме
    //targetValue = Y поле должно присутствовать на форме N - нет
    public void checkElementExisting(String fieldName, String id, String targetValue) throws IOException{
        Waiter w = new Waiter(MAX_CHECK_WAIT_TIME);

        if (targetValue.equals("N")){
            while (!driver.findElements(By.id(id)).isEmpty() && !w.isTimeout()){}
            if (w.isTimeout()){
                screenshot("ERR");
                LOGGER.severe("ERROR: Поле '"+fieldName+"' обнаружено на экранной форме, однако этого не ожидалось");
            }
        } else if (targetValue.equals("Y")){
            while (driver.findElements(By.id(id)).isEmpty() && !w.isTimeout()){}
            if (w.isTimeout()){
                screenshot("ERR");
                LOGGER.severe("ERROR: Поле '"+fieldName+"' отсутствует на экранной форме, ожидается что оно будет присутствовать");
            }
        }
    }
    
    //Проверка существования поля на форме
    //targetValue = Y элемент должен быть активен N - нет
    public void checkElementEnable(String fieldName, String id, String targetValue) throws IOException{
        Waiter w = new Waiter(MAX_CHECK_WAIT_TIME);
        boolean res = false;
        if (targetValue.equals("N")){
            while (!res && !w.isTimeout()){
                try {
                    res = driver.findElement(By.id(id)).isEnabled();
                } catch (Exception e){}
            }
            if (w.isTimeout()){
                screenshot("ERR");
                LOGGER.severe("ERROR: Элемент '"+fieldName+"' активен однако этого не ожидалось");
            }
        } else if (targetValue.equals("Y")){
            while (!res && !w.isTimeout()){
                try {
                    res = !driver.findElement(By.id(id)).isEnabled();
                } catch (Exception e){}
            }
            if (w.isTimeout()){
                screenshot("ERR");
                LOGGER.severe("ERROR: Элемент '"+fieldName+"' заблокирован, однако ожидается что он будет активен");
            }
        }
    }    
    
    //Проверка сообщения к полю
    public void checkFieldValidationMessage(String fieldName, String id, String targetMessage) throws IOException{
        /*
         * 0-Результат не определен
         * 1-Сообщение соответствует целевому
         */
        int res = 0;
        String elementText = new String();
        Date d1 = new Date();
        Waiter w = new Waiter(MAX_CHECK_WAIT_TIME);

        while (res==0 && !w.isTimeout()){
            try{
                elementText = driver.findElement(By.id(id)).getText();
                if (elementText.equals(targetMessage)){
                    res = 1;
                }
            }  catch (Exception e){
                res=0;
            }
        }
        if (res==0){
            if (w.isTimeout()){
                if (driver.findElements(By.id(id)).isEmpty()){
                    screenshot("ERR");
                    LOGGER.severe("ERROR: Для поля '"+fieldName+"' не найден текст сообщения. ID='"+id+"'");
                } else if (!elementText.equals(targetMessage)){
                    screenshot("ERR");
                    LOGGER.severe("ERROR: Для поля '"+fieldName+"' обнаружено сообщение '"+elementText+"' вместо ожидаемого '"+targetMessage+"'");
                }
            }
        }
    }

    //Проверка сообщения к полю
    public void checkFieldvalue(String fieldName, String id, String targetMessage) throws IOException{
        /*
         * 0-Результат не определен
         * 1-Сообщение соответствует целевому
         */
        int res = 0;
        String elementText = new String();
        Date d1 = new Date();
        Waiter w = new Waiter(MAX_CHECK_WAIT_TIME);
        WebElement element=null;
        String fieldType;
        while (res==0 && !w.isTimeout()){
            try{
                element = driver.findElement(By.id(id));
                fieldType = element.getTagName();
                if (fieldType.equals("input")){
                    String inputType = element.getAttribute("type");
                    if (inputType.equals("text")){
                        elementText = element.getAttribute("value");
                        if (elementText.equals(targetMessage)){
                            res = 1;
                        }
                    }
                    if (inputType.equals("radio") || inputType.equals("checkbox")){
                        //todo
                    }
                }
                if (fieldType.equals("textarea")){
                    elementText = element.getText();
                    if (elementText.equals(targetMessage)){
                        res = 1;
                    }
                }
                if (fieldType.equals("select")){
                    By by = By.xpath("//select[@id='"+id+"']/..//a[@class='chzn-single']/span");
                    elementText = driver.findElement(by).getText();
                    if (elementText.equals(targetMessage)){
                        res = 1;
                    }
                }                   
            }  catch (Exception e){
                res=0;
            }
        }
        if (res==0){
            if (w.isTimeout()){
                if (driver.findElements(By.id(id)).isEmpty()){
                    screenshot("ERR");
                    LOGGER.severe("ERROR: Не удалось найти поле '"+fieldName+"' ID='"+id+"'");
                } else if (!elementText.equals(targetMessage)){
                    screenshot("ERR");
                    LOGGER.severe("ERROR: Для поля '"+fieldName+"' обнаружено значение '"+elementText+"' вместо ожидаемого '"+targetMessage+"'");
                }
            }
        }
    }
        
    
    //Репитер функции заполнения поля
    public void fillField(String fieldId, String fieldValue) throws Exception{
        boolean res=false;
        Waiter w = new Waiter(MAX_WAIT_TIME);
        while (!res && !w.isTimeout()){
            try{
                _fillField(fieldId,fieldValue);
                res=true;
            }
            catch (Exception e){
            }
        }
        if (!res && w.isTimeout()){
            LOGGER.severe("Так и не удалось заполнить поле '"+fieldId+"'");
            throw new Exception("Так и не удалось заполнить поле '"+fieldId+"'");
        }
    }

    
    //Обертка для заполнения полей любого типа
    public void _fillField(String fieldId, String fieldValue) throws Exception{

        //WebElement element = waitElement(By.id(fieldId));
        WebElement element = driver.findElement(By.id(fieldId));
        String fieldType = element.getTagName();
        //Параметризация значений текстовых параметров

        //Попытка отобразить элемент в области видимости
        if (!fieldType.equals("select")){
            int Y = new Integer(element.getLocation().getY());
            js.executeScript("window.scrollTo(0,"+(Y-500)+");");
        }

        if (fieldType.equals("input")){
            String inputType = element.getAttribute("type");
            if (inputType.equals("text") || inputType.equals("password")){
                element.clear();
                element.sendKeys(fieldValue); 
            }
            if (inputType.equals("radio") || inputType.equals("checkbox")){
                element.click();
            }
        }
        if (fieldType.equals("textarea")){
            element.clear();
            element.sendKeys(fieldValue);
        }
        if (fieldType.equals("select")){
            setDropDownValue(fieldId,fieldValue);
        }        
     
    }

    //Установка значение в выпадающий список в стиле JQuery
    public void setDropDownValue(String id, String value) throws Exception{
        //String jQueryId = id.replace(":", "\\\\:");
        //js.executeScript("$(\"#"+jQueryId+"\").val(\""+value+"\");");
        //js.executeScript("$(\"#"+jQueryId+"\").trigger(\"liszt:updated\");");
        By by = By.xpath("//select[@id='"+id+"']/..//input[@name='preventAjaxFailBlob']");
        if (driver.findElements(by).isEmpty()){ //заполняем обычный select
            Select select = new Select(driver.findElement(By.id(id)));
            select.selectByVisibleText(value);
        } else { //заполняем JQuery select
            WebElement element = driver.findElement(by);
            //int Y = new Integer(element.getLocation().getY());
            //js.executeScript("window.scrollTo(0,"+(Y-500)+");");
            if (!value.equals("")){
                by = By.xpath("//select[@id='"+id+"']/..//div");//Клик на div нужен чтобы preventAjaxFailBlob оказался в фокусе
                clickOnElement(by); //todo нужен для заполнения multiselect
                element.sendKeys(value+"\n");
            } else {
                by = By.xpath("//select[@id='"+id+"']/..//abbr[@class='search-choice-close']");
                if (!driver.findElements(by).isEmpty()){
                    driver.findElement(by).click();
                }
            }
        }
    }
    
    //Переключение между вкладками
    public void switchToTab(String tabName) throws IOException, Exception{
        scrollUp();
        WebElement element;
        By by = By.xpath("//td[contains(@id,'header:inactive') and span='"+tabName+"']");
        element = waitElement(by);
        if (!element.getCssValue("display").equals("none")){
            //element.click();
            clickOnElement(by);
        }
        //js.executeScript("$('#editOrganization .rf-tab-cnt').each(function() {$(this).css('height', 'auto')})");
    }
    
    //Перейти на странизу ввода нового ЮЛ
    public void addNewUL() throws Exception{
        initVariables();
        clickOnElement(By.partialLinkText("ВСЕ ОРГАНИЗАЦИИ"));
        clickOnButton("Добавить организацию");
        fillField("searchForm:crfInputFld","0123456789");
        clickOnButton("Найти в EQ");
        clickOnButton("Ввод новой организации");
        clickOnButton("Зарегистрировать ЮЛ");        
    }

    public void addNewIP() throws Exception{
        initVariables();
        clickOnElement(By.partialLinkText("ВСЕ ОРГАНИЗАЦИИ"));
        clickOnButton("Добавить организацию");
        fillField("searchForm:crfInputFld","0123456789");
        clickOnButton("Найти в EQ");
        clickOnButton("Ввод новой организации");
        clickOnButton("Зарегистрировать ИП");        
    }    
    
    public void screenshot(String name) throws IOException{
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File(autotest.Common.Utils.getLogDir()+autotest.Common.Utils.getTme()+"_"+name+".png"));
    }
    
    //Перейти на страницу
    public void openPage(String url){
        driver.get(url);
    }

    //Пройти аутентификацию в МСОС
    public void authenticate(String user, String password) throws Exception{
        fillField("username", user);
        fillField("password", password);
        clickOnElement(By.cssSelector("button[type=\"submit\"]"));
    }
    
    
    public void execTestFromXLS(String fileName, int sheetNumber) throws IOException, BiffException, Exception{
        Workbook workbook = Workbook.getWorkbook(new File(fileName));
        Sheet sheet = workbook.getSheet(sheetNumber);
        int i=0;
        while (i < sheet.getRows()){
            String actionName = sheet.getCell(0,i).getContents(); //Название действия
            String fieldName = sheet.getCell(1,i).getContents(); //Название поля
            String fieldId = sheet.getCell(2,i).getContents(); //ID поля
            String fieldValue = sheet.getCell(3,i).getContents(); //Значение
            //LOGGER.info("actionName:'"+actionName+"'; fieldName:'"+fieldName+"'; fieldId:'"+fieldId+"'; fieldValue:'"+fieldValue+"'");
            //delay(30);
            execCommand(actionName,fieldName,fieldId,fieldValue);
            if (actionName.equals("stop")){
                break;
            }
            i++;
        }
        workbook.close();
    }

    
    public void execCommand(String actionName,String fieldName,String fieldId,String fieldValue) throws IOException, Exception{
        fieldName = substitutor.replace(fieldName);
        fieldValue = substitutor.replace(fieldValue);

        if (actionName.equals("fillField")){
            LOGGER.info("Заполнить поле:'"+fieldName+"'("+fieldId+") значением:'"+fieldValue+"'");
            fillField(fieldId,fieldValue);
        }
        if (actionName.equals("switchToTab")){
            LOGGER.info("Перейти на вкладку:'"+fieldName+"'");
            switchToTab(fieldName);
        }
        if (actionName.equals("clickOnButton")){
            LOGGER.info("Нажать на кнопку:'"+fieldName+"'");
            clickOnButton(fieldName);
        }
        if (actionName.equals("clickOnElement")){
            LOGGER.info("Кликнуть на элемент:'"+fieldName+"' XPath:["+fieldId+"]");
            clickOnElement(By.xpath(fieldId));
        }
        if (actionName.equals("checkFieldValidationMessage")){
            LOGGER.info("Проверить сообщение поля:'"+fieldName+"'("+fieldId+")"); 
            checkFieldValidationMessage(fieldName,fieldId,fieldValue);
        }
        if (actionName.equals("checkElementExisting")){
            LOGGER.info("Проверить наличие элемента:'"+fieldName+"'("+fieldId+")"); 
            checkElementExisting(fieldName,fieldId,fieldValue);
        }
        if (actionName.equals("checkElementEnable")){
            LOGGER.info("Проверить активность элемента:'"+fieldName+"'("+fieldId+")"); 
            checkElementEnable(fieldName,fieldId,fieldValue);
        }
        if (actionName.equals("checkFieldvalue")){
            LOGGER.info("Проверить значение элемента:'"+fieldName+"'("+fieldId+")"); 
            checkFieldvalue(fieldName,fieldId,fieldValue);
        }
        if (actionName.equals("addNewUL")){
            LOGGER.info("Создать новое ЮЛ"); 
            addNewUL();
        }
        if (actionName.equals("addNewIP")){
            LOGGER.info("Создать нового ИП"); 
            addNewIP();
        }
        if (actionName.equals("closeSuccessfulOperationMessage")){
            LOGGER.info("На сообщении об успешном сохранении нажать ОК"); 
            closeSuccessfulOperationMessage();
        }
        if (actionName.equals("closeValidationErrorMessage")){
            LOGGER.info("На сообщении об ошибке проверки полей нажать 'Закрыть'"); 
            closeValidationErrorMessage();
        }
        if (actionName.equals("clickOnLink")){
            LOGGER.info("Щелкнуть на ссылку:'"+fieldName+"'");
            clickOnLink(fieldName);
        }
        if (actionName.equals("selectRadioButtonByValue")){
            selectRadioButtonByValue(fieldId);
        }
        if (actionName.equals("screenshot")){
            screenshot("SCR");
        }
        if (actionName.equals("selectOrganizationGroup")){
            LOGGER.info("Выбрать группу организации:'"+fieldValue+"'");
            selectOrganizationGroup(fieldValue);
        }
        if (actionName.equals("selectOrganizationClientManeger")){
            LOGGER.info("Выбрать менеджера организации:'"+fieldValue+"'");
            selectOrganizationClientManeger(fieldValue);
        }
        if (actionName.equals("openOrganizationByName")){
            LOGGER.info("Открыть организацию:'"+fieldName+"'");
            openOrganizationByName(fieldName);
        }
        if (actionName.equals("closeActivationDialog")){
            LOGGER.info("На диалоге о подтверждении активации нажать 'ДА'");
            closeActivationDialog();
        }
        if (actionName.equals("delay")){
            delay(new Integer(fieldName));
        }
        if (actionName.equals("findOrganizationByName")){
            LOGGER.info("Найти организацию:'"+fieldName+"'");
            findOrganizationByName(fieldName);
        }
        if (actionName.equals("clickOnOrganizationMenu")){
            LOGGER.info("Нажать в меню организации на пункт:'"+fieldName+"'");
            clickOnOrganizationMenu(fieldName);
        }
        if (actionName.equals("openCardDialog")){
            LOGGER.info("Нажать кнопку 'Добавить' в разделе 'Сведения о картах'");
            openCardDialog();
        }
        if (actionName.equals("openCardDialog")){
            LOGGER.info("Нажать кнопку 'Добавить' в разделе 'Сведения о картах'");
            openCardDialog();
        }
        if (actionName.equals("clickSaveOnCardDialog")){
            LOGGER.info("Нажать на кнопку 'Добавить' в диалоге 'Добавление карты'");
            clickSaveOnCardDialog();
        }
        if (actionName.equals("closeSuccessfulEmployeeSaveMessage")){
            LOGGER.info("Нажать 'OK' на сообщении об успешном сохранении");
            closeSuccessfulEmployeeSaveMessage();
        }
        if (actionName.equals("execTestFromXLS")){
            if (fieldValue.equals("Y")){
                LOGGER.info("Выполнить тесты из файла '"+fieldName+"' страница: '"+fieldId+"'");
                execTestFromXLS(fieldName,new Integer(fieldId).intValue());
            }
        }
        if (actionName.equals("openPage")){
            LOGGER.info("Открыть страницу '"+fieldName+"'");
            openPage(fieldName);
        }
        if (actionName.equals("authenticate")){
            LOGGER.info("Выполнить авторизацию логин: '"+fieldName+"' пароль: '"+fieldId+"'");
            authenticate(fieldName,fieldId);
        }
        if (actionName.equals("comment")){
            LOGGER.info(fieldName);
        }
        if (actionName.equals("stop")){
            LOGGER.info("Завершение теста");
        }
        if (actionName.equals("closeEmployeeSuccessfulOperationMessage")){
            LOGGER.info("Нажать ОК на сообщении об успешном сохранении сотрудника ");
            closeEmployeeSuccessfulOperationMessage();
        }

        
    }
    
    
    public void scrollUp(){
        js.executeScript("window.scrollTo(0,0);");
    }

}
