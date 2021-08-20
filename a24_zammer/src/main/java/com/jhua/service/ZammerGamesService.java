package com.jhua.service;

import com.jhua.model.ZammerGames;

import java.io.File;

public interface ZammerGamesService {

    int collectZammerGameInfo(File file) throws Exception;

    int collectZammerGameInfo() throws Exception;

    ZammerGames selectByGameId(int game_id);

//    int updateByZammerGames(ZammerGames zammerGames);
}
