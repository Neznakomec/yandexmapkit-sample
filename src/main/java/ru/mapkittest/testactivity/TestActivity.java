package ru.mapkittest.testactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.mapkittest.R;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;

public class TestActivity extends Activity implements OnMapListener {
    /** Called when the activity is first created. */
    private final int CLEAR = 1;

    private int MaxEvent;

    MapController mMapController;
    TextView mView;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
         mActivity = this;


        final MapView mapView = (MapView) findViewById(R.id.map);

        this.MaxEvent = getResources().getInteger(R.integer.max_event);
        this.mView = (TextView)findViewById(R.id.InfoLabel);

        mMapController = mapView.getMapController();
        // add listener
        mMapController.addMapListener(this);
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
    public void onMapActionEvent(MapEvent mapEvent) {
        Log.w("TEST", "EVENT_ID is " + Integer.toString( mapEvent.getMsg() ));

        String resultAnswer = "";

        if (this.MaxEvent <= mapEvent.getMsg())
        {
            this.MaxEvent = mapEvent.getMsg();
            Log.w("TEST", "MAX_EVENT_ID set to " + Integer.toString( this.MaxEvent ));
        }

        if (this.MaxEvent >= MapEvent.MSG_LONG_PRESS) {
            resultAnswer = "ffff";
        }
        else {
            resultAnswer = Integer.toString(this.MaxEvent);
        }

        final String ans = resultAnswer;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.setText(ans);
            }
        });
    }
}
