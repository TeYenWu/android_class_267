package com.example.user.simpleui;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2016/6/13.
 */

@ParseClassName("Drink")
public class Drink extends ParseObject{

    int imageId;

    public String getName(){  return  getString("name");}

    public void setName(String name) {
       put("name", name);
    }
    public int getmPrice(){  return  getInt("mPrice");}

    public void setmPrice(int mPrice) {
        put("mPrice", mPrice);
    }

    public int getlPrice(){  return  getInt("lPrice");}

    public void setlPrice(int lPrice) {
        put("lPrice", lPrice);
    }

    public void setImage(ParseFile file){ put("image", file);}
    public ParseFile getImage(){ return getParseFile("image"); }

    public static ParseQuery<Drink> getQuery(){return ParseQuery.getQuery(Drink.class);}

    public JSONObject getData()
    {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", getName());
            jsonObject.put("price", getmPrice());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
