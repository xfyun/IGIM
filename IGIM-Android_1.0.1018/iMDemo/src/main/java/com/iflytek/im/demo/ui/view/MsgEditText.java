package com.iflytek.im.demo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import io.github.rockerhieu.emojicon.EmojiconEditText;

public class MsgEditText extends EmojiconEditText implements
		MenuItem.OnMenuItemClickListener {
	private static final int ID_SELECTION_MODE = android.R.id.selectTextMode;

	// Selection context mode
	private static final int ID_SELECT_ALL = android.R.id.selectAll;
	private static final int ID_CUT = android.R.id.cut;
	private static final int ID_COPY = android.R.id.copy;
	private static final int ID_PASTE = android.R.id.paste;

	private final Context mContext;

	/*
	 * Just the constructors to create a new EditText...
	 */
	public MsgEditText(Context context) {
		super(context);
		this.mContext = context;
	}

	public MsgEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public MsgEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// menu.add(0, ID_PASTE, 0, "粘贴").setOnMenuItemClickListener(this);
		// menu.add(0, ID_CUT, 1, "剪切").setOnMenuItemClickListener(this);
		// menu.add(0, ID_COPY, 1, "复制").setOnMenuItemClickListener(this);
		// menu.add(0, ID_SELECT_ALL, 1, "全选").setOnMenuItemClickListener(this);
		super.onCreateContextMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return onTextContextMenuItem(item.getItemId());
	}

	@Override
	public boolean onTextContextMenuItem(int id) {
		// Do your thing:
		boolean consumed = super.onTextContextMenuItem(id);
		// React:
		switch (id) {
		case android.R.id.cut:
			onTextCut();
			break;
		case android.R.id.paste:
			onTextPaste();
			break;
		case android.R.id.copy:
			onTextCopy();
		}
		return consumed;
	}

	/**
	 * Text was cut from this EditText.
	 */
	public void onTextCut() {
		Toast.makeText(mContext, "Cut!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Text was copied from this EditText.
	 */
	public void onTextCopy() {
		Toast.makeText(mContext, "Copy!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Text was pasted into the EditText.
	 */
	public void onTextPaste() {
		Toast.makeText(mContext, "Paste!", Toast.LENGTH_SHORT).show();
	}
}
