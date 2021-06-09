(function () {
    if (typeof window.exbridge != "undefined") return;
    var exbridge = {
        //同步调用原生方法
        call: (method,arg) => {
            var args = {};
            args["body"] = arg;
            args["name"] = method;
            args = JSON.stringify(args)
            return JSON.parse(window.prompt("exbridge=sync", args))["result"];
        },
        //异步调用原生方法，支持promise和函数回调
        callAsyn: function(method,arg,func){
            var _callHandlerID = setTimeout(function(){});
            _callHandlerID = "_methodid_" + _callHandlerID;
            var args = {};
            args["jsMethodId"] = _callHandlerID ;
            args["body"] = arg;
            args["name"] = method;
            args = JSON.stringify(args)
            if (typeof func == "function") {
                window.exbridge[_callHandlerID] = func;
                window.prompt("exbridge=async", args);
            }else{
                return new Promise(function(resolver){
                    window.prompt("exbridge=async", args);
                    window.exbridge[_callHandlerID] = resolver;
                });
            }
        },
        //注册原生回调的方法
        register: function(method,func){
            if (typeof func == "function" && typeof method == "string") {
                window.exbridge["_register_" + method] = func;
            }
        },
        //原生异步回调
        _nativeCallback: function(methodid,arg){
            arg = JSON.parse(arg);
            // if (typeof window.exbridge[methodid] == "function") return;
            window.exbridge[methodid](arg["result"]);
            if (arg.end == 1)  delete window.exbridge[methodid]; 
        },
        //原生主动调用
        _nativeCall: function(method,arg) {
           arg = JSON.parse(arg);
           return window.exbridge["_register_" + method](arg["result"]);
        }
    }
    window.exbridge = exbridge;
})();
