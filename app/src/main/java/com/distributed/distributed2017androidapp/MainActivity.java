package com.distributed.distributed2017androidapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.distributed.distributed2017androidapp.Controller.HandleConnections;
import org.lukhnos.nnio.file.Files;
import org.lukhnos.nnio.file.Paths;
import model.Directions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};

    int permsRequestCode = 200;
    private SharedPreferences sharedPreferences;
    static private String startLon,startLat,endLat,endLon;
    static EditText startlat, startlon, endlat, endlon;
    Directions askedDirs=null;
    HandleConnections handleConnections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startlat = (EditText) findViewById(R.id.startlat);
        startlon = (EditText) findViewById(R.id.startlon);
        endlat = (EditText) findViewById(R.id.endlat);
        endlon = (EditText) findViewById(R.id.endlon);
        final TextView textView = (TextView)findViewById(R.id.textView2);
        final Button getDirs = (Button) findViewById(R.id.GetDirs);
        final Switch select_route = (Switch)findViewById(R.id.select_route);
        getDirs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(canUse()){
                    Log.i("dvevf",startLat);
                    askedDirs= new Directions(Double.parseDouble(startLat),Double.parseDouble(startLon),Double.parseDouble(endLat),Double.parseDouble(endLon));
                    //Log.i("OurDirs",askedDirs.toString());
                    handleConnections = new HandleConnections("192.168.1.73", 4321, askedDirs);
                    handleConnections.setAskedDirs(askedDirs);
                    handleConnections.execute();

                    while(handleConnections.getOurDirs()==null){
                        int i=0;
                        i++;
                    }
                    handleConnections.cancel(true);

                    try {
                        clearAndWriteJSON(handleConnections.getOurDirs().getDirs());
                    } catch (IOException e) {
                        e.getMessage();
                    }

                   Intent goToMaps = new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(goToMaps);
                    handleConnections.setAskedDirs(null); MainActivity.this.finish();
                    askedDirs=null; handleConnections=null;
                }else{
                    Toast.makeText(getApplicationContext(),"We cannot use the inputs ", Toast.LENGTH_LONG).show();
                }
            }
        });
        select_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRoute();
            }
        });
    }

    private void switchRoute(){
        String Ostartlat=getResources().getString(R.string.OtherStartLat),
                Ostartlon = getResources().getString(R.string.OtherStartLon),
                Oendlat = getResources().getString(R.string.OtherEndLat),
                Oendlon = getResources().getString(R.string.OtherEndLon),
                Dstartlat = getResources().getString(R.string.DefaultStartLat),
                Dstartlon= getResources().getString(R.string.DefaultStartLon),
                Dendlat = getResources().getString(R.string.DefaultEndLat),
                Dendlon = getResources().getString(R.string.DefaultEndLon);
        if(startlat.getText().toString().equals(getResources().getString(R.string.DefaultStartLat))){
            startlat.setText(startLat=Ostartlat);
            startlon.setText(startLon=Ostartlon);
            endlat.setText(endLat=Oendlat);
            endlon.setText(endLon=Oendlon);
        }else{
            startlat.setText(startLat=Dstartlat);
            startlon.setText(startLon=Dstartlon);
            endlat.setText(endLat=Dendlat);
            endlon.setText(endLon=Dendlon);
        }
    }

    private static boolean canUse() {
        startLat = startlat.getText().toString().trim();
        startLon = startlon.getText().toString().trim();
        endLat = endlat.getText().toString().trim();
        endLon = endlon.getText().toString().trim();
        //Toast.makeText(getApplicationContext(),startLat+" "+startLon,Toast.LENGTH_LONG).show();
        return !(startLat.equals("") || startLon.equals("") || endLat.equals("") || endLon.equals(""))
                && isDouble(startLat) && isDouble(startLon) && isDouble(endLat) && isDouble(endLon);

    }
    private static boolean isDouble(String str) {
        try {
            BigDecimal myDec = new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            Log.e(e.getMessage(),e.getMessage());
            return false;
        }
    }

    private void clearAndWriteJSON(String json) throws IOException {
        if (shouldAskPermission()) {
            if(hasPermission(perms[0]) && hasPermission(perms[1])){

            }else{
                if(!hasPermission(perms[0]) && !hasPermission(perms[1])) {
                    requestPermissions(perms, permsRequestCode);
                }
            }

        }
        FileOutputStream output;

        // Find the SD Card path
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        // Create a new folder AndroidBegin in SD Card
        File dir = new File(filepath.getAbsolutePath());
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, "data.txt");
        output = new FileOutputStream(file,false);
        output.write(json.getBytes());
        output.flush();
        output.close();
        // Locate the image to Share
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        Uri uri=null;
        if(file.exists()){
            uri = Uri.fromFile(file);
        }
    }
    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M);

    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(writeAccepted)markAsAsked(perms[0]);
                boolean readAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(readAccepted)markAsAsked(perms[1]);
                break;

        }
    }

    @SuppressLint("NewApi")
    private boolean hasPermission(String permission){

        if(canMakeSmores()){

            return(checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED);

        }

        return true;

    }
    private boolean shouldWeAsk(String permission){

        return (sharedPreferences.getBoolean(permission, true));

    }



    private void markAsAsked(String permission){

        sharedPreferences.edit().putBoolean(permission, false).apply();

    }
    private boolean canMakeSmores() {
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

}
