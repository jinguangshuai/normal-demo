package Test2;

/**
 * @Auther：jinguangshuai
 * @Data：2023/11/13 - 11 - 13 - 15:22
 * @Description:Test2
 * @version:1.0
 */
public class test11 {

    public static void main(String[] args) {
        String s = "1微地形覆冰预测_起报时间2023110814.xls";
        String[] s1 = s.split("_");
        System.out.println(s1[1].substring(4, 14));
    }
}
