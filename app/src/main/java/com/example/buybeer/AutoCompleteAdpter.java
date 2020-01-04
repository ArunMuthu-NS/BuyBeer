package com.example.buybeer;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by arun-4399 on 30/06/18.
 */

public class AutoCompleteAdpter extends ArrayAdapter<JSONObject> {
    private ArrayList<JSONObject> data;
    private int dataSize;
    private Context context;
    public AutoCompleteAdpter(@NonNull Context context, ArrayList<JSONObject> data) {
        super(context, R.layout.activity_autocomplete,data);
        this.data = data;
        this.dataSize = data.size();
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try{
            if(convertView==null){
                LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.activity_autocomplete, parent, false);
            }

            JSONObject objectItem = data.get(position);

            TextView textViewItem = (TextView) convertView.findViewById(R.id.autoText);
            textViewItem.setText(objectItem.getString("name"));
            textViewItem.setTextSize(15);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
