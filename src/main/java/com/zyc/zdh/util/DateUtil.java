package com.zyc.zdh.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static final FastDateFormat df
            = FastDateFormat.getInstance("yyyy-MM-dd");
    public static final FastDateFormat df_nodash
            = FastDateFormat.getInstance("yyyyMMdd");


    /** *
     *
     * @example add("20180101",90),add("20180101",-90)
     * @param start  起始日期
     * @param dayNum 天数
     * @return
     */
    public static Date add(Date start,int dayNum){
        Calendar beginDate = Calendar.getInstance();
        beginDate.setTime(start);
        beginDate.add(Calendar.DAY_OF_MONTH, dayNum);
        return beginDate.getTime();
    }

    public static Date pase(String date) throws Exception{
        return df.parse(date);
    }


    public static String  format(Date date){
       return df.format(date);
    }

    public static String  formatNodash(Date date){
        return df_nodash.format(date);
    }
}
