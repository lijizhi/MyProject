package com.ljz.myproject.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.ljz.myproject.demo.bluetooth.BluetoothActivity;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Welive on 2018/11/27.
 * 开启线程服务监听连接状态
 */

public class BluetoothUtil {

    private static final String NAME_SECURE = "BluetoothChatSecure";

    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private int mState;

    // 显示当前连接状态
    public static final int STATE_NONE = 0;       // 什么都不做
    public static final int STATE_LISTEN = 1;     // 监听连接
    public static final int STATE_CONNECTING = 2; // 正在建立连接
    public static final int STATE_TRANSFER = 3;  // 现在连接到一个远程的设备，可以进行传输

    //用来向主线程发送消息
    private static Handler uiHandler;
    private BluetoothAdapter bluetoothAdapter;

    //用来连接端口的线程
    private AcceptThread mAcceptThread;
    private TransferThread mTransferThread;
    private ConnectThread mConnectThread;
    private boolean isTransferError = false;

    //获取单例
    public static volatile BluetoothUtil instance = null;

    public static BluetoothUtil getInstance(Handler handler) {
        uiHandler = handler;
        if (instance == null) {
            synchronized (BluetoothUtil.class) {
                if (instance == null) {
                    instance = new BluetoothUtil();
                }
            }
        }
        return instance;
    }

    public BluetoothUtil() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    /**
     * start service listen
     */
    public synchronized void start() {
        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }

        setState(STATE_LISTEN);

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Logger.d("stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }

        setState(STATE_NONE);
    }

    public void setState(int state) {
        this.mState = state;
    }

    /**
     * 连接访问
     * @param device 设备
     */
    public synchronized void connectDevice(BluetoothDevice device) {
        Logger.d("connectDevice");

        // 如果有正在传输的则先关闭
        if (mState == STATE_CONNECTING) {
            if (mTransferThread != null) {
                mTransferThread.cancel();
                mTransferThread = null;
            }
        }

        //如果有正在连接的则先关闭
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        sendMessageToUi(BluetoothActivity.BLUE_TOOTH_DIALOG, "正在与" + device.getName() + "连接");
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();

        //标志为正在连接
        setState(STATE_CONNECTING);
    }

    private void sendMessageToUi(int what, String s) {
        Message message = uiHandler.obtainMessage();
        message.what = what;
        message.obj = s;
        uiHandler.sendMessage(message);
    }

    /**
     * 开始连接通讯
     * @param socket
     * @param remoteDevice 远程设备
     */
    private void dataTransfer(BluetoothSocket socket, final BluetoothDevice remoteDevice) {

        //关闭连接线程，这里只能连接一个远程设备
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // 启动管理连接线程和开启传输
        mTransferThread = new TransferThread(socket);
        mTransferThread.start();

        //标志状态为连接
        setState(STATE_TRANSFER);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isTransferError) {
                    sendMessageToUi(BluetoothActivity.BLUE_TOOTH_SUCCESS, remoteDevice.getName());
                }
            }
        }, 300);
    }

    /**
     * 传输数据
     * @param out 字节数组
     */
    public void sendData(byte[] out) {
        TransferThread r;
        synchronized (this) {
            if (mState != STATE_TRANSFER)
                return;
            r = mTransferThread;
        }
        r.write(out);
    }

    //连接等待线程
    class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            //获取服务器监听端口
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            super.run();
            //监听端口
            BluetoothSocket socket;
            while (mState != STATE_TRANSFER) {
                try {
                    Logger.d("run: AcceptThread 阻塞调用，等待连接");
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.d("run: ActivityThread fail");
                    break;
                }

                //获取到连接Socket后则开始通信
                if (socket != null) {
                    synchronized (BluetoothUtil.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                //传输数据，服务器端调用
                                Logger.d("run: 服务器AcceptThread传输");
                                sendMessageToUi(BluetoothActivity.BLUE_TOOTH_DIALOG, "正在与" + socket.getRemoteDevice().getName() + "连接");
                                dataTransfer(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_TRANSFER:
                                // 没有准备好或者终止连接
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Logger.d("Could not close unwanted socket" + e);
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            Logger.d( "close: activity Thread");
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.d( "close: activity Thread fail");
            }
        }
    }

   //用来传输数据的线程
    class TransferThread extends Thread {
        private final BluetoothSocket socket;
        private final OutputStream out;
        private final InputStream in;

        public TransferThread(BluetoothSocket mBluetoothSocket) {
            socket = mBluetoothSocket;
            OutputStream mOutputStream = null;
            InputStream mInputStream = null;
            try {
                if (socket != null) {
                    //获取连接的输入输出流
                    mOutputStream = socket.getOutputStream();
                    mInputStream = socket.getInputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = mOutputStream;
            in = mInputStream;
            isTransferError = false;
        }

        @Override
        public void run() {
            super.run();
            //读取数据
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = in.read(buffer);
                    //TODO 分发到主线程显示
                    uiHandler.obtainMessage(BluetoothActivity.BLUE_TOOTH_READ, bytes, -1, buffer).sendToTarget();
                    Logger.d("run: read " + new String(buffer, 0, bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.d("run: Transform error" + e.toString());
                    BluetoothUtil.this.start();
                    //TODO 连接丢失显示并重新开始连接
                    sendMessageToUi(BluetoothActivity.BLUE_TOOTH_TOAST, "设备连接失败/传输关闭");
                    isTransferError = true;
                    break;
                }
            }
        }

        /**
         * 写入数据传输
         *
         * @param buffer
         */
        public void write(byte[] buffer) {
            try {
                out.write(buffer);
                //TODO 到到UI显示
                uiHandler.obtainMessage(BluetoothActivity.BLUE_TOOTH_WRAITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Logger.d("Exception during write " + e);
            }
        }

        public void cancel() {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                Logger.d("close() of connect socket failed" + e);
            }
        }
    }

    class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket mSocket = null;
            try {
                //建立通道
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
                e.printStackTrace();
                Logger.d("ConnectThread: fail");
                sendMessageToUi(BluetoothActivity.BLUE_TOOTH_TOAST, "连接失败，请重新连接");
            }
            socket = mSocket;
        }

        @Override
        public void run() {
            super.run();
            //建立后取消扫描
            bluetoothAdapter.cancelDiscovery();

            try {
                Logger.d("run: connectThread 等待");
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Logger.d("run: unable to close");
                }
                //TODO 连接失败显示
                sendMessageToUi(BluetoothActivity.BLUE_TOOTH_TOAST, "连接失败，请重新连接");
                BluetoothUtil.this.start();
            }


            // 重置
            synchronized (BluetoothUtil.this) {
                mConnectThread = null;
            }

            //Socket已经连接上了，默认安全,客户端才会调用
            Logger.d("run: connectThread 连接上了,准备传输");
            dataTransfer(socket, device);
        }

        public void cancel() {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

