package com.example.user.simpleui;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/6/6.
 */
public class Order implements Parcelable {

    String note;
    String storeInfo;
    List<DrinkOrder> drinkOrders;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.note);
        dest.writeString(this.storeInfo);
        dest.writeTypedList(this.drinkOrders);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this.note = in.readString();
        this.storeInfo = in.readString();
        this.drinkOrders = in.createTypedArrayList(DrinkOrder.CREATOR);
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };


    public static void getOrdersFromRemote(final OrderFindCallback callback) {

        getParseQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground("Order", objects);
                    List<Order> orders = getOrderListFromParseObjects(objects);
                    callback.done(orders, e);
                }
                else
                {
                    getOrdersFromLocal(callback);
                }

            }
        });

    }

    public static void getOrdersFromLocal(final OrderFindCallback callback)
    {
        getParseQuery().fromLocalDatastore().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                List<Order> orders = getOrderListFromParseObjects(objects);
                callback.done(orders, e);
            }
        });
    }

    public static List<Order> getOrderListFromParseObjects(List<ParseObject> objects)
    {
        List<Order> orders = new ArrayList<>();
        if(objects != null)
            for(ParseObject object: objects)
            {
                if(object.getClassName().equals("Order"))
                {
                    Order order = new Order();
                    order.note = object.getString("note");
                    order.storeInfo = object.getString("storeInfo");
                    List<ParseObject> parseObjects = object.getList("drinkOrders");
                    order.drinkOrders = DrinkOrder.getDrinkOrdersFromParseObjects(parseObjects);
                    orders.add(order);
                }
            }
        return  orders;
    }

    public void saveToRemoteAndLocal(SaveCallback callback)
    {
        ParseObject parseObject = new ParseObject("Order");
        parseObject.put("note", note);
        parseObject.put("storeInfo", storeInfo);
        parseObject.put("drinkOrders", DrinkOrder.getParseObjectsFromDrinkOrders(drinkOrders));
        parseObject.pinInBackground();
        parseObject.saveEventually(callback);
    }

    public static ParseQuery<ParseObject> getParseQuery()
    {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Order");
        parseQuery.include("drinkOrders");
        parseQuery.include("drinkOrders.drink");
        return parseQuery;
    }

    interface OrderFindCallback
    {
        void done(List<Order> orders, ParseException e);
    }
}
