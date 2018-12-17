package com.ljz.myproject.entity;

import android.support.annotation.NonNull;

import com.ljz.myproject.utils.CnToSpellUtil;

/**
 * Created by Welive on 2018/12/5.
 * 手机联系人的数据
 */

public class ContactInfo implements Comparable<ContactInfo> {
    public String id;
    public String name;
    public String phone;
    public String pinyin; // 姓名对应的拼音
    public String firstLetter; // 拼音的首字母
    public String userAvatar;
    public String userName;
    public String userNick;
    public int isFriend;
    public String userId;
    public int gradeLevel;
    public String userPosition;
    public String userCompany;
    public int userType;
    public boolean isUser = false;

    public ContactInfo(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        pinyin = CnToSpellUtil.getPinYin(name); // 根据姓名获取拼音
        firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            firstLetter = "#";
        }
    }

    @Override
    public int compareTo(@NonNull ContactInfo another) {
        if (firstLetter.equals("#") && !another.firstLetter.equals("#")) {
            return 1;
        } else if (!firstLetter.equals("#") && another.firstLetter.equals("#")) {
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(another.pinyin);
        }
    }
}

