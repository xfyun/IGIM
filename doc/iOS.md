# 概述
> 为游戏开发打造的即时通讯SDK（IGIM SDK），让游戏开发者只需调用IGIM SDK提供的接口，即可快速在手游中实现聊天、群组聊天、文字、表情、语音等多项功能。

# 四步极快速集成游戏语音

## 步骤
1. 获取XMLib.framework
2. 获取Appid
3. 导入XMLib,配置工程
4. 调用IMClient提供的接口，实现游戏内即时消息，语音识别等功能

## 具体实施
### 获取XMLib
登录讯飞开放平台 - 游戏解决方案（<http://game.xfyun.cn/>），进入SDK下载，下载XMLib。

### 获取Appid
登录讯飞开放平台 - 游戏解决方案（<http://game.xfyun.cn/>），选择/创建应用并且接入游戏解决方案，进入“控制台”查看对应Appid。

### 导入XMLib至APP工程
1. 创建一个iOS Project
2. 将MLib.framework拷贝至该工程的文件目录libs下
3. 将XMLib.framework(动态库)导入至工程中：
	Targets-->Gneral-->Embedded Binaries
4. 将工程Build Settings中的Framework Search Paths指定到刚才的libs目录
5. 将工程Build Settings中的Runpath Search Paths设置为：@executable_path/Frameworks
6. 将Enable Bitcode设置为NO
7. 配置sdk运行所需权限

	```
	<!—IGIM_SDK  运行需要的权限  --/>
	<key>NSCameraUsageDescription</key>
	<string>是否允许此App使用您的相机？</string>
	<key>NSLocationAlwaysUsageDescription</key>
	<string>是否允许此App后台获取地理位置信息？</string>
	<key>NSLocationUsageDescription</key>
	<string>是否允许此App获取地理位置信息？</string>
	<key>NSLocationWhenInUseUsageDescription</key>
	<string>是否允许此App根据地理位置获取周边信息？</string>
	<key>NSMicrophoneUsageDescription</key>
	<string>是否允许此App使用您的麦克风？</string>
	```

8. Info.list增加 如下配置
 
>https配置
	
	<key>NSAppTransportSecurity</key>
	<dict>
		<key>NSAllowsArbitraryLoads</key>
		<true/>
		<key>UIBackgroundModes</key>
		<array>
			<string>voip</string>
			<string>remote-notification</string>
		</array>
	</dict>
    
    
>后台运行模式配置

	<key>UIBackgroundModes</key>
	<array>
		<string>location</string>
		<string>remote-notification</string>
	</array>

>存储设置

		<key>UIFileSharingEnabled</key>
	<true/>



### 调用IMClient接口，实现消息传递功能

>IMClient初始化


*application didFinishLaunchingWithOptions方法中，调用初始化接口*

	[[XMManager sharedInstance] initSdk:@”XXXXXX” withToken:@”XXXXXXXX-XXXX-XXXX_XXXX_XXXXXXXXXXXX”];//初始化，X符号在下载Demo时会被替换成开发者的专属字符串，请谨慎管理。
	[[XMManager sharedInstance] setEnv:0];//设置调试环境，0：正式，1：调试


>推送绑定DeviceToken

*application didRegisterForRemoteNotificationsWithDeviceToken方法*

	[[XMManger sharedInstance] bindDeviceToken:deviceToken];	

>前后台切换处理

*applicationDidEnterBackground方法*
	
	[[XMManger sharedInstance] doBackground:nil succ:nil faild:nil];

*applicationDidEnterForeground方法*

	[[XMManger sharedInstance] doForeground];

>用户登录游戏

	-(void) login:(XMLoginParam *)param succ:(XMSucc)succ fail:(XMFail)fail
	
	功能：		异步方式实现用户登录。
	参数：		parm 包含用户ID，token，appid
			succ 成功回调
			fail 失败回调
	结果返回：	错误信息

>用户退出游戏	

	-(void) logout:(XMSucc)succ fail:(XMFail)fail
	
	功能：		登出
	参数：		succ 成功回调
			fail 失败回调
	结果返回：	无

>语音录制接口，开发者也可自己开发录音控件，建议使用SDK内部的录音器

	//创建录音器实例,并初始化	
	self.audioRecord = [[XMAudioRecorder alloc]init];
	self.audioRecord.delegate = self;
	self.audioRecord.maxRecordDuration = 60.0f;
	
	//设置录音文件存储路径和文件名
	[self.audioRecord setSaveAudioPath:savePath withSaveName:saveName];
	
	//启动录音器，若启动成功，返回YES	
	BOOL ret = [self.audioRecord startRecord]；
	//停止录音
	[self.audioRecord stopRecord];
	//取消录音
	[self.audioRecord cancelRecord];
	
	//XMAudioRecorderDelegate 录音器回调方法
	-(void) onXMAudioRecorderDidComplete: (NSString *)saveAudioFile;
	-(void) onXMAudioRecorderDidFail: (NSError *)error;
	-(void) onXMAudioRecorderSoundPower: (float)power;
	-(void) onXMAudioRecorderDidCancel;
	
>语音播放接口，开发者也可自己开发播音控件，建议使用SDK内部的播放器

	//创建录音器实例，并初始化
	self.audioPlayer = [[XMAudioPlayer alloc]init];
	self.audioPlayer.delegate = self;
	
	//启动播放器，若启动成功，返回YES	
	BOOL ret = [self.audioPlayer playAudioFile:audioFile];
	//停止播放	
	[self.audioPlayer stopPlayer];
	//暂停播放	
	[self.audioPlayer pausePlayer;
	//恢复播放	
	[self.audioPlayer resumePlayer];
	
	//XMAudioPlayerDelegate 播放器回调方法
	-(void) onXMAudioPlayerDidComplete: (NSString *)audioFile;
	-(void) onXMAudioPlayerDidFail: (NSError *)error;
	- (void) onXMAudioPlayerSoundPower: (float)power;
	
>构建单聊消息（此处展示的是构建文本，语音及语音转文本消息，其他消息构建请参照接口文件说明）

	//构建文本消息
		//构建文本消息体	
	
	
	-(instancetype)initWithConversation:(XMConversation*)conversation
	msgType:(XMMessageType)msgType
	postType:(XM_POST_TYPE)postType
	msgId:(NSString*)msgId
	sender:(NSString*)sender
	    body:(XMMessageBody*)body
	    
	功能：		构建消息
	参数：		conversation	会话对象
	msgType	消息类型，支持文本，图片，定位，声音，表情，视频等。
 	postType	消息后处理类型，可实现文字，语音转换，以及翻译等。
			msgId	消息id
	sender	发送者
	body	消息体
	结果返回：消息对象


>发送消息
	
	-(int) sendMessage(XMMessage)msg succ:(XMSucc)succ fail:(XMFail)fail
	
	功能：		发送消息
	参数：		msg 	消息
				succ   发送成功回调
				fail	发送失败回调
	结果返回：状态码

>获取当前登录的用户

	-(NSString* ) getLoginUser
	
	功能： 	获取当前登录的用户
	参数： 	无 
	结果返回：当前登陆的用户ID

>获取离线消息

	-（void）getLocalMessage:(int)count last:(XMMessage*)last succ:(XMGetMsgSucc)succ fail: (XMFail)fail
	
	功能： 	获取离线消息
	参数：		count  获取数量
				last	上次最后一条消息
				succ   发送成功回调
				fail	发送失败回调
	结果返回：无


>获取所有会话

	-(NSArrary*)getAllConversation
	
	功能：		获取所有会话
	参数：		无
	结果返回：会话对象

>根据联系人获取对应会话

	-(XMConversation*)getConversation:(XMConversationType)type receiver:(NSString*)receiver
	
	功能：		获取会话
	参数：		type	会话类型
				receiver	单聊的用户identifier，群组的id
	结果返回：会话

>删除指定会话

	-(BOOL)deleteConversation: (XMConversation*)conversation deleteMessage:(BOOL)enable
	
	功能：		删除指定会话 
	参数：		conversation  要删除的会话 
				Enable		是否删除会话下的所有消息
	结果返回：是否正确删除会话

>创建群组

	- (XMGroup)createGroupWithSubject:(NSString*)aSubject
          description:(NSString*)aDescription
          invitees:(NSArray*)aInvitees
          message:(NSString*)aMessage
          setting:(XMGroupOptions*)aSetting
          error:(NSError **)pError;
          
	功能：		创建群组 
	参数：		aSubject	群组名称
				aDescription		群组描述
				aInvitees	群组成员
				aMessage	邀请消息
				aSetting	群组属性
				pError	出错信息
	结果返回：群组对象



>至此，游戏的聊天功能已经完全实现，能够发送与接收语音或者文本消息。
