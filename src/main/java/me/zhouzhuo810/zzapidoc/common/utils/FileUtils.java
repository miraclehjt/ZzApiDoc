package me.zhouzhuo810.zzapidoc.common.utils;

import org.springframework.web.multipart.MultipartFile;

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
        String filename = System.currentTimeMillis()+".json";
        String mPath = path + File.separator
                + filename;
        OutputStream outputStream = new FileOutputStream(mPath);
        byte[] buffer = content.getBytes();
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();
        return filename;
    }


}
