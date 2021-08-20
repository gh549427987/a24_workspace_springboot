package com.jhua.controller;

import com.jhua.dao.ZammerGamesMapper;
import com.jhua.service.GamesImagesService;
import com.jhua.service.ZammerGamesService;
import com.jhua.service.impl.ZammerGamesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */

@RequestMapping(value = "/zammer")
@RestController
public class ZammerController {

    @Autowired
    GamesImagesService gamesImagesService;

    @Autowired
    ZammerGamesService zammerGamesService;

    @GetMapping(value = "/getImagesInfoInDB")
    public void getZammerImageDB(@RequestParam int fromGameID, @RequestParam int toGameID) {

        try {
            System.out.println("进来：getImagesInfoInDB");
            gamesImagesService.collectGameImagesInfo(fromGameID, toGameID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/getZammerGamesDB")
    public void getZammerGamesDB() {


        System.out.println("进来：getZammerGamesDB");
        String path = "C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\resources\\YongQi";
        File src = new File(path);
        File[] files = src.listFiles();
        for (File file : files) {

            // 非.txt结尾的直接跳过
            if (!file.getAbsolutePath().endsWith(".txt") || file.getAbsolutePath().startsWith("unidentify")) {
                continue;
            }
            try {
                // 传入单个文件，只读取.txt结尾的
                int i = zammerGamesService.collectZammerGameInfo(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @GetMapping(value = "/getZammerGames")
    public void getZammerGames() throws Exception {

        ZammerGamesServiceImpl zammerGamesService = new ZammerGamesServiceImpl();
        int i = zammerGamesService.collectZammerGameInfo();

    }
}
