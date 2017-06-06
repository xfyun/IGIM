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
2. 将MLib.framework拷贝至该工程的文件目录lib下
3. 将XMLib.framework(静态库)导入至工程中：  
	Targets-->Build Phases-->Link Binary With Libraries
4. 将工程Build Settings中的Framework Search Paths指定到刚才的lib目录
5. 添加如下动态依赖库至Link Binary With Libraries：   
	libz.tbd    
	libsqlite3.tbd
6. 将Enable Bitcode设置为NO
7. 如下设置Targets-->Build Settings-->Linking-->Other Linker Flags:  
	-ObjC  
	或者 -force_load $(PROJECT_DIR)/lib/XMLib.framework/XMLib
8. 配置sdk运行所需权限

	```
	<!—IGIM_SDK  运行需要的权限  --/>
	<key>NSLocationWhenInUseUsageDescription</key>
	<string>App需要您的同意,才能在使用期间访问位置</string>
	<key>NSMicrophoneUsageDescription</key>
	<string>是否允许此App使用您的麦克风？</string>
	```

8. Info.list增加 如下配置
 
>https配置
	
	<key>NSAppTransportSecurity</key>
	<dict>
		<key>NSAllowsArbitraryLoads</key>
		<true/>
	</dict>
    
    
>后台运行模式配置

	<key>UIBackgroundModes</key>
	<array>
		<string>remote-notification</string>
	</array>


### 调用IMClient接口，实现消息传递功能

>IMClient初始化


*application didFinishLaunchingWithOptions方法中，调用初始化接口*

	// 初始化，X符号在下载Demo时会被替换成开发者的专属字符串，请谨慎管理。
	[[XMManager sharedInstance] initSdk:@”XXXXXX” withToken:@”XXXXXXXX-XXXX-XXXX_XXXX_XXXXXXXXXXXX”];
	
	// 设置调试环境，0：正式，1：调试
	[[XMManager sharedInstance] setEnv:0];

>推送绑定DeviceToken

*application didRegisterForRemoteNotificationsWithDeviceToken方法*

	[[XMManger sharedInstance] bindDeviceToken:deviceToken];	

>前后台切换处理

*applicationDidEnterBackground方法*
	
	[[XMManger sharedInstance] doBackground:nil succ:nil fail:nil];

*applicationWillEnterForeground方法*

	[[XMManger sharedInstance] doForeground];

>用户登录游戏

	[[XMManager sharedInstance] login: loginParam succ:^{
		// 登陆成功		
	}fail:^(NSInteger code, NSString *msg){
		// 登陆失败		
	}];
	
	/*
		loginParam： 登陆参数，包含用户ID，token，appid
	*/

>用户退出游戏	

	[[XMManager sharedInstance] logout:^{
		// 成功退出	
	} fail:^(NSInteger code, NSString *msg) {
		// 退出失败	
	}];

>语音录制接口，开发者也可自己开发录音控件，建议使用SDK内部的录音器

	// 创建录音器实例,并初始化	
	self.audioRecord = [[XMAudioRecorder alloc] init];
	self.audioRecord.delegate = self;
	self.audioRecord.maxRecordDuration = 60.0f;
	
	// 设置录音文件存储路径和文件名
	[self.audioRecord setSaveAudioPath: savePath withSaveName: saveName];
	
	// 启动录音器，若启动成功，返回YES	
	BOOL ret = [self.audioRecord startRecord]；
	// 停止录音
	[self.audioRecord stopRecord];
	// 取消录音
	[self.audioRecord cancelRecord];
	
	// XMAudioRecorderDelegate 录音器回调方法
	-(void) onXMAudioRecorderDidComplete: (NSString *)saveAudioFile;
	-(void) onXMAudioRecorderDidFail: (NSError *)error;
	-(void) onXMAudioRecorderSoundPower: (float)power;
	-(void) onXMAudioRecorderDidCancel;
	
>语音播放接口，开发者也可自己开发播音控件，建议使用SDK内部的播放器

	// 创建录音器实例，并初始化
	self.audioPlayer = [[XMAudioPlayer alloc] init];
	self.audioPlayer.delegate = self;
	
	// 启动播放器，若启动成功，返回YES	
	BOOL ret = [self.audioPlayer playAudioFile: audioFile];
	// 停止播放	
	[self.audioPlayer stopPlayer];
	// 暂停播放	
	[self.audioPlayer pausePlayer;
	// 恢复播放	
	[self.audioPlayer resumePlayer];
	
	// XMAudioPlayerDelegate 播放器回调方法
	-(void) onXMAudioPlayerDidComplete: (NSString *)audioFile;
	-(void) onXMAudioPlayerDidFail: (NSError *)error;
	- (void) onXMAudioPlayerSoundPower: (float)power;

>获取会话

	self.conversation ＝ [[XMManager sharedInstance] getConversation: conversationType receiver: chatID];
	
	/*
		conversationType: 会话类型
				XM_C2C		单聊
				XM_GROUP	群聊
	*/

>构建消息（此处展示的是构建文本，语音和语音转文本消息，其他消息构建请参照接口文件说明）

	文本消息：
	
		// 构建文本消息体
		XMTextMessageBody *textMessageBody = [[XMTextMessageBody alloc]init];
		textMessageBody.text = @"......";
		
		// 构建文本消息
		XMMessage *textMessage = [[XMMessage alloc] initWithConversation: self.conversation 
									 msgType: XM_MSG_TYPE_Text 	
									postType: XM_POST_TYPE_DEFAULT 
									   msgId: nil 	// 可不设
									  sender: nil	// 可不设
									    body: textMessageBody];
<br/>

	语音消息或语音转文本消息：
	
		// 构建语音消息体
		XMSoundMessageBody *soundMessageBody = [[XMSoundMessageBody alloc] initWithLocalPath: audioLocalPath displayName: @"[语音]"];
		soundMessageBody.duration = duration;	// 音频时长
		
		// 构建语音消息
		XMMessage *soundMessage = [[XMMessage alloc] initWithConversation: self.conversation 
									  msgType: XM_MSG_TYPE_Sound 	
									 postType: XM_POST_TYPE_DEFAULT	// 语音消息：XM_POST_TYPE_DEFAULT，语音转文本消息：XM_POST_TYPE_IAT 
									    msgId: nil 	// 可不设
									   sender: nil	// 可不设
									     body: soundMessageBody];

>发送消息
	
	[self.conversation sendMessage: textMessage succ:^{
		// 消息发送成功	
	} fail:^(NSInteger code, NSString *msg) {
		// 消息发送失败		
	}];

>语音转文字消息效果图

![Alt 语音效果图](https://github.com/xfyun/IGIM/blob/master/image/%E8%AF%AD%E9%9F%B3%E8%BD%AC%E6%96%87%E5%AD%97%E6%95%88%E6%9E%9C%E5%9B%BE.png?raw=true "语音效果图")


>下载语音文件
	
	//下载语音文件	
	[soundMessage getFileData:^{
		// 语音文件下载成功	
	} fail:^(NSInteger code, NSString *msg) {
		// 下载失败			
	} progress:nil];
<br/>

	//播放语音文件
	XMSoundMessageBody *voiceMessageBody = (XMSoundMessageBody *)[soundMessage getMessageBody];
	
	BOOL ret = [self.audioPlayer playAudioFile: voiceMessageBody.localPath];



>创建群组

	[[XMManager sharedInstance] createNewGroupWithName: @"群组名称" 
					       description: @"群组描述" 
						  invitees: invitees		// 被邀请加入群组的用户数组 
						   message: @"邀请消息内容" 
						   setting: groupOptions 
						   success:^(XMGroup *aGroup) {
		//创建成功	
	} failure:^(NSError *aError) {
		//创建失败			
	}];

	/*
		groupOptions: 群组属性配置
			groupOptions.style = XMGroupStyleJoinOpen			// 讨论组		
								 XMGroupStyleJoinNeedApproval	// 群组	
	*/

>消息接收回调 \<XMMessageListener\>

	[[XMManager sharedInstance] addMessageDelegate:self];
	
	//新消息回调
	- (void) onNewMessage: (NSArray *) msgs;

>状态变化回调 \<XMClientDelegate\>

	[[XMManager sharedInstance] addClientDelegate:self];

	//网络状态变化回调（与IM Server）
	- (void) didConnectionStateChanged: (XMNetworkStatus)aConnectionState;
	
	//消息接收者已读回执
	-(void) didReadNotifyChanged: (NSNumber *)seqId;
	
	//消息在其他设备已读通知
	-(void) didUnreadMessagesCountChanged;
	
	//强制下线通知
	- (void) onForceOffline;


>至此，游戏的聊天功能已经完全实现，能够发送与接收语音或者文本消息。
