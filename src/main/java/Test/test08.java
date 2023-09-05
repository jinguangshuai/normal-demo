package Test;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther：jinguangshuai
 * @Data：2022/8/3 - 08 - 03 - 17:30
 * @Description:Test
 * @version:1.0
 */
public class test08 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        String string = StringUtils.join(list.toArray(), ",");
        System.out.println(string);

        List<String> list1 = Arrays.asList(string);
        System.out.println(list1.toString());
    }
}
