package com.zx.repeat.crawler.util;

import java.util.List;

public class MultiThread extends Thread {
    private List<String> urList;
    private int PersentThread;
    private int totalThread;

    public MultiThread(List<String> url, int PersentThread, int totalThread) {
        this.urList = url;
        this.PersentThread = PersentThread;
        this.totalThread = totalThread;
    }

    public void run() {
        int urlSize=this.urList.size();
        int pagination = urlSize / this.totalThread;
        for (int i = 1; i <= this.totalThread; i++) {
            if (this.PersentThread == i&&this.PersentThread!=this.totalThread) {
                GrabTool.getUserInfo(pagination * (i - 1) + 1, pagination * i, this.urList);
            }else{
                GrabTool.getUserInfo(pagination * (this.totalThread - 1) + 1, urlSize, this.urList);
            }
        }
    }
}
