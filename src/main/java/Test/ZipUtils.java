package Test;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Auther：jinguangshuai
 * @Data：2022/3/24 - 03 - 24 - 16:00
 * @Description:Test
 * @version:1.0
 */
public class ZipUtils {
    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException {

        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }

                }
            }
        }
    }

    //shp压缩为压缩包
    public static void shpToZip(String filePath, String outFilePath) throws FileNotFoundException {
        File[] files = new File(filePath).listFiles();
        Set<String> linkedHashSet = new LinkedHashSet<>();
        for (File file : files) {
            linkedHashSet.add(file.getName().substring(0, file.getName().length() - 4));
        }
        ArrayList<File> list = new ArrayList<>();
        for (String str : linkedHashSet) {
            for (File file : files) {
                if (str.equals(file.getName().substring(0, file.getName().length() - 4))) {
                    list.add(file);
                }
            }
            FileOutputStream fos2 = new FileOutputStream(new File(outFilePath + str + ".zip"));
            ZipUtils.toZip(list, fos2);
            list.clear();
        }
    }

    /**
     * @param zipFileName   需要解压缩的文件名称
     * @param unzipFilePath 压缩后的文件路径
     * @throws Exception
     */
    public static void upZipFile(String zipFileName, String unzipFilePath) throws Exception {
        ZipFile zfile = new ZipFile(zipFileName);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                File f = new File(zipFileName);
                f.mkdir();
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(unzipFilePath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }

            is.close();
            os.close();
        }
        zfile.close();
        new File("C:\\Users\\JGS\\Desktop\\test\\test.zip").delete();
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
            if (!ret.exists())
                ret.mkdirs();
            ret = new File(ret, dirs[dirs.length - 1]);
            return ret;
        }
        return ret;
    }

    /**
     * 解压tar.gz 文件
     *
     * @param file      要解压的tar.gz文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    public static void unTarGz(File file, String outputDir){
        TarInputStream tarIn = null;
        try {
            tarIn = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))), 1024 * 2);
            TarEntry entry = null;
            //遍历文件夹
            while ((entry = tarIn.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = entry.getName();
                    String[] split = StringUtils.split(name,"/");
                    String result = "";
                    if(null!=split){
                        result = split[split.length-1];
                        File tmpFile = new File(outputDir + File.separator + result);
                        OutputStream out = null;
                        try {
                            out = new FileOutputStream(tmpFile);
                            int length = 0;
                            byte[] b = new byte[2048];
                            while ((length = tarIn.read(b)) != -1) {
                                out.write(b, 0, length);
                            }
                        } catch (IOException ex) {
                            System.out.println("写入流失败！");
                            throw ex;
                        } finally {
                            if (out != null)
                                out.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("解压归档文件出现异常");
        } finally {
            try {
                if (tarIn != null) {
                    tarIn.close();
                    file.delete();
                }
            } catch (Exception e) {
                System.out.println("关闭tarFile出现异常");
            }
        }
    }

    public static void unTarGzLinux(File file, String outputDir){
        TarArchiveInputStream  tarIn = null;
        try {
            tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(file))), 1024 * 2);
            TarArchiveEntry entry = null;
            //遍历文件夹
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = entry.getName();
                    String[] split = StringUtils.split(name,"/");
                    String result = "";
                    if(null!=split){
                        result = split[split.length-1];
                        File tmpFile = new File(outputDir + File.separatorChar + result);
                        tmpFile.createNewFile();
                        OutputStream out = null;
                        try {
                            out = new FileOutputStream(tmpFile);
                            int length = 0;
                            byte[] b = new byte[2048];
                            while ((length = tarIn.read(b)) != -1) {
                                out.write(b, 0, length);
                            }
                        } catch (IOException ex) {
                            throw ex;
                        } finally {
                            if (out != null)
                                out.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("解压归档文件出现异常");
        } finally {
            try {
                if (tarIn != null) {
                    tarIn.close();
                    file.delete();
                }
            } catch (Exception e) {
                System.out.println("关闭tarFile出现异常");
            }
        }
    }

    /**
      * 构建目录,目的解压到相同压缩目录下
      * @param outputDir
      * @param subDir
      */
     public static void createDirectory(String outputDir,String subDir){
         File file = new File(outputDir);
         if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空
             file = new File(outputDir + "/" + subDir);
         }
         if(!file.exists()){
           if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
                file.mkdirs();
         }
     }

    public static void main(String[] args) throws Exception {
        /** 测试压缩方法1  */
		/*FileOutputStream fos1 = new FileOutputStream(new File("D:/test/iceshape/mytest01.zip"));
		ZipUtils.toZip("D:/test/iceshape", fos1,true);*/

        /** 测试压缩方法2  */
        /*List<File> fileList = new ArrayList<>();
        fileList.add(new File("C:\\Users\\JGS\\Desktop\\test\\1.pdf"));
        fileList.add(new File("C:\\Users\\JGS\\Desktop\\test\\2.txt"));
        fileList.add(new File("C:\\Users\\JGS\\Desktop\\test\\3.java"));
        fileList.add(new File("C:\\Users\\JGS\\Desktop\\test\\4.jpg"));
        FileOutputStream fos2 = new FileOutputStream(new File("C:\\Users\\JGS\\Desktop\\test\\5.zip"));
        ZipUtils.toZip(fileList, fos2);*/

        //ZipUtils.shpToZip("C:\\Users\\JGS\\Desktop\\src\\","C:\\Users\\JGS\\Desktop\\src\\");
        //ZipUtils.upZipFile("C:\\Users\\JGS\\Desktop\\test\\test.zip", "C:\\Users\\JGS\\Desktop\\");

        ZipUtils.unTarGzLinux(new File("C:\\Users\\JGS\\Desktop\\test\\20220321.tar.gz"), "C:\\Users\\JGS\\Desktop\\test\\");
    }
}
