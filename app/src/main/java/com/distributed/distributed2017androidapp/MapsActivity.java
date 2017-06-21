package com.distributed.distributed2017androidapp;

import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Directions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap=null;
    private Directions ourDirs=null;
    private LatLng _startLatLng=null, _endLatLng=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
            ourDirs = new Directions(jsonToStringFromFolder());
        }catch (IOException e){
           Log.d("dfsaf", "Cannot open json!");
        }

        _startLatLng = new LatLng(ourDirs.getStartlat(),ourDirs.getStartlon());
        _endLatLng = new LatLng(ourDirs.getEndlat(),ourDirs.getEndlon());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //from.setText(ourDirs.getStartAddress());
        //to.setText(ourDirs.getEndAddress());
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(_startLatLng).title("Marker in Start Lon").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(_endLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        PolylineOptions polylineOptions = new PolylineOptions();
        List<LatLng> slatlngs,elatlngs,latLngs=new ArrayList<LatLng>();
        List<List<LatLng>> polylinepoints;
        slatlngs = (List<LatLng>) ourDirs.getSteps().stream().parallel().map(p->new LatLng(p.getStartLat(),p.getStartLon())).collect(Collectors.toList());
        polylinepoints = (List<List<LatLng>>)ourDirs.getSteps().stream().parallel().map(p->decodePoly(p.getPolyline_points())).collect(Collectors.toList());
        elatlngs = (List<LatLng>) ourDirs.getSteps().stream().parallel().map(p->new LatLng(p.getEndLat(),p.getEndLon())).collect(Collectors.toList());
        for(int i=0; i<slatlngs.size(); i++){
            latLngs.add(slatlngs.get(i));
            for(int j=0; j<polylinepoints.get(i).size(); j++){
                latLngs.add(polylinepoints.get(i).get(j));
            }
            latLngs.add(elatlngs.get(i));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(latLngs.size()/2)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
        polylineOptions.addAll(latLngs);
        polylineOptions.color(Color.RED);
        polylineOptions.geodesic(true);
        mMap.addPolyline(polylineOptions);
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public String jsonToStringFromFolder() throws IOException {

        FileInputStream input;
        // Find the SD Card path
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        // Create a new folder AndroidBegin in SD Card
        File dir = new File(filepath.getAbsolutePath());
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, "data.txt");
        input = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }
        reader.close();
        input.close();
        // Locate the image to Share
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        Uri uri=null;
        if(file.exists()){
            uri = Uri.fromFile(file);
        }

        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MapsActivity.this.finish();
        System.exit(0);
    }
}
