package com.example.user.simpleui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by user on 2016/6/13.
 */
public class DrinkAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<Drink> drinks;

    public DrinkAdapter(Context context, List<Drink> drinks)
    {
        this.inflater = LayoutInflater.from(context);
        this.drinks = drinks;
    }

    @Override
    public int getCount() {
        return drinks.size();
    }

    @Override
    public Object getItem(int position) {
        return drinks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.listview_drink_item, null);
            holder = new Holder();
            holder.drinkNameTextView = (TextView)convertView.findViewById(R.id.noteTextView);
            holder.mPriceTextView = (TextView)convertView.findViewById(R.id.mPriceTextView);
            holder.lPriceTextView = (TextView)convertView.findViewById(R.id.lPriceTextView);
            holder.drinkImageView = (ImageView)convertView.findViewById(R.id.drinkImageView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder)convertView.getTag();
        }

        Drink drink = drinks.get(position);

        holder.drinkNameTextView.setText(drink.getName());
        holder.mPriceTextView.setText(String.valueOf(drink.getmPrice()));
        holder.lPriceTextView.setText(String.valueOf(drink.getlPrice()));

        drink.getImage().getFileInBackground(new GetFileCallback() {
            @Override
            public void done(File file, ParseException e) {
                Picasso.with(inflater.getContext()).load(file).into(holder.drinkImageView);
            }
        });
//        Picasso.with(inflater.getContext()).load(drink.getImage().getUrl()).into(holder.drinkImageView);
//        holder.drinkImageView.setImageResource(drink.imageId);
        return convertView;
    }

    class Holder
    {
        TextView drinkNameTextView;
        TextView mPriceTextView;
        TextView lPriceTextView;
        ImageView drinkImageView;
    }
}
