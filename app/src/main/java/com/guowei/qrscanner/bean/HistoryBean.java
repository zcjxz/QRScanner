package com.guowei.qrscanner.bean;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class HistoryBean {
    private String history;
    private String time;

    public HistoryBean(String history, String time) {
        this.history = history;
        this.time = time;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
