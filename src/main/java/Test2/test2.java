package Test2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther：jinguangshuai
 * @Data：2023/8/10 - 08 - 10 - 9:34
 * @Description:Test2
 * @version:1.0
 */
public class test2 {

    public static void main(String[] args) {
        try{
            File file = new File("C:\\Users\\JGS\\Desktop\\老es.txt");
            File file1 = new File("C:\\Users\\JGS\\Desktop\\新es.txt");
            List<FileInputStream> list = new ArrayList<>();
            list.add(new FileInputStream(file));
            list.add(new FileInputStream(file1));
            for (int i = 0; i < list.size(); i++) {
                File file2 = new File("C:\\Users\\JGS\\Desktop\\1"+File.separator+i);
                if(!file2.exists()){
                    file2.mkdirs();
                }
                FileOutputStream outputStream = new FileOutputStream(new File(file2.getPath()+File.separator+"es.txt"));
                int read = list.get(i).read();
                while (read != -1){
                    outputStream.write(read);
                    read = list.get(i).read();
                }
                outputStream.close();
                list.get(i).close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
