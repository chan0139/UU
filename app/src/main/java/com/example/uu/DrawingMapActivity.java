package com.example.uu;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DrawingMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener {
    private static final String API_KEY = "AIzaSyCtR1gj33Jv0oDKpb7PyHVYlXXJsFRp_KQ";
    private GeoApiContext mGeoApiContext = null;
    private Uri mapUri;
    public static String TAG = "draw_map";
    static boolean isDrawing = false;
    private GoogleMap mMap;
    private Polyline route_shape;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<LatLng> checkpoint = new ArrayList<>();
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    private String startAddress;
    private String endAddress;
    private int counter = 0;
    private double distance = 0;
    private String distance2;
    private Object Context;
    private boolean hasMap=false;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_map);
        Intent sendData = new Intent();
        checkpoint.clear();
        startAddress = null;
        endAddress = null;
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(API_KEY).build();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.make_route);
        mapFragment.getMapAsync(this);

        //mBitmapCreatedListener=(OnBitmapCreatedListener) this;

        ImageButton undo = (ImageButton) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpoint.remove(checkpoint.size() - 1);
                route_shape.setPoints(checkpoint);
            }
        });

        ImageButton delete_map = (ImageButton) findViewById(R.id.del_map);
        delete_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpoint.clear();
                route_shape.setPoints(checkpoint);
            }
        });

        ImageButton draw = (ImageButton) findViewById(R.id.draw);
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDrawing = !isDrawing;
                if (isDrawing) {
                    undo.setVisibility(View.VISIBLE);
                    delete_map.setVisibility(View.VISIBLE);
                } else {
                    undo.setVisibility(View.INVISIBLE);
                    delete_map.setVisibility(View.INVISIBLE);
                }
            }
        });

        ImageButton savemap = (ImageButton) findViewById(R.id.savemap);
        savemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getURLOfMap();
            }
        });

        ImageButton exit = (ImageButton) findViewById(R.id.saveAndexit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(hasMap){
                    float[] results = new float[3];
                    for (int i = 0; i < checkpoint.size() - 1; i++) {
                        Location.distanceBetween(checkpoint.get(i).latitude,
                                checkpoint.get(i).longitude,
                                checkpoint.get(i + 1).latitude,
                                checkpoint.get(i + 1).longitude, results);
                        distance += (int) results[0];
                    }
                    distance /= 1000.0;
                    DecimalFormat form = new DecimalFormat("#.#");
                    distance2 = form.format(distance);


                    DirectionsApiRequest converter = new DirectionsApiRequest(mGeoApiContext);
                    converter.language("ko");
                    converter.mode(TravelMode.TRANSIT);
                    converter.alternatives(false);
                    converter.origin(
                            new com.google.maps.model.LatLng(checkpoint.get(0).latitude, checkpoint.get(0).longitude)
                    );
                    converter.destination(
                            new com.google.maps.model.LatLng(checkpoint.get(checkpoint.size() - 1).latitude, checkpoint.get(checkpoint.size() - 1).longitude)
                    )
                            .setCallback(new PendingResult.Callback<DirectionsResult>() {
                                @Override
                                public void onResult(DirectionsResult result) {

                                    startAddress = result.routes[0].legs[0].startAddress;
                                    endAddress = result.routes[0].legs[0].endAddress;
                                    String[] splitStr = startAddress.split(" ");
                                    String[] splitStr2 = endAddress.split(" ");
                                    //Log.d("Tlqkf",startAddress+"");
                                    //Log.d("Tlqkf",endAddress+"");
                                    sendData.putExtra("address", startAddress);
                                    sendData.putExtra("startAddress", splitStr[2]);
                                    sendData.putExtra("endAddress", splitStr2[2]);
                                    sendData.putExtra("mapUri", mapUri);
                                    sendData.putExtra("distance", distance2);
                                    sendData.putParcelableArrayListExtra("checkpoint", (ArrayList<? extends Parcelable>) checkpoint);
                                    setResult(Activity.RESULT_OK, sendData);
                                    finish();
                                }

                                @Override
                                public void onFailure(Throwable e) {
                                    Log.d("Tlqkf", "direction fail");
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(), "The Running Map is not set", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });


    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        PolylineOptions route_info = new PolylineOptions().clickable(true);
        route_shape = mMap.addPolyline(route_info);
        LatLng Gyeongbokgung = new LatLng(37.5779805, 126.977364);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Gyeongbokgung, 15));




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if(!isDrawing){
                    //when map clicked
                    MarkerOptions markerOptions=new MarkerOptions();
                    //set marker position
                    markerOptions.position(latLng);
                    //set marker title
                    markerOptions.title("position : "+latLng.latitude + " , "+latLng.longitude);

                    //animation for zoom
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                    mMap.addMarker(markerOptions);
                }
                else{
                    checkpoint.add(latLng);

                    route_shape.setPoints(checkpoint);
                }


            }

        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });


    }
    public void getURLOfMap() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try{
                   saveImage(snapshot);

                } catch (Exception e) {
                    Log.d("snap","snapfail");
                    e.printStackTrace();
                }
            }

        };
        mMap.snapshot(callback);
    }
    private void saveImage(Bitmap bmp){
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        String path=MediaStore.Images.Media.insertImage(this.getContentResolver(),bmp,"Title",null);
        mapUri = Uri.parse(path);
        hasMap=true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}