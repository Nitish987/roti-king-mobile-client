package com.rotiking.client.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rotiking.client.R;
import com.rotiking.client.adapters.OrderRecyclerAdapter;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.models.Order;

public class OrdersFragment extends Fragment {
    private View view;
    private RecyclerView ordersRV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersRV = view.findViewById(R.id.order_rv);
        ordersRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        ordersRV.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseFirestore.getInstance().collection("orders").whereEqualTo("uid", Auth.getAuthUserUid()).orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order.class).build();
        OrderRecyclerAdapter adapter = new OrderRecyclerAdapter(options);
        ordersRV.setAdapter(adapter);
        adapter.startListening();
    }
}