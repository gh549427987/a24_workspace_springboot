package com.jhua.dao;

import com.jhua.model.ZammerGames;
import com.jhua.model.ZammerGamesExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ZammerGamesMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    long countByExample(ZammerGamesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int deleteByExample(ZammerGamesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int insert(ZammerGames record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int insertSelective(ZammerGames record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    List<ZammerGames> selectByExampleWithBLOBs(ZammerGamesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    List<ZammerGames> selectByExample(ZammerGamesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    ZammerGames selectByPrimaryKey(Integer id);
    ZammerGames selectByGameId(Integer game_id);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int updateByExampleSelective(@Param("record") ZammerGames record, @Param("example") ZammerGamesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int updateByExampleWithBLOBs(@Param("record") ZammerGames record, @Param("example") ZammerGamesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int updateByExample(@Param("record") ZammerGames record, @Param("example") ZammerGamesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int updateByPrimaryKeySelective(ZammerGames record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int updateByPrimaryKeyWithBLOBs(ZammerGames record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zammer_games
     *
     * @mbg.generated Thu Aug 19 17:14:26 CST 2021
     */
    int updateByPrimaryKey(ZammerGames record);
}