package autotest.Common;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class Utils {
    private static volatile String LOGDIR;
    public static String getLogDir() throws FileNotFoundException, IOException{
        String localLogDir = LOGDIR;
        if (localLogDir == null){
            synchronized (Utils.class){
                localLogDir = LOGDIR;
                if (localLogDir == null){
                    //make log directory
                    Properties properties = getProperties();
                    String logDir = properties.getProperty("logDir");
                    String testDir = "Test "+getTme();
                    LOGDIR = localLogDir = logDir+testDir+"\\";
                    Boolean success = (new File(localLogDir)).mkdirs();
                    if (!success) {
                        // Directory creation failed
                    }
                    //add recourses
                    File source = new File("template/JS");
                    File desc = new File(localLogDir+"/JS");
                    try {
                        FileUtils.copyDirectory(source, desc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return localLogDir;
    }
    
    private static volatile Properties PROPERTIES;
    public static Properties getProperties() throws FileNotFoundException, IOException{
        Properties localProperties = PROPERTIES;
        if (localProperties == null){
            synchronized (Utils.class){
                localProperties = PROPERTIES;
                if (localProperties == null){
                    PROPERTIES = localProperties = new Properties();
                    localProperties.load(new FileInputStream("MSOSTEST.properties"));
                }
            }
        }
        return localProperties;
    }
    
    public static String getTme(){
        Date d = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss"); 
        return dt.format(d);
    }
    
    private static volatile boolean LoggerInit=false;
    public static Logger getLogger(String className){
        Logger localLogger = Logger.getLogger(className);
        if (!LoggerInit){
            synchronized (Utils.class){
                if (!LoggerInit){                
                    try {
                        Handler fh = new FileHandler(getLogDir()+"log.html");
                        HTMLFormatter sf = new HTMLFormatter();
                        fh.setFormatter(sf);
                        localLogger.addHandler(fh);
                        LoggerInit = true;
                    } catch (IOException ex) {
                        Logger.getLogger(className).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(className).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return localLogger;
    }
    
    public static String readTemplate(String fname, String tag) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        String res = new String();
        FileInputStream fstream = new FileInputStream("./template/"+fname);
        Reader reader = new InputStreamReader(fstream,"UTF-8");
        BufferedReader fin = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s;
        boolean get=false;
        while ((s=fin.readLine())!=null) {
            if (s.indexOf("<!--<"+tag+">-->")!=-1){get=true;s="";};
            if (s.indexOf("<!--</"+tag+">-->")!=-1){get=false;s="";};
            if (get){
                sb.append(s);
                sb.append("\n");
            }
        }
        reader.close();
        return sb.toString();
    }
    
    public static String formatTime(Date d){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss"); 
        return dt.format(d);
    }
}
