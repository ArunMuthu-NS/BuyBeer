package com.example.buybeer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.R.attr.key;
import static com.example.buybeer.R.id.styleVal;


public class MainActivity extends AppCompatActivity {
    static String data;
    static TextView tv;
    static ProgressBar progress;
    static ListViewAdapter adapter;
    static ListView lv;
    static Context context;
    static EditText editText;
    static ImageView search,close;
    static RelativeLayout layout;
    static ArrayList<JSONObject> listItems;
    boolean sortOrder = true;
    static Set<String> filterValues = new HashSet<String>();
    static boolean selectedFilters[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.progress = (ProgressBar) findViewById(R.id.progressBar_cyclic);
        this.context = this;
        this.lv = (ListView) findViewById(R.id.list);
        this.editText = (EditText) findViewById(R.id.findBeer);
        this.layout = (RelativeLayout) findViewById(R.id.layout);
        new MyAsyncTask().execute();
        initializeActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort:
                if(data != null && !data.equals("")) {
                    adapter.sort(sortOrder);
                    if (sortOrder)
                        item.setIcon(R.drawable.ascending);
                    else
                        item.setIcon(R.drawable.descending);
                    sortOrder = !sortOrder;
                }
            return true;
            case R.id.filter:
                if(data!=null && !data.equals(""))
                    showFilters();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    private void showFilters(){
        final String arr[] =  new String[filterValues.size()];
        final ArrayList<String> list = new ArrayList<String>();
        int i = 0;
        for(Iterator<String> a = filterValues.iterator(); a.hasNext(); )
            arr[i++] = a.next();


        AlertDialog.Builder filterList = new AlertDialog.Builder(this);
        filterList.setTitle("Choose Beer Style");
        filterList.setMultiChoiceItems(arr, selectedFilters, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    list.add(arr[i]);
                }
                else{
                    list.remove(arr[i]);
                }
            }
        });
        filterList.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("List Value",list.size()+"");
                filterBeers(list);
            }
        });
        filterList.create().show();
    }

    private void filterBeers(ArrayList<String> list){
        Log.i("Worst Value",list.size()+"");
        if(list.size()>0){
            adapter.filter(list);
        }
        else{
            adapter.reinit();
        }
    }

    private void initializeActions(){
        search = (ImageView) findViewById(R.id.search_bar);
        close = (ImageView) findViewById(R.id.clear);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                if(text != null && !text.equals("")) {
                    close.setVisibility(View.VISIBLE);
                    adapter.filter(text);
                    editText.clearFocus();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.reinit();
                editText.setText("");
                close.setVisibility(View.INVISIBLE);
            }
        });
    }

    public static void updateList(){
        try {
            Log.i("Initialzing List","New One");
            listItems = getArrayListFromJSONArray(new JSONArray(data));
            adapter = new ListViewAdapter(context,listItems);
            lv.setAdapter(adapter);
        }
        catch(JSONException e){
            Log.i("JSON Exception",""+e);
        }
    }

    private static ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray){
        selectedFilters = new boolean[jsonArray.length()];
        ArrayList<JSONObject> aList=new ArrayList<JSONObject>();
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    aList.add(jsonArray.getJSONObject(i));
                    filterValues.add(jsonArray.getJSONObject(i).getString("style"));
                }
            }
        }catch (JSONException je){
            je.printStackTrace();
        }
        return  aList;
    }
}

class MyAsyncTask extends AsyncTask<String,String,String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.progress.setIndeterminate(true);
        MainActivity.progress.setProgress(0);
        MainActivity.progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL("http://starlord.hackerearth.com/beercraft");
            URLConnection dataCon = url.openConnection();
            InputStreamReader in = new InputStreamReader(dataCon.getInputStream());
            BufferedReader bin = new BufferedReader(in);
            String line = "" , result = "";

            while((line = bin.readLine()) != null){
                result += line;
            }
            return result;
        }
        catch(Exception e){
            Log.i("Error",e+"");
            return e.getMessage();
        }
    }

    protected void onPostExecute(String result){
        Log.i("Return Response",result);
        MainActivity.data = result;
        MainActivity.updateList();
        MainActivity.progress.setVisibility(View.INVISIBLE);
        MainActivity.editText.setVisibility(View.VISIBLE);
        MainActivity.search.setVisibility(View.VISIBLE);
    }
}