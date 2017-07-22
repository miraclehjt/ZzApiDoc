package me.zhouzhuo810.zzapidoc.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by admin on 2017/7/22.
 */
public class FileUtils {

    public static String saveFileToServer(MultipartFile multipartFile, String path)
            throws IOException {
        // 创建目录
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 读取文件流并保持在指定路径
        InputStream inputStream = multipartFile.getInputStream();
        OutputStream outputStream = new FileOutputStream(path
                + multipartFile.getOriginalFilename());
        byte[] buffer = multipartFile.getBytes();
        int byteSum = 0;
        int byteRead = 0;
        while ((byteRead = inputStream.read(buffer)) != -1) {
            byteSum += byteRead;
            outputStream.write(buffer, 0, byteRead);
            outputStream.flush();
        }
        outputStream.close();
        inputStream.close();
        return path + multipartFile.getOriginalFilename();
    }


}
