package me.zhouzhuo810.zzapidoc.common.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by admin on 2017/7/22.
 */
public class FileUtils {

    public static String saveFileToServer(String content, String path)
            throws IOException {
        // 创建目录
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 读取文件流并保持在指定路径
        String filename = System.currentTimeMillis() + ".txt";
        String mPath = path + File.separator
                + filename;
        System.out.println(path);
        OutputStream outputStream = new FileOutputStream(mPath);
        byte[] buffer = content.getBytes();
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();
        return filename;
    }

    public static void deleteFiles(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    file1.delete();
                }
            }
        }
    }


    public static String saveFile(byte[] data, String dirName, String fileName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String realPath = request.getRealPath("");
        String newpath = realPath + File.separator + dirName;
        File file = new File(newpath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = newpath + File.separator + fileName;
        try {
            FileOutputStream fos = new FileOutputStream(new File(filePath), false);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

}
