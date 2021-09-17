package com.jhua.controller;

import com.jhua.dao.ZammerGamesMapper;
import com.jhua.model.ZammerGames;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author xiejiehua
 * @DATE 9/3/2021
 */

@Controller
@Api(tags = "数据接口")
@RequestMapping("/api/data")
public class DataController {

//    @Autowired
//    ZammerGamesMapper zammerGamesMapper;
//
//    @ApiOperation("数据")
//    @GetMapping("/check")
//    public String check() throws IOException {
//        ZammerGames zammerGames = zammerGamesMapper.selectByPrimaryKey(2);
//        System.out.println(zammerGames);
//        return null;
//    }

    @ApiOperation("查询数据")
    @GetMapping("/download")
    public String fileDownLoad(HttpServletResponse response) throws IOException {

        String path = System.getProperty("user.dir") + "\\a24_data\\src\\main\\output\\平台(周)留存（判断标准是当日游戏时长大于3分钟）.xlsx";//user.dir指定了当前的路径

        String[] split = path.split("/");
        System.out.println(split[split.length - 1]);

        File file = new File(path);
        if (!file.exists()) {
            return "下载文件不存在";
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=11.xlsx");

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            System.out.println("{}" + e);
            return "下载失败";
        }
        return "下载成功";
    }

}
