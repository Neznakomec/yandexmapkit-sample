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
import java.util.Map;

import mapclasses.MapNote;
import mapclasses.MapNotes;
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

public class TestActivity extends Activity {
    private MapNotes mapNotes;
    private ListView pointsList;
    private  ArrayAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.pointsList = (ListView)findViewById(R.id.pointsList);
        final MapView mapView = (MapView) findViewById(R.id.map);
        mapNotes = new MapNotes(this, mapView);

        final ArrayList<MapNote> noteArrayList = mapNotes.getMapNotesList();
        noteAdapter = new ArrayAdapter (this, R.layout.list_row, android.R.id.text1, noteArrayList) {

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                ImageView image = (ImageView) view.findViewById(R.id.list_image);

                MapNote data = noteArrayList.get(position);

                text1.setText(data.getTitle());
                text2.setText(data.getContent());
                image.setImageDrawable(getResources().getDrawable(R.drawable.navi));
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
                            GeoPoint geoPoint = mapNotes.getGeoPoint(position);
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

        pointsList.setAdapter(noteAdapter);

        pointsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                //arrayList.remove(i);

                view.setSelected(true);
                for (int iterator=0; iterator < mapNotes.size(); ++iterator)
                {
                    mapNotes.setPicture(iterator, getResources().getDrawable(R.drawable.shop));
                }

                mapNotes.setPicture(index, getResources().getDrawable(R.drawable.a));

                noteAdapter.notifyDataSetChanged();
            }
        });
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
}

