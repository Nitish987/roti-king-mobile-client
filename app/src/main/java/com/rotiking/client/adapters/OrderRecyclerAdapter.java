package com.rotiking.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.rotiking.client.R;
import com.rotiking.client.models.Order;

import java.util.Date;

public class OrderRecyclerAdapter extends FirestoreRecyclerAdapter<Order, OrderRecyclerAdapter.OrderHolder> {
    public OrderRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderRecyclerAdapter.OrderHolder holder, int position, @NonNull Order model) {
        holder.setOrderNumber(model.getOrderNumber());
        holder.setItems(model.getItems().size());
        holder.setPaymentMethod(model.getPaymentMethod());
        holder.setStatus(model.getOrderState());
        holder.setTime(model.getTime());
        holder.setPayableAmt(model.getPayablePrice());
    }

    @NonNull
    @Override
    public OrderRecyclerAdapter.OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
    }

    public static class OrderHolder extends RecyclerView.ViewHolder {
        private final TextView orderNumber, items, paymentMethod, status, time, payableAmt;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            items = itemView.findViewById(R.id.items);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            status = itemView.findViewById(R.id.status);
            time = itemView.findViewById(R.id.time);
            payableAmt = itemView.findViewById(R.id.payable_amt);
        }

        public void setOrderNumber(int orderNumber) {
            String st = "#~order~" + orderNumber;
            this.orderNumber.setText(st);
        }

        public void setItems(int count) {
            String st = count + " Food Items";
            this.items.setText(st);
        }

        public void setPaymentMethod(String paymentMethod) {
            String st = "Payment Method : " + paymentMethod;
            this.paymentMethod.setText(st);
        }

        public void setStatus(int status) {
            String st = "";
            switch (status) {
                case 0: st = "Ordered..."; break;
                case 1: st = "Cooking..."; break;
                case 2: st = "Dispatched..."; break;
                case 3: st = "On way..."; break;
                case 4: st = "Delivered..."; break;
            }
            this.status.setText(st);
        }

        public void setTime(long time) {
            Date date = new Date(time);
            String st = date.toString();
            this.time.setText(st);
        }

        public void setPayableAmt(int amt) {
            String st = "\u20B9 " + amt;
            this.payableAmt.setText(st);
        }
    }
}
