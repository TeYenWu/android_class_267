package com.example.user.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CODE_DRINK_MENU_ACTIVITY = 0;
    static final int REQUEST_CODE_LOGIN_ACTIVITY = 1;

    TextView textView;
    EditText editText;
    RadioGroup radioGroup;
    CheckBox checkBox;
    ListView listView;
    Spinner storeSpinner;

    List<Order> orders = new ArrayList<>();
    String drinkName = "black tea";
    String menuResults = "";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TestObject");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        Toast.makeText(MainActivity.this, object.getString("foo"), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        listView = (ListView) findViewById(R.id.listView);
        storeSpinner = (Spinner) findViewById(R.id.spinner);

        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setupOrdersData();
        setupListView();
        setupSpinner();
//        setupFaceBook();

        if(ParseUser.getCurrentUser() == null)
        {
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOGIN_ACTIVITY);
        }

        editText.setText(sharedPreferences.getString("editText", ""));
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = editText.getText().toString();
                editor.putString("editText", text);
                editor.apply();

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    click(v);
                    return true;
                }
                return false;
            }
        });

        textView.setText(sharedPreferences.getString("textView", ""));

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("textView", s.toString());
                editor.apply();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = (Order)parent.getAdapter().getItem(position);
                goToDetailOrder(order);
            }
        });
        Log.d("Debug", "Main Activity OnCreate");
    }

    private void goToDetailOrder(Order order) {
        Intent intent = new Intent();

        intent.setClass(this,OrderDetailActivity.class);

        intent.putExtra("note", order.getNote());
        intent.putExtra("menuResults", order.getMenuResults());
        intent.putExtra("storeInfo", order.getStoreInfo());

        startActivity(intent);
    }

    void setupFaceBook()
    {
        callbackManager = CallbackManager.Factory.create();

        final LoginButton loginButton = (LoginButton)findViewById(R.id.loginButton);

        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                Log.d("Debug", accessToken.getPermissions().toString());

                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Debug", object.toString());
                        try {
                            textView.setText(object.getString("name"));

                            if (object.has("email")) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("fields", "email, id, name");
                request.setParameters(bundle);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null)
        {

        }
    }

    void setupListView()
    {

        OrderAdapter adapter = new OrderAdapter(this, orders);
        listView.setAdapter(adapter);
    }

    private void setupOrdersData() {
//        String content = Utils.readFile(this, "history");
//        String[] datas = content.split("\n");
//        for(int i = 0 ; i < datas.length ; i++)
//        {
//            Order order = Order.newInstanceWithData(datas[i]);
//            if(order != null)
//            {
//                orders.add(order);
//            }
//        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        final FindCallback<Order> callback = new FindCallback<Order>() {
            @Override
            public void done(List<Order> objects, ParseException e) {
                if(e == null) {
                    orders = objects;
                    setupListView();
                }
            }
        };


        if (info != null && info.isConnected()) {
            Order.getOrdersFromRemote(new FindCallback<Order>() {
                @Override
                public void done(List<Order> objects, ParseException e) {
                    if(e!=null)
                    {
                        Toast.makeText(MainActivity.this, "Sync Failed", Toast.LENGTH_LONG).show();
                        Order.getQuery().fromLocalDatastore().findInBackground(callback);
                    }
                    else
                    {
                        callback.done(objects, e);
                    }
                }
            });
        } else{
            Order.getQuery().fromLocalDatastore().findInBackground(callback);
        }
    }

    void setupSpinner()
    {
        String[] data = getResources().getStringArray(R.array.storeInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        storeSpinner.setAdapter(adapter);
    }

    public void click(View view)
    {
        String note = editText.getText().toString();
        Order order = new Order();
        order.setNote(note);
        order.setMenuResults(menuResults);
        order.setStoreInfo((String) storeSpinner.getSelectedItem());
        order.pinInBackground();
        order.saveEventually();
        orders.add(order);

        Utils.writeFile(this, "history", order.getJsonObject().toString());

        textView.setText(note);
        menuResults = "";
        editText.setText("");

        setupListView();
    }

    public void goToMenu(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);
        startActivityForResult(intent, REQUEST_CODE_DRINK_MENU_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_DRINK_MENU_ACTIVITY);
        {
            if(resultCode == RESULT_OK)
            {
                menuResults = data.getStringExtra("results");
                Toast.makeText(this, "完成菜單", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Debug", "Main Activity OnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Debug", "Main Activity onResume");
    }

    @Override
     protected void onPause() {
        super.onPause();
        Log.d("Debug", "Main Activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Debug", "Main Activity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Debug", "Main Activity onDestroy");
    }

    protected void onRestart()
    {
        super.onRestart();
        Log.d("Debug", "Main Activity onRestart");
    }
}
