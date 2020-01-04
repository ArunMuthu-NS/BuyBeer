package com.example.buybeer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.buybeer.R.id.sort;

/**
 * Created by arun-4399 on 29/06/18.
 */

public class ListViewAdapter extends ArrayAdapter<JSONObject> {
    private ArrayList<JSONObject> data,originalData;
    private int dataSize;
    private Context context;
    public ListViewAdapter(@NonNull Context context,ArrayList<JSONObject> data) {
        super(context, R.layout.list_item,data);
        this.data = data;
        this.originalData = new ArrayList<JSONObject>();
        this.originalData.addAll(data);
        this.dataSize = data.size();
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        JSONObject obj = null;
        try {
            obj = this.data.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item,parent,false);
            TextView tx1 = (TextView) convertView.findViewById(R.id.textView);
            TextView tx2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView tx3 = (TextView) convertView.findViewById(R.id.textView3);
            tx1.setText(obj.getString("name"));
            tx2.setText("Alcohol Content "+obj.get("abv"));
            tx3.setText(obj.getString("style"));
        }
        catch (JSONException e){
            Log.i("JSON Exception",e+"");
        }
        Log.i("Constructing Data ","JSON "+obj);
        return convertView;
    }

    public void filter(String text){
        if(text != null && !text.equalsIgnoreCase("")) {
            data.clear();
            try {
                for (int i = 0; i < originalData.size(); i++) {
                    Log.i(text + " " + originalData.get(i).getString("name"), originalData.get(i).getString("name").equalsIgnoreCase(text) + "");
                    if (originalData.get(i).getString("name").toLowerCase().contains(text.toLowerCase())) {
                        data.add(originalData.get(i));
                    }
                }
                notifyDataSetChanged();
            } catch (Exception e) {
                Log.i("Search Issue", "" + e);
            }
        }
    }

    public void filter(ArrayList<String> list){
        data.clear();
        Log.i("Filtering Beers",""+originalData.size());
        try {
            for (int i = 0; i < originalData.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    Log.i("Text "+originalData.get(i).getString("style"),list.get(j));
                    if (originalData.get(i).getString("style").toLowerCase().contains(list.get(j).toLowerCase())) {
                        data.add(originalData.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.i("Filter Issue", "" + e);
        }
    }

    public void reinit(){
        data.clear();
        data.addAll(originalData);
        notifyDataSetChanged();
    }

    public void sort(boolean sortOrder){
        try {
            Log.i("Size Of Array Sort",""+data.size());
            mergesort(data, 0, data.size() - 1,sortOrder);
        }
        catch (JSONException JE){
            Log.i("Sort Exception ",JE+"");
        }
        notifyDataSetChanged();
    }

    void merge(ArrayList<JSONObject> A,int start,int mid,int end,boolean sortOrder) throws JSONException{
        ArrayList<JSONObject> Arr = new ArrayList<JSONObject>();
        int k=0,p=start,q=mid+1,i;

        for(i=start;i<=end;i++){
            if(p > mid)
                Arr.add(A.get(q++));
            else if(q > end)
                Arr.add(A.get(p++));
            else{
                Double a = 0.0;
                Double b = 0.0;
                if(!A.get(p).getString("abv").equals("")) a = Double.parseDouble(A.get(p).getString("abv"));
                if(!A.get(q).getString("abv").equals("")) b = Double.parseDouble(A.get(q).getString("abv"));
                if(sortOrder) {
                    if (a < b)
                        Arr.add(A.get(p++));
                    else if (a > b)
                        Arr.add(A.get(q++));
                    else if (A.get(p).getInt("id") < A.get(q).getInt("id"))
                        Arr.add(A.get(p++));
                    else
                        Arr.add(A.get(q++));
                }
                else{
                    if (b < a)
                        Arr.add(A.get(p++));
                    else if (b > a)
                        Arr.add(A.get(q++));
                    else if (A.get(p).getInt("id") > A.get(q).getInt("id"))
                        Arr.add(A.get(p++));
                    else
                        Arr.add(A.get(q++));
                }
            }
        }

        for(p=0;p<Arr.size();p++){
            A.remove(start);
            A.add(start++,Arr.get(p));
        }
    }

    void mergesort(ArrayList<JSONObject> A, int start, int end,boolean sortOrder) throws JSONException{
        if(start < end){
            int mid = (start + end) / 2;
            mergesort(A,start,mid,sortOrder);
            mergesort(A,mid+1,end,sortOrder);
            merge(A,start,mid,end,sortOrder);
        }
    }
}
