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

package com.android.dialer.calllog;

import android.content.Context;
import android.content.res.Resources;
import android.provider.CallLog.Calls;
import android.text.TextUtils;
import android.util.Log;

import com.android.dialer.R;

import com.android.contacts.common.util.DualSimConstants;
import com.android.contacts.common.util.SimUtils;


/**
 * Helper for formatting and managing the display of phone numbers.
 */

public class PhoneNumberDisplayHelper {
    private final PhoneNumberUtilsWrapper mPhoneNumberUtils;
    private final Resources mResources;
    private final Context mContext;
    public PhoneNumberDisplayHelper(Resources resources) {
        mResources = resources;
        mPhoneNumberUtils = new PhoneNumberUtilsWrapper();
        mContext = null;
    }

    public PhoneNumberDisplayHelper(PhoneNumberUtilsWrapper phoneNumberUtils, Resources resources) {
        mPhoneNumberUtils = phoneNumberUtils;
        mResources = resources;
        mContext = null;
    }

    public PhoneNumberDisplayHelper(Context context, PhoneNumberUtilsWrapper phoneNumberUtils, Resources resources) {
        mPhoneNumberUtils = phoneNumberUtils;
		mContext = context;
        mResources = resources;
    }

    public Context getContext() {
        return mContext;
    }

    /* package */ CharSequence getDisplayName(CharSequence number, int presentation) {
        return getDisplayName(number, presentation, DualSimConstants.DSDS_INVALID_SLOT_ID);
    }

    /* package */ CharSequence getDisplayName(CharSequence number, int presentation, int simIndex) {
        if (presentation == Calls.PRESENTATION_UNKNOWN) {
            return mResources.getString(R.string.unknown);
        }
        if (presentation == Calls.PRESENTATION_RESTRICTED) {
            return mResources.getString(R.string.private_num);
        }
        if (presentation == Calls.PRESENTATION_PAYPHONE) {
            return mResources.getString(R.string.payphone);
        }
        if (mPhoneNumberUtils.isVoicemailNumber(number, simIndex)) {
            return mResources.getString(R.string.voicemail);
        }
        if (PhoneNumberUtilsWrapper.isLegacyUnknownNumbers(number)) {
            return mResources.getString(R.string.unknown);
        }
        return "";
    }

    /**
     * Returns the string to display for the given phone number.
     *
     * @param number the number to display
     * @param formattedNumber the formatted number if available, may be null
     */
    public CharSequence getDisplayNumber(CharSequence number,
            int presentation, CharSequence formattedNumber) {
        return getDisplayNumber(number, presentation, formattedNumber, DualSimConstants.DSDS_INVALID_SLOT_ID);
    }

    public CharSequence getDisplayNumber(CharSequence number,
            int presentation, CharSequence formattedNumber, int simIndex) {

        final CharSequence displayName = getDisplayName(number, presentation, simIndex);
        if (!TextUtils.isEmpty(displayName)) {
            return displayName;
        }

        if (TextUtils.isEmpty(number)) {
            return "";
        }

        if (TextUtils.isEmpty(formattedNumber)) {
            return number;
        } else {
            return formattedNumber;
        }
    }
}
