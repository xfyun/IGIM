package com.iflytek.im.demo;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.iflytek.cloud.im.entity.msg.MessageConstant.MESSAGE_CONTENT_SEND_DEFAULT;

/**
 * Created by imxqd on 2016/8/25.
 * 常量类
 */
public class Constants {

    private Constants() {
        throw new UnsupportedOperationException("You can't fuck it.");
    }


    public static class Id {
        public static final String IM_APPID             = "";
        public static final String IM_TOKEN             = "";
    }

    public static class AttachmentType {
        public static final int COLLECTION = 0;
        public static final int SMALL_VIDEO = 1;
        public static final int LOCATION = 2;
        public static final int PICTURE = 3;
        public static final int CHAT_WITH_VIDEO = 4;
        public static final int CHAT_WITH_VOICE = 5;
        public static final int VOICE_TO_TEXT = 6;

        @IntDef({
                COLLECTION,
                SMALL_VIDEO,
                LOCATION,
                PICTURE,
                CHAT_WITH_VIDEO,
                CHAT_WITH_VOICE,
                VOICE_TO_TEXT
        })
        @Retention(RetentionPolicy.SOURCE)
        public @interface Value {}
    }

    public static class Action {
        public static final String MESSAGE_RECEIVER     = "android.intent.action.MessageReceiver";
        public static final String CONNECTIVITY_CHANGE  = "android.net.conn.CONNECTIVITY_CHANGE";
    }

    public static class Parameter {
        public static final String KEY_RECEIVER_ID      = "receiverID";
        public static final String KEY_RECEIVER_NAME    = "receiverName";
        public static final String KEY_CONVERSATION_TYPE = "conversationType";
        public static final String KEY_IS_NOTIFICATION = "isNotification";
        public static final String KEY_IS_SHARE = "isShare";

        public static final String KEY_VIDEO_DURATION   = "video_duration";
        public static final String KEY_VIDEO_PATH       = "video_path";
        public static final String KEY_VIDEO_NAME       = "video_name";
        public static final String KEY_VIDEO_WIDTH      = "video_width";
        public static final String KEY_VIDEO_HEIGHT     = "video_height";
        public static final String KEY_VIDEO_THUMBNAIL  = "video_thumbnail";

        public static final String KEY_IMAGE_URL        = "imageUrl";
        public static final String KEY_IMAGE_MESSAGE    = "imgMessage";
        public static final String KEY_IMAGE_NAME       = "imgName";
        public static final String KEY_IMAGE_FILE_ID    = "imgFid";

        public static final String KEY_TEXT = "text";

        public static final String KEY_MSG              = "MSG";
    }

    public static class Storage {


        public static final String SPLICER = ".";
        public static final String SUFFIX_JPG = ".jpg";
        public static final String SUFFIX_JPEG = ".jpeg";
        public static final String SUFFIX_PNG = ".png";
        public static final String SUFFIX_WAV = ".wav";
    }

    public static class Message {

        public static final String IMAGE_DATA   = "[图片]";
        public static final String AUDIO_DATA   = "[语音]";
        public static final String VIDEO_DATA   = "[小视频]";

        public static final String LAST_TIME    = "lastTime";
        public static final String TITLE        = "title";
        public static final String LAST_MSG     = "lastMsg";
        public static final String NOT_READ_NUM = "not_read_num";
    }

    public static class Preference {
        public static final String NAME_ACCOUNT = "account";
        public static final String KEY_UID      = "uid";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_EMPTY_STRING = "";
        public static final int KEY_EMPTY_INT = 0;
        public static final long KEY_EMPTY_LONG = 0;
        public static final boolean KEY_EMPTY_BOOLEAN = true;
        public static final int KEY_EMPTY_FLOAT = 0;

    }

    public static class Format {
        public static final String SIMPLE_DATE_FORMAT = "MM-dd HH:mm";

    }

    public static class Event {
        public static final String GET_CONVERSATIONS_SUCCESS = "get_conversations_success";
        public static final String NEW_MESSAGE_IN = "new_message_in";
        public static final String ON_NETWORK_STATE_CHANGED = "on_network_state_changed";
        public static final String SYNC_MESSAGE_SUCCESS = "sync_message_success";
        public static final String CREATE_GROUP_SUCCESS = "create_group_success";
        public static final String CREATE_DIS_GROUP_SUCCESS = "create_dis_group_success";
        public static final String GET_GROUP_LIST = "get_group_list_success";
    }

    public static class Url {
        public static final String LOGIN        = "https://im.voicecloud.cn:444/v1/IM/user/login.do";
        public static final String REGISTER     = "https://im.voicecloud.cn:444/v1/IM/user/register.do";
        public static final String GET_USERS    = "http://im.voicecloud.cn:1208/v1/IM/user/getUsers.do";
        public static final String GET_TOKEN    = "http://im.voicecloud.cn:1208/v1/IM/user/getUserToken.do";


        public static final String BASE_CLOUD_STORAGE = "http://60.166.12.151:1208/rest/v1/file/";
        public static final String STITCH_TOKEN       = "?token=";
        public static final String STITCH_APPID       = "&appid=";
    }

    public static class RequestCodeAndResultCode{
        public static final int PICTURE_REQUESTCODE = 21;
        public static final int PICTURE_RESULTCODE = 22;
        public static final int SMALL_VIDEO_REQUESTCODE = 31;
        public static final int SMALL_VIDEO_RESULTCODE = 32;
        public static final int EXIT_GROUP_REQUESTCODE = 41;
        public static final int EXIT_GROUP_RESULTCODE = 42;
        public static final int ADD_GROUPMANAGER_REQUESTCODE = 51;
        public static final int ADD_GROUPMANAGER_RESULTCODE = 52;

    }



    public static class GroupMessageContent{
        public static final int MESSAGE_CMD_GRP_OPERINVITE = 1;
        public static final int MESSAGE_CMD_GRP_OPERJOIN = 2;
        public static final int MESSAGE_CMD_GRP_OPERVERIFY = 3;
        public static final int MESSAGE_CMD_GRP_OPERINVITE_RESP = 4;
        public static final int MESSAGE_CMD_GRP_OPERJOIN_RESP =5;
        public static final int MESSAGE_CMD_GRP_NOTYFY_GRP_EXIT = 6;
        public static final int MESSAGE_CMD_GRP_NOTYFY_GRP_KICK = 7;
        public static final int MESSAGE_CMD_GRP_OPER_INVITE_VERIFY_RESP = 8;
        public static final int MESSAGE_CMD_GRP_NOTYFY_GRP_APPOINT_MGR = 9;
        public static final int MESSAGE_CMD_GRP_NOTYFY_GRP_REVOKE_MGR = 10;
        public static final int MESSAGE_CMD_GRP_NOTYFY_TRANSFER_OWNER = 11;
        public static final int MESSAGE_CMD_GRP_NOTYFY_REMOVE_GROUP = 12;
        public static final int MESSAGE_CMD_GRP_OPERATE_MANAGER = 13;
        public static final int MESSAGE_CMD_GRP_NOTIFY_NEW_OWNER = 14;
    }

    public static class OperateGroupManager {
        public static final int APPOINT_MANAGER_TYPE = 1;
        public static final int REMOVE_MANAGER_TYPE = 2;

    }

    public static class GroupType{
        public static final int GROUP = 1;
        public static final int SMALLGROUP = 0;
    }

    public static class GroupMsgStatus{
        //群组通知消息已处理/未处理
        public static final int MESSAGE_CONTENT_UNHANDLED = 0;
        public static final int MESSAGE_CONTENT_HANDLED = 1;
        public static final int MESSAGE_CONTENT_INVALID = 2;
        public static final int MESSAGE_CONTENT_REFUSED = 3;
    }

    public static class LoginErrorCode{
        public static final String KEY_LOGIN_ERRORCODE = "errorcode";

        public static final int OTHER_ERROR = 1;
        public static final int LOGIN_INFO_ERROR = 2;
        public static final int LOGIN_SOMENE_ONLINE = 3;
        public static final int NETWORK_ERROR = 4;
    }

    public static class Oper{
        public static final int AGREE = 0;
        public static final int REFUSE = 1;
    }

    public static class SendStatus{
        public static final int UNSEND = 0;
        public static final int SENDING = 1;
        public static final int SENDED = 2;
        public static final int SEND_DEFAULT = 3;
    }

    public static class OtherSideRead {
        public static final int UNREAD = 0;
        public static final int READED = 1;
    }

    public static class TokenParams {
        public static final String UID = "uid";
        public static final String APPID = "appid";
        public static final String RET = "ret";
        public static final String TOKEN = "token";
    }
}
