package Test;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther：jinguangshuai
 * @Data：2022/8/17 - 08 - 17 - 10:47
 * @Description:Test
 * @version:1.0
 */
public class test09 {
    public static void main(String[] args) {
        Date date = new Date();
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        System.out.println(format);
        String replace = format.replace("-", "");
        System.out.println(StringUtils.substring(replace, 0, 4));
        System.out.println(StringUtils.substring(replace, 0, 6));
        System.out.println(StringUtils.substring(replace, 0, 8));

        String s = "shanxi";
        System.out.println(s.toUpperCase());

//        System.out.println(StringUtils.substring(format, 0, 4));
//        System.out.println(StringUtils.substring(format, 5, 7));
//        System.out.println(StringUtils.substring(format, 8, 10));

        /*String password = new Md5Hash("password").toHex();
        System.out.println(password);

        String s = "1.2.3";
        String[] split = StringUtils.split(s,".");
        int length = split.length;
        System.out.println(length);*/
    }
}
