package com.example.user.simpleui;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wudeyan on 6/16/16.
 */
public class DrinkOrder {

    String ice = "正常";
    String sugar = "正常";
    String note = "";
    String drinkName;
    int lNumber = 0;
    int mNumber = 1;
    int lPrice;
    int mPrice;

    public JSONObject getJsonObject()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ice", ice);
            jsonObject.put("sugar", sugar);
            jsonObject.put("lNumber", lNumber);
            jsonObject.put("mNumber", mNumber);
            jsonObject.put("lPrice", lPrice);
            jsonObject.put("mPrice", mPrice);
            jsonObject.put("drinkName",drinkName);
            jsonObject.put("note", note);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static DrinkOrder newInstanceWithJsonObject(String data)
    {
        try {
            JSONObject jsonObject = new JSONObject(data);
            DrinkOrder drinkOrder = new DrinkOrder();
            drinkOrder.ice = jsonObject.getString("ice");
            drinkOrder.drinkName = jsonObject.getString("drinkName");
            drinkOrder.sugar = jsonObject.getString("sugar");
            drinkOrder.note = jsonObject.getString("note");
            drinkOrder.mPrice = jsonObject.getInt("mPrice");
            drinkOrder.mNumber = jsonObject.getInt("mNumber");
            drinkOrder.lPrice = jsonObject.getInt("lPrice");
            drinkOrder.lNumber = jsonObject.getInt("lNumber");
            return drinkOrder;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
