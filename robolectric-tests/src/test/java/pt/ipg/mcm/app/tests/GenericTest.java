package pt.ipg.mcm.app.tests;


import org.junit.Assert;
import org.junit.Test;
import pt.ipg.mcm.app.util.Formatter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static pt.ipg.mcm.app.util.Formatter.dateSimpleToString;
import static pt.ipg.mcm.app.util.Formatter.dateTimeToString;

public class GenericTest {
    @Test
    public void testIntToBigdecimal() {
        Assert.assertEquals("1,00 €", Formatter.centsToEuros(100));
        Assert.assertEquals("1,50 €", Formatter.centsToEuros(150));
    }

    @Test
    public void testSimpleDateToString() {
        Date date = new GregorianCalendar(2015, Calendar.JANUARY, 2).getTime();
        Assert.assertEquals("2015-01-02", dateSimpleToString(date));
    }
    @Test
    public void testDateTimeToString() {
        Date date = new GregorianCalendar(2015, Calendar.JANUARY, 2,10,15,35).getTime();
        Assert.assertEquals("2015-01-02 10:15:35", dateTimeToString(date));
    }
}
