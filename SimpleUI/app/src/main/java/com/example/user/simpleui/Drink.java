package com.example.user.simpleui;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/6/13.
 */
public class Drink implements Parcelable {

    String name;
    int lPrice;
    int mPrice;
    String imageURL;
    String objectId;

    private ParseFile parseFile;
    private ParseObject parseObject;


    public Drink() {
    }

    public ParseFile getParseFile()
    {
        return parseFile;
    }

    public ParseObject getParseObject()
    {
//        if(parseObject != null)
//            return parseObject;
        return ParseObject.createWithoutData("Drink", objectId);
//        ParseObject parseObject = ParseObjec;
//        parseObject.put("name", name);
//        parseObject.put("lPrice", lPrice);
//        parseObject.put("mPrice", mPrice);
//        return  parseObject;
    }

    public static Drink getDrinkFromParseObject(ParseObject object)
    {
        if(object.getClassName().equals("Drink"))
        {
            final Drink drink = new Drink();
            drink.lPrice = object.getInt("lPrice");
            drink.mPrice = object.getInt("mPrice");
            drink.name = object.getString("name");
            drink.parseFile = object.getParseFile("image");
            drink.imageURL = drink.parseFile.getUrl();
            drink.objectId = object.getObjectId();
//            drink.parseObject = object;
            return  drink;
        }
        return null;
    }


    public static void getDrinksFromRemote(final FindDrinkCallBack callback) {

        getParseQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground("Drink", new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null)
                            {
                                ParseObject.pinAllInBackground("Drink", objects);
                            }
                        }
                    });

                    List<Drink> drinks = getDrinksListFromParseObjects(objects);
                    callback.done(drinks, e);
                }
                else
                {
                    getDrinksFromLocal(callback);
                }

            }
        });

    }

    public static void getDrinksFromLocal(final FindDrinkCallBack callback)
    {
        getParseQuery().fromLocalDatastore().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                List<Drink> drinks = getDrinksListFromParseObjects(objects);
                callback.done(drinks, e);
            }
        });
    }

    public static List<Drink> getDrinksListFromParseObjects(List<ParseObject> objects)
    {
        List<Drink> drinks = new ArrayList<>();
        if(objects != null)
            for(ParseObject object: objects)
            {
                if(object.getClassName().equals("Drink"))
                {
                    Drink drink = getDrinkFromParseObject(object);
                    drinks.add(drink);
                }
            }
        return  drinks;
    }

    public static ParseQuery<ParseObject> getParseQuery()
    {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Drink");
        return parseQuery;
    }

    interface FindDrinkCallBack
    {
        void done(List<Drink> drinks, ParseException e);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.lPrice);
        dest.writeInt(this.mPrice);
        dest.writeString(imageURL);
        dest.writeString(objectId);
    }

    protected Drink(Parcel in) {
        this.name = in.readString();
        this.lPrice = in.readInt();
        this.mPrice = in.readInt();
        this.imageURL = in.readString();
        this.objectId = in.readString();
    }

    public static final Creator<Drink> CREATOR = new Creator<Drink>() {
        @Override
        public Drink createFromParcel(Parcel source) {
            return new Drink(source);
        }

        @Override
        public Drink[] newArray(int size) {
            return new Drink[size];
        }
    };
}
