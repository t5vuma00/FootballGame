package football.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

class simpleJoystick extends View{

    //Arvot haetaan setSize -metodilla
    private float joystickCenterX = 0;
    private float joystickCenterY = 0;
    private float joystickRadius = 0;
    private float joystickOffset = 0;

    private Bitmap bitmapJoystick;

    private float jsX = 0;
    private float jsY = 0;
    private double jsAngle = 0;
    private float xDistance = 0;
    private float yDistance = 0;
    private float x = 0;
    private float y = 0;

    private boolean onJoystickArea = false;

    String direction = null;

    public simpleJoystick(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        //Estää joystickin säteen nolla-arvon, jotta ohjelma ei crashaa
        if(joystickRadius == 0)
        {
            Log.d("radius_not_set", "radius_set_52");
            joystickRadius = 52;
        }

        bitmapJoystick = BitmapFactory.decodeResource(getResources(), R.drawable.joystick2);
        bitmapJoystick = Bitmap.createScaledBitmap(bitmapJoystick, (int) joystickRadius * 2, (int) joystickRadius * 2, true);
    }

    public void setJoystickCenterX(float x)
    {
        joystickCenterX = x;
    }

    public void setJoystickCenterY(float y)
    {
        joystickCenterY = y;
    }

    public void setJoystickRadius(float radius)
    {
        joystickRadius = radius;
    }

    public void setColor(String color)
    {
        if(color == "RED")
        {
            bitmapJoystick = BitmapFactory.decodeResource(getResources(), R.drawable.joystick1);
            bitmapJoystick = Bitmap.createScaledBitmap(bitmapJoystick, (int) joystickRadius * 2, (int) joystickRadius * 2, true);
        }

        else if(color == "BLUE")
        {
            bitmapJoystick = BitmapFactory.decodeResource(getResources(), R.drawable.joystick2);
            bitmapJoystick = Bitmap.createScaledBitmap(bitmapJoystick, (int) joystickRadius * 2, (int) joystickRadius * 2, true);
        }
    }

    public float getJsAngle()
    {
        return (float) jsAngle;
    }

    public void setOffset(float offset)
    {
        joystickOffset = offset;
    }

    public void calculateAngle(float sentX, float sentY)
    {
        x = sentX;
        y = sentY;

        //Joystickin alueen määrittely
        if(x > joystickCenterX - joystickRadius - joystickOffset
                && x < joystickCenterX + joystickRadius + joystickOffset
                && y > joystickCenterY - joystickRadius - joystickOffset
                && y < joystickCenterY + joystickRadius + joystickOffset) {
            //Log.d("JS1", "Kosketellaan joystick 1");
            //Jos kosketus joystickin alueella otetaan x ja y talteen
            onJoystickArea = true;
            jsX = x;
            jsY = y;
            //Log.d("X ", String.valueOf(js1X));
            //Log.d("Y", String.valueOf(js1Y));

            if (jsX == joystickCenterX && jsY == joystickCenterY) {
                Log.d("JS1", "JS1 keskipiste");
            }

            //Joystickin oikea yläkulma
            if (jsX > joystickCenterX && jsY < joystickCenterY) {
                //Log.d("JS1", "JS1 oikea yläkulma");

                //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                xDistance = Math.abs(jsX - joystickCenterX);

                //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                yDistance = Math.abs(jsY - joystickCenterY);

                //Lasketaan kulma
                jsAngle = Math.toDegrees(Math.atan(xDistance / yDistance)) + 270;

                //Log.d("JS1 kulma on", String.valueOf(js1Angle));
            }

            //Joystickin oikea alakulma
            if (jsX > joystickCenterX && jsY > joystickCenterY) {
                //Log.d("JS1", "JS1 oikea alakulma");

                //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                xDistance = Math.abs(jsX - joystickCenterX);

                //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                yDistance = Math.abs(jsY - joystickCenterY);

                //Lasketaan kulma
                jsAngle = (float) Math.toDegrees(Math.atan(yDistance / xDistance));

                //Log.d("JS1 kulma on", String.valueOf(js1Angle));
            }

            //Joystickin vasen alakulma
            if (jsX < joystickCenterX && jsY > joystickCenterY) {
                //Log.d("JS1", "JS1 vasen alakulma");

                //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                xDistance = Math.abs(jsX - joystickCenterX);

                //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                yDistance = Math.abs(jsY - joystickCenterY);

                //Lasketaan kulma
                jsAngle = Math.toDegrees(Math.atan(xDistance / yDistance)) + 90;

                //Log.d("JS1 kulma on", String.valueOf(js1Angle));
            }

            //Joystickin vasen yläkulma
            if (jsX < joystickCenterX && jsY < joystickCenterY) {
                //Log.d("JS1", "JS1 vasen yläkulma");

                //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                xDistance = Math.abs(jsX - joystickCenterX);

                //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                yDistance = Math.abs(jsY - joystickCenterY);

                //Lasketaan kulma
                jsAngle = Math.toDegrees(Math.atan(yDistance / xDistance)) + 180;

                //Log.d("JS1 kulma on", String.valueOf(js1Angle));
            }
        }
        else{
            onJoystickArea = false;
        }
    }

    //Metodi, jolla joystick lopulta piirretään
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(bitmapJoystick, joystickCenterX - joystickRadius, joystickCenterY - joystickRadius, null);
    }

    //Metodi, jolla voi määrittää joystickin koon
    public void setJoystickSize(int width, int height)
    {
        bitmapJoystick = Bitmap.createScaledBitmap(bitmapJoystick, width, height, false);
    }

    //Metodi jolla saadaan joystickin suunta
    public String getDirection()
    {
        if(onJoystickArea == true){
            //Hahmon liikuttaminen
            //Oikealle 45 asteen sektori
            if(jsAngle > 0 && jsAngle < 22.5 || jsAngle > 337.5 && jsAngle < 360)
            {
                direction = "RIGHT";
            }
            //Oikealle alas
            if(jsAngle > 22.5 && jsAngle < 67.5)
            {
                direction = "DOWNRIGHT";
            }
            //Alas
            if(jsAngle > 67.5 && jsAngle < 112.5)
            {
                direction = "DOWN";
            }
            //Vasemmalle alas
            if(jsAngle > 112.5 && jsAngle < 157.5)
            {
                direction = "DOWNLEFT";
            }
            //Vasemmalle
            if(jsAngle > 157.5 && jsAngle < 202.5)
            {
                direction = "LEFT";
            }
            //Vasemmalle ylös
            if(jsAngle > 202.5 && jsAngle < 247.5)
            {
                direction = "UPLEFT";
            }
            //Ylös
            if(jsAngle > 247.5 && jsAngle < 292.5)
            {
                direction = "UP";
            }
            //Oikealle ylös
            if(jsAngle > 292.5 && jsAngle <  337.5)
            {
                direction = "UPRIGHT";
            }
        }
        else{
            direction = "NULL";
        }
        return direction;
    }

    public float getJSX()
    {
        return jsX;
    }

    public float getJSY()
    {
        return jsY;
    }

}

