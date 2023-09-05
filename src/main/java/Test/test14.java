package Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther：jinguangshuai
 * @Data：2023/4/13 - 04 - 13 - 15:08
 * @Description:Test
 * @version:1.0
 */
public class test14 {
    public static void main(String[] args) {
//        File file = new File("C:\\Users\\JGS\\AppData\\Local\\Temp\\met\\highRain\\2023\\202303\\20230307\\2023030720\\highRain_2023030720.nc");
//        file.delete();
        Date date = convertTimeStamp2Date(2010090812);
        System.out.println(date.toString());

    }

    public static Date convertTimeStamp2Date(long timeStamp) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeStamp);
        try {
            String format = df.format(date);
            date = df.parse(df.format(date));
        } catch (ParseException e) {
            System.out.println("时间格式化错误");
        }
        return date;
    }


}
