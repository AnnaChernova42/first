/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autotest.Common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import autotest.Common.Utils;

/**
 *
 * @author Alexandr
 */
class HTMLFormatter extends java.util.logging.Formatter {
  public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("                  <tr id=\""+record.getLevel()+"\">\n");
        sb.append("                     <td>"+Utils.formatTime(new Date(record.getMillis()))+"</td>\n");
        sb.append("                     <td>"+record.getLevel()+"</td>\n");
        sb.append("                     <td>"+record.getMessage().replaceAll("\n", "<br/>\n") +"</td>\n");
        sb.append("                  </tr>\n");
        return sb.toString();
  }

  public String getHead(Handler h){
      String head = "";
        try {
            head = Utils.readTemplate("temlate.html","head");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTMLFormatter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HTMLFormatter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTMLFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
      return head;
  }

  public String getTail(Handler h) {
      String tail = "";
        try {
            tail = Utils.readTemplate("temlate.html","tail");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTMLFormatter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HTMLFormatter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTMLFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
      return tail;
  }
}
