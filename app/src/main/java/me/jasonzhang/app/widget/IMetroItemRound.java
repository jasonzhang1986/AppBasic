package me.jasonzhang.app.widget;
import android.graphics.Canvas;

public interface IMetroItemRound {

    void drawRadious(Canvas canvas);

    int getWidth();

    int getHeight();

    MetroItemRound getRoundImpl();

}
