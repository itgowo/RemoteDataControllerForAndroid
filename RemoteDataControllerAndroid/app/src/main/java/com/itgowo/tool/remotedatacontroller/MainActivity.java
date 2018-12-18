package com.itgowo.tool.remotedatacontroller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.itgowo.httpserver.HttpRequest;
import com.itgowo.tool.rdc.androidlibrary.DebugDataTool;
import com.itgowo.tool.rdc.androidlibrary.RemoteInfo;
import com.itgowo.tool.rdc.androidlibrary.onLocalServerListener;
import com.itgowo.tool.rdc.androidlibrary.onRemoteServerListener;
import com.itgowo.tool.rdc.androidlibrary.view.SuperDialog;


public class MainActivity extends AppCompatActivity {
    private TextView logTV;
    private Button startRDCBtn;
    private Button startLDCBtn;
    private Button clearLogBtn;
    private Button stopBtn;
    private Button openConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putBoolean("Booblean", true).commit();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putFloat("Float", 1.5f).commit();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putLong("Long", 1232131231).commit();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("String", "tadsfsadfest").commit();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putInt("Int", 1234).commit();
//        DBManager.init(getApplication(), "appinfo.db", null);
//        DBManager.updateCache("first", "yes");
//        DBManager.updateCache("second", "no");
//        DBManager.updateCache("haha", "hehe");
//        for (int i = 0; i < 50; i++) {
//            DBManager.addCache("haha"+i, "hehe");
//        }

    }

    private void initListener() {
        startRDCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRemoteServerPre();
            }
        });
        startLDCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLocal();
            }
        });
        clearLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logTV.setText("");
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugDataTool.shutDown();
            }
        });
        openConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugDataTool.goConfigActivity(MainActivity.this);
            }
        });
    }

    private void initView() {
        clearLogBtn = findViewById(R.id.clear);
        startLDCBtn = findViewById(R.id.startLDC);
        startRDCBtn = findViewById(R.id.startRDC);
        stopBtn = findViewById(R.id.stop);
        logTV = findViewById(R.id.msg);
        openConsole = findViewById(R.id.openConsole);
        logTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        logTV.setMaxEms(1024 * 10);
    }

    private void initLocal() {
        DebugDataTool.initLocalServer(this, 8088, new onLocalServerListener() {

            @Override
            public void onServerStared(final String address, final int port, String proxyAuthenticate) {
//                DebugDataTool.downloadWebData(MainActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, address + ":" + port, Toast.LENGTH_LONG).show();
                    }
                });
                printTv(Color.GREEN, "本地服务启动", "port" + port);
            }

            @Override
            public void onSystemMsg(final String s) {
                printTv(Color.GREEN, "系统信息", s);
            }

            @Override
            public String onObjectToJson(Object object) {
                return JSON.toJSONString(object);
            }

            @Override
            public <T> T onJsonStringToObject(String jsonString, Class<T> tClass) {
                return JSON.parseObject(jsonString, tClass);
            }

            @Override
            public void onGetRequest(String request, final HttpRequest httpRequest) {
                printTv(Color.BLUE, "onGetRequest", httpRequest.toString());
                Log.d("onGetRequest", httpRequest.toString());
            }


            @Override
            public void onResponse(final String response) {
                printTv(Color.BLUE, "onResponse", response);
                Log.d("onResponse", response);
            }

            @Override
            public void onError(final String tip, final Throwable throwable) {
                printTv(Color.RED, "onError", tip + "   " + throwable.getMessage());
                Log.e("DebugDataWebTool", tip + "  " + throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onServerStop() {
                printTv(Color.BLUE, "本地服务关闭", "服务关闭");
            }
        });
    }

    private void initRemoteServerPre() {
        final String clientId = DebugDataTool.getRandomClientId();
        SuperDialog dialog = new SuperDialog(this).setTitle("安全验证码");
        dialog.setInputListener(new SuperDialog.onDialogInputListener() {
            @Override
            public void onInitEditText(EditText inputView) {
                inputView.setText(DebugDataTool.getRandomToken());
                inputView.setMaxEms(8);
                inputView.setMinEms(4);
            }

            @Override
            public void onComplete(int buttonIndex, String text) {
                if (text == null || text.trim().length() < 4) {
                    Toast.makeText(MainActivity.this, "输入验证码过短", Toast.LENGTH_LONG).show();
                } else {
                    initRemote("222222", "222222");
//                    initRemote(clientId, text);
                }
            }
        }).setContent("远程调试功能为危险功能，请输入随机验证码，此验证码为本机本次随机生成，将此验证码提供给技术人员，技术人员即可远程作业(设备ID：" + clientId + ")").show();
    }

    private void initRemote(final String clientId, final String code) {
//        final RemoteInfo remoteInfo1 = new RemoteInfo().setRemoteHost("10.1.100.121").setRemoteServerPort(16671).setClientId(clientId).setToken(code).setLocalServerPort(-1).setContext(this);
        final RemoteInfo remoteInfo1 = new RemoteInfo().setRemoteHost("rdc.itgowo.com").setRemoteServerPort(16671).setClientId(clientId).setToken(code).setLocalServerPort(-1).setContext(this);
        DebugDataTool.initRemoteServer(remoteInfo1, true, new onRemoteServerListener() {
            @Override
            public void onConnectRemoteServer(final RemoteInfo remoteInfo) {
                StringBuilder builder = new StringBuilder(remoteInfo.getRemoteHost() + ":" + remoteInfo.getRemoteServerPort() + "\r\n\r\n");
                builder.append("本机设备ID：" + remoteInfo.getClientId() + "\r\n");
                builder.append("本机设备验证码：" + remoteInfo.getToken() + "\r\n");
                printTv(Color.BLUE, "远程服务器已连接", builder.toString());
            }

            @Override
            public void onReconnectRemoteServer(RemoteInfo remoteInfo) {
                printTv(Color.GREEN, "远程服务器已重连", remoteInfo.getRemoteHost() + ":" + remoteInfo.getRemoteServerPort());
            }

            @Override
            public void onOffline(RemoteInfo remoteInfo) {
                printTv(Color.GREEN, "设备已离线", "正在重连。。。");

            }

            @Override
            public void onAuthRemoteServer(final RemoteInfo remoteInfo) {
                printTv(Color.GREEN, "远程服务器认证通过", remoteInfo.getRemoteHost() + ":" + remoteInfo.getRemoteServerPort());
            }

            @Override
            public void onError(final RemoteInfo remoteInfo, final Throwable throwable) {
                printTv(Color.RED, "服务异常", throwable.getMessage());
            }

            @Override
            public void onRemoteServerStop() {
                printTv(Color.BLUE, "RDC服务停止", "连接中断");
            }


            @Override
            public void onServerStared(String address, int port, String proxyAuthenticate) {
                printTv(Color.GREEN, "本地服务已启动", "");
            }

            @Override
            public void onSystemMsg(final String mS) {
                printTv(Color.GREEN, "onSystemMsg", mS);
            }

            @Override
            public String onObjectToJson(Object mObject) {
                return JSON.toJSONString(mObject);
            }

            @Override
            public <T> T onJsonStringToObject(String mJsonString, Class<T> mClass) {
                return JSON.parseObject(mJsonString, mClass);
            }

            @Override
            public void onGetRequest(String mRequest, final HttpRequest mHttpRequest) {
                printTv(Color.BLUE, "onGetRequest", mHttpRequest.toString());
                Log.d("onGetRequest", mHttpRequest.toString());
            }


            @Override
            public void onResponse(final String mResponse) {
                printTv(Color.BLUE, "onResponse", mResponse);
                Log.d("onResponse", mResponse);
            }

            @Override
            public void onError(final String mTip, final Throwable mThrowable) {
                printTv(Color.RED, "onError", mTip + "   " + mThrowable.getMessage());
                Log.e("DebugDataWebTool", mTip + "  " + mThrowable.getMessage());
                mThrowable.printStackTrace();
            }

            @Override
            public void onServerStop() {
                printTv(Color.BLUE, "本地服务停止", "服务关闭");
            }
        });
    }

    private void printTv(final int color, final String tip, final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SpannableStringBuilder mBuilder = new SpannableStringBuilder(tip + ":" + string + "\r\n\r\n");
                mBuilder.setSpan(new ForegroundColorSpan(color), 0, tip.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                logTV.append(mBuilder);
            }
        });
    }
}
