package Test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther：jinguangshuai
 * @Data：2022/12/8 - 12 - 08 - 10:23
 * @Description:Test
 * @version:1.0
 */
public class test12 {
    public static void main(String[] args) {
        String s = "123";
        String substring = s.substring(s.length() - 1);
        System.out.println(substring);

        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        list.add("1");
        list.add("2");

        if(list.contains("1")){
            System.out.println(list.get(3));
        }

        String m = "1.2.3.4";

        int a= 4;
        int b = 5;
        System.out.println(a^b);

        String ss = "2023-01-20 00:00:00";
        DateTime parse = DateUtil.parse(ss, "yyyy-MM-dd HH:mm:ss");
//        System.out.println(parse.toString());
//        DateUtil.format(parse,"yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = parse.toLocalDateTime();
        LocalDateTime addTime = localDateTime.plusDays(1);
        String format = DateUtil.format(localDateTime, "yyyy-MM-dd HH:mm:ss");
        System.out.println(format);

        String adddTimeFormat = DateUtil.format(addTime, "yyyy-MM-dd HH:mm:ss");
        System.out.println(adddTimeFormat);


//        LocalDateTime localDateTime1 = LocalDateTime.now();
//        System.out.println(localDateTime1);

    }
}
