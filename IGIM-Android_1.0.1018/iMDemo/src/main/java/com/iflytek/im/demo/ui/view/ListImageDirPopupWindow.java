package com.iflytek.im.demo.ui.view;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.iflytek.im.demo.R;
import com.iflytek.im.demo.adapter.CommonAdapter;
import com.iflytek.im.demo.bean.ImageFolder;
import com.iflytek.im.demo.common.imageUtil.BasePopupWindowForListView;
import com.iflytek.im.demo.common.imageUtil.ViewHolder;



public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFolder>
{
	private ListView mListDir;

	public ListImageDirPopupWindow(int width, int height,
								   List<ImageFolder> datas, View convertView)
	{
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews()
	{
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mListDir.setAdapter(new CommonAdapter<ImageFolder>(context, mDatas,
				R.layout.list_dir_item)
		{
			@Override
			public void convert(ViewHolder helper, ImageFolder item)
			{
				helper.setText(R.id.id_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.id_dir_item_image,
						item.getFirstImagePath());
				helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
			}
		});
	}

	public interface OnImageDirSelected
	{
		void selected(ImageFolder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents()
	{
		mListDir.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				if (mImageDirSelected != null)
				{
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params)
	{
		// TODO Auto-generated method stub
	}

}
