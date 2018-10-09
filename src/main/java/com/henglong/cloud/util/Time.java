package com.henglong.cloud.util;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class Time {

    public String TimePuls(String time,Integer T) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date dt=sdf.parse(time);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.MONTH,T);;
        Date dt1=rightNow.getTime();
        return sdf.format(dt1);
    }

    public int differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2) //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0) //闰年
                {
                    timeDistance += 366;
                }
                else //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return (timeDistance + (day2-day1)+1) ;
        }
        else //不同年
        {
            return ((day2-day1)+1);
        }
    }


    /**
     *
     * N为分钟数
     * 前面一个为当前参数，后面一个为历史参数，过期则true，没过期则false
     * @param time
     * @param now
     * @param n
     * @return
     */
    public boolean belongDate(Date time, Date now, int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(now);//把当前时间赋给日历
        calendar.add(Calendar.MINUTE, n);
        Date before7days = calendar.getTime();   //得到n前的时间
        if (before7days.getTime() <= time.getTime()) {
            return true;
        } else {
            return false;
        }
    }

}
