package com.rschina.heilongjiang.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinars.mapapi.Bounds;
import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.LocationData;
import com.chinars.mapapi.MapController;
import com.chinars.mapapi.MapLayer;
import com.chinars.mapapi.MapLayerConstant;
import com.chinars.mapapi.MapTouchListener;
import com.chinars.mapapi.MapView;
import com.chinars.mapapi.MyLocationOverlay;
import com.chinars.mapapi.Overlay;
import com.chinars.mapapi.PopupOverlay;
import com.chinars.mapapi.Projection;
import com.chinars.mapapi.SphericalMercatorLayer;
import com.chinars.mapapi.xml.MyOverlay;
import com.rschina.heilongjiang.MainActivity;
import com.rschina.heilongjiang.MyApplication;
import com.rschina.heilongjiang.adapter.ImageAdapter;
import com.rschina.heilongjiang.db.MarkInfo;
import com.rschina.heilongjiang.db.RsService;
import com.rschina.heilongjiang.model.BaseIdDes;
import com.rschina.heilongjiang.model.MapInfo;
import com.rschina.heilongjiang.model.MonitorInfo;
import com.rschina.heilongjiang.views.ImageWidget;
import com.rschina.heilongjiang.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;


/**
 * Created by Administrator on 2016/9/8.
 */
public class MapFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MapFragment";
    /* @ViewInject(R.id.cancle_compare)
     TextView cancle_compare;
     @ViewInject(R.id.goto_report)
     RelativeLayout goto_report;*/
    @ViewInject(R.id.mapview)
    MapView mapView;
    @ViewInject(R.id.tv_info)
    TextView tv_info;
    @ViewInject(R.id.ll_legend)
    LinearLayout ll_legend;
    @ViewInject(R.id.loading)
    LinearLayout loading;
    @ViewInject(R.id.iv_legend)
    ImageView iv_legend;
    @ViewInject(R.id.iv_btn_legend)
    ImageView iv_btn_legend;
    @ViewInject(R.id.tv_btn_legend)
    TextView tv_btn_legend;
    @ViewInject(R.id.ll_image)
    LinearLayout ll_image;
    @ViewInject(R.id.ditu_text)
    TextView ditu_text;
    //@ViewInject(R.id.image_ditu)
    //ImageView ditu_image;
    @ViewInject(R.id.ll_ditu_width)
    LinearLayout ll_ditu_width;
    @ViewInject(R.id.luwang)
    ImageView roadWidget;
    @ViewInject(R.id.image_center)
    ImageView image_center;
    @ViewInject(R.id.image_marker)
    ImageView image_marker;

    SphericalMercatorLayer realtimeChina_mapbox;//实时中国mapbox底图
    SphericalMercatorLayer realtimeChina_mercator;//实时中国墨卡托图层
    SphericalMercatorLayer tianditu_mercator;//天地图墨卡托图层
    @ViewInject(R.id.clean)
    ImageView clean;
    @ViewInject(R.id.add)
    ImageView add;
    @ViewInject(R.id.shwo_lenger)
    TextView shwo_lenger;
    boolean showRoad = true;
    Activity activity;
    private MapLayer baseLayer;
    private MapLayer overLayer = null;
    Map<String, MapLayer> mapLayers;
    private MapInfo baseInfo = null;
    private MapInfo overInfo = null;
    private MonitorInfo baseMoni;
    private MonitorInfo overMoni;
    private BaseIdDes baseDes;
    private BaseIdDes overDes;
    private List<MonitorInfo> monis;
    private List<List<String>> listid;
    private List<BaseIdDes> baseIdDes;
    ImageWidget locationWidget;
    private boolean isComparing = false;
    private boolean showLegend = false;
    private int[] legendDrawables = {R.drawable.luohu_water, R.drawable.luohu_water_change, R.drawable.luohu_vegetation
            , R.drawable.luohu_vegetationchange, R.drawable.luohu_buildingchange, R.drawable.vegetation_cover};
    private int drawableId;
    private GeoPoint topLeft;
    private GeoPoint rightBottom;
    int local[] = new int[2];
    private boolean isLoging = false;
    private int baseMapIdIndex = 0;
    private MapController mapController;
    private boolean isExit = false;
    private List<Integer> imageList;
    private List<String> listDes;
    private List<List<String>> baseIdDess;
    private List<List<BaseIdDes>> baseIdDesList;
    private double lati;
    private double longi;
    private int baseIndex;
    private int count;
//    //弹出窗口图层
//    private PopupOverlay mPopupOverlay;
//    //弹出位置
//    private LocationData mLocationData;
//    //弹出窗口的view
//    private View markView;
//    //位置图层
//    private LocationOverlay mLocationOverlay;

    private double latitude;
    private double longitude;
    private String name;
    private String des;
    private PopupWindow popup;
    private Bitmap bitmap;
    private Point point;
    private int screenWidth;
    private int screenHeight;
    private View markView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        topLeft = new GeoPoint(mercator2wgsX(14561557.288098205), mercator2wgsY(5695060.039647025));
//        rightBottom = new GeoPoint(mercator2wgsX(14602581.968332503), mercator2wgsY(5635474.644796334));
//        realtimeChina_mapbox = new SphericalMercatorLayer("mi_mc-gf2-80cm-20150929-jixi-pm-0public",
//                "http://210.77.87.225:8080/geowebcache/service/wms", "image%2Fpng",
//                "mi_mc-gf2-80cm-20150929-jixi-pm-0public", 0, 25,
//                topLeft,
//                rightBottom, null
//        );

        topLeft = new GeoPoint(mercator2wgsX(14509582.4552), mercator2wgsY(5880147.7111));
        rightBottom = new GeoPoint(mercator2wgsX(14910723.97958825), mercator2wgsY(5596413.462142457));
        realtimeChina_mapbox = new SphericalMercatorLayer("mi_mc-pl-5m-20170701_20170730-jixi-am-0public",
                "http://210.77.87.225:8080/geowebcache/service/wms", "image%2Fpng",
                "mi_mc-pl-5m-20170701_20170730-jixi-am-0public", 0, 25,
                topLeft,
                rightBottom, null
        );
        tianditu_mercator = new SphericalMercatorLayer("cia",
                "http://t#.tianditu.com/cia_w/wmts",
                "w", null, "tiles", 0, 17, topLeft,
                rightBottom
        );
        baseLayer = realtimeChina_mapbox;
        overLayer = null;
        monis = RsService.getInstance().getMonitorInfos();
        listid = RsService.getInstance().getBaseMapId();
        //baseIdDes = RsService.getInstance().getBaseIdDes();
        baseIdDesList = RsService.getInstance().getBaseIdDesList();
        baseIdDess = RsService.getInstance().getBaseIdDess();


    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        mapLayers = new HashMap<>();
        View view = inflater.inflate(R.layout.fragment_map, null);
        x.view().inject(this, view);
        mapView.setMaxResolution(MapLayerConstant.MAX_RESOLUTION_DOT_703125);
        mapView.addLayer(baseLayer);
        mapView.disableAllBuildinWidget();
        mapView.disableLogo();
        mapView.setMinZoomlevel(7);
        mapView.setMaxZoomlevel(25);
        mapView.setZoomLevel(9);
        //14509582.4552,5596413.462142457,14910723.97958825,5880147.7111
        mapView.setMaxBounds(new Bounds(mercator2wgsX(14509582.4552), mercator2wgsY(5596413.462142457), mercator2wgsX(14910723.97958825), mercator2wgsY(5880147.7111)));
        mapView.setMapCenter(tianditu_mercator.getGeoCenter());
        if (showRoad) {
            mapView.removeLayer(tianditu_mercator);
            roadWidget.setImageResource(R.drawable.luwang_normal);
            showRoad = false;
        } else {
            if (overLayer == null) {
                mapView.addOverLayer(tianditu_mercator, baseLayer);
            } else {
                mapView.addOverLayer(tianditu_mercator, overLayer);
            }
            roadWidget.setImageResource(R.drawable.luwang_pressed);
            showRoad = true;
        }
        //showOverlay();
/*
        // 路网按钮
        roadWidget = (ImageWidget) inflater.inflate(R.layout.widget_image, null);
        roadWidget.setMargin(-ResourseUtil.dip2px(activity, 10),
                ResourseUtil.dip2px(activity, 60));
        mapView.addWidget(roadWidget);*/


        mapView.setMapTouchListenner(new MapTouchListener() {

            @Override
            public void onTap(GeoPoint p) {

            }

            @Override
            public boolean onDoubleTap(GeoPoint p) {
                System.out.println("zoomLevel :" + mapView.getZoomLevel());
                return false;
            }

            @Override
            public void onLongPress(GeoPoint p) {
/*
                //地图控制类，用于设置地图中心，缩放等事件
                mapController = mapView.getController();
                mapController.setCenter(p);
                //获取图层列表
                list = mapView.getOverlays();
                //自定义overlay
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
                MyOverlay overlay = new MyOverlay(getActivity(), p, bitmap);
                //添加图层
                list.add(overlay);*/
            }
        });
        /**
         * 添加标注
         */
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoging) {
                    ((MainActivity) getActivity()).dismissBootom();
                    ((MainActivity) getActivity()).setVisible();
                    image_center.setVisibility(View.VISIBLE);
                    //mapController = mapView.getController();
                    add.setVisibility(View.GONE);
                    clean.setVisibility(View.GONE);
                } else {
                    ((MainActivity) getActivity()).showBootom();
                    Toast.makeText(getActivity(), "请先登入帐号", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).gotoUserFragment();
                    /*Intent intent = new Intent(getActivity(), UserActivity.class);
                    startActivity(intent);*/
                }
            }
        });

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("yzx", "删除");
                //   if (popup.isShowing()) {
                //popup.dismiss();
                mapView.getOverlays().clear();
                mapView.refresh();

                //   }
            }
        });

        shwo_lenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showLegend) {
                    shwo_lenger.setText("打开图例");
                    iv_legend.setVisibility(View.INVISIBLE);
                    ll_legend.setBackgroundResource(R.drawable.selector_shape_legend);
                    tv_btn_legend.setTextColor(Color.BLACK);
                    iv_btn_legend.setImageResource(R.drawable.ic_legend);
                    showLegend = false;
                } else {

                    shwo_lenger.setText("关闭图例");
                    iv_legend.setVisibility(View.VISIBLE);
                    ll_legend.setBackgroundResource(R.drawable.selector_shape_selected);
                    tv_btn_legend.setTextColor(Color.YELLOW);
                    iv_btn_legend.setImageResource(R.drawable.ic_legend_pressed);
                    showLegend = true;
                }
            }
        });

        /**
         * 点击标注弹窗
         */

        image_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DisplayMetrics dis = new DisplayMetrics();
                //getActivity().getWindowManager().getDefaultDisplay().getMetrics(dis);
                //int widthpx = dis.widthPixels;
                //int heightpx = dis.heightPixels;
                //float density = dis.density;
                //int screenWidth = (int) (widthpx * density);
                //int screenHeight = (int) (heightpx * density);
                //GeoPoint centerPoint = mapView.getProjection().fromPixels(screenWidth/2, screenHeight/2);
                //double latitude = centerPoint.getLatitude();
                //double longitude = centerPoint.getLongitude();

                //Toast.makeText(getActivity(), "当前经度为"+longitude+",  纬度为"+latitude, Toast.LENGTH_SHORT).show();
            }
        });
//        /**
//         * mapview移动触摸事件
//         */
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                //1 获取屏幕宽高
                DisplayMetrics dis = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dis);
                int widthPx = dis.widthPixels-80;
                int heightpx = dis.heightPixels-420;
                GeoPoint centerPoint = mapView.getProjection().fromPixels(widthPx / 2, heightpx / 2);
                latitude = centerPoint.getLatitude();
                longitude = centerPoint.getLongitude();
                ((MainActivity) getActivity()).setInfo(latitude, longitude);

                return false;
            }

        });


        roadWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (showRoad) {
                    mapView.removeLayer(tianditu_mercator);
                    roadWidget.setImageResource(R.drawable.luwang_normal);
                    showRoad = false;
                } else {
                    if (overLayer == null) {
                        mapView.addOverLayer(tianditu_mercator, baseLayer);
                    } else {
                        mapView.addOverLayer(tianditu_mercator, overLayer);
                    }
                    roadWidget.setImageResource(R.drawable.luwang_pressed);
                    showRoad = true;
                }
            }
        });

        mapView.setOnSingleUpListener(new Runnable() {
            @Override
            public void run() {
                if (isComparing && overInfo != null) {
                    setAlpha(255);
                    isComparing = false;
                }
            }
        });
        ll_legend.setOnClickListener(this);

        //点击弹出底图
        ll_ditu_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //影像集合
                imageList = new ArrayList<Integer>();
                //名称集合
                listDes = new ArrayList<String>();
                listDes.clear();
                imageList.clear();
                View dituView = getActivity().getLayoutInflater().inflate(R.layout.list_ditu, null);
                ListView listView = (ListView) dituView.findViewById(R.id.list_ditu);
                int width = ll_image.getMeasuredWidth();
                final PopupWindow pop = new PopupWindow(dituView, width, 160, true);
                pop.setFocusable(true);
                pop.setOutsideTouchable(true);
                dituView.getLocationOnScreen(local);
                pop.showAsDropDown(ll_image, 0, 0);
                for (int i = 0; i < listid.get(baseMapIdIndex).size(); i++) {
                    imageList.add(i);
                }

                //Log.e("yzx", imageList.size()+"=========");
                //Log.e("yzx", baseMapIdIndex + "");
                if (listid.get(baseMapIdIndex).size() == 1) {
                    listDes.add(baseIdDess.get(baseMapIdIndex).get(0));
                } else if (listid.get(baseMapIdIndex).size() == 2) {
                    listDes.add(baseIdDess.get(baseMapIdIndex).get(0));
                    listDes.add(baseIdDess.get(baseMapIdIndex).get(1));
                }
                final ImageAdapter adapter = new ImageAdapter(getActivity(), imageList, listDes);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ditu_text.setText(baseIdDess.get(baseMapIdIndex).get(i));
                        //setBaseIds(baseIdDes.get(imageList.get(i)), true);
                        setBaseIds(baseIdDesList.get(baseMapIdIndex).get(i), true);
                        pop.dismiss();

                    }
                });

            }
        });
        roadWidget.performClick();
        return view;
    }

    public void setMapInfo(MapInfo mapInfo, boolean isBase) {
        if (mapInfo.name == null) {
            return;
        }
        MapLayer mapLayer = mapLayers.get(mapInfo.name);

        if (mapLayer == null) {
            mapLayer = mapInfo.buildMapLayer();
            mapLayers.put(mapInfo.name, mapLayer);
        }
        if (isBase) {
            baseInfo = mapInfo;
            setBaseLayer(mapLayer);
            mapView.setMaxZoomlevel(20);
            mapView.setLayerVisibility(tianditu_mercator, true);
        } else {
            overInfo = mapInfo;
            setOverLayer(mapLayer);
        }
    }


    public void setMonitorInfo(MonitorInfo monitorInfo, boolean isBase) {
        MapLayer mapLayer = mapLayers.get(monitorInfo.name);
        //ditu_text.setText(monitorInfo.name);
        tv_info.setText("专题：" + monitorInfo.name);
        setLegend(monitorInfo);
        if (mapLayer == null) {
            mapLayer = monitorInfo.buildMapLayer();
            mapLayers.put(monitorInfo.name, mapLayer);
        }
        if (isBase) {
            baseMoni = monitorInfo;
            setBaseLayer(mapLayer);
            mapView.setMaxZoomlevel(baseInfo.maxZoom);
            mapView.setLayerVisibility(tianditu_mercator, true);
        } else {
            overMoni = monitorInfo;
            setOverLayer(mapLayer);
        }
    }


    public void setBaseIds(BaseIdDes baseIdDes, boolean isBase) {
        MapLayer mapLayer = mapLayers.get(baseIdDes.name);
        if (baseIdDess.get(baseMapIdIndex).size() > 0) {
            ditu_text.setText(baseIdDess.get(baseMapIdIndex).get(0));
        }
        Log.e("yzx", baseIdDes.name + "mapfragment");
        if (mapLayer == null) {
            mapLayer = baseIdDes.buildMapLayer();
            mapLayers.put(baseIdDes.name, mapLayer);
        }
        if (isBase) {
            baseDes = baseIdDes;
            setBaseLayer(mapLayer);
            mapView.setMaxZoomlevel(baseInfo.maxZoom);
            mapView.setLayerVisibility(tianditu_mercator, true);
        } else {
            overDes = baseIdDes;
            setOverLayer(mapLayer);
        }
    }

    public void setText() {
        if (overInfo == null) {
            if (baseInfo != null) {
                tv_info.setText(baseInfo.name);
            }


        } else {
            if (overInfo != null) {
                tv_info.setText(overInfo.name);
            }

        }
    }


    public void setMonitorText() {
        //ditu_text.setText(baseIdDes.get(0).name);
        /*if (overMoni == null) {
            tv_info.setText("专题"+baseMoni.name);
        } else {
            tv_info.setText("专题"+baseInfo.name);
        }*/

    }

    public void removeOverLayer() {
        if (overLayer != null) {
            mapView.removeLayer(overLayer);
            if (showRoad) {
                mapView.addOverLayer(tianditu_mercator, baseLayer);
            }
            overInfo = null;
            overLayer = null;
        }
    }

    public boolean isBase(MapInfo mapInfo) {
        return baseInfo == mapInfo;
    }

    private void setLegend(MonitorInfo monitorInfo) {
        if (monitorInfo.legend != null) {

            Glide.with(getActivity()).load(monitorInfo.legend).into(iv_legend);
        }
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: " + v.getId());
        switch (v.getId()) {
            case R.id.ll_legend:
                if (showLegend) {
                    iv_legend.setVisibility(View.INVISIBLE);
                    ll_legend.setBackgroundResource(R.drawable.selector_shape_legend);
                    tv_btn_legend.setTextColor(Color.BLACK);
                    iv_btn_legend.setImageResource(R.drawable.ic_legend);
                    showLegend = false;
                } else {
                    iv_legend.setVisibility(View.VISIBLE);
                    ll_legend.setBackgroundResource(R.drawable.selector_shape_selected);
                    tv_btn_legend.setTextColor(Color.YELLOW);
                    iv_btn_legend.setImageResource(R.drawable.ic_legend_pressed);
                    showLegend = true;
                }
                break;
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    public void setAlpha(int alpha) {
        mapView.setLayerAlpha(overLayer, alpha);
    }

    public void setBaseLayer(MapLayer baseLayer) {
        mapView.replaceLayer(baseLayer, this.baseLayer);
        this.baseLayer = baseLayer;
    }

    public void setMapCenter() {
        mapView.setMapCenter(baseLayer.getGeoCenter());
    }

    public void setOverLayer(MapLayer overLayer) {

        if (this.overLayer != null) {
            mapView.replaceLayer(overLayer, this.overLayer);
        } else {
            mapView.addLayer(overLayer);
            if (showRoad) {
                mapView.removeLayer(tianditu_mercator);
                mapView.addOverLayer(tianditu_mercator, overLayer);
            }
        }
        this.overLayer = overLayer;
    }

    public void setDisplayRatio(double ratio) {
        mapView.setDisplayRatio(realtimeChina_mercator, ratio);
    }

    public void setMoo(int moo) {
        if (ll_image != null) {
            if (moo == 1) {
                ll_image.setVisibility(View.VISIBLE);
                iv_legend.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
                clean.setVisibility(View.GONE);
                shwo_lenger.setVisibility(View.VISIBLE);
            } else {
                ll_image.setVisibility(View.GONE);
                iv_legend.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
                clean.setVisibility(View.VISIBLE);
                shwo_lenger.setVisibility(View.GONE);
            }
        }
    }

    public static double mercator2wgsX(double mercatorX) {
        // web墨卡托 转 WGS-84，主要用于将坐标单位为米的值转为单位为度的值
        mercatorX = mercatorX * 180.0 / 20037508.34;
        return mercatorX;
    }

    public static double mercator2wgsY(double mercatorY) {
        // web墨卡托 转 WGS-84，主要用于将坐标单位为米的值转为单位为度的值
        mercatorY = 180.0 / Math.PI * (2.0 * Math.atan(Math.exp((mercatorY / 20037508.34) * Math.PI)) - Math.PI / 2.0);
        return mercatorY;
    }


    public void setBaseMapIdIndex(int baseMapIdIndex) {
        this.baseMapIdIndex = baseMapIdIndex;
        //Log.e("yzx", baseMapIdIndex + "");
    }


    public void setLengerText() {
        shwo_lenger.setText("关闭图例");
    }


    /**
     * 标记取消操作
     */
    public void setcancel() {
        add.setVisibility(View.VISIBLE);
        clean.setVisibility(View.VISIBLE);
        image_center.setVisibility(View.GONE);
    }


    public void setHasLogon() {
        isLoging = true;
    }

    /**
     * 显示覆盖物与弹窗
     *
     * @param latitude
     * @param longitude
     * @param baseIndex
     */
    public void showOverlay(double latitude, double longitude, String name, String des, int baseIndex) {
        this.lati = latitude;
        this.longi = longitude;
        this.name = name;
        this.des = des;
        this.baseIndex = baseIndex;
        GeoPoint gp = new GeoPoint(longitude, latitude);
        MyOverlay myOverlay = new MyOverlay();
        List<Overlay> overlays = mapView.getOverlays();
        overlays.add(myOverlay);
        myOverlay.onTap(gp, mapView);
    }


    class MyOverlay extends Overlay {

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {

            Projection projection = mapView.getProjection();
            GeoPoint gp = new GeoPoint(longi, lati);
            point = projection.toPixels(gp);
            Paint paint = new Paint();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
            canvas.drawBitmap(bitmap, point.x, point.y, paint);

        }


        @Override
        public boolean onTap(GeoPoint p, MapView mapView) {
            DecimalFormat df = new DecimalFormat("#0.0");
            String format = df.format(lati);
            String format1 = df.format(p.getLatitude());
            String format2 = df.format(longi);
            String format3 = df.format(p.getLongitude());
            if (format.equals(format1) && format2.equals(format3)) {
                //在这里弹窗标注信息吧
                View markView = getActivity().getLayoutInflater().inflate(R.layout.pop_layout, null);
                final ImageView pop_collection = (ImageView) markView.findViewById(R.id.pop_collection);
                ImageView close = (ImageView) markView.findViewById(R.id.close);
                TextView pop_title = (TextView) markView.findViewById(R.id.pop_title);
                TextView pop_content = (TextView) markView.findViewById(R.id.pop_content);
                //image_marker.layout(0,0,0,0);
                popup = new PopupWindow(markView, 850, 425, true);
                popup.setFocusable(true);
                popup.setOutsideTouchable(false);
                pop_title.setText(name);
                pop_content.setText(des);
                popup.showAsDropDown(image_marker, 0, 0);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        diss();
                    }
                });
                pop_collection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MarkInfo info = new MarkInfo();
                        info.setName(name);
                        info.setDes(des);
                        info.setLongitude(longitude);
                        info.setLatitude(latitude);
                        info.setNumber(baseIndex);
                        info.save();
                        Log.e("yzx", "baseIndex=" + baseIndex);
                        pop_collection.setImageResource(R.drawable.collect_pressed);
                        Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            return true;
        }
        public void diss (){
            mapView.getOverlays().clear();
            mapView.refresh();
            popup.dismiss();
        }
    }


    /**
     * show has collection overlay
     */
    public void showCollectionOverlay(double latitude, double longitude, String name, String des, int baseIndex) {
        GeoPoint gp = new GeoPoint(longitude, latitude);
        MyCollectionOverlay myCollectionOverlay = new MyCollectionOverlay();
        List<Overlay> overlays = mapView.getOverlays();
        overlays.add(myCollectionOverlay);
    }

    class MyCollectionOverlay extends Overlay{
        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            Projection projection = mapView.getProjection();
            GeoPoint gp = new GeoPoint(longi, lati);
            Point point = projection.toPixels(gp);
            Paint paint = new Paint();
            Bitmap collectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.collect_pressed);
            canvas.drawBitmap(collectionBitmap, point.x, point.y, paint);
        }


    }



}
