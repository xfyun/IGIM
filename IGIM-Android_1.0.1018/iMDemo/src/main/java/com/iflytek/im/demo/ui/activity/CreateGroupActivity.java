package com.iflytek.im.demo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.group.Group;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.im.demo.Constants;
import com.iflytek.im.demo.ImApplication;
import com.iflytek.im.demo.R;
import com.iflytek.im.demo.common.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends BaseActivity {
	private static final String TAG = "CreateGroupActivity";
	private static final String DIS_GROUP = "0";
	private static final String GROUP = "1";
	private EditText  mGroupNameEdt;
	private RadioGroup mGroupSelectionRdoGrp;
	private RadioButton mDisGroupRdoBnt, mGroupRdoBtn;
	private Button mGroupCreBut;
	private String mGroupType;

	@Override
	protected int getLayoutRes() {
		return R.layout.creategroup;
	}

    @Override
    protected void initMembers() {
        mGroupType = DIS_GROUP;
    }

    @Override
	protected void findViews() {
		mGroupNameEdt = (EditText) findViewById(R.id.gname);
		mGroupSelectionRdoGrp = (RadioGroup) findViewById(R.id.groupType);
		mGroupCreBut = (Button) findViewById(R.id.createGroup);
		mDisGroupRdoBnt = (RadioButton) findViewById(R.id.disGroup);
		mGroupRdoBtn = (RadioButton) findViewById(R.id.group);
	}

    @Override
    protected void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.action_add_group);
    }

    @Override
    protected void setupEvents() {
        mGroupSelectionRdoGrp
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == mDisGroupRdoBnt.getId()) {
                            mGroupType = DIS_GROUP;
                        } else if (checkedId == mGroupRdoBtn.getId()) {
                            mGroupType = GROUP;
                        }
                    }
                });

        mGroupCreBut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String groupName = mGroupNameEdt.getText().toString();
                Map<String, String> creGroupParams = new HashMap<>();
                creGroupParams.put(Group.GNAME, groupName);
                creGroupParams.put(Group.TYPE, mGroupType);
                if (TextUtils.isEmpty(groupName)
                        || TextUtils.isEmpty(mGroupType)) {
                    ToastUtil.showText("请确定，群组名，群类型都已经填写");
                    return;
                }

                IMClient.getInstance().createGroup(creGroupParams,
                        new ResultCallback<String>() {

                            @Override
                            public void onSuccess(String data) {
                                ToastUtil.showText("群组创建成功");
                                if(mGroupType.equals(DIS_GROUP) ){
                                    RxBus.get().post(Constants.Event.CREATE_DIS_GROUP_SUCCESS,new Object());
                                }else{
                                    RxBus.get().post(Constants.Event.CREATE_GROUP_SUCCESS,new Object());
                                }
                                finish();
                            }

                            @Override
                            public void onError(int errorCode) {
                                ToastUtil.showText("群组创建失败");
                            }

                        });
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
	protected void onResume() {
		initData();
		super.onResume();
	}


	private void initData() {
		ImApplication app = (ImApplication) getApplicationContext();
	}
}
