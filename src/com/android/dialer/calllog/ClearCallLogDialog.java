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
 * limitations under the License
 */

package com.android.dialer.calllog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.text.TextUtils;

import com.android.contacts.common.CallUtil;
import com.android.contacts.common.util.DualSimConstants;
import com.android.contacts.common.util.SimUtils;
import com.android.contacts.common.ContactsUtils;
import com.android.dialer.R;
import com.android.dialer.service.CachedNumberLookupService;
import com.android.dialerbind.ObjectFactory;

/**
 * Dialog that clears the call log after confirming with the user
 */
public class ClearCallLogDialog extends DialogFragment {
    private static final CachedNumberLookupService mCachedNumberLookupService =
            ObjectFactory.newCachedNumberLookupService();

    private int mSimIndex = DualSimConstants.DSDS_INVALID_SLOT_ID;

    private static final String Calls_IMSI = "imsi";

    /** Preferred way to show this dialog */
    public static void show(FragmentManager fragmentManager) {
        ClearCallLogDialog dialog = new ClearCallLogDialog();
        dialog.show(fragmentManager, "deleteCallLog");
    }

    public static void show(FragmentManager fragmentManager, int simIndex) {
        ClearCallLogDialog dialog = new ClearCallLogDialog();
        if (ContactsUtils.isDualSimSupported()) {
            dialog.mSimIndex = simIndex;
        }
        dialog.show(fragmentManager, "deleteCallLog");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ContentResolver resolver = getActivity().getContentResolver();
        final Context context = getActivity().getApplicationContext();
        final OnClickListener okListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                        getString(R.string.clearCallLogProgress_title),
                        "", true, false);
                final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        if (mCachedNumberLookupService != null)
                            mCachedNumberLookupService.clearAllCacheEntries(context);
                        final Context context = getActivity();
                        final String imsi;
                        if (mSimIndex == CallLogFragment.CALL_ORIGIN_SIP) {
                            imsi = DualSimConstants.IMSI_FOR_SIP_CALL;
                        } else {
                            imsi = SimUtils.getSimImsi(context, mSimIndex);
                        }
                        if (!TextUtils.isEmpty(imsi)) {
                            resolver.delete(Calls.CONTENT_URI,
                                    String.format("%s like ?", Calls_IMSI),
                                    new String[] { imsi });
                        } else {
                            resolver.delete(Calls.CONTENT_URI, null, null);
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
                        progressDialog.dismiss();
                    }
                };
                // TODO: Once we have the API, we should configure this ProgressDialog
                // to only show up after a certain time (e.g. 150ms)
                progressDialog.show();
                task.execute();
            }
        };
        CharSequence message;
        switch (mSimIndex) {
        case DualSimConstants.DSDS_SLOT_1_ID:
            message = context.getText(R.string.sim1Text);
            message = context.getString(R.string.clearCallLogConfirmation_ds, message);
            break;
        case DualSimConstants.DSDS_SLOT_2_ID:
            message = context.getText(R.string.sim2Text);
            message = context.getString(R.string.clearCallLogConfirmation_ds, message);
            break;
        case CallLogFragment.CALL_ORIGIN_SIP:
            message = context.getText(R.string.clearSipCallLogConfirmation);
            break;
        default:
            message = context.getText(R.string.clearCallLogConfirmation);
            break;
        }
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.clearCallLogConfirmation_title)
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setMessage(message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, okListener)
            .setCancelable(true)
            .create();
    }
}
