package com.iflytek.im.demo.bean;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.core.constant.FileConstant;
import com.iflytek.cloud.im.core.util.StringUtil;
import com.iflytek.cloud.im.entity.msg.AudioMsg;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.ImageMsg;
import com.iflytek.cloud.im.entity.msg.MessageConstant;
import com.iflytek.cloud.im.entity.msg.PostRltText;
import com.iflytek.cloud.im.entity.msg.PostRltVoice;
import com.iflytek.cloud.im.entity.msg.TextMsg;
import com.iflytek.cloud.im.entity.msg.TipMsg;
import com.iflytek.cloud.im.entity.msg.VideoMsg;
import com.iflytek.im.demo.common.MsgParseUtil;
import com.iflytek.im.demo.common.imageUtil.BitmapUtils;

import org.json.JSONException;

import java.io.Serializable;


public class ChatInfo implements Serializable {

	private final String TAG = "ChatInfo";
	private static final long serialVersionUID = -6240488099748291325L;
	private Object content;
	private long time;
	private int isSend;// 0 是收到的消息，1是发送的消息
	private String nameFrom;
	private String fileName;
//	private String fileUrl;
	private int msgType;
	private String fileFid;// 这个字段后面不需要
	private String msgID;
	private int sendStatus; //0，未发送/发送失败  1，发送中  2，已发送
	private int duration;
	private int height;
	private int width;
	private int postType;
    private String thumbnailFid;
    private String thumbnailName;
	private int otherSideRead;

    private CommonMsgContent msg;

	public ChatInfo() {
	}

	
	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getIsSend() {
		return isSend;
	}

	public void setIsSend(int isFrom) {
		this.isSend = isFrom;
	}

	public String getNameFrom() {
		return nameFrom;
	}

	public void setNameFrom(String nameFrom) {
		this.nameFrom = nameFrom;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

//	public String getFileUrl() {
//		return fileUrl;
//	}
//
//	public void setFileUrl(String fileUrl) {
//		this.fileUrl = fileUrl;
//	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getFileFid() {
		return fileFid;
	}

	public void setFileFid(String fileFid) {
		this.fileFid = fileFid;
	}
	
	public String getMsgID() {
		return msgID;
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public int getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getPostType() {
		return postType;
	}

	public void setPostType(int postType) {
		this.postType = postType;
	}

    public String getThumbnailFid() {
        return thumbnailFid;
    }

    public void setThumbnailFid(String thumbnailFid) {
        this.thumbnailFid = thumbnailFid;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

	public int getOtherSideRead() {
		return otherSideRead;
	}

	public void setOtherSideRead(int otherSideRead) {
		this.otherSideRead = otherSideRead;
	}

    public CommonMsgContent getMsg() {
        return msg;
    }

    public void setMsg(CommonMsgContent msg) {
        this.msg = msg;
    }

    public ChatInfo(CommonMsgContent msg ) throws JSONException {
        this.msg = msg;
		String sender = msg.getSender();
		this.time = msg.getSendTime() * 1000;
		this.nameFrom = sender;
		this.msgID = msg.getCMsgID();
		this.sendStatus = msg.getState();
		this.otherSideRead = msg.getOtherSideRead();
		if(sender.equals(IMClient.getInstance().getCurrentUser())){
			// 发送过去的消息
			this.isSend = 1;
		}else{
			// 接收到的发送过来的消息
			this.isSend = 0;
		}

		switch (msg.getMsgType()) {
		case MessageConstant.MESSAGE_CONTENT_MSG_TYPE_TEXT:
			TextMsg textMsg = IMClient.getInstance().parse2TextMsg(msg);
			String msgContent = MsgParseUtil.getTextMsgBody(textMsg.getContent());
			this.content = msgContent;

			if (msg.getPostType() == 0) {
				// 普通文本
				this.fileName = null;
//				this.fileUrl = null;
				this.fileFid = null;
				this.msgType = 0;
			} else if (msg.getPostType() == 1) {
				//转写为语音
				final PostRltVoice postRltVoice = IMClient.getInstance().parse2PostVoiceRlt(msg);
				if(postRltVoice != null){
					this.fileFid = postRltVoice.getPostRlt_fid();
					this.fileName = TextUtils.isEmpty(postRltVoice.getLocalName()) ? StringUtil.MD5(fileFid) : postRltVoice.getLocalName() ;
					this.msgType = msg.getSender().equals(IMClient.getInstance().getCurrentUser())?0:2;
					this.postType = msg.getPostType();
				}else{
					this.fileName = null;
					this.fileFid = null;
					this.msgType = 0;
				}
			}
			break;
		case MessageConstant.MESSAGE_CONTENT_MSG_TYPE_IMG:
			this.msgType = 1;
			ImageMsg imageMsg = IMClient.getInstance().parse2ImageMsg(msg);
			String imgName = imageMsg.getName();
//			String imgSuffix = "." + imageMsg.getFormat();
			String imagePath = FileConstant.getPathImg() + "/"+ imgName;
			Bitmap bitmap = BitmapUtils.readBitmapFromFile(imagePath);
			this.content = bitmap;
			this.fileName = imgName;
			this.fileFid = imageMsg.getFid();
			break;
		case MessageConstant.MESSAGE_CONTENT_MSG_TYPE_AUDIO:
			AudioMsg audioMsg = IMClient.getInstance().parse2AudioMsg(msg);
			this.fileFid = audioMsg.getFid();
            this.fileName = audioMsg.getName();
            Log.d(TAG, "ChatInfo: filename:" + audioMsg.getName());
            if(TextUtils.isEmpty(fileName)){
                this.fileName = StringUtil.Md5(fileFid);
            }
			this.duration = audioMsg.getDuration();

			if(msg.getPostType() == 0){
				//纯语音信息
				this.content = null;
				this.msgType = 2;
			}else{
				this.msgType = msg.getSender().equals(IMClient.getInstance().getCurrentUser())?2:0;
				if(msg.getPostRlt() != null){
					PostRltText postRltText = IMClient.getInstance().parse2PostTextRlt(msg);
					this.content = postRltText.getText();
				}else{
					this.content = null;
				}
				this.postType = msg.getPostType();
			}
			break;

		case MessageConstant.MESSAGE_CONTENT_MSG_TYPE_VIDEO:
			this.msgType = 3;
			final VideoMsg videoMsg = IMClient.getInstance().parse2VideoMsg(msg);
			this.fileName = videoMsg.getName();
			this.fileFid = videoMsg.getFid();
			this.content = BitmapUtils.readBitmapFromFile(FileConstant.getPathVideo() + "/" +videoMsg.getThumbnailName());
			this.duration = videoMsg.getDuration();
            this.thumbnailFid = videoMsg.getThumbnailFid();
			this.height = videoMsg.getHeight();
			this.width = videoMsg.getWidth();
            this.thumbnailName = videoMsg.getThumbnailName();
		default:
			break;
		}

	}


	public ChatInfo(TipMsg tipMsg) {
		this.nameFrom = tipMsg.getSender();
		this.time = tipMsg.getSendTime()*1000;
		this.msgID = tipMsg.getCMsgID();
		this.msgType = tipMsg.getTipType();
        this.content = tipMsg.getExContent();
    }

	@Override
	public String toString() {
		return "ChatInfo{" +
				"TAG='" + TAG + '\'' +
				", content=" + content +
				", time=" + time +
				", isSend=" + isSend +
				", nameFrom='" + nameFrom + '\'' +
				", fileName='" + fileName + '\'' +
//				", fileUrl='" + fileUrl + '\'' +
				", msgType=" + msgType +
				", fileFid='" + fileFid + '\'' +
				", msgID='" + msgID + '\'' +
				", sendStatus=" + sendStatus +
				'}';
	}
}
