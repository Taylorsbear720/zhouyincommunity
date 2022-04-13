package com.zhouyin.comunity.dao;

import org.springframework.stereotype.Repository;

@Repository
public class TestDaocompile implements TestDao {
    @Override
    public String slect() {
        return "hhh";
    }
}
