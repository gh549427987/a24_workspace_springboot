package com.jhua.vo;


import java.util.Date;

/**
 * @Description todo
 * @Author weipeng01
 * @Date 2021/6/8 19:41
 */
public class SprintVectorOriginGameData {

    public SprintVectorOriginGameData(String map) {
        this.level_name = map;
        this.match_uid = String.valueOf(new Date().getTime());
        this.session_uid = String.valueOf(new Date().getTime()+21);
        this.skin_id = "阿波罗";
        this.character_name = "Mr Entertainment";
        this.time = new Date().getTime() / 1000;
    }

    /**
     * 用户id，每个客户端不一样
     * @value 231
     */
    private String user_id;

    /**
     * unix utc 时间
     * @value 1622136678
     */
    private long time;

    /**
     * 游戏事件名称
     * @value match_recap 游戏到达终点事件
     * @value match_start 游戏开始事件
     * @value match_checkpoint 到达其中某个检查点事件
     * @value item_pickup
     * @value player_boosted
     */
    private String event_name;

    /**
     * 检查点id
     * @value 0.984522
     */
    private String checkpoint_id;

    /**
     * 地图名称
     * @value 温布尔·汤德拉
     */
    private String level_name;

    /**
     * @value B8D70BC947AF3A01E92CA7B607A57666
     */
    private String match_uid;

    /**
     * @value false|true
     */
    private String casual_mode;

    /**
     * 本局排名
     * @value 7
     */
    private String ranking;

    /**
     * 圈速计时，用于标识最佳圈速
     * @value 671.576294
     */
    private String time_since_race_started;

    /**
     * 速度 545.671936
     * @value
     */
    private String speed;

    /**
     * @value 0.984435
     */
    private String position;

    /**
     * 本局角色名称
     * @value SpaceBoyRacer
     */
    private String character_name;

    /**
     * 皮肤id
     * @value 阿波罗
     */
    private String skin_id;

    /**
     * check point是否是终点
     * @value true|false
     */
    private String finished_match;

    /**
     * @value true|false
     */
    private String is_hosting;

    /**
     * 是否是机器人
     * @value 0-否，1-是
     */
    private String is_bot;

    /**
     * 其他玩家id，最多8人
     * @value bot_level_3 | 231
     */
    private String other_player_id_1;
    private String other_player_id_2;
    private String other_player_id_3;
    private String other_player_id_4;
    private String other_player_id_5;
    private String other_player_id_6;
    private String other_player_id_7;

    private String jump_count;
    private String double_jump_count;
    private String drift_count;
    private String fly_count;
    private String climb_grabs_count;
    private String drift_distance;
    private String fly_distance;
    private String shots_count;
    private String item_nitroboost;
    private String item_chronoshift;
    private String previous_stars_on_level;
    private String new_stars_on_level;
    /**
     * @value 1622136678
     */
    private String end_time;

    /**
     * @value 1f995296-b10c-4d40-94b7-dc5dc9a9c18d
     */
    private String session_uid;
    private String td_ip = "td_ip";
    private String build_id = "248884";

    /**
     * @value SprintVector-NetEase
     */
    private String game_id = "SprintVector-NetEase";

    /**
     * @value steam
     */
    private String package_type = "steam";

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getCheckpoint_id() {
        return checkpoint_id;
    }

    public void setCheckpoint_id(String checkpoint_id) {
        this.checkpoint_id = checkpoint_id;
    }

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public String getMatch_uid() {
        return match_uid;
    }

    public void setMatch_uid(String match_uid) {
        this.match_uid = match_uid;
    }

    public String getCasual_mode() {
        return casual_mode;
    }

    public void setCasual_mode(String casual_mode) {
        this.casual_mode = casual_mode;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getTime_since_race_started() {
        return time_since_race_started;
    }

    public void setTime_since_race_started(String time_since_race_started) {
        this.time_since_race_started = time_since_race_started;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCharacter_name() {
        return character_name;
    }

    public void setCharacter_name(String character_name) {
        this.character_name = character_name;
    }

    public String getSkin_id() {
        return skin_id;
    }

    public void setSkin_id(String skin_id) {
        this.skin_id = skin_id;
    }

    public String getFinished_match() {
        return finished_match;
    }

    public void setFinished_match(String finished_match) {
        this.finished_match = finished_match;
    }

    public String getIs_hosting() {
        return is_hosting;
    }

    public void setIs_hosting(String is_hosting) {
        this.is_hosting = is_hosting;
    }

    public String getIs_bot() {
        return is_bot;
    }

    public void setIs_bot(String is_bot) {
        this.is_bot = is_bot;
    }

    public String getOther_player_id_1() {
        return other_player_id_1;
    }

    public void setOther_player_id_1(String other_player_id_1) {
        this.other_player_id_1 = other_player_id_1;
    }

    public String getOther_player_id_2() {
        return other_player_id_2;
    }

    public void setOther_player_id_2(String other_player_id_2) {
        this.other_player_id_2 = other_player_id_2;
    }

    public String getOther_player_id_3() {
        return other_player_id_3;
    }

    public void setOther_player_id_3(String other_player_id_3) {
        this.other_player_id_3 = other_player_id_3;
    }

    public String getOther_player_id_4() {
        return other_player_id_4;
    }

    public void setOther_player_id_4(String other_player_id_4) {
        this.other_player_id_4 = other_player_id_4;
    }

    public String getOther_player_id_5() {
        return other_player_id_5;
    }

    public void setOther_player_id_5(String other_player_id_5) {
        this.other_player_id_5 = other_player_id_5;
    }

    public String getOther_player_id_6() {
        return other_player_id_6;
    }

    public void setOther_player_id_6(String other_player_id_6) {
        this.other_player_id_6 = other_player_id_6;
    }

    public String getOther_player_id_7() {
        return other_player_id_7;
    }

    public void setOther_player_id_7(String other_player_id_7) {
        this.other_player_id_7 = other_player_id_7;
    }

    public String getJump_count() {
        return jump_count;
    }

    public void setJump_count(String jump_count) {
        this.jump_count = jump_count;
    }

    public String getDouble_jump_count() {
        return double_jump_count;
    }

    public void setDouble_jump_count(String double_jump_count) {
        this.double_jump_count = double_jump_count;
    }

    public String getDrift_count() {
        return drift_count;
    }

    public void setDrift_count(String drift_count) {
        this.drift_count = drift_count;
    }

    public String getFly_count() {
        return fly_count;
    }

    public void setFly_count(String fly_count) {
        this.fly_count = fly_count;
    }

    public String getClimb_grabs_count() {
        return climb_grabs_count;
    }

    public void setClimb_grabs_count(String climb_grabs_count) {
        this.climb_grabs_count = climb_grabs_count;
    }

    public String getDrift_distance() {
        return drift_distance;
    }

    public void setDrift_distance(String drift_distance) {
        this.drift_distance = drift_distance;
    }

    public String getFly_distance() {
        return fly_distance;
    }

    public void setFly_distance(String fly_distance) {
        this.fly_distance = fly_distance;
    }

    public String getShots_count() {
        return shots_count;
    }

    public void setShots_count(String shots_count) {
        this.shots_count = shots_count;
    }

    public String getItem_nitroboost() {
        return item_nitroboost;
    }

    public void setItem_nitroboost(String item_nitroboost) {
        this.item_nitroboost = item_nitroboost;
    }

    public String getItem_chronoshift() {
        return item_chronoshift;
    }

    public void setItem_chronoshift(String item_chronoshift) {
        this.item_chronoshift = item_chronoshift;
    }

    public String getPrevious_stars_on_level() {
        return previous_stars_on_level;
    }

    public void setPrevious_stars_on_level(String previous_stars_on_level) {
        this.previous_stars_on_level = previous_stars_on_level;
    }

    public String getNew_stars_on_level() {
        return new_stars_on_level;
    }

    public void setNew_stars_on_level(String new_stars_on_level) {
        this.new_stars_on_level = new_stars_on_level;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSession_uid() {
        return session_uid;
    }

    public void setSession_uid(String session_uid) {
        this.session_uid = session_uid;
    }

    public String getTd_ip() {
        return td_ip;
    }

    public void setTd_ip(String td_ip) {
        this.td_ip = td_ip;
    }

    public String getBuild_id() {
        return build_id;
    }

    public void setBuild_id(String build_id) {
        this.build_id = build_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getPackage_type() {
        return package_type;
    }

    public void setPackage_type(String package_type) {
        this.package_type = package_type;
    }
}
