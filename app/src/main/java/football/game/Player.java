package football.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Player extends View {

    public Player(Context context) {
        super(context);
    }

    @Override
    protected void onDraw ( Canvas canvas){
        Log.d("ball", "2");
        //super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

        canvas.drawCircle(700, 800, 62, paint);

    }
}
