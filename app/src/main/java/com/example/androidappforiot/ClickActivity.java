package com.example.androidappforiot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.filament.View;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ClickActivity extends AppCompatActivity {

    String stringId;
    int id;
    final static String TAG = "Marker Click Event";
    protected Activity activity;

    String returnMsgString="";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        //id = Integer.parseInt(stringId);

        Log.i(TAG, "id="+id);

        BarChart chart2;
        chart2 = (BarChart)findViewById(R.id.tab1_chart_2);




        //TextView textView = (TextView)findViewById(R.id.textView2);

        //Log.d(this.getClass().getName(), (String)textView.getText());


        new Thread() {
            @Override
            public void run() {

                String urlAddress = "https://907epdyfgc.execute-api.ap-northeast-2.amazonaws.com/prod/trash/one/click";
                StringBuffer sbParams = new StringBuffer();

                try {
                    URL url = new URL(urlAddress);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

                    // [2-1]. urlConn ??????.
                    urlConn.setConnectTimeout(15000);
                    urlConn.setReadTimeout(5000);
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestMethod("POST"); // URL ????????? ?????? ????????? ?????? : POST.
                    urlConn.setRequestProperty("Accept","application/json"); // Accept-Charset ??????.
                    urlConn.setRequestProperty("Content-Type","applicaiton/json;utf-8");
//                    urlConn.setRequestProperty("apikey", ""); // ""?????? apikey??? ??????
                    urlConn.setDoOutput(true);

                    // Json ??????
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",id);

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream()));
                    bw.write(jsonObject.toString());
                    bw.flush();
                    bw.close();

                    // ?????? ??????
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    String returnMsg = in.readLine();

//                    returnMsgString = returnMsg;
                    System.out.println("???????????????: " + returnMsg);

                    // jsonData??? ?????? JSONObject ????????? ?????????.
                    JSONObject obj = new JSONObject(returnMsg);
                    // obj??? "boxOfficeResult"??? JSONObject??? ??????
                    String glass = obj.getString("glass");
                    double dglass = Double.parseDouble(glass);

                    String normal = obj.getString("normal");
                    double dnormal = Double.parseDouble(normal);

                    String metal = obj.getString("metal");
                    double dmetal = Double.parseDouble(metal);

                    String plastic = obj.getString("plastic");
                    double dplastic = Double.parseDouble(plastic);

//
                    // ??????????????? ????????? String
                    returnMsgString = glass;

                    // ??????????????? Toast ????????? ?????? ???
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), returnMsg,Toast.LENGTH_LONG);

                            // ??????????????? ??????
                           // textView.setText(returnMsgString);
                            chart2.clearChart();

                            chart2.addBar(new BarModel("glass", (float) dglass, 0xFF56B7F1));
                            chart2.addBar(new BarModel("metal", (float) dmetal, 0xFF56B7F1));
                            chart2.addBar(new BarModel("plastic", (float) dplastic, 0xFF56B7F1));
                            chart2.addBar(new BarModel("normal", (float) dnormal, 0xFF56B7F1));

                            chart2.startAnimation();



                        }
                    }, 0);




                    // [2-2]. parameter ?????? ??? ????????? ????????????.
//                    String strParams = sbParams.toString(); //sbParams??? ????????? ?????????????????? ??????????????? ??????. ???)id=id1&pw=123;
//                    OutputStream os = urlConn.getOutputStream();
//                    os.write(strParams.getBytes("UTF-8")); // ?????? ???????????? ??????.
//                    os.flush(); // ?????? ???????????? ?????????(?????????)?????? ????????? ??? ?????? ?????? ???????????? ?????? ??????.
//                    os.close(); // ?????? ???????????? ?????? ?????? ????????? ????????? ??????.

                    // [2-3]. ?????? ?????? ??????.
                    // ?????? ??? null??? ???????????? ???????????? ??????.
//                    if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
//                        System.out.println("?????? ??????");
//
//                    // [2-4]. ????????? ????????? ??????.
//                    // ????????? URL??? ???????????? BufferedReader??? ?????????.
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
//
//                    // ???????????? ????????? ??? ?????? ?????? ??????.
//                    String line;
//                    String page = "";
//
//                    // ????????? ????????? ?????????.
//                    while ((line = reader.readLine()) != null){
//                        page += line;
//                    }
//
////                    return page;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        //ArrayList<Tag> arrayList = getArrayListFromJSONString(returnMsgString);
        //ArrayList<String> output = new ArrayList();

       // output.add(returnMsgString);

//        final ArrayAdapter adapter = new ArrayAdapter(activity,
//                android.R.layout.simple_list_item_1,
//                output);
//                arrayList.toArray());
//        ListView txtList = activity.findViewById(R.id.testList);
//        txtList.setAdapter(adapter);
//        txtList.setDividerHeight(10);
    }



    protected ArrayList<Tag> getArrayListFromJSONString(String jsonString) {
        ArrayList<Tag> output = new ArrayList();

        try {
            // ?????? double-quote??? ????????? double-quote ??????
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" ??? \"??? ??????
            jsonString = jsonString.replace("\\\"","\"");

            Log.i(TAG, "jsonString="+jsonString);

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject)jsonArray.get(i);

                Tag thing = new Tag(jsonObject.getString("temperature"),
                        jsonObject.getString("LED"),
                        jsonObject.getString("timestamp"));

                output.add(thing);
            }

        } catch (JSONException e) {
            //Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }



    class Tag {
        String temperature;
        String LED;
        String timestamp;

        public Tag(String temp, String led, String time) {
            temperature = temp;
            LED = led;
            timestamp = time;
        }

        public String toString() {
            return String.format("[%s] Temperature: %s, LED: %s", timestamp, temperature, LED);
        }
    }
}