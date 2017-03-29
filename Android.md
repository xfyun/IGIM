#概述
> 为游戏开发打造的即时通讯SDK（IM SDK），让游戏开发者只需调用IM SDK提供的接口，即可快速在手游中实现聊天、群组聊天、文字、表情、语音等多项功能。

#四步极快速集成游戏语音

##步骤
1. 获取IMLib
2. 获取Appid
3. 导入IMLib,配置AndroidManifest.xml
4. 调用IMClient提供的接口，实现游戏内即时消息，语音识别等功能

##具体实施
###获取IMLib
登录讯飞开放平台 - 游戏解决方案（<http://game.xfyun.cn/>），进入SDK下载，下载IMLib。

###获取Appid
登录讯飞开放平台 - 游戏解决方案（<http://game.xfyun.cn/>），选择/创建应用并且接入游戏解决方案，进入“控制台”查看对应Appid。

###导入IMLib,配置AndroidManifest.xml
1. 导入IMLib.jar包,请自行根据IDE搜索导入jar包的方式，并添加依赖。
2. 配置AndroidManifest.xml
 
>配置权限
	
	<uses-permission android:name="android.permission.VIBRATE" />
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.GET_TASKS" />
	<uses-permission android:name="android.permission.WRITE_SETTINGS" />
	<uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
	<uses-permission android:name="android.permission.RESTART_PACKAGES" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <uses-permission android:name="andorid.permission.CHANGE_CONFIGURATION" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />	
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" /> 
    <uses-permission android:name="android.permission.ACCESS_DOWNLOAD_MANAGER" />
	<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
    
    
>在<application\>标签中配置AppId

	<meta-data
        android:name="IFLYTEK_APPKEY"
        android:value="" />  //步骤1中获取的AppId

>在<application\>标签中配置service

		<service
            android:name=".core.service.NetWorkStateMonitor"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="NetworkStateChangeService" />
            </intent-filter>
        </service>



###调用IMClient接口，实现消息传递功能

>IMClient初始化

	IMClient.createInstance(Context context);

*此处传入的参数context最好为Application的context*

>注册消息监听器

	IMClient.getInstance().regMsgListener(new MsgListener() {
        @Override
        public void onMsg(MessageContent messageContent) {
            //收到消息后，对消息的处理
        }
    });
	

>登录到游戏时
	
	//登录接口，var1--登录的用户名（String）, var2--是否强制登录（boolean），var3--在服务端获取的用户的token（String）
	IMClient.getInstance().login(var1, var2, var3, new ResultCallback<String>() {
            @Override
            public void onSuccess(String data) {
                //登录成功，data是登录用户名
            }

            @Override
            public void onError(int errorCode) {
                //登录失败，errorcode为失败码
            }

        });

>构建单聊消息（此处展示的是构建文本，语音及语音转文本消息，其他消息构建请参照接口文件说明）

	//构建文本消息
	//var1--游戏玩家名（String）， var2--文本消息（String）， var3--是否是群消息（boolean）false，
	//var4--后处理类型(int)，var4=0 ==> 正常文本消息， var4=1 ==> 文字转成语音消息
	CommonMsgContent msg = IMClient.getInstance().buildTextMsg(var1, var2, var3, var4);
<br/>

	//构建语音消息及语音转文本消息
	//此接口既有返回值，又有回调。返回值是为了给你快速的做页面展示，而回调则是真正构建成功，在构建成功回调中发送消息。
	  否则对端无法下载该消息中的文件。修改界面的展示可根据消息的唯一性标志MsgId进行查找

	//var1--消息接收者（String），var2--语音文件路径（String），var3--是否是群消息（boolean）false，
	//var4--后处理类型（int)，var4=0 ==> 正常语音消息，var=2 ==> 语音转文字消息
	CommonMsgContent msg = IMClient.getInstance().buildAudioMsg(var1, audioFilePath, isGroup, postType, new BuildMsgResultCallback<CommonMsgContent>() {
	 		@Override
            public void onSuccess(final CommonMsgContent msg) {
                //构建消息成功，msg--消息。
            }


            @Override
            public void onError(Object msgID, int errorCode) {
               msgID--消息在本地唯一性标志(String)，errorCode--错误码

            }
        });

>构建群发消息（此处展示的是构建文本，语音及语音转文本消息，其他消息构建请参照接口文件说明）

	//构建文本消息
	//var1--世界、工会、组队分别不同的gid(String)， var2--文本消息（String）， var3--是否是群消息（boolean）true，
	//var4--后处理类型(int)，var4=0 ==> 正常文本消息， var4=1 ==> 文字转成语音消息
	CommonMsgContent msg = IMClient.getInstance().buildTextMsg(var1, var2, var3, var4);	
<br/>

	//构建语音消息及语音转文本消息

	//var1--世界、工会、组队分别不同的gid（String），var2--语音文件路径（String），var3--是否是群消息（boolean）true，
	//var4--后处理类型（int)，var4=0 ==> 正常语音消息，var=2 ==> 语音转文字消息
	CommonMsgContent msg = IMClient.getInstance().buildAudioMsg(var1, audioFilePath, isGroup, postType, new BuildMsgResultCallback<CommonMsgContent>() {
	 		@Override
            public void onSuccess(final CommonMsgContent msg) {
                //构建消息成功，msg--消息。
            }


            @Override
            public void onError(Object msgID, int errorCode) {
               msgID--消息在本地唯一性标志(String)，errorCode--错误码

            }
        });

>发送语音或者文本消息

	//var1--构建成功后的消息（Message Content）， var2--发送的超时时间（long）,单位是ms，
	IMClient.getInstance().sendMessage(var1,var2, new SendMessageCallback<String>() {
            @Override
            public void onFaile(String s, int i) {
                // 发送失败 s==>错误阐述  i==>错误码
            }

            @Override
            public void onSuccess(String s, long l) {
                //发送成功  s==>消息在本地唯一性标志, l==>消息在服务端的唯一性标志
            }
        });

>下载语音文件
	
	//当收到语音消息时，需要下载该文件，var1--语音消息(CommonMsgContent)，var2--false  var3--下载回调
    IMClient.getInstance().downloadFile( var1, var2, new ResultCallback() {
                @Override
                public void onError(int errorCode) {
                    //下载失败，errorCode--错误码
                }

                @Override
                public void onSuccess(Object obj) {
					//下载成功，obj--下载完成后的信息类（DownloadInfo）,必须以下面的方式转型。
                   	DownloadInfo info = new DownloadInfo(obj);
                }
            });



>至此，游戏的聊天功能已经完全实现，能够发送与接收语音或者文本消息。