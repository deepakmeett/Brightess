package com.example.brightess;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private boolean success = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        Toast.makeText( getApplicationContext()
                , "Automatic brightness should be disabled for this feature", Toast.LENGTH_LONG).show();
        setContentView( R.layout.activity_main );
        SeekBar seekBar = findViewById( R.id.seek );
        textView = findViewById( R.id.text1 );
        seekBar.setMax( 255 );
        seekBar.setProgress( getBrightness() );
        get_permission();

        seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isChecked) {
                String percent = progress + "%";
                textView.setText( percent );
                if (isChecked && success) {
                    setBrightness( progress );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!success) {
                    Toast.makeText( getApplicationContext(), "Need Permission", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

    }

    private void setBrightness(int brightness) {
        if (brightness < 0) {
            brightness = 0;
        } else if (brightness > 255) {
            brightness = 255;
        }

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Settings.System.putInt( contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness );
    }

    private int getBrightness() {
        int brightness = 255;
        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            brightness = Settings.System.getInt( contentResolver, Settings.System.SCREEN_BRIGHTNESS );


        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    private void get_permission() {
        boolean value;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            value = Settings.System.canWrite( getApplicationContext() );
            if (value) {
                success = true;
            } else {
                Intent intent = new Intent( Settings.ACTION_MANAGE_WRITE_SETTINGS );
                intent.setData( Uri.parse( "package:" + getApplicationContext().getPackageName() ) );
                startActivityForResult( intent, 1000 );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean value = Settings.System.canWrite( getApplicationContext() );
                if (value) {
                    success = true;
                } else {
                    Toast.makeText( getApplicationContext(), "Permission not granted", Toast.LENGTH_SHORT ).show();
                }
            }
        }


    }
}
