package pt.ipg.mcm.app.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {
    public static String centsToEuros(long cents){
        DecimalFormat df = new DecimalFormat("#,##0.00 €");
        return df.format(new BigDecimal(cents).divide(new BigDecimal(100)));
    }
    public static String dateSimpleToString(Date date){
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
        return dtf.format(date);
    }
    public static String dateTimeToString(Date date){
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dtf.format(date);
    }
}
