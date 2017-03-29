# 讯飞云通讯V1.0.0

<!--TOC-->

## 1. 简介

讯飞云通讯致力于为应用提供稳定的IM通讯能力，同时结合讯飞开放平台丰富的语音能力，为开发者提供一站式服务。

## 2. 接口概述

### 2.1 API说明

1. 本文档采用OAuth2认证方式，调用接口前需要从服务器获取到token，调用接口时将token放在HTTP请求头中。
2. 所有接口统一为UTF-8编码。
3. 所有接口支持http和https。

### 2.2 app级别token获取

**1. 说明：**  
建议app级别token由开发者自己的APPServer获取，以防止apiKey泄露。

**2. 请求类型和path：**  
HTTP GET请求  
/v1/rest/getToken.do

**3. 参数列表：**  
以下参数需要放在HTTP Request Header中

| 参数 | 参数说明 | 必须 |
| -- | -- | -- |
|X-Appid |    讯飞开放平台注册申请应用的应用ID(appid)| 是 |
|X-Nonce    |随机数（最大长度128个字符）| 是 |
|X-CurTime|    当前UTC时间戳，从1970年1月1日0点0 分0 秒开始到现在的秒数(String) | 是 |
|X-CheckSum|    MD5(apiKey + Nonce + CurTime),三个参数拼接的字符串，进行MD5哈希计算，转化成16进制字符(String，小写) | 是 |
|X-Expiration| token过期时间，单位为妙(s),若希望token不过期，设置为-1，默认1天 | 否 |

*注:*
checkSum有效期：出于安全性考虑，每个checkSum的有效期为5分钟(用curTime计算),同时curTime要与标准时间同步，否则，时间相差太大，服务端会直接认为curTime无效。

**4. checkSum生成示例:**
`计算checkSum的golang代码举例:`

    func generateCheckSum(apikey, nonce, curtime string) string {
        input := fmt.Sprintf("%s%s%s", apikey, nonce, curtime)
        h := md5.New()
        h.Write([]byte(input))
        return hex.EncodeToString(h.Sum(nil))
    }
    
    
**5. 示例:**  
*请求:*
`curl -XGET -H 'X-Appid: test' -H 'X-Nonce: fadfadfafd' -H 'X-CurTime: 1461827147' -H 'X-CheckSum: xxxxxxxx' http[s]://im.voicecloud.cn/v1/rest/getToken.do`
*响应体:*

    {
        "ret": 0,
        "token": "123456789",
        "expire": "xxxx" // 过期时间，unix时间戳
    }


### 2.3用户token获取
**1. 说明：**  
用户级别token是由开发的APPServer为自己app下用户申请接入讯飞云通讯系统的token

**2. 请求类型和path：**  
HTTP GET请求  
/v1/rest/getUserToken.do

**3. 参数列表：**  
以下参数需要放在HTTP Request Header中

| 参数 | 参数说明 | 必须 |
| -- | -- | -- |
|X-Appid |    讯飞开放平台注册申请应用的应用ID(appid)| 是 |
|X-Token |app级别token，由getToken.do接口获取| 是 |
|X-Uid|用户uid，详见第三部分用户系统对接|是|
|X-Expiration| token过期时间，单位为妙(s),若希望token不过期，设置为-1，默认1天 | 否 |

**4. 示例：**
*请求:*
`curl -XGET -H 'X-Appid: test' -H 'X-Token: 123456789' -H 'X-Uid: zhzhai' http[s]://im.voicecloud.cn/v1/rest/getUserToken.do`
*响应体:*

    {
        "ret": 0,
        "token": "xxxx",
        "expire": "xxxx", // 过期时间，unix时间戳
        "sid": "abadafad@1234" //session id
    }


### 2.4用户token更新
**1. 说明：**  
若开发者发现其应用下某个用户行为异常，可以更新该用户的token，更换后该用户原有的token不能再接入讯飞云通讯系统

**2. 请求类型和path：**  
HTTP GET请求  
/v1/rest/updateUserToken.do

**3. 参数列表：**  
以下参数需要放在HTTP Request Header中

| 参数 | 参数说明 | 必须 |
| -- | -- | -- |
|X-Appid |    讯飞开放平台注册申请应用的应用ID(appid)| 是 |
|X-Token |app级别token，由getToken.do接口获取| 是 |
|X-Uid|用户uid，详见第三部分用户系统对接|是|
|X-Expiration| token过期时间，单位为妙(s),若希望token不过期，设置为-1，默认1天 | 否 |

**4. 示例：**
*请求:*
`curl -XGET -H 'X-Appid: test' -H 'X-Token: 123456789' -H 'X-Uid: zhzhai' http[s]://im.voicecloud.cn/v1/rest/updateUserToken.do`
*响应体:*

    {
        "ret": 0,
        "token": "xxxx",
        "expire": "xxxx", // 过期时间，unix时间戳
        "sid": "abadafad@1234" //session id
    }
## 3. 用户系统对接

### 3.1 说明
讯飞云通讯是即时通讯的消息通道，不提供用户体系的维护，也不需要保存任何app的用户信息。

使用讯飞云通讯，您只需要为您app的用户创建一个云通讯系统id（可以是您app用户uid，或者uid的md5，保证唯一即可）导入到讯飞云通讯服务即可，之后该用户在讯飞云通讯系统中所有操作（如登陆上线、下线、发送消息，接收其他人发送的消息）都以该uid标识。

### 3.2 导入用户

#### 3.2.1 说明
1. 将已上线的 APP 的现有用户导入到云通讯系统；
2. APP 创建新用户时同步创建云通讯系统id。

#### 3.2.2 接口

**请求说明**

	POST http://im.voicecloud.cn/v1/user/import.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

该接口主要将第三方的用户账户信息导入到讯飞云通讯系统

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|uid|string|是|导入用户id，唯一标识一个用户|
|name|string|是|导入用户的昵称|
|props|string|否|用户自定义属性，数据格式可为JSON，也可以是其他数据格式|
|icons|string|否|用户头像URL|
|cMsgID|string|是|客户端构造，消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/user/import.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"uid":"testUser","name":"小明","props":"{\"age\":18,\"sex\":\"man\"}","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"import userinfo successfully"	
	}

**主要返回码**

0、30001、30003、30005、30007、30012、31014、

### 3.3 用户信息更新

#### 3.3.1 说明

APP修改用户信息时调用接口同步云通讯系统id

#### 3.3.2 接口

**请求说明**

	POST http://im.voicecloud.cn/v1/user/update.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

该接口供第三方调用以修改用户的信息

**参数说明**

name、props、icons三个参数不能同时为空值

|参数|类型|必须|参数说明|
|--|--|--|--|
|uid|string|是|用户id，唯一标识一个用户|
|name|string|否|用户新的昵称|
|props|string|否|用户自定义属性，数据格式可为JSON，也可以是其他数据格式|
|icons|string|否|用户头像URL|
|cMsgID|string|是|唯一标识一次http请求|

**curl请求示例**

	curl -XPOST http[s]://im.voicecloud.cn/v1/user/update.do -H "X-Appid:xxx" -H "X-Token:xxx" -d '{
	"uid":"test","name":"hello","props":"{\"age\":9,\"addr\":\"xxx\"}","cMsgID":"xxx"}'

**返回说明**

	{
		"ret":	0
	}

**主要返回码**

0,30001,30003,30005,30012,31015,31017

### 3.4 用户信息删除

#### 3.4.1 说明

APP 删除用户时同步删除云通讯系统id

#### 3.4.2 接口

**请求说明**

	POST http://im.voicecloud.cn/v1/user/delete.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

该接口供第三方调用以删除用户

**参数说明**

|参数|类型|必须|参数说明|
|--|--|--|--|
|uid|string|是|唯一标识一个用户|
|cMsgID|string|是|唯一标识一次http请求|

**curl请求示例**

	curl -XPOST http[s]://im.voicecloud.cn/v1/user/delete.do -H "X-Appid:xxx" -H 
	"X-Token:xxx" -d '{"uid":"test","cMsgID":"xxx"}'

**返回说明**

	{
		"ret":	0
	}

**主要返回码**

0,30001,30003,30005,30012,31016

## 4. 消息功能
**以下参数需要放在Http Request Header中**

|参数|类型|参数说明|
|----|----|----|
|X-Appid|string|应用ID，唯一标识一个应用|
|X-Token|string|app权限校验时使用|

### 4.1 功能介绍
通讯系统现支持如下消息类型  

|消息类型|sdk支持|rest支持|备注|
|:--:|:--:| :--:|:--:|
|普通消息|是|是|无|
|图片消息|是|是|无|
|语音消息|是|是|无|
|视频消息|是|是|无|
|转写(后处理消息)|是|是|语音转文字|
|合成(后处理消息)|是|是|文字转语音|
|翻译(后处理消息)|否|否|中英文互译，待支持|
|用户通知|否|是|应用级别消息通知|

	
### 4.2 用户通知
**说明**  
1 用户通知消息提供开发者自定义消息的通知  
2 支持点对点通知、点对多通知以及点对应用下所有用户通知  
3 用户通知消息使用HTTP协议，body中携带消息内容，消息内容统一为json格式。

**请求**  

* HTTP方法: `POST`
* URL: `im.voicecloud.cn`
* PATH: `/v1/IM/notify.do`

**公用概念说明**

|名词|类型|说明|
|----|----|----|----|
|gid|string|云通讯群组id|
|uid|string|云通讯用户id|

**发送消息协议**  

|参数|类型|描述|是否一定携带|
|:--:| :--: |:--|:--:|
|to|json string array|接受对象数组，对象为uid或gid，objectType为1、3时array长度为1|objectType为4时不必|
|objectType|int|通知目标类型 1 单个通知， 2 列表通知， 3 群组内用户通知，4 app下所有用户通知|是|
|deviceType|int|1 phone设备，2 pc设备，3 pad设备, 4 全设备通知|是|
|notifyType|int|通知类型，1 通用用户通知消息 |是|
|notification|json object|用户自定义字段，客户端传递给app|是|

**返回消息协议**

|参数|类型|描述|是否一定携带|
|:--:| :--: |:--|:--:|
|ret|int|错误码  详见模块7|是|
|sid|string|服务端返回的session id|是|
|detail|string|服务端返回的错误信息 ret=0时为空|否|

**示例**  
请求：

	curl -XPOST -H "X-Appid: test" -H "X-Token: 123456789" http[s]://im.voicecloud.cn/v1/IM/notify.do -d '
	{
		"to":["tom"],
		"objectType":1,
		"notifyType":1,
		"deviceType":1,
		"notification":{"custom":"whateveritis"}
	}

响应：

	{
		"ret":0,
		"sid":"f4a12a9c-652b-4e94-95bb-50b27ad2b2f5",
		"detail":"Succeed"
	}

### 4.3 普通消息rest
**说明**  
1 此为普通消息rest接口  
2 支持对象类型为点对点、点对多、点对群组；消息类型为文本、图片、短语音、短视频、合成、转写的消息。  
3 使用HTTP协议，body中携带消息内容，消息内容统一为json格式。

**请求**  

* HTTP方法: `POST`
* URL: `im.voicecloud.cn`
* PATH: `/v1/IM/push.do`

**公用概念说明**

|名词|类型|说明|
|----|----|----|----|
|gid|string|云通讯群组id|
|uid|string|云通讯用户id|

**发送消息协议**  

|参数|类型|描述|是否一定携带|
|:--:| :--: |:--|:--:|
|from|string|发送者用户名，app内唯一 |是|
|to|json string array|接受对象数组，对象为uid或gid，objectType为1、3时array长度为1|是|
|msgType|int|消息类型，1 文本消息，2 图片消息，3 短音频消息， 4 短视频消息，5 文字转语音消息，6 语音转文字消息|是|
|objectType|int|消息目标类型 1 单目标， 2 列表目标， 3 群组消息 |是|
|msg|json object|根据msgType的不同，msg的内容也相应不同，如下所示|是|

***msgType为1*** 

|参数|类型|描述|是否一定携带|
|:--:| :--: |:--|:--:|
|text|string|发送消息文本|是|

***msgType为2***   

|参数|类型|描述|是否一定携带|备注|
|:--:| :--: |:--|:--:|:--:|
|text|string|图片附带信息|否|无|
|name|string|图片名称|是|无|
|fmt|string|图片格式|是|"jpg"、"jpeg"、"png"|
|md5|string|图片文件的md5值|否|无|
|fid|string|云通讯提供的小文件存储返回后的id|3选1|后续文档说明|
|url|string|存放图片资源的url|3选1|无|
|content|string|图片二进制数据base64编码后的字符串|3选1|fid／url／content必须至少携带一个|

***msgType为3***  

|参数|类型|描述|是否一定携带|备注|
|:--:| :--: |:--|:--:|:--:|
|fmt|string|短音频格式|是|"amr"|
|md5|string|短音频文件的md5值|否|无|
|dur|int|短音频文件持续时间|否|无|
|fid|string|云通讯提供的小文件存储返回后的id|3选1|后续文档说明|
|url|string|存放短音频资源的url|3选1|无|
|content|string|短音频二进制数据base64编码后的字符串|3选1|fid／url／content必须至少携带一个|

***msgType为4***  

|参数|类型|描述|是否一定携带|备注|
|:--:| :--: |:--|:--:|:--:|
|fmt|string|短视频格式|是|"mp4"|
|md5|string|短视频文件的md5值|否|无|
|dur|int|短视频文件持续时间|否|无|
|fid|string|短视频文件上传云通讯小文件存储返回后的id|3选1|后续文档说明|
|url|string|存放短视频资源的url|3选1|无|
|content|string|短视频二进制数据base64编码后的字符串|3选1|fid／url／content必须至少携带一个|
|thumbFid|string|缩略图上传云通讯小文件存储返回后的id|3选1|后续文档说明|
|thumbUrl|string|存放缩略图资源的url|3选1|无|
|thumbContent|string|缩略图二进制数据base64编码后的字符串|3选1|thumbFid/thumbUrl/thumbContent必须至少携带一个|
|H|int|视频和缩略图的像素高度|是|无|
|W|int|视频和缩略图的像素宽度|是|无|

***msgType为5***

|参数|类型|描述|是否一定携带|
|:--:| :--: |:--|:--:|
|text|string|发送消息文本|是|

***msgType为6***

|参数|类型|描述|是否一定携带|备注|
|:--:| :--: |:--|:--:|:--:|
|fmt|string|短音频格式|是|"amr"|
|md5|string|短音频文件的md5值|否|无|
|dur|int|短音频文件持续时间|3选1|无|
|fid|string|云通讯提供的小文件存储返回后的id|3选1|后续文档说明|
|url|string|存放短音频资源的url|3选1|无|
|content|string|短音频二进制数据base64编码后的字符串|否|fid／url／content必须至少携带一个|

**返回消息协议**

|参数|类型|描述|是否一定携带|
|:--:| :--: |:--:|:--:|
|ret|int|错误码  详见模块7|是|
|sid|string|服务端返回的session id|是|
|detail|string|服务端返回的错误信息 ret=0时为空|否|

**示例1**  
请求：

	curl -XPOST -H "X-Appid: test" -H "X-Token: 123" http[s]://im.voicecloud.cn/v1/IM/push.do -d '
	{
		"from":"tom",
		"to":["jake","jones"],
		"objectType":2,
		"msgType":1,
		"msg":{
			"text":"hello"
		}
	}

响应：

	{
		"ret":0,
		"sid":"d3a12a9c-652b-4e94-95bb-50b27adadfaf",
		"detail":"Succeed"
	}
	
**示例2**  
请求(bash环境)：

	curl -XPOST -H "X-Appid: test" -H "X-Token: 123" http[s]://im.voicecloud.cn/v1/IM/push.do -d @- <<IMPROTOCOL
	{
		"from":"tom",
		"to":["gid10000"],
		"objectType":3,
		"msgType":4,
		"msg":{
			"fmt":"mp4",
			"content":"$(base64 test.mp4 | tr -d "\n")",
			"thumbContent":"$(base64 test.jpg | tr -d "\n")",
			"H":720,
			"W":480
		}
	}
	IMPROTOCOL

响应：

	{
		"ret":0,
		"sid":"d3a12a9c-652b-4e94-95bb-50b27adadfaf",
		"detail":"Succeed"
	}

## 5. 群组功能

**以下参数需要放在Http Request Header中**

|参数|类型|参数说明|
|----|----|----|
|X-Appid|string|应用ID，唯一标识一个应用|
|X-Token|string|app权限校验时使用|

### 5.1 创建群组

**请求说明**

	POST http://im.voicecloud.cn/v1/group/create.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

1.创建讨论组或者群组

2.创建讨论组或群组成功后会返回gid（唯一标识一个群组或者讨论组），需要保存，以便后续的加人及踢人等操作

**参数说明**
	
|参数|类型|必须|说明|
|----|----|----|----|
|owner|string|是|群主id（讨论组创建者id）|
|gname|string|是|群组（讨论组）名称|
|type|int|是|type值为0，表示创建的是讨论组，值为1创建的是群组，其他值将返回错误码|
|cMsgID|string|是|消息标识|

**curl请求示例**
	
`curl -XPOST http[s]://im.voicecloud.cn/v1/group/create.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"owner":"test","gname":"testGroup","type":0,"cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"gid"		:	"100001",
		"detail"	:	"create group successfully"
	}

**主要返回码**

0、30001,30005,30007,30012,31002,30007,

### 5.2 添加群组（讨论组）成员

**请求说明**

	POST http://im.voicecloud.cn/v1/group/add.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

1.批量向群组（讨论组）中添加新成员

2.调用此接口的用户须在该群组（讨论组）中

3.向讨论组中添加新成员时，被添加成员会立刻进入讨论组；向群组中添加成员时，会向被添加成员发送邀请信息，经过一系列验证后，方可进入群组

**参数说明**

|参数|类型|必须|说明|
|----|----|----|----|
|gid|string|是|群组（讨论组）id|
|uid|string|是|发送邀请的用户id|
|members|JsonArray|是|添加成员列表|
|type|int|是|type为0表示讨论组，为1表示群组，讨论组不需要被拉取人同意，群组需要验证|
|msg|string|否|发送邀请时附带的邀请信息|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/add.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","uid":"testUser","members":["aaa","bbb"],"type":0,"msg":"hello","cMsgID":"xxx"}`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"add members successfully"
	}

**主要返回码**

0、30001、30002、30003、30005、30007、30011、30013、30014、30015

### 5.3 加入群组

**请求说明**

	POST http://im.voicecloud.cn/v1/group/join.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

1.此接口仅适用于群组，可调用此接口请求加入一个群组
	
2.需要等待管理员或者群组的验证后方可进入群组

**参数说明**

|参数|类型|必须|说明|
|----|----|----|----|
|gid|string|是|群组id|
|uid|string|是|待加入群组的用户id|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/join.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","uid":"testUser","cMsgID":"xxx"}`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"wait mananger accept"
	}

**主要返回码**

0、30001,30005,30007,30008,30012,31001,31005,31003,31006,31012

### 5.4 退出群组

**请求说明**

	POST http://im.voicecloud.cn/v1/group/exit.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

1.主动退出群组（讨论组）

2.用户退出后，其他在群组（讨论组）内的成员会收到一条通知消息
	
**参数说明**

|参数|类型|必须|说明|
|----|----|-----|-----|
|gid|string|是|群组id|
|uid|string|是|要退群用户的uid|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/exit.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","uid":"testUser","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"exit group successfully"
	}
	
**主要返回码**

0、30001,30005,30007,30012,31001,31004,30008,

### 5.5 踢除群组（讨论组）成员

**请求说明**

	POST http://im.voicecloud.cn/v1/group/kick.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

1.讨论组中只有讨论组创建者有权限踢除成员

2.群组中管理员可以踢除普通成员，但是不能踢除其他管理员，群主拥有最高权限，可踢除所有人

**参数说明**

|参数|类型|必须|说明|
|----|----|----|----|
|gid|string|是|群组id|
|members|JsonArray|是|待剔除的成员名单|
|uid|string|是|调用该接口的用户id，如果是讨论组，只有讨论组创建者才有权限调用该接口，如果选用其他uid则会返回对应的错误码，如果是群组，只有管理员和群主有权限调用该接口，填写其他的uid会返回对应的错误码|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/kick.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","members":["aaa","bbb"],"uid":"testUser","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"kick members successfully"
	}

**主要返回码**

0、30001,30005,30007,30008,30012,31001,31004,31005

### 5.6 解散群组

**请求说明**

	POST http://im.voicecloud.cn/v1/group/remove.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

此接口为解散群组接口，服务端会删除该群组的信息

**参数说明**

|参数|类型|必须|说明|
|----|----|----|----|
|gid|string|是|群组id|
|uid|string|是|发送该请求的用户，只有群主有权限，其他用户发送会报错|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/remove.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","uid":"testUser","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"remove group successfully"
	}

**主要返回码**

0、30001,30005,30007,30009,30012,31001,31005

### 5.7 获取群组信息

**请求说明**

	POST http://im.voicecloud.cn/v1/group/getinfo.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

此接口用户获取群组的基本信息，如群主，群大小，群成员列表等信息

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|gid|string|是|群组id|
|uid|string|是|用户id|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/getinfo.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","uid":"testUser","cMsgID":"xxx"}'`
 
**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"get groupinfo successfully",
		"info"		:	{
							"appid"			:	"574faab4"
							"gname"			:	"UserDemo",
							"gid"			:	"10000",
							"members"		:	["Jack","Tom","Amy"],
							"type"			:	0,
							"owner"			:	"admin",
							"managers"		:	["Jack","Tom"]
							"maxusers"		:	200
							"announcement"	:	"IM"
							"describe"		:	"this is a test group"
						}
	}


**主要返回码**

0、30001,30005,30007,30012,31001,31005

### 5.8 任命管理员

**请求说明**

	POST http://im.voicecloud.cn/v1/group/appointmgr.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

用户任命管理员，只有群主有这样的权限，其他用户操作会返回错误码

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|gid|string|是|群组id|
|members|JsonArray|是|待添加为管理员的名单，如 {"members":["aaa","bbb"]}|
|uid|string|是|调用该接口的用户id，id必须是群主，其他用户id会报错|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST -http[s]://im.voicecloud.cn/v1/group/appiontmgr.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","members":["aaa","bbb"],"uid":"testUser","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"Appoint managers successfully"
	}

**主要返回码**

0、30001,30005,30007,30012,31001,31004,31005,31006,31013

### 5.9 移除管理员

**请求说明**

	POST http://im.voicecloud.cn/v1/group/revokemgr.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

移除管理员，只有群主有该权限

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|gid|string|是|群组id|
|members|JsonArray|是|待解除的管理员列表，如{"members":["aaa","bbb"]}|
|uid|string|是|调用该接口的用户id，须是群组，其他用户id会返回错误码|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/revokemgr.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","members":["aaa","bbb"],"uid":"testUser","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		: 0,
		"detail"	: "revoke managers successfully"
	}

**主要返回码**

0、30001,30005,30007,30008,30012,31001,31004,31005,31006

### 5.10 获取群组列表

**请求说明**

	POST http://im.voicecloud.cn/v1/group/getlist.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

获取一个用户加入的群组及讨论列表，返回基本的群组（讨论组）信息

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|uid|string|是|要查询用户的id|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/getlist.do -H "X-Appid:574faab4" "X-Token:12345689" -d '{"uid":"userTest","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"grouplist"	:	[
							{"owner":"admin","gname":"test_1","maxusers":200,"gid":"10001","size":6,"type":0},
							{"owner":"admin","gname":"test_2","maxusers":200,"gid":"10002","size":2,"type":1},
							{"owner":"admin","gname":"test_3","maxusers":200,"gid":"10003","size":2,"type":1}
						]
	}

**主要返回码**

0、30001,30005,30007,30012,31001

### 5.11 群组验证

**请求说明**

	POST http://im.voicecloud.cn/v1/group/verify.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

1.该接口是群组验证接口

2.当邀请人添加被邀请人入群时，需要被邀请人验证（同意或拒绝）;当邀请人不是管理员时，被邀请人同意入群邀请后，需要管理员或群主的验证通过后方可入群；用户主动加群时，需要管理员或是群主的验证通过方可入群

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|type|int|必须|验证类型，1(被邀请人对入群邀请的验证),2(管理员或群主对入群邀请的验证),3(管理员或群主对入群申请的验证)|
|oper|int|必须|操作类型 同意（0）拒绝（1）|
|uid|string|必须|调用该接口的用户id|
|gid|string|必须|群组id|
|inviter|string|由type值确定|取值1和2时须携带该字段，邀请人id|
|invitee|string|由type值确定|取值1和2时须携带该字段，被邀请人id|
|applicant|string|由type值确定|取值3时须携带该字段，申请人id|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/verify.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"type":1,"oper":0,"uid":"test1","gid":"100001","inviter":"test3","invitee":"test4","cMsgID":"xxx"}'`


**返回说明**

	{
		"ret"	:	0
	}

**主要返回码**

0、30001,30005,30007,30008,30012,31001,31005,31006,31008,31009,

### 5.12 编辑群组资料

**请求说明**

	POST http://im.voicecloud.cn/v1/group/edit.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

1.该接口用户编辑群组（讨论组）资料

2.只有管理员或群主有权限编辑群资料

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|gid|string|是|群组id|
|gname|string|否|新的群组名称|
|uid|string|是|调用接口的用户id|
|announcement|string|否|群公告|
|describe|string|否|群描述|
|icon|string|否|群头像url|
|custom|string|否|自定义属性，数据格式可为JSON，也可以是其他数据格式|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/edit.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","gname":"testGroup","uid":"testUser","announcement":"work plan","describe":"this is a test","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"update groupinfo successfully"
	}

**主要返回码**

0、30001,30005,30007,30008,30012,31001,31005

### 5.13 移交群主

**请求说明**

	POST http://im.voicecloud.cn/v1/group/transfer.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

该接口用户移交群主给群组中的其他人

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|gid|string|是|群组id|
|uid|string|是|调用接口的用户，须是群主，其他id会报错|
|newowner|string|是|新群主id|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/transfer.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"gid":"100001","uid":"oldOwner","newowner":"newOwner","cMsgID":"xxx"}'`

**返回说明**

	{
		"ret"		:	0,
		"detail"	:	"transfer owner successfully"
	}

**主要返回码**

0、30001,30005,30007,30008,30012,31001,31005,31006

### 5.14 群组搜索

**请求说明**

	POST http://im.voicecloud.cn/v1/group/search.do HTTP/1.1
	Content-Type:"application/json; charset=utf-8"

**接口描述**

该接口用于群组查找，返回一切匹配或包含查找条件的群组

**参数说明**

|参数|类型|必须|参数说明|
|----|----|----|----|
|condition|string|是|查找条件|
|cMsgID|string|是|消息标识|

**curl请求示例**

`curl -XPOST http[s]://im.voicecloud.cn/v1/group/search.do -H "X-Appid:574faab4" -H "X-Token:12345689" -d '{"condition":"test","cMsgID":"xxx"}'`

**返回说明**

	{	
		"ret"		:	0,
		"grouplist"	:	[
							{"owner":"admin","gname":"test_1","maxusers":200,"gid":"10001","size":6,"type":0},
							{"owner":"admin","gname":"test_2","maxusers":200,"gid":"10002","size":2,"type":1},
							{"owner":"admin","gname":"test_3","maxusers":200,"gid":"10003","size":2,"type:1"}
						]
	}

**主要返回码**

0、30001,30005,30006,30007,30012,31007


## 6. 历史记录

### 6.1 说明
消息历史记录默认在服务端保存7天，如需保存更长时间请联系商务。

### 6.2 查询接口
尚未开放。

## 7. 语音视频通话

即将支持！

## 8. 错误码

|错误码|错误描述|
|:--:|:--|
|0|成功|
|30001|token非法|
|30002|json格式非法|
|30003|缺少相关字段或参数非法|
|30004|后台无更多数据|
|30005|读取http body错误，服务端解析http协议时产生|
|30006~30016|服务内部错误|
|31001|群组不存在，需检查群组id是否正确;用户信息不存在，查看用户信息是否导入|
|31002|创建群组id出错|
|31003|调用加入群组接口时，待加入的用户已经存在于群组会报该错误|
|31004|请求用户不在群组或不是管理员|
|31005|权限错误，调用接口的用户没有权限会报该错误|
|31006|群组类型错误|
|31007|没有查到群组|
|31008|用户状态非法|
|31009~31011|服务内部错误|
|31012|修改用户状态失败|
|31013|已为管理员|
|31014|用户已经导入|
|31015|更新用户信息失败|
|31016|删除用户信息失败|
|31017|用户未导入|


## 9. 高级特性

### 9.1 在线状态查询

功能已支持，接口尚未开放；

### 9.2 敏感词过滤

即将支持。

### 9.3 后处理功能

已支持合成、转写。
