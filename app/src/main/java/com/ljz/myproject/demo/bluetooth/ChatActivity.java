package com.ljz.myproject.demo.bluetooth;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.ljz.myproject.R;
import com.ljz.myproject.adapter.ChatAdapter;
import com.ljz.myproject.demo.database.SQLHelper;
import com.ljz.myproject.entity.ChatInfo;
import com.ljz.myproject.utils.BluetoothUtil;
import com.ljz.myproject.utils.Md5Util;
import com.ljz.myproject.utils.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import static com.ljz.myproject.demo.database.SQLHelper.COLUMN_CONTENT;
import static com.ljz.myproject.demo.database.SQLHelper.COLUMN_ID;
import static com.ljz.myproject.demo.database.SQLHelper.COLUMN_NAME;
import static com.ljz.myproject.demo.database.SQLHelper.COLUMN_TAG;
import static com.ljz.myproject.demo.database.SQLHelper.DB_NAME;
import static com.ljz.myproject.demo.database.SQLHelper.TABLE_NAME;
import static com.ljz.myproject.demo.bluetooth.BluetoothActivity.BLUE_TOOTH_READ;
import static com.ljz.myproject.demo.bluetooth.BluetoothActivity.BLUE_TOOTH_TOAST;
import static com.ljz.myproject.demo.bluetooth.BluetoothActivity.BLUE_TOOTH_WRAITE;

/**
 * Created by Welive on 2018/11/27.
 * 聊天页
 */

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    public static final String DEVICE_NAME_INTENT = "device_name";
    private static final int UPDATE_DATA = 0x666;
    private static final int READ_DATA = 0;
    private static final int WRITE_DATA = 1;
    private String deviceName; //名字
    private BluetoothUtil bluetoothUtil;
    private RecyclerView recyclerView;
    private ChatAdapter recyclerChatAdapter;
    private EditText etWrite;
    private SQLHelper sqlHelper;
    private List<ChatInfo> list = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BLUE_TOOTH_TOAST:{
                    ToastUtil.showText(ChatActivity.this, (String) msg.obj);
                    finish();
                }break;
                case BLUE_TOOTH_READ:{
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    list.add(new ChatInfo(ChatInfo.TAG_LEFT , deviceName, readMessage));
                    recyclerChatAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(list.size());
                }break;
                case BLUE_TOOTH_WRAITE:{
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    list.add(new ChatInfo(ChatInfo.TAG_RIGHT , "我" , writeMessage));
                    recyclerChatAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(list.size());
                }break;
                case UPDATE_DATA:{
                    recyclerChatAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(list.size());
                }break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if(null!=getSupportActionBar())
        //设置标题栏
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        deviceName =  intent.getStringExtra(DEVICE_NAME_INTENT);
        setTitle(deviceName);

        sqlHelper = new SQLHelper(ChatActivity.this ,  DB_NAME ,null , 1);

        //数据库读取数据
        new ModelThread(READ_DATA).start();

        recyclerView =  findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        etWrite =  findViewById(R.id.et_write);

        recyclerView.setFocusable(true);
        recyclerView.setFocusableInTouchMode(true);
        recyclerView.requestFocus();

        recyclerChatAdapter = new ChatAdapter(this);
        recyclerChatAdapter.setList(list);
        recyclerView.setAdapter(recyclerChatAdapter);
        Log.e(TAG, "onCreate: mac " + deviceName);
    }

    class ModelThread extends Thread{
        private int tag;
        public ModelThread(int tag){
            this.tag = tag;
        }
        @Override
        public void run() {
            super.run();
            if(tag == READ_DATA){
                readData();
            }else saveData();
        }

    }

    private void saveData() {
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        //清除之前数据
        db.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID  + " = ?" , new String[]{Md5Util.stringToMD5(deviceName + "我")});

        for(ChatInfo chatInfo : list){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID , Md5Util.stringToMD5(deviceName + "我"));
            contentValues.put(COLUMN_TAG , chatInfo.getTag());
            contentValues.put(COLUMN_NAME , chatInfo.getName());
            contentValues.put(COLUMN_CONTENT , chatInfo.getContent());
            db.insert(TABLE_NAME , null , contentValues);
        }
    }

    private void readData() {
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_ID + " = ?"  , new String[]{Md5Util.stringToMD5(deviceName + "我")});
        if(cursor.moveToFirst()){
            do{
                ChatInfo chatInfo = new ChatInfo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_TAG))) ,
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) ,
                        cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                list.add(chatInfo);
            }while (cursor.moveToNext());
        }
        handler.sendEmptyMessage(UPDATE_DATA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
        //获取到连接服务进行读写
        bluetoothUtil = BluetoothUtil.getInstance(handler);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //返回事件监听
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 发送监听
     * @param view
     */
    public void send(View view){
        if(!etWrite.getText().toString().equals("")) {
            bluetoothUtil.sendData(etWrite.getText().toString().getBytes());
            etWrite.setText("");
        } else {
            ToastUtil.showText(ChatActivity.this, "发送的消息不能为空");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
        bluetoothUtil.stop();
        //数据保存
        new ModelThread(WRITE_DATA).start();
    }
}

