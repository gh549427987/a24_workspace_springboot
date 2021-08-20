package com.jhua.vo;

import com.jhua.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author jiangdonglin
 * @project netvios_wxapp_server
 * @created 2020/10/26 - 14:49
 * @Description:
 */

@Getter
@Setter
public class CreedOriginGameData {

    public CreedOriginGameData() {
        // 得分点
        this.playerHitsLanded = RandomUtils.RandomInt(10, 70);
//        this.playerHitsLanded = 1;
    }

    /**
     * 游戏开始时间，单位：秒
     */
    private long begin;

    /**
     * 游戏结束时间，单位：秒
     */
    private long end;

    /**
     * 对战类型：1练习赛，2pvp
     */
    private int fightType;

    /**
     * 是否胜利：1胜场，0败场
     */
    private int playerWon;

    /**
     * 对手union id
     */
    private String opponentUnionId;

    /**
     * 玩家角色
     */
    private String playerActor;

    /**
     * 对手角色
     */
    private String opponentActor;

    /**
     * 击中次数
     */
    private long playerHitsLanded;

    /**
     * 格挡次数
     */
    private long playerHitsBlocked;

    /**
     * 受击次数
     */
    private long opponentHitsReceived;

    /**
     * 被格挡次数
     */
    private long opponentHitsBlocked;

    /**
     * 击倒次数
     */
    private long playerKnockedDown;

    /**
     * 被击倒次数
     */
    private long playerWasKnockedDown;
}
