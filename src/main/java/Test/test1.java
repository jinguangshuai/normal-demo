package Test;

import cn.hutool.core.collection.CollUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @Auther：jinguangshuai
 * @Data：2022/3/6 - 03 - 06 - 21:07
 * @Description:Test
 * @version:1.0
 */
public class test1 {
    public static void main(String[] args)throws Exception{
        FileInputStream fis = new FileInputStream("C:\\Users\\JGS\\Desktop\\tower.txt");
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        ArrayList<String> list = new ArrayList<>();
        String line = "";
        while ((line = br.readLine()) != null) {
            // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
            if (line.lastIndexOf("---") < 0) {
                list.add(line);
                System.out.println(line);
            }
        }
        br.close();
        /*if(CollUtil.isNotEmpty(list)){
            String date = list.get(0).substring(0,list.get(0).length()-8);
            System.out.println(date);
            for (int i = 1; i < list.size(); i++) {
                while("24小时预警数据".equals(list.get(i+1))){
                    String tfDate = list.get(i+1).substring(0,list.get(i+1).length()-6);
                    System.out.println("预警数据"+tfDate);
                }
            }
        }*/
    }
}
