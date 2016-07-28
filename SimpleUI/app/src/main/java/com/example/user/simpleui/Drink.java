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
@ParseClassName("Drink")
public class Drink extends ParseObject implements Parcelable {

    public void setName(String name) {
        put("name", name);
    }

    public String getName() {
        return getString("name");
    }

    public void setmPrice(int mPrice) {
        put("mPrice", mPrice);
    }

    public int getmPrice() {
        return getInt("mPrice");
    }

    public int getlPrice() {
        return getInt("lPrice");
    }

    public void setlPrice(int lPrice) {
        this.put("lPrice", lPrice);
    }

    public ParseFile getImage(){ return getParseFile("image");}

    private void setImage(File file)
    {
        this.put("image", new ParseFile(file));
    }

    public ParseObject getParseObjectWithoutData()
    {
        return ParseObject.createWithoutData(this.getClass(), this.getObjectId());
    }

    public static void syncDrinksFromRemote(final FindCallback<Drink> callback)
    {
        Drink.getQuery().findInBackground(new FindCallback<Drink>() {
            @Override
            public void done(final List<Drink> objects, ParseException e) {
                if(e == null)
                {
                    Drink.unpinAllInBackground("Drink", new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                Drink.pinAllInBackground("Drink", objects);
                            }
                        }
                    });
                    callback.done(objects, e);
                }
                else
                {
                    Drink.getQuery().fromLocalDatastore().findInBackground(callback);
                }
            }
        });
    }

    public static ParseQuery<Drink> getQuery(){ return  ParseQuery.getQuery(Drink.class);}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.getObjectId());
        dest.writeString(this.getName());
        dest.writeInt(this.getlPrice());
        dest.writeInt(this.getmPrice());
        try {
            dest.writeSerializable(this.getImage().getFile());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static Drink getDrinkFromCache(String objectId)
    {
        try {
            return getQuery().fromLocalDatastore().get(objectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Drink()
    {
        super();
    }

    protected Drink(String objectId, Parcel in) {
        super();
        this.setObjectId(objectId);
//        this.set
        this.setName(in.readString());
        this.setlPrice(in.readInt());
        this.setmPrice(in.readInt());
        File file = (File) in.readSerializable();
        if(file != null)
            this.setImage(file);
    }

    public static final Creator<Drink> CREATOR = new Creator<Drink>() {
        @Override
        public Drink createFromParcel(Parcel source) {
            String objectId = source.readString();
            Drink drink = Drink.getDrinkFromCache(objectId);
            if(drink != null)
                return drink;
            return new Drink(objectId, source);
        }

        @Override
        public Drink[] newArray(int size) {
            return new Drink[size];
        }
    };
}
