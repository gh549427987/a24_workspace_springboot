package com.jhua.service.impl;

import com.jhua.dao.GamesImagesMapper;
import com.jhua.model.GamesImages;
import com.jhua.service.GamesImagesService;
import com.jhua.utils.ZammerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiejiehua
 * @DATE 8/16/2021
 */

@Service
public class GamesImagesServiceImpl implements GamesImagesService {

    @Autowired
    GamesImagesMapper gamesImagesMapper;

    @Override
    public String collectGameImagesInfo(int gameIDFrom, int gameIDTo) throws Exception {

        ZammerUtils zammerUtils = new ZammerUtils();

        for (int game_id = gameIDFrom; game_id <= gameIDTo; game_id++) {
            List<GamesImages> imageUrls = zammerUtils.getImageUrls(game_id);
            for (GamesImages imageUrl : imageUrls) {
                imageUrl.setGameId(game_id);
                if (imageUrl.getName().isEmpty()) {
                    imageUrl.setName("视频");
                }
                gamesImagesMapper.insert(imageUrl);
            }
        }

        return null;
    }

}
