package Test2;

import jdk.nashorn.internal.ir.CaseNode;
import org.apache.commons.lang3.StringUtils;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther：jinguangshuai
 * @Data：2023/10/21 - 10 - 21 - 10:23
 * @Description:com.mashibing.jmh.class07
 * @version:1.0
 */
public class test06<T> {
//    public static void main(String[] args) {
//        try {
//            File f1 = new File("C:\\Users\\JGS\\Desktop\\SURF_HOR_2023101910.txt");
//            FileReader fileReader = new FileReader(f1);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            int i = 0;
//            String str = bufferedReader.readLine();//每次读取文本文件一行，返回字符串
//            while(str!=null){
//                str = bufferedReader.readLine();
//                i++;
//                if(i!=0 && StringUtils.isNotBlank(str)){
//                    String[] s = str.split("\t");
//                    System.out.println(s);
//                    StringBuilder ss=new StringBuilder("");
//                    for (int j = 0; j < s.length; j++) {
//                        ss.append("-"+s[j]+"-");
//                    }
//                    System.out.println(ss);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }


    public  List analysisTxt(Object object, String filePath){

        List<Object> list = new ArrayList();
        try {
            File f1 = new File(filePath);
            FileReader fileReader = new FileReader(f1);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int i = 0;
            String str = bufferedReader.readLine();//每次读取文本文件一行，返回字符串
            while(str!=null){
                str = bufferedReader.readLine();
                i++;
                if(i!=0 && StringUtils.isNotBlank(str)){
                    user user = new user();
                    //属性值
                    String[] s = str.split("\t");
                    for (int j = 0; j < s.length; j++) {
                        user.setAge(s[0]);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        List list = new test06().analysisTxt(new user(), "C:\\Users\\JGS\\Desktop\\1.txt");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
            user user = (user) list.get(i);
            System.out.println(user.getAge());
            System.out.println(user.getName());
        }

    }
}
