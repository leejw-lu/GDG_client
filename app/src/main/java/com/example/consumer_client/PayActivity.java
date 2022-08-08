package com.example.consumer_client;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface PayOrderService{
    @POST("insertOrder")
    Call<ResponseBody> insertOrder(@Body JsonObject body);
}

public class PayActivity extends AppCompatActivity {

    String mdName, purchaseNum, prodPrice;
    String store_name,store_loc, store_lat, store_long;
    String pickupDate,pickupTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        TextView ProdName=(TextView) findViewById(R.id.JP_ProdName);
        TextView OrderCount = (TextView) findViewById(R.id.ClientOrderCount);
        TextView OrderPrice = (TextView) findViewById(R.id.ClientOrderPrice);
        TextView StoreName = (TextView) findViewById(R.id.Pay_Store_Name);
        TextView StoreAddr = (TextView) findViewById(R.id.Pay_Store_Addr);
        TextView PuDate = (TextView) findViewById(R.id.Pay_PU_Date);

        Intent intent = getIntent(); //intent 값 받기
        mdName = intent.getStringExtra("mdName");
        purchaseNum = intent.getStringExtra("purchaseNum");
        prodPrice = intent.getStringExtra("prodPrice");
        store_name = intent.getStringExtra("store_name");
        store_loc=intent.getStringExtra("store_loc");
        store_lat = intent.getStringExtra("store_lat");
        store_long = intent.getStringExtra("store_long");
        pickupDate = intent.getStringExtra("pickupDate");
        pickupTime = intent.getStringExtra("pickupTime");

        //n 세트만큼 가격 결정.
        for(int i=1; i<=Integer.parseInt(purchaseNum.substring(0,1)); i++){
            prodPrice= String.valueOf(Integer.parseInt(prodPrice)* i);
        }

        ProdName.setText(mdName);
        OrderCount.setText(purchaseNum);
        OrderPrice.setText(prodPrice);
        StoreName.setText(store_name);
        StoreAddr.setText(store_loc);
        PuDate.setText(pickupDate);

        //지도
        MapView mapView = new MapView(this);
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(store_lat), Double.parseDouble(store_long)), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(1, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        //스토어위치 마커 아이콘 띄우기
        MapPoint s_MarkPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(store_lat), Double.parseDouble(store_long));  //마커찍기

        MapPOIItem farm_marker=new MapPOIItem();
        farm_marker.setItemName(store_name); //클릭했을때 스토어 이름 나오기
        farm_marker.setTag(0);
        farm_marker.setMapPoint(s_MarkPoint);   //좌표입력받아 현위치로 출력

        //  (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
        farm_marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        farm_marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
        mapView.addPOIItem(farm_marker);

        //나중에 스토어위치 마커 커스텀 이미지로 바꾸기
        //farm_marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        //farm_marker.setCustomImageResourceId(R.drawable.homeshape);

    }
}