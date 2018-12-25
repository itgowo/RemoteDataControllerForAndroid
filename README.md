[GitHub](https://github.com/itgowo/RemoteDataControllerForAndroid)

### 一：开发环境
Mac OS 10、Android Studio3.2.1
### 二：介绍
1. 基于互联网技术的效率工具。
2. 提高开发与测试人员效率的辅助工具。
3. 新人培训快速熟悉项目。
4. 线上数据问题紧急修复工具。

##### 提醒，库里不集成Json框架，利用反射查找项目内Json框架，优先使用FastJson其次Gson，其他暂不支持。如果写demo，请先引入任何一个支持的Json框架。
### 二：介绍
1. 作为项目的核心项目，除了提供数据库管理、共享参数管理和文件管理外，还集成新版内置Http解析框架，增加远程服务框架，实现TCP长连接方案，解决粘包分包问题。三重认证连接保证授权安全，关闭本地调试开关状态下启动云控功能，本地服务虽然会被启动，但是只接受远程控制服务的命令，来自局域网的请求会被拒绝，安全保证+1。

2. Android端项目云端版可以独立运行，[第一版](https://github.com/hnsugar/android-debugdata-webtool)只支持局域网功能。作为独立使用库，依然保持局域网访问功能，方便开发阶段调试。如果是上线应用，需要将本地功能关闭，需要使用时再打开本地服务，在云服务中，手机远程服务被启动后会启动本地服务，默认只有远程服务可以访问本地服务，保证接口不被未授权使用。

3. 内置[MiniHttpServer](https://www.jianshu.com/p/de98fa07140d)服务是自开发微型服务框架，支持解析Http报文信息，常见的如普通接口请求或者文件上传下载等都能正常识别，具体可看[MiniHttpServer](https://www.jianshu.com/p/de98fa07140d)。

4. 内置[MiniHttpClient](https://www.jianshu.com/p/41b0917271d3)作为网络请求框架，基于HttpURLConnection实现异步同步请求，也支持文件上传下载和扩展。

5. POST请求接口逻辑定义为Action框架方式，即用Action作为Key，处理器对象作为Value放到map中，当收到接口请求时，从map中获取对应处理器，利用hash算法提高接口辨别。

6. 在第二版中重点解决了第一版（[2017年项目](https://github.com/hnsugar/android-debugdata-webtool)）中出现的各种问题，并添加了很多实用功能。DataTables等也使用最新版本，也遇到了问题[dataTables.altEditor.free](https://www.jianshu.com/p/a28d5a4c333b)兼容性问题，经过修改源码已支持此项目。

7. 所有管理页面表格都增加了自定义显示字段功能，同时提供数据导出到CSV和打印功能，方便用Excel等工具管理。

8. 在远程模式下，token只在客户端存在，认证是服务端向客户端确认是否成功，客户端的远程服务框架会向本地服务发送时携带本地服务的认证信息，每次启动生成一次，增强安全性。

### 三：引入依赖

```
    implementation 'com.itgowo:RemoteDataControllerForAndroid:1.0.1'
```

### 四：特点
    
* 纯Java API实现，性能好。
* 体积小，依赖包不到400kb。
* 统一接口。
* 兼容性好。
* 使用简单，无侵入性。
### 五：说明
分为数据库管理、共享参数管理和文件管理三个模块， 依赖库提供接口服务，通过访问服务端口，可以静态获取资源文件；支持跨域请求；采用web页面控制台控制，页面操作请看[RemoteDataControllerForWeb](https://www.jianshu.com/p/75747ff4667f)。服务启动后会检查本地服务文件，默认服务器目录为***app_MiniHttpServer***目录，通过遍历zip包内文件信息，及zip内文件最后更新时间大于***app_MiniHttpServer***目录内相对文件的更新日期，会替换成新文件，从而达到依赖库升级后web静态文件升级。

### 六：接入本地服务
1. 最简单初始化本地服务，傻瓜式无代码方式我弃用了，自动启动太无耻了🤔。
2. 最简单的初始化本地服务
```
DebugDataTool.initLocalServer(application, 8089, null);
```
3. 添加服务回调方式
##### 回调如果不为null，那么必须实现这两个方法：
```
  public String onObjectToJson(Object o);
  public <T> T onJsonStringToObject(String s, Class<T> aClass);
```
##### 如果为null，会自动用反射查找当前项目的Json工具，优先用FastJson，其次是Gson。
```
 DebugDataTool.initLocalServer(application, 8089, new onLocalServerListener() {
             @Override
            public void onServerStared(String address, int port, String proxyAuthenticate) {
                System.out.println("XSLApplicationLike.onServerStared："+port);
            }

            @Override
            public void onSystemMsg(String s) {
                System.out.println(s);
            }

            @Override
            public String onObjectToJson(Object o) {
                return JSON.toJSONString(o);
            }

            @Override
            public <T> T onJsonStringToObject(String s, Class<T> aClass) {
                return JSON.parseObject(s, aClass);
            }

            @Override
            public void onGetRequest(String s, HttpRequest httpRequest) {
                //收到的请求，文件请求只显示部分信息，不会打印整个文件。
                System.out.println("s = [" + s + "], httpRequest = [" + httpRequest + "]");
            }

            @Override
            public void onResponse(String s) {
                //返回的结果，文件只显示部分标识信息。
                System.out.println("s = [" + s + "]");
            }

            @Override
            public void onError(String s, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
```
### 六：接入云控服务
可以看库中[RDCConfigActivity](https://github.com/itgowo/RemoteDataControllerForAndroid/blob/master/RemoteDataControllerAndroid/RemoteDataControllerForAndroidLibrary/src/main/java/com/itgowo/tool/rdc/androidlibrary/RDCConfigActivity.java)演示，RDCConfigActivity.go(Context)可以直接跳转。
界面上的TitleBar可以通过配置下面资源改变主题：
 
```
    <color name="remotetool_titlebar_backgroundcolor">#443333</color>
    <color name="remotetool_titlebar_textcolor">#FFFFFF</color>
    <dimen name="remotetool_titlebar_textsize">30sp</dimen>
```
##### 1.RemoteInfo类为配置信息类如下：
```
RemoteInfo remoteInfo1 = new RemoteInfo().setRemoteHost("rdc.itgowo.com").setRemoteServerPort(16671).setClientId(clientId).setToken(code).setLocalServerPort(-1).setContext(this);
```
|变量|类型|举例|说明|
|---|---|---|---|
|clientId|String|10086|设备id，APP唯一标识，一般用用户id或者其他简单文本，不宜复杂|
|context|Context|application|上下文对象|
|remoteHost|String|rdc.itgowo.com|云服务器地址|
|remoteServerPort|Integer|16671|远程服务端口|
|localServerPort|Integer|-1|本地服务端口，为了安全请用-1，使用随机端口方式，端口号<1024则自动随机|

##### 2.初始化服务

|参数|类型|举例|说明|
|---|---|---|---|
|remoteInfo1|RemoteInfo||远程服务配置信息|
|enableLANAccess|Boolean|false|启动远程服务后是否允许本地网络访问，如果是线上环境一定设为false，安全考虑，即使知道本地服务端口也会提示403权限拒绝|
|onRemoteServerListener|onRemoteServerListener||服务信息回调，继承自onLocalServerListener|

建议clientId用userid，如果没有，token和ClientID可以用以下方法生成：
```
String clientId = DebugDataTool.getRandomClientId();
String token = DebugDataTool.getRandomToken();
```
##### 3.快速使用

```
DebugDataTool.initRemoteServer(remoteInfo1,true, null);
```
##### 4.添加回调，回调如果不为null，那么必须实现这两个方法：
```
  public String onObjectToJson(Object o);
  public <T> T onJsonStringToObject(String s, Class<T> aClass);
```
##### 注意事项与本地服务一致
```
        DebugDataTool.initRemoteServer(remoteInfo1,true, new onRemoteServerListener() {
            @Override
            public void onConnectRemoteServer(final RemoteInfo remoteInfo) {
                StringBuilder builder = new StringBuilder(remoteInfo.getRemoteHost() + ":" + remoteInfo.getRemoteServerPort() + "\r\n\r\n");
                builder.append("本机设备ID：" + remoteInfo.getClientId() + "\r\n");
                builder.append("本机设备验证码：" + remoteInfo.getToken() + "\r\n");
                printTv(Color.BLUE, "远程服务器已连接", builder.toString());
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
            public void onStop() {
                printTv(Color.BLUE, "RDC服务停止", "连接中断");
            }

            @Override
            public void onServerStared(String address, int port,String proxyAuthenticate) {
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
        });
    }
```
### 七：数据库管理

1. 作为最初提供的功能，数据库管理一直是重点。界面采取分区设计，最左测试数据库列表，点击可以切换数据库文件；其次是当前打开数据库的表列表，点击表可以显示数据库数据，并且支持分页查询和显示数量调整。

2. 目前通过Web页面已经实现数据的增删改查基本功能，同时扩展了SQL执行功能，ALTER 功能可以通过SQL执行框输入，几乎支持所有没有直接提供的SQL功能。

3. 新升级的Web组件DataTables增加了扩展，支持数据直接导出。例如：打开一个表，数据显示100项，选择导出CSV，这时浏览器会下载一个CSV表格文件，表格数据就是当前显示数据。如果想导出特别逻辑的表格，可以通过SQL语句Select出某些条件的数据，此时数据表内数据与直接点开表不同，此时不是分页数据，及所有数据都是一次性加载的，选择显示所有数据再导出就是SQL查出的所有数据了。

4. 新添加的功能还包括数据列过多自动隐藏功能，左侧会有一个+号，点击+号会展开未显示数据列；通过选择显示列也可控制显示哪些列，只保留感兴趣的可以提高阅读查找速度。

5. 删除表可以通过SQL执行ALTER语句，那删除数据库可以通过文件管理，找到内部目录的database目录，里面就有app的所有数据库文件，点击删除即可。点击数据库文件下载亦可。

6. 每次选择数据库时上方会有提示，提示下载的文案会变化，点击此文案也可下载当前数据库。

 ### 八：共享参数管理

1. 使用方式基本与数据库管理相同，只要知道共享参数是XML文件即可，也有数据编辑导出等功能。

### 九：文件管理

1. 默认显示app内部存储目录，可以执行文件的上传与下载，也可以删除和重命名。

2. 文件夹会显示文件夹大小和文件数量，方便我们查看。

3. 快速过滤可以帮助我们快速找到想要的文件或目录。

4. 文件管理数据不支持分页，所以打开外部存储时第一次会很慢，以后会缓存较大目录的信息，从而提高响应速度。

### 十：简单图片例子，具体使用请看主项目
![web-17.jpg](https://upload-images.jianshu.io/upload_images/3213604-4ebbaf4ae86e8c5f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-12.jpg](https://upload-images.jianshu.io/upload_images/3213604-4e0e67c769b68ebe.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-13.jpg](https://upload-images.jianshu.io/upload_images/3213604-68bb15bfd45dc875.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-14.jpg](https://upload-images.jianshu.io/upload_images/3213604-d053818f207f7ee3.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-15.jpg](https://upload-images.jianshu.io/upload_images/3213604-c5c2e7c6692a6779.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-16.jpg](https://upload-images.jianshu.io/upload_images/3213604-a57dc9cb7f6239c1.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![app-1.png](https://upload-images.jianshu.io/upload_images/3213604-b6fe0507f155e3a3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![app-2.png](https://upload-images.jianshu.io/upload_images/3213604-f09e2595f80c0069.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![app-3.png](https://upload-images.jianshu.io/upload_images/3213604-feb546a294ae50ca.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![app-4.png](https://upload-images.jianshu.io/upload_images/3213604-7b39b066b45d106b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![app-5.png](https://upload-images.jianshu.io/upload_images/3213604-a1b948b955025e5e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![app-6.png](https://upload-images.jianshu.io/upload_images/3213604-3dadfcd57764ca3a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![app-7.png](https://upload-images.jianshu.io/upload_images/3213604-07a3ac96b7d583f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-1.jpg](https://upload-images.jianshu.io/upload_images/3213604-dd192f6782b77ee4.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-2.jpg](https://upload-images.jianshu.io/upload_images/3213604-11c7f4ece5321749.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-3.jpg](https://upload-images.jianshu.io/upload_images/3213604-1cf8a6b521d993be.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-4.jpg](https://upload-images.jianshu.io/upload_images/3213604-5db5abbdbab85b43.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-5.jpg](https://upload-images.jianshu.io/upload_images/3213604-14ca87699e2b2370.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-6.jpg](https://upload-images.jianshu.io/upload_images/3213604-b7f960bc35591d9f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-7.jpg](https://upload-images.jianshu.io/upload_images/3213604-d035732515258324.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-8.jpg](https://upload-images.jianshu.io/upload_images/3213604-2641fb229372191f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-9.jpg](https://upload-images.jianshu.io/upload_images/3213604-29cca589a435d1bb.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-10.jpg](https://upload-images.jianshu.io/upload_images/3213604-0c68b32f74436a0b.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![web-11.jpg](https://upload-images.jianshu.io/upload_images/3213604-5abadee852e46b6c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)




### 十一：小期待

以下项目都是我围绕远程控制写的项目和子项目。都给star一遍吧。😍

|项目(Github)|语言|其他地址|运行环境|项目说明|
|---|---|---|---|---|
|[RemoteDataControllerForWeb](https://github.com/itgowo/RemoteDataControllerForWeb)|JavaScript|[简书](https://www.jianshu.com/p/75747ff4667f)|浏览器|远程数据调试控制台Web端|
|[RemoteDataControllerForAndroid](https://github.com/itgowo/RemoteDataControllerForAndroid)|Java|[简书](https://www.jianshu.com/p/eb692f5709e3)|Android设备|远程数据调试Android端|
|[RemoteDataControllerForServer](https://github.com/itgowo/RemoteDataControllerForServer)|Java|[简书](https://www.jianshu.com/p/3858c7e26a98)|运行Java的设备|远程数据调试Server端|
|[MiniHttpClient](https://github.com/itgowo/MiniHttpClient)|Java|[简书](https://www.jianshu.com/p/41b0917271d3)|运行Java的设备|精简的HttpClient|
|[MiniHttpServer](https://github.com/itgowo/MiniHttpServer)|Java|[简书](https://www.jianshu.com/p/de98fa07140d)|运行Java的设备|支持部分Http协议的Server|
|[MiniTCPClient](https://github.com/itgowo/MiniTCPClient)|Java|[简书](https://www.jianshu.com/p/4b993100eae5)|运行Java的设备|TCP长连接库，支持粘包拆包处理|
|[PackageMessage](https://github.com/itgowo/PackageMessage)|Java|[简书](https://www.jianshu.com/p/8a4a0ba2f54a)|运行Java的设备|TCP粘包与半包解决方案|
|[ByteBuffer](https://github.com/itgowo/ByteBuffer)|Java|[简书](https://www.jianshu.com/p/ba68224f30e4)|运行Java的设备|二进制处理工具类|
|[DataTables.AltEditor](https://github.com/itgowo/DataTables.AltEditor)|JavaScript|[简书](https://www.jianshu.com/p/a28d5a4c333b)|浏览器|Web端表格编辑组件|

[我的小站：IT狗窝](http://itgowo.com)
技术联系QQ:1264957104