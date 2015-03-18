package mapclasses;

import android.app.Activity;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

/**
 * Created by Ershov on 18.03.2015.
 */


public class SimpleMap {
    protected Activity activity;

    protected MapView mapView;
    protected MapController mapController;
    protected OverlayManager overlayManager;

    protected Overlay userLayer;

    // CONSTRUCTOR
    public SimpleMap(Activity activity, MapView initialMap){
        this.activity = activity;
        initializeMap(initialMap);
        createUserLayer();
    };

    public void initializeMap(MapView map)
    {
        this.mapView = map;
        this.mapController = map.getMapController();
        overlayManager = mapController.getOverlayManager();
    };

    public void createUserLayer()
    {
        // Create a layer of objects for the map
        userLayer = new Overlay(mapController);
        // Add the layer to the map
        overlayManager.addOverlay(userLayer);
    };

    // METHODS
    public void myLocation(boolean doSet)
    {
        overlayManager.getMyLocation().setEnabled(doSet);
    }

    public GeoPoint getPointOnScreen(float screenX, float screenY)
    {
        return mapController.getGeoPoint(
                new ScreenPoint(screenX, screenY)
        );
    }

}
