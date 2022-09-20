package com.rotiking.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.rotiking.client.adapters.CheckoutCartItemRecyclerAdapter;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.models.CartItem;
import com.rotiking.client.models.CheckoutCartItem;
import com.rotiking.client.sheets.AddressBottomSheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity implements LocationListener {
    private RecyclerView cartItemRV;
    private ImageButton closeBtn;
    private TextView totalCartPriceTxt, deliveryCartPriceTxt, totalPayableTxt, addressTxt;
    private AppCompatButton orderBtn, changeAddressBtn, currentAddressBtn;

    private List<CartItem> cartItems;
    private int total_cart_price = 0, delivery_price = 70;

    private static final int LOCATION_PERMISSION_CODE = 101, COARSE_LOCATION_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartItems = (List<CartItem>) getIntent().getSerializableExtra("CART_ITEM");
        total_cart_price = getIntent().getIntExtra("TOTAL_CART_PRICE", 0);

        closeBtn = findViewById(R.id.close);
        totalCartPriceTxt = findViewById(R.id.total_price);
        deliveryCartPriceTxt = findViewById(R.id.delivery_price);
        totalPayableTxt = findViewById(R.id.payable_price);
        orderBtn = findViewById(R.id.place_order);
        addressTxt = findViewById(R.id.address);
        changeAddressBtn = findViewById(R.id.change_address_btn);
        currentAddressBtn = findViewById(R.id.current_address_btn);

        cartItemRV = findViewById(R.id.cart_item_rv);
        cartItemRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartItemRV.setHasFixedSize(true);

        if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            checkLocationPermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckoutCartItemRecyclerAdapter adapter = new CheckoutCartItemRecyclerAdapter(createOrderItemList());
        cartItemRV.setAdapter(adapter);

        setPayablePrice(0);

        FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                String myAddress = value.get("address", String.class);
                GeoPoint geoPoint = getLatLongFromAddress(myAddress);
                assert geoPoint != null;
                double distance = calculateDistance(geoPoint.getLatitude(), geoPoint.getLongitude());
                setPayablePrice(distance);

                addressTxt.setText(myAddress);
            }
        });

        changeAddressBtn.setOnClickListener(view -> AddressBottomSheet.newInstance(addressTxt.getText().toString()).show(getSupportFragmentManager(), "ADDRESS_DIALOG"));

        orderBtn.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                checkLocationPermission();
            } else {
                String address = addressTxt.getText().toString();
                if (address.equals(getString(R.string.no_address))) {
                    Toast.makeText(this, "No Address Provided.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        currentAddressBtn.setOnClickListener(view -> setCurrentLocation());

        closeBtn.setOnClickListener(view -> finish());
    }

    private void setPayablePrice(double distance) {
        if (distance <= 6) delivery_price = 30;
        else if (distance >= 7 && distance <= 11) delivery_price = 50;

        String tcp = "\u20B9 " + total_cart_price;
        totalCartPriceTxt.setText(tcp);

        String dp_ = "\u20B9 " + delivery_price;
        deliveryCartPriceTxt.setText(dp_);

        String tp_ = "\u20B9 " + (total_cart_price + delivery_price);
        totalPayableTxt.setText(tp_);
    }

    private List<CheckoutCartItem> createOrderItemList() {
        List<CheckoutCartItem> checkoutCartItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            String orderName = cartItem.getFood_data().getName();
            if (!cartItem.getTopping_ids().equals("None")) {
                orderName = orderName + " + Toppings";
            }
            CheckoutCartItem item = new CheckoutCartItem(orderName, cartItem.getTotal_price());
            checkoutCartItems.add(item);
        }
        return checkoutCartItems;
    }

    private Double calculateDistance(double lat, double log) {
        Location origin = new Location("Sahastradhara Road, Aman Vihar, Dehradun, Uttarakhand");
        origin.setLatitude(30.35214743972844);
        origin.setLongitude(78.08044403983544);

        Location client = new Location("Client Location");
        client.setLatitude(lat);
        client.setLongitude(log);

        return (double) Math.round(origin.distanceTo(client) * 0.001);
    }

    private GeoPoint getLatLongFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint((double) (location.getLatitude()), (double) (location.getLongitude()));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAddressFromLatLong(Double latitude, Double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);

            return address;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(CheckoutActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        else if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(CheckoutActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE || requestCode == COARSE_LOCATION_PERMISSION_CODE) {
            setCurrentLocation();
        }
    }

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            Toast.makeText(CheckoutActivity.this, "Location is required for delivery purpose.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        String myAddress = getAddressFromLatLong(location.getLatitude(), location.getLongitude());
        double distance = calculateDistance(location.getLatitude(), location.getLongitude());
        setPayablePrice(distance);

        addressTxt.setText(myAddress);
    }
}