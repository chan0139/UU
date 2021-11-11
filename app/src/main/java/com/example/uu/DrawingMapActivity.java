package com.example.uu;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DrawingMapActivity extends AppCompatActivity  implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener{

    private OnBitmapCreated mListener;
    interface OnBitmapCreated{
        void saveBitmap(Bitmap bm);
    }

    public static String TAG="draw_map";
    static boolean isDrawing=false;
    private GoogleMap mMap;
    private Polyline route_shape;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<LatLng> checkpoint=new ArrayList<>();
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    private Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_map);
        checkPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.make_route);
        mapFragment.getMapAsync(this);

        ImageButton undo=(ImageButton)findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpoint.remove(checkpoint.size()-1);
                route_shape.setPoints(checkpoint);
            }
        });

        ImageButton delete_map=(ImageButton)findViewById(R.id.del_map);
        delete_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpoint.clear();
                route_shape.setPoints(checkpoint);
            }
        });

        ImageButton draw=(ImageButton) findViewById(R.id.draw);
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDrawing= !isDrawing;
                if(isDrawing){
                    undo.setVisibility(View.VISIBLE);
                    delete_map.setVisibility(View.VISIBLE);
                }
                else{
                    undo.setVisibility(View.INVISIBLE);
                    delete_map.setVisibility(View.INVISIBLE);
                }
            }
        });

        ImageButton savemap=(ImageButton) findViewById(R.id.savemap);
        savemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageOfMap();
                checkpoint.clear();
                finish();
            }
        });

        ImageButton exit=(ImageButton) findViewById(R.id.saveAndexit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkpoint.clear();
                route_shape.setPoints(checkpoint);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
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
        mMap=googleMap;
        PolylineOptions route_info=new PolylineOptions().clickable(true);
        route_shape=mMap.addPolyline(route_info);
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
    }
    public void getImageOfMap() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try{
                    FileOutputStream out = new FileOutputStream(
                            Environment.getExternalStorageDirectory()
                                    + "/MapScreenShot"
                                    +System.currentTimeMillis()+ ".png");
                    snapshot.compress(Bitmap.CompressFormat.PNG, 90,out);

                } catch (Exception e) {
                    Log.d("snap","snapfail");
                    e.printStackTrace();
                }
            }
        };

        mMap.snapshot(callback);
    }
}