package mapclasses;

import android.graphics.drawable.Drawable;

import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

class Note
{
    private String title;
    private String content;

    public void setTitle(String title, String content)
    {
        this.title = title;
        this.content = content;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getContent()
    {
        return this.content;
    }
}

public class MapNote extends Note {
    private OverlayItem mapItem;

    public GeoPoint getNoteGeoPoint()
    {
        return mapItem.getGeoPoint();
    }

    public void setPicture(Drawable drawable)
    {
        mapItem.setDrawable(drawable);
    }

    public MapNote(String title, String content, OverlayItem ovItem)
    {
        super.setTitle(title, content);
        this.mapItem = ovItem;
    }
}
