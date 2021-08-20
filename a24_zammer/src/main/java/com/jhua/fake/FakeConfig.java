package com.jhua.fake;

import com.alibaba.druid.pool.ha.selector.StickyRandomDataSourceSelector;
import com.jhua.enumeration.BackendURL;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

public class FakeConfig {

    static String auth = "";
//    static String backendURL = BackendURL.DEVELOPMENT.getUrl();
//    static String xc_backendURL = BackendURL.DEVELOPMENT.getXc_url();
    static String backendURL = BackendURL.PRODUCTION.getUrl();
    static String xc_backendURL = BackendURL.PRODUCTION.getXc_url();

    // Creed
    /**
     * 对战类型：1练习赛，2pvp
     */
    public static Integer[] fightType_CR = {1, 3};

    /**
     * 是否胜利：1胜场，0败场
     */
    public static Integer[] playerWon_CR = {1, 0};

    /**
     * 玩家角色
     */
    public static final String[] playerActor_CR = {"Andy_Pono", "Ricky_Conlan", "Axel_Ramirez", "Danny_Wheeler", "Leo_Sporino", "Adonis_Creed"};


}
