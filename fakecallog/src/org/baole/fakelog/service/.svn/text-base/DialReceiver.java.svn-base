
package org.baole.fakelog.service;

import org.baole.ad.AdUtil;
import org.baole.fakelog.WrappedFakeLogActivity;
import org.baole.fakelog.model.Configuration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DialReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Configuration conf = Configuration.getInstance();
        conf.init(context);
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        if (!AdUtil.hasAd() && number != null && number.equals(conf.getDialNumber())) {
            setResultData(null);

            Intent confirmIntent = new Intent(context, WrappedFakeLogActivity.class);
            confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            confirmIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, number);
            context.startActivity(confirmIntent);
        }

    }

}
