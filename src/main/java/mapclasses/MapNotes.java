package mapclasses;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Map;

import ru.mapkittest.R;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

/**
 * Created by Ershov on 18.03.2015.
 */



public class MapNotes extends SimpleMap implements  OnMapListener{
    // DATA
    private ArrayAdapter<MapNote> mapNoteAdapter;

    private ArrayList<MapNote> mapNotesList;
    private Time lastAddTime = new Time();
    private int noteCount;

    // CONSTRUCTOR

    public MapNotes(Activity activity, MapView initialMap) {
        super(activity, initialMap);
        mapNotesList = new ArrayList<MapNote>();
        noteCount = 0;
        mapController.addMapListener(this);
    }

    // PROPERTY
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setPicture(int position, Drawable drawable)
    {
        if (position <= mapNotesList.size())
        {
            this.mapNotesList.get(position).setPicture(drawable);
            mapController.notifyRepaint(); // Invalidating MapView
        }
        else
            Log.e("NULL OBJECT", "ASKING A №"+Integer.toString(position) + "GEOPOINT THAT NULL");
    }

    public ArrayList<MapNote> getMapNotesList()
    {
        return this.mapNotesList;
    }

    public GeoPoint getGeoPoint(int position)
    {
        if (position <= mapNotesList.size())
            return mapNotesList.get(position).getNoteGeoPoint();
        else
        {
            Log.e("NULL OBJECT", "ASKING A №"+Integer.toString(position) + "GEOPOINT THAT NULL");
            return null;
        }
    };

    public void bringAdapter(ArrayAdapter adapter)
    {
        this.mapNoteAdapter = adapter;
    }

    public int size()
    {
        return mapNotesList.size();
    }

    public void clear()
    {
        // clearing points on map
        userLayer.clearOverlayItems();
        this.mapNotesList.clear();
        // invalidate point list and map
        mapNoteAdapter.notifyDataSetChanged();
        mapController.notifyRepaint();

    }
    //  Note Adding on long-click
    public void onMapActionEvent(final MapEvent mapEvent) {

        boolean addNote = false;

        if (mapEvent.getMsg() == MapEvent.MSG_LONG_PRESS) {
            addNote = true;
        }

        Time now = new Time();
        now.setToNow();

        final boolean addIt = addNote;
        final Time nowF = now;

        final float x = mapEvent.getX();
        final float y = mapEvent.getY();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (addIt) {
                    if (nowF.toMillis(true) - lastAddTime.toMillis(true) > 3000) {
                        int currentId = noteCount + 1;
                        MapNote addingNote = new MapNote("Point №" + Integer.toString(currentId),
                                nowF.format("%d.%m %H:%M:%S"),
                                putBalloon(getPointOnScreen(x, y))
                        );
                        mapNotesList.add(addingNote);

                        if (mapNoteAdapter != null)
                            mapNoteAdapter.notifyDataSetChanged(); // ????
                        else
                            Log.e("NULL OBJECT", "PLEASE CHECK MAPNOTE ADAPTER, USING WITHOUT INIT");

                        lastAddTime.set(nowF);
                    }
                }
            }
        });
    }

    public OverlayItem putBalloon(GeoPoint point) {
        // Create an object for the layer
        OverlayItem pickingItem = new OverlayItem(point, activity.getResources().getDrawable(R.drawable.shop));
        // Create a balloon model for the object
        BalloonItem pickingBalloon  = new BalloonItem(this.activity, pickingItem.getGeoPoint());

        // Add the balloon model to the object
        pickingItem.setBalloonItem(pickingBalloon);
        // Add the object to the layer
        userLayer.addOverlayItem(pickingItem);

        return pickingItem;
    }
}
