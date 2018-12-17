package com.ljz.myproject.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ljz.myproject.R;
import com.ljz.myproject.entity.ContactInfo;

import java.util.List;

/**
 * Created by Welive on 2018/12/5.
 * 联系人的适配器
 */

public class ContactAdapter extends BaseQuickAdapter<ContactInfo,BaseViewHolder> {

    public ContactAdapter(int layoutResId, @Nullable List<ContactInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ContactInfo item) {
        helper.setText(R.id.tv_name,item.name).setText(R.id.tv_phone,item.phone);
    }
}
