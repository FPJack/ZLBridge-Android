package com.example.bridge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.example.zlbridge.WebViewJavascriptBridge;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {
    WebView webView;
    String [] abc;
    Handler handler=new Handler(Looper.getMainLooper());
    WebViewJavascriptBridge.JSCallback jsCallback;
    int time = 0;
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            time += 1;
            if (time < 10){
                jsCallback.callback((time * 10) + "%",false);
                handler.postDelayed(runnable, 1000);
            }else {
                jsCallback.callback("100%",true);
                handler.removeCallbacks(runnable);
                time = 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);

        final WebViewJavascriptBridge bridge = new WebViewJavascriptBridge(webView,true);
        bridge.registHandler("test", new WebViewJavascriptBridge.RegisterJSHandlerInterface() {
            @Override
            public void callback(Object body, WebViewJavascriptBridge.JSCallback callBack) {
                ArrayList list = new ArrayList();
                list.add("js调用了原生");
                list.add(body);
                callBack.callback(list,true);
            }
        });
        bridge.registHandler("upload", new WebViewJavascriptBridge.RegisterJSHandlerInterface() {
            @Override
            public void callback(Object body, WebViewJavascriptBridge.JSCallback completion) {
                jsCallback = completion;
                handler.postDelayed(runnable, 1000);
            }
        });

        final TextView textView = findViewById(R.id.text);
        bridge.registUndefinedHandler(new WebViewJavascriptBridge.RegisterJSUndefinedHandlerInterface() {
            @Override
            public void callback(String name, Object body, WebViewJavascriptBridge.JSCallback callBack) {

                textView.setText("收到原生未监听的js调用事件" +name );
                Log.d("MainActivity", name);
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList list = new ArrayList();
                list.add("已收到原生调用js传过来的值");
                bridge.callHander("jsMethod",list, new WebViewJavascriptBridge.EvaluateJSResultCallback() {
                    @Override
                    public void onReceiveValue(Object value) {
                        Log.d("MainActivity", "value:" + value);
                    }
                });
            }

        });
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList list = new ArrayList();
                list.add("已收到原生调用js传过来的值");
                bridge.callHander("jsMethodWithCallback",list, new WebViewJavascriptBridge.EvaluateJSResultCallback() {
                    @Override
                    public void onReceiveValue(Object value) {
                        Log.d("MainActivity", "value:" + value);
                    }
                });
            }

        });
        webView.loadUrl("file:///android_asset/web/index.html");

    }

    /**
     *
     */
    @Override
    public void finalize(){

    }

}