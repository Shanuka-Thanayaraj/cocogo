package com.s23010188.cocogo;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private EditText etSearch;
    private Button btnSearch;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);

        // Setup map
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(3.0); // Zoomed out, no fixed center

        btnSearch.setOnClickListener(v -> {
            String locationName = etSearch.getText().toString().trim();
            if (locationName.isEmpty()) {
                Toast.makeText(MapActivity.this, "Please enter an address", Toast.LENGTH_SHORT).show();
                return;
            }
            Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                if (addresses == null || addresses.isEmpty()) {
                    Toast.makeText(MapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                Address address = addresses.get(0);
                GeoPoint point = new GeoPoint(address.getLatitude(), address.getLongitude());

                // Move map to the searched location
                mapView.getController().animateTo(point);
                mapView.getController().setZoom(15.0);

                // Remove old marker if exists
                if (marker != null) {
                    mapView.getOverlays().remove(marker);
                }

                // Add new marker
                marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setTitle(locationName);
                mapView.getOverlays().add(marker);
                mapView.invalidate();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MapActivity.this, "Error finding location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();  // needed for compass, my location overlays, etc.
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();  // needed for compass, my location overlays, etc.
    }
}
