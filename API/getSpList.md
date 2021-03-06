**简要描述：**

- 获取共享参数列表

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
	{
		action : getSpList
	}

 ```

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作|


 **返回示例**

 ```
 {
     code : 200,
     msg : success,
     SpList : [
                 {
                      fileName: WebViewChromiumPrefs,
                      path: /data/user/0/com.itgowo.tool.android_debugdata_webtool/shared_prefs/WebViewChromiumPrefs.xml
                },
                {
                     fileName: appinfo,
                     path: /data/user/0/com.itgowo.tool.android_debugdata_webtool/shared_prefs/appinfo.xml
               },
              {
                     fileName: appinfo11,
                     path: /data/user/0/com.itgowo.tool.android_debugdata_webtool/shared_prefs/appinfo11.xml
              }
         ]
 }

 ```


 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |
|SpList |array   |返回结果，数据库文件对象数组  |
|fileName |String   |返回结果，文件名  |
|path |String   |返回结果，文件路径  |

 **备注**

- 更多返回错误代码请看首页的错误代码描述


