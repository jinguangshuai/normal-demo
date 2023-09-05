package Test;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * @Auther：jinguangshuai
 * @Data：2022/3/14 - 03 - 14 - 17:11
 * @Description:Test
 * @version:1.0
 */
public class test5 {
    public static void main(String[] args) throws ParseException {
        String file = StringUtils.join(Collections.singleton("202211122"),"中期预测报告.dco");

        String ss = "11/22/33/44.png";

        String[] str = StringUtils.split(ss,"/");
        String s = str[str.length-1];
        System.out.println(s);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time="2050-01-06 11:45:55";//注：改正后这里前后也加了空格
        Date date = format.parse(time);
        System.out.print("Format To times:"+date.getTime());
    }

}
