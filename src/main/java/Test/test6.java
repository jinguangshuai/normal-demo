package Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @Auther：jinguangshuai
 * @Data：2022/4/7 - 04 - 07 - 17:15
 * @Description:Test
 * @version:1.0
 */
public class test6 {
    public static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String format = simpleDateFormat.format("1678363200000");
//        System.out.println(format);

        Instant instant = Instant.ofEpochMilli(Long.parseLong("1678363200000"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String strDate = formatter.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        System.out.println(strDate);

    }


}
