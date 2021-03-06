package com.example.bridge;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import com.example.zlbridge.ZLBridge;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    String [] abc;
    Handler handler=new Handler(Looper.getMainLooper());
    ZLBridge.JSCallback jsCallback;
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
        final ZLBridge bridge = new ZLBridge(webView);
       

        
        bridge.registHandler("test", new ZLBridge.RegisterJSHandlerInterface() {
            @Override
            public void callback(Object body, ZLBridge.JSCallback callBack) {
                ArrayList list = new ArrayList();
                list.add("js调用了原生");
                list.add(body);
                callBack.callback(list,true);
            }
        });
        bridge.registHandler("upload", new ZLBridge.RegisterJSHandlerInterface() {
            @Override
            public void callback(Object body, ZLBridge.JSCallback completion) {
                jsCallback = completion;
                handler.postDelayed(runnable, 1000);
            }
        });

        final TextView textView = findViewById(R.id.text);
        bridge.registUndefinedHandler(new ZLBridge.RegisterJSUndefinedHandlerInterface() {
            @Override
            public void callback(String name, Object body, ZLBridge.JSCallback callBack) {
                textView.setText("收到原生未监听的js调用事件" +name );
                Log.d("MainActivity", name);
            }
        });
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList list = new ArrayList();
                list.add("已收到原生调用js事件1传过来的值");
                bridge.callHander("jsMethod",list, new ZLBridge.EvaluateJSResultCallback() {
                    @Override
                    public void onReceiveValue(Object value,String error) {
                         String text = error != null ? error : "点击原生调用js事件1";
                        button.setText(text);
                        Log.d("MainActivity", "value:" + value);
                    }
                });
            }

        });
       final Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList list = new ArrayList();
                list.add("已收到原生调用js事件2传过来的值");
                bridge.callHander("jsMethodWithCallback",list, new ZLBridge.EvaluateJSResultCallback() {
                    @Override
                    public void onReceiveValue(Object value,String error) {
                        String text = error != null ? error : "点击原生调用js事件2";
                        button1.setText(text);
                    }
                });
            }

        });
        webView.loadUrl("file:///android_asset/web/index.html");
//        webView.loadUrl("http://localhost:3000");
        webView.setWebViewClient(new Client(bridge));

    }
    class Client extends WebViewClient {
        ZLBridge bridge;
        Client(ZLBridge bridge) {
            this.bridge = bridge;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            bridge.injectLocalJS();
        }
    }
    /**
     *
     */
    @Override
    public void finalize(){

    }

}