package com.priyesh.hexatime;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class NoTitleListPref extends ListPreference {

	public NoTitleListPref(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setNegativeButton(null,null);
		builder.setTitle(null);
	}
}
