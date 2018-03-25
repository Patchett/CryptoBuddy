package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

/**
 * Created by Ryan on 3/24/2018.
 */

public class AboutLibrariesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new LibsBuilder()
                //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withAboutIconShown(true)
                .withLicenseShown(true)
                .withVersionShown(true)
                .withAboutVersionShownName(true)
                .withAboutVersionShownCode(true)
                .withAboutVersionString("Version: " + BuildConfig.VERSION_NAME)
                //start the activity
                .start(this);
    }

}
