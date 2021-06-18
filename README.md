# ZLBridge

[![CI Status](https://img.shields.io/travis/范鹏/ZLBridge.svg?style=flat)](https://travis-ci.org/范鹏/ZLBridge)
[![Version](https://img.shields.io/cocoapods/v/ZLBridge.svg?style=flat)](https://cocoapods.org/pods/ZLBridge)
[![License](https://img.shields.io/cocoapods/l/ZLBridge.svg?style=flat)](https://cocoapods.org/pods/ZLBridge)
[![Platform](https://img.shields.io/cocoapods/p/ZLBridge.svg?style=flat)](https://cocoapods.org/pods/ZLBridge)


## Requirements

## 安装
```ruby
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.FPJack:ZLBridge-Android:0.1.8'
}

```
## 原生bridge初始化
```Java
ZLBridge bridge = new ZLBridge(webView);
```

## H5端window.zlbridge初始化
原生通过注入本地js代码初始化
```Java
@Override
public void onPageFinished(WebView view, String url) {
  super.onPageFinished(view, url);
  bridge.injectLocalJS(true);
}
```
或者H5端导入初始化
```JavaScript
 //导入一次后也可以通过全局window.zlbridge拿zlbridge对象
 var zlbridge = require('zlbridge-js')
```

## 原生与JS交互

### JS调用原生test事件

#### 无参数
```JavaScript
window.zlbridge.call('test',(arg) => {

});
```
#### 有参数参数
```JavaScript
window.zlbridge.call('test',{key:"value"},(arg) => {

});
```
#### 原生注册test事件
```Java
bridge.registHandler("test", new ZLBridge.RegisterJSHandlerInterface() {
    @Override
    public void callback(Object body, ZLBridge.JSCallback callBack) {
      ArrayList list = new ArrayList();
      list.add("js调用了原生");
      list.add(body);
      //true：js调用一次test事件只能接受一次原生回调结果，false：可以接受多次回调结果
      callBack.callback(list,true);
   }
});
```


### 原生调用js

#### 原生调用JS的jsMethod事件
```objective-c
ArrayList list = new ArrayList();
list.add("已收到原生调用js传过来的值");
bridge.callHander("jsMethod",list, new ZLBridge.EvaluateJSResultCallback() {
  @Override
  public void onReceiveValue(Object value,String error) {
      Log.d("MainActivity", "value:" + value);
  }
});
```

#### js注册jsMethod事件
```JavaScript
window.zlbridge.register("jsMethod",(arg) => {
     return arg;
});
 ```
 或者
 ```JavaScript
window.zlbridge.registerWithCallback("jsMethod",(arg,callback) => {
  //ture代表原生只能监听一次回调结果，false可以连续监听，默认传为true
  callback(arg,true);
});
  ```

## 通过本地注入JS脚本的，H5可以监听ZLBridge初始化完成
```JavaScript
document.addEventListener('ZLBridgeInitReady', function() {
    consloe.log('ZLBridge初始化完成');
},false);
  ```
## ！！！原生传给JS的值需要支持放入Map里面可以JSONObject的对象，例如boolean,String，int，List，Map,Set等等...

## Author

范鹏, 2551412939@qq.com



## License

ZLBridge is available under the MIT license. See the LICENSE file for more info.
