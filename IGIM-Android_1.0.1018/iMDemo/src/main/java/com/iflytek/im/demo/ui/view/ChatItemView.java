package com.iflytek.im.demo.ui.view;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.iflytek.im.demo.R;


/**
 * Created by Administrator on 2016/9/12.
 */
public class ChatItemView {
    public TextView time,connectPerson,friendTextContent,myTextContent,friendVoiceDur,myVoiceDur,tipMsgContent, otherSideHint;
    public ImageView friendIcon,friendImgContent,myIcon,myImgContent,sendMsgHint,friendVideoPlayPic,myVideoPlayPic;
    public VideoView friendVideoContent,myVideoContent;
    public FrameLayout myVideoFL, friendVideoFL;
    public LinearLayout friendContainer,myContainer,friendTxtImgContainer,myTxtImgContainer;

   public ChatItemView(View convertView){
       time = (TextView) convertView.findViewById(R.id.chat_time);
       connectPerson = (TextView) convertView.findViewById(R.id.name_from);
       friendTextContent = (TextView) convertView.findViewById(R.id.chatfrom_txt_content);
       myTextContent = (TextView) convertView.findViewById(R.id.chatto_text_content);
       friendVoiceDur = (TextView) convertView.findViewById(R.id.chatfrom_voice_dur);
       myVoiceDur = (TextView) convertView.findViewById(R.id.chatto_voice_dur);
       tipMsgContent = (TextView) convertView.findViewById(R.id.tip_msg_content);
       otherSideHint = (TextView) convertView.findViewById(R.id.other_side_hint);

       friendIcon = (ImageView) convertView.findViewById(R.id.chatfrom_icon);
       friendImgContent = (ImageView) convertView.findViewById(R.id.chatfrom_img_content);
       myIcon = (ImageView) convertView.findViewById(R.id.chatto_icon);
       myImgContent =(ImageView) convertView.findViewById(R.id.chatto_img_content);
       sendMsgHint =(ImageView) convertView.findViewById(R.id.send_msg_hint);
       myVideoPlayPic = (ImageView) convertView.findViewById(R.id.chatto_video_play_content);
       friendVideoPlayPic = (ImageView) convertView.findViewById(R.id.chatfrom_video_play_content);

       friendVideoContent = (VideoView) convertView.findViewById(R.id.chatfrom_video_content);
       myVideoContent = (VideoView) convertView.findViewById(R.id.chatto_video_content);

       myVideoFL = (FrameLayout) convertView.findViewById(R.id.to_video_Fl);
       friendVideoFL = (FrameLayout) convertView.findViewById(R.id.from_video_Fl);

       myContainer = (LinearLayout) convertView.findViewById(R.id.chat_to_container);
       friendContainer = (LinearLayout) convertView.findViewById(R.id.chat_from_container);
       friendTxtImgContainer = (LinearLayout)convertView.findViewById(R.id.chatfrom_txt_img_container);
       myTxtImgContainer = (LinearLayout) convertView.findViewById(R.id.chatto_txt_img_container);


       friendTextContent.setVisibility(View.GONE);
       myTextContent.setVisibility(View.GONE);
       friendImgContent.setVisibility(View.GONE);
       myImgContent.setVisibility(View.GONE);
       myVideoFL.setVisibility(View.GONE);
       friendVideoFL.setVisibility(View.GONE);
       friendVoiceDur.setVisibility(View.GONE);
       myVoiceDur.setVisibility(View.GONE);
       friendVideoPlayPic.setVisibility(View.GONE);
       myVideoPlayPic.setVisibility(View.GONE);
       tipMsgContent.setVisibility(View.GONE);

   }



}
