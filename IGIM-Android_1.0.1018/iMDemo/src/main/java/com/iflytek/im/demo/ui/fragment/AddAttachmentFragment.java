package com.iflytek.im.demo.ui.fragment;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.BaseViewHolder;

/**
 * 一个用来显示要发送哪种媒体消息的Fragment
 * Created by imxqd on 2016/8/31.
 */
public class AddAttachmentFragment extends BaseFragment {

    private static final int COLUMNS = 4;


    private RecyclerView mRecyclerView;

    private OnAddAttachmentClickListener mListener;
    private AttachmentAdapter mAdapter;

    public AddAttachmentFragment() {
        // Required empty public constructor
    }
    public static AddAttachmentFragment newInstance() {
        return new AddAttachmentFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_add_attachment;
    }


    @Override
    protected void initMember() {
        mAdapter = new AttachmentAdapter();
    }

    @Override
    protected void findViews() {
        mRecyclerView = f(R.id.attachment_list);
    }

    @Override
    protected void initUI() {
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), COLUMNS));
    }

    @Override
    protected void setupEvents() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddAttachmentClickListener) {
            mListener = (OnAddAttachmentClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddAttachmentClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.Holder> {

        private int[] mIcons = new int[] {
                R.drawable.my_collection,
                R.drawable.smll_video,
                R.drawable.location,
                R.drawable.picture,
                R.drawable.chat_with_video,
                R.drawable.chat_with_voice,
                R.drawable.voice_to_text
        };

        private String[] mTexts
                = getActivity().getResources().getStringArray(R.array.attachment_type);

        private LayoutInflater mInflater = LayoutInflater.from(getActivity());

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(mInflater.inflate(R.layout.rv_add_attachment, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.tv.setText(mTexts[position]);
            holder.icon.setImageResource(mIcons[position]);
        }

        @Override
        public int getItemCount() {
            return mIcons.length;
        }

        class Holder extends BaseViewHolder implements View.OnClickListener {

            TextView tv;
            ImageView icon;

            public Holder(View itemView) {
                super(itemView);
                tv = f(R.id.add_attachment_tv);
                icon = f(R.id.add_attachment_icon);
                itemView.setOnClickListener(this);
            }

            @SuppressWarnings("WrongConstant")
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                mListener.onAddAttachmentClicked(pos);
            }
        }

    }


    public interface OnAddAttachmentClickListener {
        void onAddAttachmentClicked(@Constants.AttachmentType.Value int type);
    }
}
