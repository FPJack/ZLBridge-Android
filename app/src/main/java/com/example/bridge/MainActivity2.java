package com.example.bridge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.example.zlbridge.WebViewJavascriptBridge;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    WebView webView;
    String [] abc;
    WebViewJavascriptBridge bridge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        webView = findViewById(R.id.webview);

        final WebViewJavascriptBridge bridge = new WebViewJavascriptBridge(webView);
        bridge.registHandler("test", new WebViewJavascriptBridge.RegisterJSHandlerInterface() {
            @Override
            public void callback(Object body, WebViewJavascriptBridge.JSCallback callBack) {
                ArrayList list = new ArrayList();
                list.add(3);
                list.add("kdkdkd");
                callBack.callback(list,true);
            }
        });
        bridge.registHandler("upload", new WebViewJavascriptBridge.RegisterJSHandlerInterface() {
            @Override
            public void callback(Object body, WebViewJavascriptBridge.JSCallback callBack) {
                callBack.callback(body,false);
            }
        });
        bridge.registUndefinedHandler(new WebViewJavascriptBridge.RegisterJSUndefinedHandlerInterface() {
            @Override
            public void callback(String name, Object body, WebViewJavascriptBridge.JSCallback callBack) {
                Log.d("MainActivity", name);
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity2.this,MainActivity2.class);
                MainActivity2.this.startActivity(intent);

//                ArrayList list = new ArrayList();
//                list.add("kdkdkdkdkd");
//                bridge.callHander("jsMethod", new WebViewJavascriptBridge.EvaluateJSResultCallback() {
//                    @Override
//                    public void onReceiveValue(Object value) {
//                        Log.d("MainActivity", "value:" + value);
//                    }
//                });
            }

        });
        webView.loadUrl("file:///android_asset/web/index.html");
        this.bridge = bridge;
    }

    /**
     *
     */
    @Override
    public void finalize(){
        Log.d("MainActivity2", "销毁了");
    }
}