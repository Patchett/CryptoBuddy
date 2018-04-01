package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaredrummler.android.device.DeviceName;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

/**
 * Created by Ryan on 3/22/2018.
 */

public class AboutTheDevActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceName.DeviceInfo deviceInfo = DeviceName.getDeviceInfo(this);
        Intent helpIntent = new Intent(Intent.ACTION_SEND);
        helpIntent.putExtra(Intent.EXTRA_EMAIL, "cryptobuddydev@gmail.com");
        helpIntent.putExtra(Intent.EXTRA_SUBJECT, "CryptoBuddy Support - Android");
        helpIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\n\n------------------------------\nApp Version: " + BuildConfig.VERSION_NAME +
                " (" + BuildConfig.VERSION_CODE + ")\nDevice Market Name: "
                + deviceInfo.marketName + "\nModel: " + deviceInfo.model +
                "\nManufacturer: " + deviceInfo.manufacturer);

        // TODO: Make a cryptobuddydev email address
        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.oath_pic_edit_cropped)
                .setCover(R.mipmap.background_material_design)
                .setName("Ryan Bridges")
                .setSubTitle("Mobile Developer")
                .addLinkedInLink("ryan-bridges")
                .setBrief("Innovator, dreamer, and boundary pusher with a deep passion for mobile app development")
                .setAppIcon(R.drawable.cbteal_icon)
                .setAppName(R.string.app_name)
                .addGitHubLink("Patchett")
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .setAppIcon(R.drawable.cbteal_icon)
                .addEmailLink("rybridges16@gmail.com")
                .addFacebookLink("ryan.bridges.37")
                .addHelpAction(helpIntent)
                .addUpdateAction()
                .addFeedbackAction("rybridges16@gmail.com")
                .build();
        setContentView(view);
    }
}
