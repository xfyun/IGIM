# 概述
> 为游戏开发打造的即时通讯SDK（IM SDK），让游戏开发者只需调用IM SDK提供的接口，即可快速在手游中实现聊天、群组聊天、文字、表情、语音等多项功能。

# 四步极快速集成游戏语音

## 步骤
1. 获取IGIM、Sunflower及so文件
2. 获取Appid
3. 导入IGIM、Sunflower,配置AndroidManifest.xml
4. 调用IMClient提供的接口，实现游戏内即时消息，语音识别等功能

## 具体实施
### 获取IGIM及so文件
登录讯飞开放平台 - 游戏解决方案（<http://game.xfyun.cn/>），进入SDK下载，下载IGIM、Sunflower及so文件。

### 获取Appid
登录讯飞开放平台 - 游戏解决方案（<http://game.xfyun.cn/>），选择/创建应用并且接入游戏解决方案，进入“控制台”查看对应Appid。

### 导入IGIM及so文件,配置AndroidManifest.xml
1. 导入IGIM.jar和Sunflower包,请自行根据IDE搜索导入jar包的方式，并添加依赖。
2. 将下载的so文件统一放在libs文件夹下。效果图如下：

![Alt 导包效果图](https://github.com/xfyun/IGIM/blob/master/image/%E5%AF%BC%E5%8C%85%E5%B1%95%E7%A4%BA%E5%9B%BE.png?raw=true "导包效果图")<br/><br/>
3. 配置AndroidManifest.xml
 
>配置权限
	
	<uses-permission android:name="android.permission.VIBRATE" />
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.GET_TASKS" />
	<uses-permission android:name="android.permission.RECORD_AUDIO" />
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
	
*注意  如果这里的appid是形似于123e456的，就是e的两端都是数字的形式，必须在appid的两端加'，即value="'123e456'"*

	<meta-data
        android:name="IFLYTEK_APPKEY"
        android:value="" />  //步骤1中获取的AppId




### 调用IMClient接口，实现消息传递功能

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
	
	//uid--玩家的唯一ID，name--玩家的昵称,props--玩家自定义属性（数据格式可为JSON，也可以是其他数据格式），icon 玩家头像Url
	User user = new User(String uid, String name, String props, String icon); 
	//登录接口，user--登录的玩家实例（User）, var2--是否强制登录（boolean），var3--在服务端获取的玩家的token（String）
	IMClient.getInstance().login(user, var2, var3, new ResultCallback<String>() {
            @Override
            public void onSuccess(String data) {
                //登录成功，data是登录玩家名
            }

            @Override
            public void onError(int errorCode) {
                //登录失败，errorcode为失败码
            }

        });
>创建群或讨论组

	//创建群或者讨论组，必须在登录之后调用此接口
	//群和讨论组的区别，讨论组和群都可以主动加入，或者被别人拉入，区别在于讨论组不需要确认。
	//讨论组里面的任何一个成员都可以退出，当讨论组里面的人数为0时，讨论组自动销毁。
	//讨论组也有创建者，创建者能够踢人。其他区别请参照QQ讨论组和群组的用法。
	//creGroupParams--创建群的具体参数（如下：gname--群或者讨论组名称, type---0,代表创建讨论组,type---1代表群组),listener--创建群的回调
    IMClient.getInstance().createGroup(creGroupParams,	new ResultCallback<String>(){
			@Override
            public void onSuccess(String data) {
                //创建群组成功，data是创建的群组生成的唯一标识gid。
            }

            @Override
            public void onError(int errorCode) {
                //创建群组成功，errorcode为失败码
            }

	});

>加入群组或者讨论组

	//加入群组或者讨论组，必须在登录之后调用此接口
	//joinToGroupParams--加入的群组或者讨论组的具体参数（如下：gid--需要加入的群组或者讨论组的唯一标识）
	IMClient.getInstance().joinToGroup(joinToGroupParams, new ResultCallback<String>(){
			@Override
            public void onSuccess(String data) {
                //加入群组成功，data是加入到群组的uid
            }

            @Override
            public void onError(int errorCode) {
                //创建群组成功，errorcode为失败码
            }
	});

>拉人进入群组或者讨论组
	
	//拉人加入群组或者讨论组，必须在登录之后调用此接口，群组需要对方同意，讨论组不需要
	//addMemberToGroupParams--拉人加入的群组或者讨论组的具体参数。如下：gid--群组或者讨论组的唯一标识,members（JsonArray格式的）--需要拉入的玩家,type（0是讨论组，1是群组）,msg（添加群成员的说明）
	IMClient.getInstance().addMemToGroup(addMemberToGroupParams, new ResultCallback<String>(){
			@Override
            public void onSuccess(String data) {
                //请求成功，data是加入到群组的uid
            }

            @Override
            public void onError(int errorCode) {
                //创建群组成功，errorcode为失败码
            }
	});

>退出讨论组或者群组


	//退出讨论组或者群组，必须在登录之后调用此接口
	// exitFromGroupParams--退出群组或者讨论组的具体参数。如下：gid--群组或者讨论组的唯一标识
	IMClient.getInstance().exitFromGroup(exitFromGroupParams,new ResultCallback<String>(){
	
			@Override
            public void onSuccess(String data) {
                //请求成功，data是加入到群组的uid
            }

            @Override
            public void onError(int errorCode) {
                //创建群组成功，errorcode为失败码
            }

	});

>其他关于群组和讨论组的接口调用请参照文档"IM_Android客户端用户手册"，此文档有对所有接口的详细说明。

<hr>

>调用语音录制接口，若游戏商家想要自己开发录音，可跳过此步骤。但录音接口中录制的是本公司特定的格式，转写结果更为精确.
	
	//设置文件保存路径   dir--string类型，到达需要存储的文件夹的名称。不调用，则使用默认路径
	IMClient.getInstance().setAudioPath(dir);

	//开始录音，该接口所有回调不在主线程，不要在回调中做主线程操作。
	//mCurrentFileName, 文件名。构建语音消息一定要使用录音结束后的回调的文件路径。因为，在该方法内会改变传入文件的后缀，如果使用的就是pcm后缀，则无影响。

	IMClient.getInstance().startRecording(mCurrentFileName, new PcmRecordListener() {
			@Override
			public void onRecordBuffer(int length, double volume) {
				//length 录制文件 单位：byte
				//volume 音量大小 单位：分贝值
			}

			@Override
			public void onError(int error) {
				//录制错误，返回错误码
			}

			@Override
			public void onRecordStarted(boolean success) {
				//开始录制的回调
			}

			@Override
			public void onRecordFinished(String filePath) {
				//录制结束，返回文件路径
				//使用该路径，构建消息。

			}
	});

>播放语音接口，若使用的是我们提供的语音录制接口，请使用我们的播放接口播放。
	
	//初始化播放接口，filePath--播放文件路径
	IMClient.getInstance().initPlayer(filePath, new AudioPlayer.PlayerListener() {
            @Override
            public void onPause() {
                //暂停回调
            }

            @Override
            public void onStart() {
				//开始回调
            }

            @Override
           	public void onStop() {
               	//停止回调
			}

            @Override
            public void onCompleted() {
                //完成回调
            }

            @Override
            public void onError(int errorCode) {
                //错误回调
            }
    });
    //开始播放                
	IMClient.getInstance().startPlay();
	//停止播放
	IMClient.getInstance().stopPlay();
	//暂停播放
	IMClient.getInstance().pausePlay();	

><font color="#FF0000">特殊消息（语音转文字、文字转语音），在发出消息后，会接收到带有相同msgID的消息（在上面注册消息接收的广播处），此时再将消息转化为PostTextRlt，或者PostVoiceRlt，就可获得转化后的内容。具体操作请参考Demo。</font>
>
>构建单聊消息（此处展示的是构建文本，语音及语音转文本消息，其他消息构建请参照接口文件说明）

*构建文本消息*

	//var1--游戏玩家名（String）， var2--文本消息（String）， var3--是否是群消息（boolean）false，
	//var4--后处理类型(int)，var4=0 ==> 正常文本消息， var4=1 ==> 文字转成语音消息 
	//var5--扩展字段（String）
	CommonMsgContent msg = IMClient.getInstance().buildTextMsg(var1, var2, var3, var4, var5);
<br/>

*构建语音消息及语音转文本消息*

	//此接口既有返回值，又有回调。返回值是为了给你快速的做页面展示，而回调则是真正构建成功，在构建成功回调中发送消息。
	  否则对端无法下载该消息中的文件。修改界面的展示可根据消息的唯一性标志MsgId进行查找

	//var1--消息接收者（String），var2--语音文件路径（String）（必须带有后缀名），var3--是否是群消息（boolean）false，
	//var4--后处理类型（int)，var4=0 ==> 正常语音消息，var4=2 ==> 语音转文字消息
	//var5--扩展字段（String）
	CommonMsgContent msg = IMClient.getInstance().buildAudioMsg(var1, var2, var3, var4, var5, new BuildMsgResultCallback<CommonMsgContent>() {
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

*构建文本消息*

	//var1--世界、工会、组队分别不同的gid(String)， var2--文本消息（String）， var3--是否是群消息（boolean）true，
	//var4--后处理类型(int)，var4=0 ==> 正常文本消息， var4=1 ==> 文字转成语音消息
	//var5--扩展字段（String）
	CommonMsgContent msg = IMClient.getInstance().buildTextMsg(var1, var2, var3, var4， var5);	
<br/>

*构建语音消息及语音转文本消息*

	//var1--世界、工会、组队分别不同的gid（String），var2--语音文件路径（String）（必须带有后缀名），var3--是否是群消息（boolean）true，
	//var4--后处理类型（int)，var4=0 ==> 正常语音消息，var4=2 ==> 语音转文字消息
	//var5--扩展字段（String）
	CommonMsgContent msg = IMClient.getInstance().buildAudioMsg(var1, var2, var3, var4, var5, new BuildMsgResultCallback<CommonMsgContent>() {
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

>语音转文字消息效果图

![Alt 语音效果图](https://github.com/xfyun/IGIM/blob/master/image/%E8%AF%AD%E9%9F%B3%E8%BD%AC%E6%96%87%E5%AD%97%E6%95%88%E6%9E%9C%E5%9B%BE.png?raw=true "语音效果图")


>下载语音文件
	
	//参数传入整个参数进行下载，推荐使用。能跟数据库中的保存的文件名保持统一。
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

	//不推荐使用，不能够实时更新数据库中保存的文件名。
	//当收到语音消息时，需要下载该文件，var1--消息类型  语音消息-2，图片消息-1，视频消息-3，此类型只是为了区分下载的文件夹，var2--需要下载文件的唯一性标志（fid）,可在语音消息中获取  var3--下载回调
	IMClient.getInstance().downloadFile(var1, var2, new ResultCallback() {
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



>至此，游戏的聊天功能已经完全实现，能够发送与接收语音或者文本消息。若想简单的实现语音转文字功能，可参考Demo精简版。

<hr>

>附错误码：

|错误码| 说明|
| :--:| --|
| 0   | 正确|
|21001|服务端返回的json解析有误|
|21002|sdk中的json解析有误|
|21003|demo中传入的json解析有误|
|22001|文件IO异常|
|22002|没有文件的权限|
|22003|文件不存在或者数据为空|
|23001|参数为空|
|23002|错误参数（参数不为空，但不符合要求）|
|23003|sdk初始化参数为空，可能未初始化sdk|
|23004|登录携带的参数为空，可能未登录|
|23005|构建消息错误|
|23006|发送消息错误|
|23007|没有群组|
|24001|用户处于离线状态|
|24002|创建socket失败|
|25001|上传音频错误|
|25002|下载音频错误|
|25003|上传图片错误|
|25004|下载图片错误|
|25005|上传视频错误|
|25006|下载视频错误|
|25007|不支持的消息类型|
|25008|上传失败|
|25009|下载失败|
|25010|文件过大|
|26001|错误的http请求|
|26002|http返回为空|
|26003|发送消息超时|
|26004|未连接到服务器|
|26005|网络错误|
|27000|数据库错误|
|28000|其他包错误|
|29001|录制音频失败|
|29002|播放音频失败|
|30001| token非法 |
|30002|json数据非法|
|30003|缺少相关字段|
|30004|没有更多数据|
|30005-30012|服务端内部错误|
|31001|群组不存在或者用户信息不存在|
|31002|创建群组id失败，服务端内部错误|
|31003|加入群组的用户已经在群组中|
|31004|用户不在群组或者用户不是管理员|
|31005|调用接口的用户没有权限|
|31006|群组类型错误，与数据库中的类型不一致|
|31007|群组搜索的结果为空|
|31008|群组验证时，用户状态错误|
|31009|群组验证时，未找到用户对应的状态码|
|31010|创建会话id错误|
|34001|内部参数非法|
|34002|seqID非法|
|34003|syncKey非法|
|34005|拉取非用户或群组消息|
|34006|gids字段非数组类型|
|35001|标签通知暂不支持|
|35002|通知类型未知|
|35003|服务端内部错误|
|35004|to字段为空|
|35005|字段非数组类型|
|36001|该用户已在线|

>常见问题讨论：
