package com.example.user.simpleui;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wudeyan on 6/16/16.
 */
public class DrinkOrder implements Parcelable {

    String ice = "正常";
    String sugar = "正常";
    String note = "";

    Drink drink;

    int lNumber = 0;
    int mNumber = 1;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ice);
        dest.writeString(this.sugar);
        dest.writeString(this.note);
        dest.writeParcelable(this.drink, flags);
        dest.writeInt(this.lNumber);
        dest.writeInt(this.mNumber);
    }

    public DrinkOrder(Drink drink) {
        this.drink = drink;
    }

    public DrinkOrder() {
    }

    protected DrinkOrder(Parcel in) {
        this.ice = in.readString();
        this.sugar = in.readString();
        this.note = in.readString();
        this.drink = in.readParcelable(Drink.class.getClassLoader());
        this.lNumber = in.readInt();
        this.mNumber = in.readInt();
    }

    public static final Parcelable.Creator<DrinkOrder> CREATOR = new Parcelable.Creator<DrinkOrder>() {
        @Override
        public DrinkOrder createFromParcel(Parcel source) {
            return new DrinkOrder(source);
        }

        @Override
        public DrinkOrder[] newArray(int size) {
            return new DrinkOrder[size];
        }
    };

    public static List<ParseObject> getParseObjectsFromDrinkOrders(List<DrinkOrder> drinkOrders)
    {
        List<ParseObject> parseObjects = new ArrayList<>();
        for(DrinkOrder drinkOrder : drinkOrders)
        {
            ParseObject parseObject = new ParseObject("DrinkOrder");
            parseObject.put("ice", drinkOrder.ice);
            parseObject.put("sugar", drinkOrder.sugar);
            parseObject.put("note", drinkOrder.note);
            parseObject.put("lNumber", drinkOrder.lNumber);
            parseObject.put("mNumber", drinkOrder.mNumber);
            parseObject.put("drink", drinkOrder.drink.getParseObjectWithoutData());
            parseObjects.add(parseObject);
        }
        return parseObjects;
    }

    public static List<DrinkOrder> getDrinkOrdersFromParseObjects(List<ParseObject> objects)
    {
        List<DrinkOrder> drinkOrders = new ArrayList<>();
        if(objects != null)
            for(ParseObject object: objects)
            {
                if(object.getClassName().equals("DrinkOrder"))
                {
                    DrinkOrder order = new DrinkOrder();
                    order.note = object.getString("note");
                    order.ice = object.getString("ice");
                    order.sugar = object.getString("sugar");
                    order.lNumber = object.getInt("lNumber");
                    order.mNumber = object.getInt("mNumber");

                    ParseObject o = object.getParseObject("drink") ;
                    order.drink = (Drink) o;
//


                    drinkOrders.add(order);
                }
            }
        return  drinkOrders;
    }
}
