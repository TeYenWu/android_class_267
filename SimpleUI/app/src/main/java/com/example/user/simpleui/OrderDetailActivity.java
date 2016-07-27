package com.example.user.simpleui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Intent intent = getIntent();
        Order order = intent.getParcelableExtra("order");

        Log.d("debug", order.note);
        for(DrinkOrder drinkOrder : order.drinkOrders)
        {
            Log.d("debug", drinkOrder.note);
            Log.d("debug", drinkOrder.drink.objectId);
        }

        Log.d("debug", order.storeInfo);

    }
}
