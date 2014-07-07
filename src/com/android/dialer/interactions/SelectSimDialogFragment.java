/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.dialer.interactions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.android.dialer.R;

public class SelectSimDialogFragment extends DialogFragment {

    public interface Listener {
        public void onItemCliecked();
        public void onItem2Cliecked();
    }

    private Context mContext;
    private Intent mIntent;
    private Intent mIntent2;
    private Listener mListener;

    private Integer mTitleResId;
    private CharSequence mItemString;
    private CharSequence mItem2String;

    /** Preferred way to show this dialog */
    public static void show(FragmentManager fragmentManager, Context context, Integer titleResId,
            CharSequence itemString, CharSequence item2String, Intent intent, Intent intent2) {
        SelectSimDialogFragment dialog = new SelectSimDialogFragment();
        dialog.mContext = context;
        dialog.mTitleResId = titleResId;
        dialog.mItemString = itemString;
        dialog.mItem2String = item2String;
        dialog.mIntent = intent;
        dialog.mIntent2 = intent2;
        dialog.mListener = null;
        dialog.show(fragmentManager, "selectSim");
    }

    public static void show(FragmentManager fragmentManager, Context context, Integer titleResId,
            CharSequence itemString, CharSequence item2String, Listener listener) {
        SelectSimDialogFragment dialog = new SelectSimDialogFragment();
        dialog.mContext = context;
        dialog.mTitleResId = titleResId;
        dialog.mItemString = itemString;
        dialog.mItem2String = item2String;
        dialog.mListener = listener;
        dialog.show(fragmentManager, "selectSim");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        final OnClickListener selectListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:
                    if (mListener != null) {
                        mListener.onItemCliecked();
                    } else if (mIntent != null) {
                        mContext.startActivity(mIntent);
                    }
                    break;
                case 1:
                    if (mListener != null) {
                        mListener.onItem2Cliecked();
                    } else if (mIntent2 != null) {
                        mContext.startActivity(mIntent2);
                    }
                    break;
                }
                dialog.dismiss();
            }
        };

        CharSequence[] items = new CharSequence[2];
        items[0] = mItemString;
        items[1] = mItem2String;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (mTitleResId == null) {
            builder.setTitle(R.string.select_sim_title);
        } else {
            builder.setTitle(mTitleResId);
        }
        builder.setSingleChoiceItems(items, -1, selectListener);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setCancelable(true);
        return builder.create();
    }
}
