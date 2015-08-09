package pl.d30.binClock;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.flask.colorpicker.ColorPickerPreference;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static pl.d30.binClock.ClockService.sendToService;

public class Configuration extends PreferenceActivity implements BillingProcessor.IBillingHandler {

    private static final String TAG_BILLING = "BinaryBilling";

    private static final String PREMIUM           = "premium";
    private static final String PREMIUM_STORE_KEY = "premium.8b021080de";

    private BinaryWidgetSettings bws;
    private BillingProcessor     bp;

    private int widgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // NOTE: small hobby project - not worth the hassle to obfuscate
        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtQNvm3LK3qekOoo78aJIrrttSB4ZBdSPaUtbTSKSKKh4WS65guzavw1uLBE/sNcncPCLg9umWedvNeis++6gaxCQqTBT58RFsSjAap5zcbAWOpAD5UHrcmXt9DIKv66gcXtYEeTbRKPuoaSLWyoeng9O91TQjnHaPDkV0VgyFhN3GYY1eqrTCMRi9NnNiHvrKTV2HwFhtcbYpxU+W2ypSDfI5Sevs2s+GubCcyC3t850phVJe+qH1lDZPAmoxulWChTdNjyHR5WFQ6b6LSGv4gmgK0NlpMTJCa/7XpROlxXF18IhdjTgDC7lHjKK7DtC0E0beVrYv5jUAhZLkG18lQIDAQAB", this);

        widgetId = getWidgetId(getIntent().getExtras());

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_CANCELED, resultValue);

        if (widgetId == INVALID_APPWIDGET_ID)
            finish();

        bws = new BinaryWidgetSettings();
        Bundle b = new Bundle();
        b.putInt(EXTRA_APPWIDGET_ID, widgetId);
        bws.setArguments(b);

        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, bws)
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preferences, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
            case android.R.id.home:
                return createWidget();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean createWidget() {
        Bundle b = new Bundle();
        b.putInt(Provider.KEY_WID, widgetId);
        sendToService(this, Provider.BINARY_WIDGET_CREATE, b);

        // "Phantom widgets" protection
        new Widget(this, widgetId).makeValid();

        Intent resultValue = new Intent();
        resultValue.putExtra(EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);

        finish();
        return true;
    }

    @Override
    public void onBillingInitialized() {
        bp.loadOwnedPurchasesFromGoogle();
        bws.setPremium(checkPremium());
    }

    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {
        if (s.equals(PREMIUM_STORE_KEY))
            bws.enablePremium();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        bws.setPremium(checkPremium());
    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
        Log.e(TAG_BILLING, "onBillingError: " + i, throwable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    private void showPremiumDialog() {
        // TODO: add some intermediate Dialog presenting what's in premium
        bp.purchase(Configuration.this, PREMIUM_STORE_KEY);
    }

    protected static int getWidgetId(Bundle b) {
        return b != null
            ? b.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
            : INVALID_APPWIDGET_ID;
    }

    protected boolean checkPremium() {
        TransactionDetails tDetails = bp.getPurchaseTransactionDetails(PREMIUM_STORE_KEY);
        if (tDetails == null)
            return false;

        PurchaseInfo.ResponseData p = tDetails.purchaseInfo.parseResponseData();
        return p.purchaseState == PurchaseInfo.PurchaseState.PurchasedSuccessfully;
    }

    public static class BinaryWidgetSettings extends PreferenceFragment {

        private ColorPickerPreference dotColor;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle b = getArguments();
            int widgetId = getWidgetId(b);

            getPreferenceManager().setSharedPreferencesName(Widget.getPrefsName(widgetId));
            addPreferencesFromResource(R.xml.preferences);

            ListPreference bkg = (ListPreference) findPreference(Widget.SP_KEY_BACKGROUND);
            bkg.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference pref, Object newBkg) {

                boolean setBlack = false;
                switch (newBkg.toString()) {
                    case "0":
                        pref.setSummary(R.string.background_nobkg);
                        break;

                    case "1":
                        pref.setSummary(R.string.background_black);
                        break;

                    case "2":
                        pref.setSummary(R.string.background_white);
                        setBlack = true;
                        break;
                }

                if (dotColor != null) {
                    int dot = getPreferenceManager().getSharedPreferences().getInt(Widget.SP_KEY_DOT_COLOR, -1);
                    if (dot == -1)
                        dotColor.setValue(setBlack ? 0xff000000 : 0xffffffff, true);
                }

                return true;
                }
            });

            setPremium(false);
        }

        private void enablePremium() {
            Preference premium = findPreference(PREMIUM);
            getPreferenceScreen().removePreference(premium);

            dotColor = (ColorPickerPreference) findPreference(Widget.SP_KEY_DOT_COLOR);
            dotColor.setEnabled(true);

            Preference compact = findPreference(Widget.SP_KEY_TYPE);
            compact.setEnabled(true);

            CheckBoxPreference am_pm = (CheckBoxPreference) findPreference(Widget.SP_KEY_AM_PM);
            am_pm.setEnabled(true);
            am_pm.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean enabled = (Boolean) newValue;
                if (enabled)
                    toast(R.string.ampm_hint);

                return true;
                }
            });

        }

        private void showBuyPremiumDialog() {
            ((Configuration) getActivity()).showPremiumDialog();
        }

        private void disablePremium() {
            Preference premium = findPreference(PREMIUM);
            premium.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                showBuyPremiumDialog();
                return false;
                }
            });
        }

        private void setPremium(boolean premium) {
            if (premium) enablePremium();
            else disablePremium();
        }

        private void toast(@StringRes int textId) {
            Toast.makeText(getActivity(), textId, Toast.LENGTH_LONG).show();
        }
    }
}
