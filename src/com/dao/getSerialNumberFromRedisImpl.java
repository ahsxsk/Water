package com.dao;

import org.springframework.stereotype.Repository;

/**
 * Created by shike on 16/2/2.
 * get serial number from mysql
 * return  int
 */
@Repository
public class getSerialNumberFromRedisImpl implements  getSerialNumberFromRedis {
    public int getSerialNum() {
        return 12345;
    }
}
