package com.example.sravyadara.smartlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    ImageView image ;
    SensorManager sensorManager;
    Sensor lightSensor;
    TextView displayText;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Parameters params;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView)findViewById(R.id.imageView);
        displayText = (TextView)findViewById(R.id.displayText);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        getCamera();

        if(lightSensor != null){

            image.setImageDrawable(getResources().getDrawable(R.drawable.bulb_off));

            sensorManager.registerListener(this,lightSensor,sensorManager.SENSOR_DELAY_NORMAL);
        }else{
            image.setImageDrawable(getResources().getDrawable(R.drawable.bulb_on));

        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float light = event.values[0];

        if (event.values[0] <3 ) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.bulbon));

            turnOnFlash();

        }else{

            image.setImageDrawable(getResources().getDrawable(R.drawable.bulb_off));
            if(isFlashOn) {
                turnOffFlash();
            }

        }


    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void getCamera(){
        if(camera ==null){
            try {
                camera = Camera.open();
                params = camera.getParameters();
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }



    // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;


        }

    }


    // Turning Off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;


        }
    }

}
