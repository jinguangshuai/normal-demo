package Test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Auther：jinguangshuai
 * @Data：2022/8/31 - 08 - 31 - 11:27
 * @Description:Test
 * @version:1.0
 */
public class test10 {

    public static void main(String[] args) {

        Date st = new Date();
        String start = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(start);

        String end = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(end);

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Date en = new Date();
        long between = DateUtil.between(st, en, DateUnit.MS);
        System.out.println(between);
    }
}
