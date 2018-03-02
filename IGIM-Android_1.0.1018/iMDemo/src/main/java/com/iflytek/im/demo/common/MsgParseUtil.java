package com.iflytek.im.demo.common;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.core.util.FileUtil;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupAppointMgrMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupExitMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupMsgContent;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupNewOwnerNotifyMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupNotifyKickMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOpMgrMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOperInviteMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOperInviteRespMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOperInviteVerifyRespMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOperJoinMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOperJoinRespMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupOperVerifyMsg;
import com.iflytek.cloud.im.entity.msg.GroupNotifyMsg.GroupTransferOwnerMsg;
import com.iflytek.cloud.im.entity.msg.TextMsg;
import com.iflytek.cloud.im.entity.msg.TipMsg;
import com.iflytek.im.demo.Config;
import com.iflytek.im.demo.Constants;

import org.json.JSONException;

import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTIFY_NEW_OWNER;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_GRP_APPOINT_MGR;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_GRP_EXIT;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_GRP_KICK;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_GRP_REVOKE_MGR;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_REMOVE_GROUP;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_NOTYFY_TRANSFER_OWNER;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERATE_MANAGER;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERINVITE;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERINVITE_RESP;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERJOIN;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERJOIN_RESP;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPERVERIFY;
import static com.iflytek.im.demo.Constants.GroupMessageContent.MESSAGE_CMD_GRP_OPER_INVITE_VERIFY_RESP;
import static com.iflytek.im.demo.Constants.Oper.AGREE;
import static com.iflytek.im.demo.Constants.OperateGroupManager.APPOINT_MANAGER_TYPE;
import static com.iflytek.im.demo.Constants.OperateGroupManager.REMOVE_MANAGER_TYPE;

public class MsgParseUtil {
	private static final int MSG_BEGIN_INDEX = 2;
	private static final int MSG_END_INDEX = 2;
	private static final String MSG_SEPARATOR = ":";
	public static final String REGEX = "\\/";
	public static final String REPLACEMENT = "/";

	public static String getTextMsgBody(String content) {
		int end = content.length() - MSG_END_INDEX;
		int start = content.indexOf(MSG_SEPARATOR) + MSG_BEGIN_INDEX;
//		String  msgString = content.substring(start, end);
		String msgString = content;
		String replacedMsgString = "";
		if(msgString.contains(REGEX)){
			replacedMsgString = msgString.replace(REGEX, REPLACEMENT);
			return replacedMsgString;
		}else{
			return msgString;
		}
	}

	public static String getCommonMsgContent(CommonMsgContent content) {
		switch (content.getMsgType()) {
			case 0:
				TextMsg lastTextMsg = null;
                try {
					lastTextMsg = IMClient.getInstance().parse2TextMsg(content);
				} catch (JSONException e) {
					if (Config.isDebug) {
						e.printStackTrace();
						FileUtil.printExpectionLog(e);
					}
					return "[异常]";
				}
				return MsgParseUtil.getTextMsgBody(lastTextMsg.getContent());
			case 1:
				return Constants.Message.IMAGE_DATA;
			case 2:
				return Constants.Message.AUDIO_DATA;
			case 3:
				return Constants.Message.VIDEO_DATA;
			default:
				return "";
		}
	}

	public static String getTipMsgContent(TipMsg msg){
		return (String)msg.getExContent();

	}

	public static String getGroupMsgContent(GroupMsgContent groupMsgContent){
		// TODO 完善各个消息的反馈。
		String gid = groupMsgContent.getGid();

		String groupName =  IMClient.getInstance().getGroupNameByGid(gid);
		if(groupName == null) {
			groupName = gid;
		}
		switch (groupMsgContent.getgMsgType()){
			case MESSAGE_CMD_GRP_OPERINVITE:
                GroupOperInviteMsg groupOperInviteMsg = (GroupOperInviteMsg) groupMsgContent;
				return groupOperInviteMsg.getInviter() + " 邀请你加入 " + groupName;
			case MESSAGE_CMD_GRP_OPERJOIN:
                GroupOperJoinMsg groupOperJoinMsg = (GroupOperJoinMsg) groupMsgContent;
				return groupOperJoinMsg.getApplicant() + " 申请加入 "+ groupName;
			case MESSAGE_CMD_GRP_OPERVERIFY:
                GroupOperVerifyMsg groupOperVerifyMsg = (GroupOperVerifyMsg) groupMsgContent;
				return groupOperVerifyMsg.getInviter() + " 邀请 " +groupOperVerifyMsg.getInvitee() +" 加入 " + groupName;
			case MESSAGE_CMD_GRP_OPERINVITE_RESP:
				GroupOperInviteRespMsg groupOperInviteRespMsg = (GroupOperInviteRespMsg) groupMsgContent;
				if(groupOperInviteRespMsg.getOper() == AGREE ){
					return "您邀请的 " + groupOperInviteRespMsg.getInvitee() + " 同意加入群 " + groupName ;
				}else{
					return "您邀请的 " + groupOperInviteRespMsg.getInvitee() + " 拒绝加入群 " + groupName ;
				}
			case MESSAGE_CMD_GRP_OPERJOIN_RESP:
				GroupOperJoinRespMsg groupOperJoinRespMsg = (GroupOperJoinRespMsg) groupMsgContent;
				if(groupOperJoinRespMsg.getOper() == AGREE ){
					return "您已被同意加入 " + groupName;
				}else{
					return "对不起，您已被拒绝加入 " + groupName;
				}
			case MESSAGE_CMD_GRP_NOTYFY_GRP_EXIT:
                GroupExitMsg groupExitMsg = (GroupExitMsg) groupMsgContent;
				return groupExitMsg.getExitMember() +" 退出群 " +groupName;
			case MESSAGE_CMD_GRP_NOTYFY_GRP_KICK:
                GroupNotifyKickMsg groupNotifyKickMsg = (GroupNotifyKickMsg) groupMsgContent;
                if(groupNotifyKickMsg.getKicked() != null
                        && groupNotifyKickMsg.getKicked().equals(IMClient.getInstance().getCurrentUser())){
                    return "您从群 " + groupName + " 中被移除";
                }else{
                    return groupNotifyKickMsg.getKicked() + " 被管理员踢出群组";
                }
			case MESSAGE_CMD_GRP_OPER_INVITE_VERIFY_RESP:
                GroupOperInviteVerifyRespMsg groupOperInviteVerifyRespMsg = (GroupOperInviteVerifyRespMsg) groupMsgContent;
                if(groupOperInviteVerifyRespMsg.getVerifyType() == AGREE ){
                    return  "您被同意加入群 " + groupName;
                }else{
                    return  "您被拒绝加入群 " + groupName;
                }
			case MESSAGE_CMD_GRP_NOTYFY_GRP_APPOINT_MGR:
                GroupAppointMgrMsg groupAppointMgrMsg = (GroupAppointMgrMsg) groupMsgContent;
                return "您被 " + groupAppointMgrMsg.getOperator() +" 提升为群 " + groupName + " 的管理员";
			case MESSAGE_CMD_GRP_NOTYFY_GRP_REVOKE_MGR:
				return "您在群 " + groupName + " 中的管理员身份被移除";
			case MESSAGE_CMD_GRP_NOTYFY_TRANSFER_OWNER:
                GroupTransferOwnerMsg groupTransferOwnerMsg = (GroupTransferOwnerMsg) groupMsgContent;
				return "您被" + groupTransferOwnerMsg.getOldOwner() +"委任为群 " + groupName +" 的群主";
			case MESSAGE_CMD_GRP_NOTYFY_REMOVE_GROUP:
				return "群 "+groupName+" 已被解散";
			case MESSAGE_CMD_GRP_OPERATE_MANAGER:
				GroupOpMgrMsg opMgrMsg = (GroupOpMgrMsg) groupMsgContent;
				//type==1 任命  ==2是解除
				if(opMgrMsg.getType() == APPOINT_MANAGER_TYPE) {
					return opMgrMsg.getModifyManager() + " 被任命为管理员";
				}else if(opMgrMsg.getType() == REMOVE_MANAGER_TYPE) {
					return opMgrMsg.getModifyManager() + " 被移除管理员权限";
				}
				break;
			case MESSAGE_CMD_GRP_NOTIFY_NEW_OWNER:
				GroupNewOwnerNotifyMsg groupNewOwnerNotifyMsg = (GroupNewOwnerNotifyMsg) groupMsgContent;
				return groupNewOwnerNotifyMsg.getOldOwner() + " 将群主之位交给了 " + groupNewOwnerNotifyMsg.getNewOwner();
			default:
				break;
		}
		return "other";
	}
}

