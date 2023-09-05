package Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther：jinguangshuai
 * @Data：2022/3/7 - 03 - 07 - 17:26
 * @Description:Test
 * @version:1.0
 */
public class test3 {

    public static void main(String[] args) {
        String startTime = "2022-02-27";
        String endTime = "2022-03-06";
        String ss = "20220207";
        startTime.replaceAll("-","");
        endTime.replaceAll("-","");
        List<String> days = getDays(startTime, endTime);
        for (String s:days){
            s.replaceAll("-","");
            System.out.println(s);
        }

        String substring = ss.substring(0, 4);
        String substring1 = ss.substring(0, 6);
        String substring2 = ss.substring(0, 8);
        System.out.println(substring);
        System.out.println(substring1);
        System.out.println(substring2);

    }
    public static List<String> getDays(String startTime, String endTime) {
        // 返回的日期集合
        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat1.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }
}
