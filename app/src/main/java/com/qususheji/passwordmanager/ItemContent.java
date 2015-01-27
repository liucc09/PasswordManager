package com.qususheji.passwordmanager;

import java.util.HashMap;

/**
 * Created by liucc09 on 2015/1/18.
 */
public class ItemContent{
    public int get_id() {
        return _id;
    }

    private int _id;
    public  String des = "";
    public  String account = "";
    public  String password = "";

    public ItemContent(){

    }

    public ItemContent(int _id, String des, String account, String password){
        this._id = _id;
        this.des = des;
        this.account = account;
        this.password = password;
    }



}
