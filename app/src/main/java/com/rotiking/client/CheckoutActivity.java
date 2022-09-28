package com.rotiking.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rotiking.client.adapters.CheckoutCartItemRecyclerAdapter;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.common.security.AES128;
import com.rotiking.client.models.CartItem;
import com.rotiking.client.models.CheckoutCartItem;
import com.rotiking.client.models.Order;
import com.rotiking.client.models.Topping;
import com.rotiking.client.sheets.AddressBottomSheet;
import com.rotiking.client.utils.Promise;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {
    private RecyclerView cartItemRV;
    private ImageButton closeBtn;
    private TextView totalCartPriceTxt, deliveryCartPriceTxt, totalPayableTxt, nameTxt, phoneTxt, addressTxt;
    private AppCompatSpinner paymentMethodSelector;
    private AppCompatButton orderBtn, changeDetailsBtn, currentAddressBtn;

    private List<CartItem> items;
    private int total_cart_price = 0, delivery_price = 0;
    private String name = null, phone = null;
    private double latitude = 0, longitude = 0;
    private boolean isLocationListenerCalled = true;

    private int orderNumberPO, payablePricePO;
    private String namePO, phonePO, addressPO;

    private static final int LOCATION_PERMISSION_CODE = 101, COARSE_LOCATION_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Checkout.preload(getApplicationContext());

        items = (List<CartItem>) getIntent().getSerializableExtra("CART_ITEM");
        total_cart_price = getIntent().getIntExtra("TOTAL_CART_PRICE", 0);

        closeBtn = findViewById(R.id.close);
        totalCartPriceTxt = findViewById(R.id.total_price);
        deliveryCartPriceTxt = findViewById(R.id.delivery_price);
        totalPayableTxt = findViewById(R.id.payable_price);
        orderBtn = findViewById(R.id.place_order);
        nameTxt = findViewById(R.id.name);
        phoneTxt = findViewById(R.id.phone);
        addressTxt = findViewById(R.id.address);
        paymentMethodSelector = findViewById(R.id.payment_method_selector);
        changeDetailsBtn = findViewById(R.id.change_details_btn);
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
                name = value.get("name", String.class);
                phone = value.get("phone", String.class);
                String myAddress = value.get("address", String.class);

                GeoPoint geoPoint = getLatLongFromAddress(myAddress);
                assert geoPoint != null;
                latitude = geoPoint.getLatitude();
                longitude = geoPoint.getLongitude();

                double distance = calculateDistance(geoPoint.getLatitude(), geoPoint.getLongitude());
                setPayablePrice(distance);

                nameTxt.setText(name);
                phoneTxt.setText(phone);
                addressTxt.setText(myAddress);
            }
        });

        changeDetailsBtn.setOnClickListener(view -> AddressBottomSheet.newInstance(nameTxt.getText().toString(), phoneTxt.getText().toString(), addressTxt.getText().toString()).show(getSupportFragmentManager(), "ADDRESS_DIALOG"));

        orderBtn.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                checkLocationPermission();
            } else {
                namePO = nameTxt.getText().toString();
                phonePO = phoneTxt.getText().toString();
                addressPO = addressTxt.getText().toString();

                if (name.equals(getString(R.string.no_name)) || phone.equals(getString(R.string.no_phone)) || addressPO.equals(getString(R.string.no_address))) {
                    AddressBottomSheet.newInstance(nameTxt.getText().toString(), phoneTxt.getText().toString(), addressTxt.getText().toString()).show(getSupportFragmentManager(), "ADDRESS_DIALOG");
                    return;
                }

                orderNumberPO = (int) (Math.random() * 100000000);
                payablePricePO = total_cart_price + delivery_price;
                if (paymentMethodSelector.getSelectedItemPosition() == 0) {
                    placeOrder(null);
                } else {
                    payAndPlaceOrder();
                }
            }
        });

        currentAddressBtn.setOnClickListener(view -> {
            isLocationListenerCalled = true;
            setCurrentLocation();
        });

        closeBtn.setOnClickListener(view -> finish());

        FirebaseFirestore.getInstance().collection("shop").document("state").addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                boolean isOpen = value.get("open", Boolean.class);
                if (isOpen) {
                    String o_ = "Place Order";
                    orderBtn.setText(o_);
                    orderBtn.setEnabled(true);
                } else {
                    String o_ = "Order are close for today";
                    orderBtn.setText(o_);
                    orderBtn.setEnabled(false);
                }
            }
        });
    }

    private void setPayablePrice(double distance) {
        orderBtn.setVisibility(View.VISIBLE);
        orderBtn.setEnabled(true);

        if (distance <= 6) delivery_price = 30;
        else if (distance >= 7 && distance <= 11) delivery_price = 50;
        else if (distance >= 12 && distance <= 20) delivery_price = 70;
        else {
            orderBtn.setVisibility(View.GONE);
            orderBtn.setEnabled(false);
            Toast.makeText(this, "Sorry! we cannot deliver in your range.", Toast.LENGTH_SHORT).show();
            delivery_price = 0;
        }

        String tcp = "\u20B9 " + total_cart_price;
        totalCartPriceTxt.setText(tcp);

        String dp_ = "\u20B9 " + delivery_price;
        if (delivery_price == 0) {
            dp_ = "No Delivery.";
        }
        deliveryCartPriceTxt.setText(dp_);

        String tp_ = "\u20B9 " + (total_cart_price + delivery_price);
        totalPayableTxt.setText(tp_);
    }

    private void placeOrder(String paymentOrderID) {
        int totalDiscount = 0;
        for (CartItem i : items) {
            totalDiscount += i.getFood_data().getDiscount();
        }

        GeoPoint point = new GeoPoint(latitude, longitude);

        DocumentReference doc = FirebaseFirestore.getInstance().collection("orders").document();
        String orderId = doc.getId();

        String secureNumber = AES128.encrypt(Auth.ENCRYPTION_KEY, Integer.toString((int) (Math.random() * 10000)));

        Order order = new Order(
                addressPO,
                null,
                null,
                null,
                items,
                delivery_price,
                totalDiscount,
                point,
                namePO,
                orderId,
                orderNumberPO,
                0,
                true,
                payablePricePO,
                paymentMethodSelector.getSelectedItem().toString(),
                paymentOrderID,
                phonePO,
                secureNumber,
                System.currentTimeMillis(),
                total_cart_price,
                Auth.getAuthUserUid()
        );

        doc.set(order).addOnSuccessListener(unused -> {
            Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
            intent.putExtra("ORDER", orderId);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> Toast.makeText(this, "Unable to place order.", Toast.LENGTH_SHORT).show());
    }

    private void payAndPlaceOrder() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Auth.PAYMENT_KEY);
        checkout.setImage(R.drawable.logo);
        checkout.setFullScreenDisable(false);

        final Activity activity = this;

        Database.createRazorPayOrder(
                this,
                payablePricePO,
                "INR",
                "#~order~" + orderNumberPO,
                new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        orderBtn.setEnabled(false);
                    }

                    @Override
                    public void resolved(JSONObject o) {
                        try {
                            JSONObject paymentOptions = new JSONObject();
                            paymentOptions.put("name", getString(R.string.app_name));
                            paymentOptions.put("description", "#~order~" + orderNumberPO);
                            paymentOptions.put("order_id", o.getJSONObject("orderDetails").getString("id"));
                            paymentOptions.put("theme.color", "#FF9C9C");
                            paymentOptions.put("currency", "INR");
                            paymentOptions.put("amount", Integer.toString(payablePricePO * 100));

                            JSONObject prefillOptions = new JSONObject();
                            prefillOptions.put("name", namePO);
                            prefillOptions.put("email", Auth.getAuthUserEmail());
                            prefillOptions.put("contact", phonePO.substring(3));
                            paymentOptions.put("prefill", prefillOptions);

                            JSONObject retryOptions = new JSONObject();
                            retryOptions.put("enabled", true);
                            retryOptions.put("max_count", 2);
                            prefillOptions.put("retry", retryOptions);

                            checkout.open(activity, paymentOptions);
                        } catch (Exception e) {
                            Toast.makeText(activity, "Unable to make a payment.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(activity, "Unable to make a payment.", Toast.LENGTH_SHORT).show();
                        orderBtn.setEnabled(true);
                    }
                }
        );
    }

    @Override
    public void onPaymentSuccess(String s) {
        placeOrder(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed.", Toast.LENGTH_SHORT).show();
    }

    private List<CheckoutCartItem> createOrderItemList() {
        List<CheckoutCartItem> checkoutCartItems = new ArrayList<>();
        for (CartItem cartItem : items) {
            StringBuilder orderName = new StringBuilder();
            orderName.append(cartItem.getFood_data().getName()).append("(").append(cartItem.getQuantity()).append(")");
            if (!cartItem.getTopping_ids().equals("None")) {
                orderName.append(" + Toppings(");
                for (Topping topping : cartItem.getToppings()) {
                    orderName.append(topping.getName()).append(", ");
                }
                orderName.replace(orderName.length() - 2, orderName.length(), ")");
            }
            CheckoutCartItem item = new CheckoutCartItem(orderName.toString(), cartItem.getTotal_price());
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

            p1 = new GeoPoint(location.getLatitude(), location.getLongitude());

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
            return addresses.get(0).getAddressLine(0);
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
            isLocationListenerCalled = true;
            setCurrentLocation();
        }
    }

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location -> {
                if (isLocationListenerCalled) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    String myAddress = getAddressFromLatLong(location.getLatitude(), location.getLongitude());
                    double distance = calculateDistance(location.getLatitude(), location.getLongitude());
                    setPayablePrice(distance);

                    addressTxt.setText(myAddress);

                    isLocationListenerCalled = false;
                }
            });
        } else {
            Toast.makeText(CheckoutActivity.this, "Location is required for delivery purpose.", Toast.LENGTH_SHORT).show();
        }
    }
}