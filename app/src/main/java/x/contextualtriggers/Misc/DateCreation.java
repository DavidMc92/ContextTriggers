package x.contextualtriggers.Misc;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class DateCreation {

    public static Date getStartOfDay(@NonNull Date date){
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date addHours(@NonNull Date date, int numHours){
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, numHours);
        return c.getTime();
    }

    public static Date getEndOfDay(@NonNull Date date){
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
}
