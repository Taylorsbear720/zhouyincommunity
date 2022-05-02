package com.zhouyin.comunity.entity;

import java.util.List;

public class EsearchResult {
    private List<DiscussPost> list;
    private long total;
    public EsearchResult(List<DiscussPost> list, long total) {
        this.list = list;
        this.total = total;
    }

    public List<DiscussPost> getList() {
        return list;
    }

    public void setList(List<DiscussPost> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}