package com.example.user.simpleui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DrinkMenuActivity extends AppCompatActivity implements DrinkOrderDialog.OnDrinkOrderListener {

    ListView drinkListView;
    TextView priceTextView;

    List<Drink> drinks = new ArrayList<>();
    ArrayList<DrinkOrder> drinkOrders = new ArrayList<>();

    // SET DATA
    String[] names = {"冬瓜紅茶", "玫瑰鹽奶蓋紅茶", "珍珠紅茶拿鐵", "紅茶拿鐵"};
    int[] mPrices = {25,35,45,35};
    int[] lPrices = {35, 45, 55, 45};
    int[] imageId = {R.drawable.drink1, R.drawable.drink2, R.drawable.drink3, R.drawable.drink4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
        setData();

        //get  UI component
        drinkListView = (ListView)findViewById(R.id.drinkListView);
        priceTextView = (TextView)findViewById(R.id.priceTextView);

        setupListView();

        drinkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DrinkAdapter drinkAdapter = (DrinkAdapter)parent.getAdapter();
                    Drink   drink = (Drink)drinkAdapter.getItem(position);
                    showDrinkOderDialog(drink);
                }
        });

        Log.d("Debug", "Drink Menu Acitivity OnCreate");
    }
    private void showDrinkOderDialog(Drink drink) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        DrinkOrder drinkOrder = new DrinkOrder();
        Boolean flag = false;

        for (DrinkOrder order : drinkOrders)
        {
            if(order.drinkName.equals(drink.getName()))
            {
                drinkOrder = order;
                flag = true;
                break;
            }
        }
        if(!flag)
        {
            drinkOrder.mPrice = drink.getmPrice();
            drinkOrder.lPrice = drink.getlPrice();
            drinkOrder.drinkName = drink.getName();
        }

        DrinkOrderDialog orderDialog = DrinkOrderDialog.newInstance(drinkOrder);
        orderDialog.show(ft, "DrinkOrderDialog");
    }

    private void updateTotalPrice()
    {
        int total = 0;
        for (DrinkOrder order : drinkOrders)
        {
            total += order.mPrice * order.mNumber + order.lPrice * order.lNumber;
        }
        priceTextView.setText(String.valueOf(total));
    }

    private void setData()
    {
        Drink.getQuery().findInBackground(new FindCallback<Drink>() {
            @Override
            public void done(List<Drink> objects, ParseException e) {
                if(e == null)
                {
                    drinks = objects;
                    setupListView();
                }
            }
        });
//        for(int i = 0; i < 4; i++)
//        {
//            Drink drink = new Drink();
//            drink.name = names[i];
//            drink.mPrice = mPrices[i];
//            drink.lPrice = lPrices[i];
//            drink.imageId = imageId[i];
//            drinks.add(drink);
//        }
    }

    public void setupListView()
    {
        DrinkAdapter adapter = new DrinkAdapter(this, drinks);
        drinkListView.setAdapter(adapter);
    }

    public void done(View view)
    {
        Intent intent = new Intent();

        JSONArray array = new JSONArray();
        for (DrinkOrder order : drinkOrders)
        {
            JSONObject object = order.getJsonObject();
            array.put(object);
        }

        intent.putExtra("results", array.toString());

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Debug", "Drink Menu Acitivity OnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Debug", "Drink Menu Acitivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Debug", "Drink Menu Acitivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Debug", "Drink Menu Acitivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Debug", "Drink Menu Acitivity onDestroy");
    }

    protected void onRestart()
    {
        super.onRestart();
        Log.d("Debug", "Drink Menu Acitivity onRestart");
    }

    @Override
    public void OnDrinkOrderFinished(DrinkOrder drinkOrder) {
        for(int i = 0; i < drinkOrders.size(); i++)
        {
            if(drinkOrders.get(i).drinkName.equals(drinkOrder.drinkName))
            {
                drinkOrders.set(i, drinkOrder);
                updateTotalPrice();
                return;
            }
        }

        drinkOrders.add(drinkOrder);
        updateTotalPrice();
    }
}
