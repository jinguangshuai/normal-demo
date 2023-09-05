package Test;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * @Auther：jinguangshuai
 * @Data：2022/3/7 - 03 - 07 - 21:16
 * @Description:Test
 * @version:1.0
 */
public class test4 {
    /**
     * 递归删除
     * 删除某个目录及目录下的所有子目录和文件
     * @param file 文件或目录
     * @return 删除结果
     */
    public static boolean delFiles(File file){
        boolean result = false;
        //目录
        if(file.isDirectory()){
            File[] childrenFiles = file.listFiles();
            for (File childFile:childrenFiles){
                result = delFiles(childFile);
                if(!result){
                    return result;
                }
            }
        }
        //删除 文件、空目录
        result = file.delete();
        return result;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\JGS\\Desktop\\test\\2");
        FileUtil.del(file.getAbsolutePath());
    }
}
