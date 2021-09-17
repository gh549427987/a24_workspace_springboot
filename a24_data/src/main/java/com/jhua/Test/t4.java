package com.jhua.Test;

import com.jhua.constants.Constants;
import com.jhua.utils.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static jdk.nashorn.internal.objects.NativeArray.lastIndexOf;

public class t4{

    public static void main(String[] args) throws IOException {

        String dest = Constants.DESKTOP;
        String srcPath = "C:\\Users\\wb.xiejiehua\\Desktop\\302NVIDIA_VR_Funhouse.zip";
        String srcPath2 = "C:\\Users\\wb.xiejiehua\\Desktop\\urls_1629020621941 - Copy.zip";
        String srcPath3 = "C:\\Users\\wb.xiejiehua\\Desktop\\925VR_Zombie_JJ_191129A_ZM_IN105UE4.zip";

        ArrayList<String> exeFromZip = getExeFromZip(srcPath3);
        System.out.println(exeFromZip);
    }

    public static ArrayList<String> getExeFromZip(String srcPath) throws IOException {

        ArrayList<String> exeFileList = new ArrayList<>();

        File file = new File(srcPath);
        if (!file.exists()) {
            throw new RuntimeException(srcPath + "所指文件不存在");
        }
        ZipFile zf = new ZipFile(file, Charset.forName("GBK"));
        Enumeration entries = zf.entries();
        ZipEntry entry = null;
        while (entries.hasMoreElements()) {

            entry = (ZipEntry) entries.nextElement();
            String name = entry.getName();

            int index = name.lastIndexOf("/");
            System.out.println("遍历zip包文件: " + name + " " + index + " " + (name.length() - 1));

            if (index != name.length() - 1 && name.endsWith(".exe")) {
                String exe = name.substring(index + 1);
                exeFileList.add(exe);
            }

        }
        zf.close();

        System.out.println("================ exe list" + exeFileList);
        return exeFileList;

    }
}
