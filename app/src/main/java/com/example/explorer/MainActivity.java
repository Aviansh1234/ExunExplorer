package com.example.explorer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.explorer.helpers.BackendHelper;
import com.example.explorer.helpers.FrontendHelper;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    MapView map;
    CardView camera, identify, sos;
    final int permCode = 42;
    final int cameraCode = 420;
    final int identifyCode = 4242;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<GeoPoint> arr = null;
                        try {
                            arr = BackendHelper.getLatLongs();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        List<Overlay> l = map.getOverlays();
                        for (Overlay o : l) {
                            map.getOverlays().remove(o);
                        }
                        for (GeoPoint home : arr) {
                            Marker startMarker = new Marker(map);
                            startMarker.setPosition(home);
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher_foreground));
                            map.getOverlays().add(startMarker);
                        }
                    }
                });
                thread.start();
            } finally {
                handler.postDelayed(runnable, 5000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Context ctx = this;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FrontendHelper.setLocationListener((LocationManager) getSystemService(LOCATION_SERVICE), this);
        findViewById(R.id.main).setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (i != View.VISIBLE)
                    findViewById(R.id.main).setVisibility(View.VISIBLE);
            }
        });
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        final GeoPoint home = new GeoPoint(28.652, 77.1663);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(false);
        map.setVerticalMapRepetitionEnabled(false);
        IMapController mapController = new MapController(map);
        mapController.setCenter(home);
        mapController.zoomTo(15);
        map.getController().animateTo(home);

        handler = new Handler();
        checkPermissions();
        camera = findViewById(R.id.camera);
        identify = findViewById(R.id.identify);
        sos = findViewById(R.id.sos);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("eeee");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                System.out.println("starting");
                startActivityForResult(intent, cameraCode);
                System.out.println("started honi chahiye");
            }
        });
        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, identifyCode);
            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackendHelper.toggleSOS();
            }
        });
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == cameraCode) {
            BackendHelper.imageUpload(data);
        } else if (requestCode == identifyCode) {
            String desc = BackendHelper.imageIdentification(data);
            Intent intent = new Intent(MainActivity.this, IdentifiedObject.class);
            intent.putExtra("desc", desc);
            startActivity(intent);
        }
    }

    public boolean checkPermissions() {
        ArrayList<String> perms = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.CAMERA);
        }
        if (perms.isEmpty()) {
            return true;
        }
        String arr[] = new String[perms.size()];
        for (int i = 0; i < perms.size(); i++) {
            arr[i] = perms.get(i);
        }
        try {
            for (String i : perms) {
                System.out.println(i);
            }
            ActivityCompat.requestPermissions(this, arr, permCode);
            System.out.println("here");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("nooooooooo");
        }
        return false;
    }

    int cn = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permCode == requestCode) {
            cn++;
            if (cn > 5) {
                System.out.println("not asking again");
                return;
            }
            checkPermissions();
        }
    }
}