package Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther：jinguangshuai
 * @Data：2022/3/6 - 03 - 06 - 21:07
 * @Description:Test
 * @version:1.0
 */
public class test2 {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHH");
        SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = dateFormat.parse("2010090812");
        String fomart =dateFormat2.format(d);
        System.out.println(fomart);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyyMMdd");
        String x = simpleDateFormat.format(date);
        System.out.println(x);

        for (int i = 0; i < 25; i++) {
            String str = String.format("%02d",i);
            String s = x + str;
            System.out.println(s);
        }
        String str = "国网黑龙江c:/adc/aav02.txt";
        String str1 = "/2021100712/pdf";
        String str2 = "yjhp24h";
        String substring1 = str.substring(0, str.length()-2);
        String substring2 = str.substring(1,3);
        String substring3 = str1.substring(1,11);
        String substring4 = str2.substring(4,7);
        String substring5 = str.substring(0,str.length()-4);
        String substring6 = str2.substring(0,2);
        String substring7 = str2.substring(str2.length()-3,str2.length()-1);
        System.out.println(substring2);
        System.out.println(substring4);
        System.out.println(substring5);
        System.out.println(substring6);
        System.out.println(substring7);
        /*System.out.println(substring1);
        String substring = str.substring(2, 5);
        System.out.println(substring);*/
    }
}
