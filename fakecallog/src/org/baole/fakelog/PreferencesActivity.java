
package org.baole.fakelog;

import com.anttek.common.activity.HtmlViewerActivity;
import com.anttek.common.dialog.AdDialog;
import com.anttek.common.utils.ComponentUtil;
import com.anttek.common.utils.Devices;
import com.anttek.common.utils.Intents;

import org.baole.ad.AdUtil;
import org.baole.fakelog.model.Configuration;
import org.baole.fakelog.model.FileItems;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity implements
        android.content.SharedPreferences.OnSharedPreferenceChangeListener,
        OnPreferenceClickListener {
    public static final String KEY_HIDE_ICON = "hide_icon";

    private static final int REQUEST_CODE = 1;
    private String mCommentKey;
    private String mChangeLogsKey;
    private String mReportProblemKey;
    private String mBuyProKey;

    private Preference hideIconPref;

    private boolean isHideAppIcon;

    private Configuration mConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConf = Configuration.getInstance();
        mConf.init(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        addPreferencesFromResource(R.xml.settings);

        Preference selectImage = (Preference) findPreference("IncomingImagePref");
        selectImage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(
                        new Intent(PreferencesActivity.this, ImageSwitcherCall.class), 1);
                return false;
            }
        });

        mCommentKey = getString(R.string.PREF_KEY_COMMENT);
        mChangeLogsKey = getString(R.string.PREF_KEY_CHANGE_LOGS);
        mReportProblemKey = getString(R.string.PREF_KEY_REPORT_PROBLEM);
        mBuyProKey = getString(R.string.PREF_KEY_BUY_PRO);

        findPreference(mCommentKey).setOnPreferenceClickListener(this);
        findPreference(mChangeLogsKey).setOnPreferenceClickListener(this);
        findPreference(mReportProblemKey).setOnPreferenceClickListener(this);

        Preference pref = findPreference(mBuyProKey);

        if (AdUtil.hasAd() && pref != null) {
            pref.setOnPreferenceClickListener(this);
        }

        isHideAppIcon = ComponentUtil.isComponentEnabled(this, FakeLogActivity.class);

        hideIconPref = findPreference(KEY_HIDE_ICON);
        if (hideIconPref != null) {
            hideIconPref.setOnPreferenceClickListener(this);
        }
        onHideIconChanged();
    }

    private void onHideIconChanged() {
        if (hideIconPref != null) {
            hideIconPref.setTitle(isHideAppIcon ? R.string.hide_icon_disable
                    : R.string.hide_icon_enable);
        }
        findPreference(Configuration.KEY_DIAL_NUMBER).setEnabled(
                !isHideAppIcon);
    }

    @Override
    protected void onPause() {
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && REQUEST_CODE == requestCode) {
            FileItems.setId_image(data.getIntExtra("position", 0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mBuyProKey.equals(preference.getKey())) {
            AdDialog.showGoProDialog(this, getString(R.string.pro_package));
        } else if (mChangeLogsKey.equals(preference.getKey())) {
            HtmlViewerActivity.showActivity(this, R.raw.change_logs);
        } else if (mReportProblemKey.equals(preference.getKey())) {
            Intents.startEmailIntent(this,
                    getString(R.string.email_antteksupport),
                    getString(R.string.support_email_subject),
                    Devices.buildDeviceInfo(this));
        } else if (mCommentKey.equals(preference.getKey())) {
            Intents.startMarketAppActivity(this, getPackageName());
        } else if (KEY_HIDE_ICON.equals(preference.getKey())) {
            if (AdUtil.hasAd()) {
                AdDialog.showGoProDialog(PreferencesActivity.this,
                        getString(R.string.pro_package));
            } else {

                String title = getString(isHideAppIcon ? R.string.hide_icon_disable
                        : R.string.hide_icon_enable);
                String message = getString(
                        isHideAppIcon ? R.string.hide_icon_disable_warning_msg
                                        : R.string.hide_icon_enable_warning_msg,
                        mConf.getDialNumber());

                new AlertDialog.Builder(PreferencesActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        isHideAppIcon = !isHideAppIcon;
                                        ComponentUtil.setComponentEnabled(
                                                getApplicationContext(),
                                                FakeLogActivity.class,
                                                isHideAppIcon);
                                        onHideIconChanged();
                                    }
                                })
                         .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        }
        return false;
    }

}
