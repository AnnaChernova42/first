package autotest.Common;

import java.util.Date;

public class Waiter {
    private long MaxWait;
    private Date d;
    //MaxWait - количество миллисекунд
    public Waiter(long MaxWait){
        this.MaxWait = MaxWait;
        this.d = new Date();
    }
    public boolean isTimeout(){
        return System.currentTimeMillis() - d.getTime() >= MaxWait;
    }
}
