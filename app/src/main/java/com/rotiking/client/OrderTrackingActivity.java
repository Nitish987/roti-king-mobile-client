package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import com.rotiking.client.common.db.Database;

public class OrderTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageButton closeBtn;

    private GoogleMap mMap;
    private Marker marker;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        closeBtn = findViewById(R.id.close);

        orderId = getIntent().getStringExtra("order_id");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        trackDeliveryLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeBtn.setOnClickListener(view -> finish());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void trackDeliveryLocation() {
        Database.getInstance().collection("orders").document(orderId).addSnapshotListener(this, (value, error) -> {
            try {
                if (value != null && value.exists()) {
                    GeoPoint point = value.getGeoPoint("agentLocation");
                    String agentName = value.getString("agentName");
                    String agentPhone = value.getString("agentPhone");

                    if (marker != null) {
                        marker.remove();
                    }

                    assert point != null;
                    LatLng deliveryAgentLoc = new LatLng(point.getLatitude(), point.getLongitude());

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable(R.drawable.delivery);
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 250, 170, false);

                    String title = "Agent : " + agentName + " Phone : " + agentPhone;
                    marker = mMap.addMarker(new MarkerOptions().position(deliveryAgentLoc).title(title).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliveryAgentLoc, 18));
                } else {
                    Toast.makeText(this, "unable to track Delivery Agent.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "unable to track Delivery Agent.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}