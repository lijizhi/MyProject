package com.ljz.myproject.demo.contentProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.ljz.myproject.R;
import com.ljz.myproject.adapter.ContactAdapter;
import com.ljz.myproject.common.BaseActivity;
import com.ljz.myproject.entity.ContactInfo;
import com.ljz.myproject.utils.ContactUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Welive on 2018/12/5.
 * 联系人列表
 */

public class ContactActivity extends BaseActivity {

    @BindView(R.id.recycle_contact)
    RecyclerView recycleContact;

    private List<ContactInfo> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showContacts();
        initRecyclerView();
    }

    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 0);
        } else {
          list=ContactUtil.getAllContacts(this);
        }
    }

    private void initRecyclerView() {
        recycleContact.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ContactAdapter contactAdapter = new ContactAdapter(R.layout.recycler_item_contact, list);
        recycleContact.setAdapter(contactAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this, "您拒绝权限，联系人将无法显示", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
