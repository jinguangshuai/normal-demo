package Test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.pinyin.PinyinUtil;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @Auther：jinguangshuai
 * @Data：2022/9/2 - 09 - 02 - 16:49
 * @Description:Test
 * @version:1.0
 */
public class test11 {

    public static void main(String[] args) {

        LocalDateTime str = DateUtil.parse("2022-01-01", "yyyy-MM-dd").toLocalDateTime();

        LocalDateTime end = DateUtil.parse("2022-03-01", "yyyy-MM-dd").toLocalDateTime();


        System.out.println(str.toString());

        Date date = new Date();
        System.out.println(date.toString());
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

//        while(str.isBefore(end)){
//
//            str = str.plusDays(1);
//            System.out.println(str);
//        }
//        Date from = Date.from(str.atZone(ZoneOffset.ofHours(8)).toInstant());
//        System.out.println(from);
//
//        String s = PinyinUtil.getPinyin("师姐");
//        System.out.print(s);


    }
}
