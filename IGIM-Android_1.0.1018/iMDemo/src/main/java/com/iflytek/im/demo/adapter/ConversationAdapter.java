package com.iflytek.im.demo.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.ExpressionController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConversationAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Map<String, Object>> mConversations = new ArrayList<>();

    public ConversationAdapter(Context context, List<Map<String, Object>> conversations) {
        this.mInflater = LayoutInflater.from(context);
        this.mConversations = conversations;
        mContext = context;
    }

    public int getCount() {
        return mConversations.size();
    }

    public Object getItem(int position) {
        return mConversations.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.conversation_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        String lastTime = (String) mConversations.get(position).get(Constants.Message.LAST_TIME);
        holder.lastTimeTv.setText(lastTime);
        String title = (String) mConversations.get(position).get(Constants.Message.TITLE);
        holder.titleTv.setText(title);
        String lastMsg = (String) mConversations.get(position).get(Constants.Message.LAST_MSG);
        SpannableStringBuilder sb =
                ExpressionController.expressionBuilder(lastMsg, mContext);
        holder.lastMsgTv.setText(sb);
        int notReadNum = (int) mConversations.get(position).get(Constants.Message.NOT_READ_NUM);
        if (notReadNum == 0) {
            holder.notReadNum.setVisibility(View.GONE);
        } else if (notReadNum < 100) {
            holder.notReadNum.setVisibility(View.VISIBLE);
            holder.notReadNum.setText(String.valueOf(notReadNum));
        } else {
            holder.notReadNum.setVisibility(View.VISIBLE);
            holder.notReadNum.setText("99+");
        }

//		String lastMsg = (String) mConversations.get(position).get(IMConstants.Message.LAST_MSG);
//		holder.lastMsgTv.setText(lastMsg);
        return convertView;
    }

    public class ViewHolder {
        TextView lastMsgTv;
        TextView lastTimeTv;
        TextView titleTv;
        TextView notReadNum;

        public ViewHolder(View view) {
            lastMsgTv = (TextView) view.findViewById(R.id.lastMessage);
            lastTimeTv = (TextView) view.findViewById(R.id.lastTime);
            titleTv = (TextView) view.findViewById(R.id.title);
            notReadNum = (TextView) view.findViewById(R.id.not_read_num_text);
        }
    }
}
