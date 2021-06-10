 (function () {
     if (window.ZLBridge) return;
     var ZLBridge = {
         call: function(method,arg,func){
             if (typeof method != 'string') return;
             if (typeof arg == 'function') {
                 func = arg;
                 arg = null;
             }
             var args = {};
             args['body'] = arg;
             args['name'] = method;
             args['end'] = true;
             args['callID'] = '';
             if (typeof func == 'function') {
                 var _callHandlerID = setTimeout(function(){});
                 _callHandlerID = '_methodid_' + _callHandlerID;
                 args['jsMethodId'] = _callHandlerID ;
                 window.ZLBridge[_callHandlerID] = func;
             }
             window.ZLBridge._callNative(args);
         },
          _callNative: function(arg) {
                window.androidBridge.messageHandlers(JSON.stringify(arg));
           },
         register: function(method,func){
             if (typeof func == 'function' && typeof method == 'string') {
                 window.ZLBridge['_register_' + method] = func;
             }
         },
         registerWithCallback: function(method,func){
             if (typeof func == 'function' && typeof method == 'string') {
                 window.ZLBridge['_register_callback' + method] = func;
             }
         },
         _nativeCall: function(method,arg) {
             setTimeout(() => {
                   try {
                        var obj = JSON.parse(arg);
                        var result = obj['result'];
                        var func = window.ZLBridge['_register_' + method];
                        if (typeof func == 'function') {
                            var funcResult = func(result);
                            return {sync:true,result:funcResult};
                        }
                        func = window.ZLBridge['_register_callback' + method];
                        var callID = obj['callID'];
                        var callback = function (params,end) {
                            var args = {};
                            if (callID) args['callID'] = callID;
                            args['body'] = params;
                            args['end'] = (typeof end == 'boolean')?end:true;
                            window.ZLBridge._callNative(args);
                        };
                        func(result,callback);
                      } catch (error) {
                        console.log(error);
                        return {sync:true,error:error.message};
                   }
            });
         },
         _nativeCallback: function(methodid,arg){
             var func = window.ZLBridge[methodid];
             if (typeof func != 'function') return;
             arg = JSON.parse(arg);
             func(arg['result']);
             if (arg.end==1) delete window.ZLBridge[methodid];
         },
         _hasNativeMethod: function(method) {
             var func = window.ZLBridge['_register_' + method];
             return (func!=null||func!=undefined);
         }
     };
     window.ZLBridge = ZLBridge;
     var doc = document;
     var event = doc.createEvent('Events');
     event.initEvent('ZLBridgeInitReady');
     doc.dispatchEvent(event);
 })();