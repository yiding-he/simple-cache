package com.hyd.simplecache.bean;

import java.io.Serializable;

/**
 * [description]
 *
 * @author yiding.he
 */
public class User extends NonSerializableUser implements Serializable {

    public User() {
    }

    public User(String username, String password) {
        super(username, password);
    }
}
