package com.example.androidappforiot;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.androidappforiot.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 참고자료
 * api 호출 : https://jbin0512.tistory.com/118
 */


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
//    View marker_root_view;

//    TextView tv_marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 쓰레기통 위치들 받아오기
//        String idm = String.valueOf(new RestAPITask("https://907epdyfgc.execute-api.ap-northeast-2.amazonaws.com/prod/trash").execute());

        new Thread() {
            @Override
            public void run() {

                String urlAddress = "https://907epdyfgc.execute-api.ap-northeast-2.amazonaws.com/prod/trash";

                try {
                    URL url = new URL(urlAddress);

                    InputStream is = url.openStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();
                    while (line != null) {
                        buffer.append(line + "\n");
                        line = reader.readLine();
                    }

                    String jsonData = buffer.toString();

                    // jsonData를 먼저 JSONObject 형태로 바꾼다.
                    JSONObject obj = new JSONObject(jsonData);
                    // obj의 "boxOfficeResult"의 JSONObject를 추출
                    JSONArray apiResult = (JSONArray) obj.get("Items");
                    // boxOfficeResult의 JSONObject에서 "dailyBoxOfficeList"의 JSONArray 추출
//                    JSONArray dailyBoxOfficeList = (JSONArray)boxOfficeResult.get("dailyBoxOfficeList");

                    for (int i = 0; i < apiResult.length(); i++) {

                        JSONObject temp = apiResult.getJSONObject(i);


                        String address = temp.getString("address");
                        double latitude = Double.parseDouble(temp.getString("latitude"));
                        double longitude = Double.parseDouble(temp.getString("longitude"));
                        String name = temp.getString("name");
                        double id = Double.parseDouble(temp.getString("id"));
                        int intId = (int) id;
//                        MarkerItem markerItem = new MarkerItem(latitude, longitude, name);

//                        items.add(movieNm);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//                                String formatted = NumberFormat.getCurrencyInstance().format((name));
//                                tv_marker.setText(formatted);

                                LatLng position = new LatLng(latitude, longitude);

                                mMap.addMarker(
                                        new MarkerOptions()
                                                .position(position)
                                                .title(name)
                                                .snippet(String.valueOf(intId)));


                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // 마커 클릭에 대한 이벤트 처리
        mMap.setOnMarkerClickListener(this);
        LatLng startMakrer = new LatLng(37.5820884891329, 127.0108260376386);

        /**
         * zoom level 별 지도 크기 예시
         *  1 : 세계
         *  5 : 대륙
         *  10 : 시
         *  15 : 거리
         *  20 : 건물
         *  */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startMakrer, 17));

    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String id = marker.getSnippet();
        int idd = Integer.parseInt(id);


        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());

        mMap.animateCamera(center);
        marker.showInfoWindow(); // 정보 창 열기

        String url = "https://907epdyfgc.execute-api.ap-northeast-2.amazonaws.com/prod/trash/shadow";
        // 새로운 액티비티 실행
        Intent intent = new Intent(MapsActivity.this, ClickActivity.class);
        intent.putExtra("id",idd );//getLogsURL.getText().toString());
        startActivity(intent);
        //
//        changeSelectedMarker(marker);
//
        return true;

    }


}