package ru.mapkittest.testactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ru.mapkittest.R;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

public class TestActivity_temp extends Activity implements OnMapListener {
    /** Called when the activity is first created. */
    private final int CLEAR = 1;

    private int MaxEvent;

    Time lastTime = new Time();

    MapController mMapController;
    TextView mView;
    Activity mActivity;
    ListView pointsList;

    Overlay overlay;
    OverlayManager mOverlayManager;

    //private ArrayAdapter<String> adapter;
    private  ArrayAdapter adapter;
    private ArrayList<BasicNameValuePair> arrayList;
    private ArrayList<OverlayItem> geoPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

         mActivity = this;
        arrayList = new ArrayList<BasicNameValuePair>();
        geoPoints = new ArrayList<OverlayItem>();

        //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2, arrayList);
        adapter = new ArrayAdapter (this, R.layout.list_row, android.R.id.text1, arrayList) {

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                ImageView image = (ImageView) view.findViewById(R.id.list_image);

                BasicNameValuePair data = arrayList.get(position);

                text1.setText(data.getName());
                text2.setText(data.getValue());
                image.setImageDrawable(getResources().getDrawable(R.drawable.a));
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Создаем интент для построения маршрута
                        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
                        intent.setPackage("ru.yandex.yandexnavi");

                        PackageManager pm = getPackageManager();
                        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

                        // Проверяем, установлен ли Яндекс.Навигатор
                        if (infos == null || infos.size() == 0) {
                            // Если нет - будем открывать страничку Навигатора в Google Play
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
                        } else {
                            GeoPoint geoPoint = geoPoints.get(position).getGeoPoint();
                            intent.putExtra("lat_to", geoPoint.getLat());
                            intent.putExtra("lon_to", geoPoint.getLon());
                        }

                        // Запускаем нужную Activity
                        startActivity(intent);
                    }
                });
                text2.setTextColor(Color.parseColor("#FF0000"));
                return view;
            }
        };

        // Here, you set the data in your ListView
        this.MaxEvent = getResources().getInteger(R.integer.max_event);
        this.mView = (TextView)findViewById(R.id.InfoLabel);
        this.pointsList = (ListView)findViewById(R.id.pointsList);
        pointsList.setAdapter(adapter);

        final MapView mapView = (MapView) findViewById(R.id.map);

        mMapController = mapView.getMapController();
        mOverlayManager = mMapController.getOverlayManager();
        mOverlayManager.getMyLocation().setEnabled(false);

        // add listener
        mMapController.addMapListener(this);

        // Create a layer of objects for the map
        overlay = new Overlay(mMapController);
        // Add the layer to the map
        mOverlayManager.addOverlay(overlay);

        pointsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                //arrayList.remove(i);

                view.setSelected(true);
                for (int iterator=0; iterator < geoPoints.size(); ++iterator)
                {
                    geoPoints.get(iterator).setDrawable(getResources().getDrawable(R.drawable.shop));
                }

                OverlayItem layItem = geoPoints.get(index);
                layItem.setDrawable(getResources().getDrawable(R.drawable.a));
                //overlay.removeOverlayItem(item);
                //geoPoints.remove(i);

                adapter.notifyDataSetChanged();
                mMapController.notifyRepaint();
            }
        });
    }

    public void myButtonClick(View view)
    {
        adapter.add("aaa");

    }

    public void eraseClick(View view)
    {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapActionEvent(final MapEvent mapEvent) {
        // Log.w("TEST", "EVENT_ID is " + Integer.toString( mapEvent.getMsg() ));

        String resultAnswer = "";
        boolean add = false;

        if (this.MaxEvent <= mapEvent.getMsg())
        {
            this.MaxEvent = mapEvent.getMsg();
            // Log.w("TEST", "MAX_EVENT_ID set to " + Integer.toString( this.MaxEvent ));
        }

        if (this.MaxEvent >= MapEvent.MSG_LONG_PRESS) {
            resultAnswer = "ffff";
            add = true;
        }
        else {
            resultAnswer = Integer.toString(this.MaxEvent);
        }

        Time now = new Time();
        now.setToNow();

        final String ans = resultAnswer;
        final boolean Addi = add;
        final Time nowF = now;

        final float x = mapEvent.getX();
        final float y = mapEvent.getY();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.setText(ans);
                if (Addi)
                {
                    if (nowF.toMillis(true) - lastTime.toMillis(true) > 3000) {
                        int currentId = pointsList.getCount() + 1;
                        //arrayList.add(nowF.toString());
                        arrayList.add(
                                new BasicNameValuePair("Point №"+Integer.toString(currentId), nowF.format("%d.%m %H:%M:%S"))
                        );
                        geoPoints.add(showObject(mMapController.getGeoPoint(new ScreenPoint(x, y))));
                        // next thing you have to do is check if your adapter has changed
                        adapter.notifyDataSetChanged();
                        lastTime.set(nowF);

                    }

                    Log.w("COORD", Double.toString(x) + " " + Double.toString(y));
                }
            }
        });
    }

    public OverlayItem showObject(GeoPoint point){
        // Load required resources
        Resources res = getResources();
        // Create a layer of objects for the map
        //Overlay overlay = new Overlay(mMapController);

        // Create an object for the layer
        OverlayItem kremlin = new OverlayItem(point, res.getDrawable(R.drawable.shop));
        // Create a balloon model for the object
        BalloonItem balloonKremlin = new BalloonItem(this, kremlin.getGeoPoint());
//
//        // Add the balloon model to the object
        kremlin.setBalloonItem(balloonKremlin);
        // Add the object to the layer
        overlay.addOverlayItem(kremlin);

        // Add the layer to the map
        //mOverlayManager.addOverlay(overlay);
    return kremlin;
    }

    public void showObject(){
        // Load required resources
        Resources res = getResources();


        // Create an object for the layer
        OverlayItem kremlin = new OverlayItem(new GeoPoint(55.752004 , 37.617017), res.getDrawable(R.drawable.shop));
        // Create a balloon model for the object
        BalloonItem balloonKremlin = new BalloonItem(this,kremlin.getGeoPoint());
//
//        // Add the balloon model to the object
        kremlin.setBalloonItem(balloonKremlin);
        // Add the object to the layer
        overlay.addOverlayItem(kremlin);
        mMapController.notifyRepaint();
    }


}

