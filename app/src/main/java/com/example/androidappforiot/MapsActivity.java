package com.example.androidappforiot;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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

        new Thread() {
            @Override
            public void run() {

                String urlAddress = "https://907epdyfgc.execute-api.ap-northeast-2.amazonaws.com/prod/trash/shadow";
                StringBuffer sbParams = new StringBuffer();

                try {
                    URL url = new URL(urlAddress);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

                    // [2-1]. urlConn 설정.
                    urlConn.setConnectTimeout(15000);
                    urlConn.setReadTimeout(5000);
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                    urlConn.setRequestProperty("Accept","application/json"); // Accept-Charset 설정.
                    urlConn.setRequestProperty("Content-Type","applicaiton/json;utf-8");
//                    urlConn.setRequestProperty("apikey", ""); // ""안에 apikey를 입력
                    urlConn.setDoOutput(true);

                    // Json 전송
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",idd);

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream()));
                    bw.write(jsonObject.toString());
                    bw.flush();
                    bw.close();

                    // 서버 응답
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    String returnMsg = in.readLine();
                    System.out.println("응답메시지: " + returnMsg);

                    // 스레드에서 Toast 메시지 띄울 때
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), returnMsg,Toast.LENGTH_LONG);

                        }
                    }, 0);

                    // [2-2]. parameter 전달 및 데이터 읽어오기.
//                    String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
//                    OutputStream os = urlConn.getOutputStream();
//                    os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
//                    os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
//                    os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

                    // [2-3]. 연결 요청 확인.
                    // 실패 시 null을 리턴하고 메서드를 종료.
//                    if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
//                        System.out.println("연결 실패");
//
//                    // [2-4]. 읽어온 결과물 리턴.
//                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
//
//                    // 출력물의 라인과 그 합에 대한 변수.
//                    String line;
//                    String page = "";
//
//                    // 라인을 받아와 합친다.
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
        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());

        mMap.animateCamera(center);
        marker.showInfoWindow(); // 정보 창 열기
        //
//        changeSelectedMarker(marker);
//
        return true;

    }


}