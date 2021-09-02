package com.jhua.service;

import com.jhua.model.ZammerGames;

import java.io.File;
import java.util.List;

public interface ZammerGamesService {

    int collectZammerGameInfo(File file) throws Exception;

    int collectZammerGameInfo() throws Exception;

    ZammerGames selectByGameId(int game_id);

    ZammerGames selectByPrimaryKey(Integer id);

    List<Integer> selectByAllID();

    int updateByPrimaryKey(ZammerGames zammerGames);

}
