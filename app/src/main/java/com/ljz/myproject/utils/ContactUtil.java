package com.ljz.myproject.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ljz.myproject.entity.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Welive on 2018/12/5.
 * 获取联系人数据的工具类
 */

public class ContactUtil {

    //获取联系人数据
    public static List<ContactInfo> getAllContacts(Context context) {
        List<ContactInfo> list = new ArrayList<>();
        // 获取解析者
        ContentResolver resolver = context.getContentResolver();
        // 访问地址
        Uri raw_contacts = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri data = Uri.parse("content://com.android.contacts/data");
        // 查询语句
        Cursor cursor = resolver.query(raw_contacts, new String[] { "contact_id" }, null, null, null);

        assert cursor != null;
        while (cursor.moveToNext()) {
            // getColumnIndex根据名称查列号
            String id = cursor.getString(cursor.getColumnIndex("contact_id"));
            // 创建实例
            String name = "";
            String phone = "";
            Cursor item = resolver.query(data, new String[] { "mimetype", "data1" }, "raw_contact_id=?", new String[] { id }, null);
            assert item != null;
            while (item.moveToNext()) {
                String mimetype = item.getString(item.getColumnIndex("mimetype"));
                String data1 = item.getString(item.getColumnIndex("data1"));
                if ("vnd.android.cursor.item/name".equals(mimetype)) {
                    name = data1;
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    // 有的手机号中间会带有空格
                    phone = data1.replace(" ","");
                }
            }
            ContactInfo info = new ContactInfo(id,name,phone);
            item.close();
            // 添加集合
            list.add(info);
        }
        cursor.close();
        return list;
    }
}

