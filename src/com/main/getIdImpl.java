package com.main;

import org.springframework.stereotype.Service;

/**
 * Created by shike on 16/2/2.
 * produce a unique id
 * return id String
 */
@Service
public class getIdImpl implements getId {
    public String getId(String userId) {
        return "12345";
    }
}
