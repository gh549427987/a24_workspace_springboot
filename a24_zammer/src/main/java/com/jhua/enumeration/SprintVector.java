package com.jhua.enumeration;

/**
 * @author xiejiehua
 * @DATE 8/17/2021
 */

public enum SprintVector {
    SV_EVENT_START("match_start"),
    SV_EVENT_END("match_recap"),
    ;

    public String event;

    SprintVector(String event) {
        this.event = event;
    }
}
