package com.rotiking.client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.adapters.CheckoutCartItemRecyclerAdapter;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.security.AES128;
import com.rotiking.client.models.CartItem;
import com.rotiking.client.models.CheckoutCartItem;
import com.rotiking.client.models.Order;
import com.rotiking.client.models.Topping;
import com.rotiking.client.utils.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView orderItemRV;
    private ImageButton closeBtn;
    private TextView itemsTxt, orderNumberTxt, totalCartPriceTxt, deliveryCartPriceTxt, totalPayableTxt, nameTxt, phoneTxt, addressTxt, agentNameTxt, agentPhoneTxt, timeTxt, paymentMethodTxt;
    private AppCompatButton cancelOrderBtn;
    private LinearProgressIndicator orderStateIndicator;
    private TextView orderedStateTxt, orderedState, cookingState, dispatchedState, onWayState, deliveredState, deliveryCodeTxt;
    private LinearLayout deliveryAgentDesk, deliveryVerificationDesk;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getStringExtra("ORDER");

        closeBtn = findViewById(R.id.close);
        itemsTxt = findViewById(R.id.items);
        orderNumberTxt = findViewById(R.id.order_number);
        totalCartPriceTxt = findViewById(R.id.total_price);
        deliveryCartPriceTxt = findViewById(R.id.delivery_price);
        totalPayableTxt = findViewById(R.id.payable_price);
        cancelOrderBtn = findViewById(R.id.cancel);
        nameTxt = findViewById(R.id.name);
        phoneTxt = findViewById(R.id.phone);
        addressTxt = findViewById(R.id.address);
        agentNameTxt = findViewById(R.id.agent_name);
        agentPhoneTxt = findViewById(R.id.agent_phone);
        deliveryAgentDesk = findViewById(R.id.delivery_agent_desk);
        timeTxt = findViewById(R.id.time);
        paymentMethodTxt = findViewById(R.id.payment_method);
        orderStateIndicator = findViewById(R.id.order_state_indicator);
        orderedStateTxt = findViewById(R.id.order_state_text);
        orderedState = findViewById(R.id.ordered_state);
        cookingState = findViewById(R.id.cooking_state);
        dispatchedState = findViewById(R.id.dispatched_state);
        onWayState = findViewById(R.id.on_way_state);
        deliveredState = findViewById(R.id.delivered_state);
        deliveryVerificationDesk = findViewById(R.id.delivery_verification_desk);
        deliveryCodeTxt = findViewById(R.id.delivery_verification_code);

        orderItemRV = findViewById(R.id.ordered_item_rv);
        orderItemRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderItemRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection("orders").document(orderId).addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                Order order = value.toObject(Order.class);

                assert order != null;
                CheckoutCartItemRecyclerAdapter adapter = new CheckoutCartItemRecyclerAdapter(createOrderItemList(order.getItems()));
                orderItemRV.setAdapter(adapter);

                String tcp = "\u20B9 " + order.getTotalPrice();
                totalCartPriceTxt.setText(tcp);

                String dp_ = "\u20B9 " + order.getDeliveryPrice();
                deliveryCartPriceTxt.setText(dp_);

                String tp_ = "\u20B9 " + order.getPayablePrice();
                totalPayableTxt.setText(tp_);

                String c_ = order.getItems().size() + " Food Items";
                itemsTxt.setText(c_);

                String o_ = "#~order~" + order.getOrderNumber();
                orderNumberTxt.setText(o_);

                String t_ = DateParser.parse(new Date(order.getTime()));
                timeTxt.setText(t_);

                paymentMethodTxt.setText(order.getPaymentMethod());
                nameTxt.setText(order.getName());
                addressTxt.setText(order.getAddress());
                phoneTxt.setText(order.getPhone());
                orderedStateTxt.setText(getOrderStateTxt(order.getOrderState()));

                if (order.getOrderState() > 1) {
                    deliveryAgentDesk.setVisibility(View.VISIBLE);

                    agentNameTxt.setText(order.getAgentName());
                    agentPhoneTxt.setText(order.getAgentPhone());

                    deliveryVerificationDesk.setVisibility(View.VISIBLE);

                    String code = AES128.decrypt(Auth.ENCRYPTION_KEY, order.getSecureNumber());
                    deliveryCodeTxt.setText(code);
                }

                switch (order.getOrderState()) {
                    case 0:
                        orderStateIndicator.setProgressCompat(0, true);
                        orderedState.getBackground().setTint(getColor(R.color.red));
                        break;
                    case 1:
                        orderStateIndicator.setProgressCompat(25, true);
                        orderedState.getBackground().setTint(getColor(R.color.red));
                        cookingState.getBackground().setTint(getColor(R.color.red));
                        break;
                    case 2:
                        orderStateIndicator.setProgressCompat(50, true);
                        orderedState.getBackground().setTint(getColor(R.color.red));
                        cookingState.getBackground().setTint(getColor(R.color.red));
                        dispatchedState.getBackground().setTint(getColor(R.color.red));
                        break;
                    case 3:
                        orderStateIndicator.setProgressCompat(75, true);
                        orderedState.getBackground().setTint(getColor(R.color.red));
                        cookingState.getBackground().setTint(getColor(R.color.red));
                        dispatchedState.getBackground().setTint(getColor(R.color.red));
                        onWayState.getBackground().setTint(getColor(R.color.red));
                        break;
                    case 4:
                        orderStateIndicator.setProgressCompat(100, true);
                        orderedState.getBackground().setTint(getColor(R.color.red));
                        cookingState.getBackground().setTint(getColor(R.color.red));
                        dispatchedState.getBackground().setTint(getColor(R.color.red));
                        onWayState.getBackground().setTint(getColor(R.color.red));
                        deliveredState.getBackground().setTint(getColor(R.color.red));
                        deliveryAgentDesk.setVisibility(View.GONE);
                        deliveryVerificationDesk.setVisibility(View.GONE);
                        cancelOrderBtn.setVisibility(View.INVISIBLE);
                        cancelOrderBtn.setEnabled(false);
                        break;
                }

                if (!order.isOrderSuccess()) {
                    orderStateIndicator.setProgressCompat(0, true);
                    String cancelMsg = "Your Order was Canceled.";
                    cancelOrderBtn.setText(cancelMsg);
                    cancelOrderBtn.setEnabled(false);
                    orderedStateTxt.setText(cancelMsg);
                    deliveryAgentDesk.setVisibility(View.GONE);
                    deliveryVerificationDesk.setVisibility(View.GONE);
                }
            }
        });

        cancelOrderBtn.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Order Cancel");
            alert.setMessage("Are you sure, you want to cancel your order.");
            alert.setCancelable(true);
            alert.setPositiveButton("Yes", (dialogInterface, i) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("orderSuccess", false);
                FirebaseFirestore.getInstance().collection("orders").document(orderId).update(map)
                        .addOnSuccessListener(unused -> Toast.makeText(this, "Your Order was canceled.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Unable to cancel Order.", Toast.LENGTH_SHORT).show());
            });
            alert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        });

        closeBtn.setOnClickListener(view -> finish());
    }

    private List<CheckoutCartItem> createOrderItemList(List<CartItem> items) {
        List<CheckoutCartItem> checkoutCartItems = new ArrayList<>();
        for (CartItem cartItem : items) {
            StringBuilder orderName = new StringBuilder();
            orderName.append(cartItem.getFood_data().getName()).append("(").append(cartItem.getQuantity()).append(")");
            if (!cartItem.getTopping_ids().equals("None")) {
                orderName.append(" + Toppings(");
                for (Topping topping: cartItem.getToppings()) {
                    orderName.append(topping.getName()).append(", ");
                }
                orderName.replace(orderName.length() - 2, orderName.length(), ")");
            }
            CheckoutCartItem item = new CheckoutCartItem(orderName.toString(), cartItem.getTotal_price());
            checkoutCartItems.add(item);
        }
        return checkoutCartItems;
    }

    private String getOrderStateTxt(int state) {
        switch (state) {
            case 0: return  "Ordered...";
            case 1: return  "Cooking...";
            case 2: return  "Dispatched...";
            case 3: return  "On way...";
            case 4: return  "Delivered...";
            default: return "";
        }
    }
}