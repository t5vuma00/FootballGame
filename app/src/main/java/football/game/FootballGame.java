package football.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

//implements View.OnTouchListener

public class FootballGame extends SurfaceView implements View.OnTouchListener{

    //Display d = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

    SurfaceHolder holder;
    FootballGameThread footballGameThread;
    private Bitmap bitmapFootball;
    private Bitmap bitmapJoystick;
    private Bitmap bitmapJoystick2;

    private AttributeSet attributeSet2;

    Bitmap bitmap2;
    //Canvas canvas;
    //Bitmap mBitmap = Bitmap.createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter);

    private int ballSpeedX = 0;
    private int ballSpeedY = 0;

    int ballCenterX = 400;
    int ballCenterY = 400;

    int ballRadius = 52;

    double gravity = 0.05;

    float startX = 0;
    float startY = 0;
    float endX = 0;
    float endY = 0;

    float joystickX = 0;
    float joystickY = 0;

    String teksti = "";
    float tekstiX = 0;
    float tekstiY = 0;


    Ball ball;
    Joystick joystick;

    View view;
    //AttributeSet attributeSet

    public FootballGame(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        attributeSet2 = attributeSet;

        this.setOnTouchListener(this);
        //joystick = new Joystick(context, attributeSet);
        ball = new Ball(context, attributeSet);
        footballGameThread = new FootballGameThread(this);
        holder = getHolder();
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aurinko);
        Log.d("football", "aloitus");

        holder.addCallback(new SurfaceHolder.Callback(){
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("football", "aloitetaan saie2");
                footballGameThread.setRunning(true);
                footballGameThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("football", "tuhotiin koko softa");
                boolean retry = true;
                footballGameThread.setRunning(false);
                while(retry){
                    try {
                        footballGameThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            }
        });
        bitmapFootball = BitmapFactory.decodeResource(getResources(), R.drawable.jalkapallo);
        bitmapFootball = Bitmap.createScaledBitmap(bitmapFootball, 104, 104, true);
        //bitmap2 = Bitmap.createScaledBitmap(bitmap, 1920, 1080, filter);
    }

    protected void testi(){
        /*

        joystickX = joystick.getX();
        joystickY = joystick.getY();

        if(joystickX > 0){
            ballSpeedX = (int)joystickX;
        }
        if(joystickY > 0){
            ballSpeedY = (int)joystickY;
            if(ballSpeedY > 30){
                ballSpeedY = 30;
            }
            if(ballSpeedY < -30){
                ballSpeedY = -30;
            }

            if(ballSpeedX > 30){
                ballSpeedX = 30;
            }
            if(ballSpeedX < -30){
                ballSpeedX = -30;
            }
        }
        */

        Log.d("hehehe", "hohoho");
        if(ballSpeedX > 0){
            ballSpeedX -= 0.05;

        }
        if(ballSpeedX < 0){
            ballSpeedX += 0.05;
        }
        if(ballSpeedY < 20){
            ballSpeedY += gravity;
            //gravity += 0.04;
        }
        ballCenterX += ballSpeedX;
        ballCenterY += ballSpeedY;

        if(ballCenterX - ballRadius < 0 || ballCenterX + ballRadius > 1920  ){
            ballSpeedX = -ballSpeedX;
        }
        if( ballCenterY - ballRadius < 0 || ballCenterY + ballRadius > 980){
            ballSpeedY = -ballSpeedY;
        }
        /*
        if (ballCenterY + ballRadius < 0){
            ballSpeedY = -ballSpeedY;
            ballSpeedY = 0;
        }*/


    }
    //Bitmap scaledBitmap = scaleDown(realImage, MAX_IMAGE_SIZE, true);

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        //realImage = R.drawable.aurinko;
        /*
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());*/

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, 1920,
                1080, filter);
        return newBitmap;
    }


    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);
        //Log.d("football", "piirretaan");
        //ball.invalidate();
        ball.draw(canvas);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(38);
        paint.setColor(Color.CYAN);

        canvas.drawText(teksti, tekstiX, tekstiY, paint);
        /*

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

        canvas.drawCircle(ballCenterX, ballCenterY, ballRadius, paint);*/

        canvas.drawBitmap(bitmapFootball, ballCenterX, ballCenterY, null);


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.d("football", "kosketeltiin näyttoa");


            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    tekstiX = event.getX();
                    tekstiY = event.getY();

                    teksti = Float.toString(tekstiX);
                    teksti += " ";
                    teksti += Float.toString(tekstiY);

                    startX = event.getX();
                    startY = event.getY();


                case MotionEvent.ACTION_UP:
                    endX = event.getX();
                    endY = event.getY();

            }
            float changeX = endX - startX;
            float changeY = endY - startY;


            if (Math.abs(changeX) < 100 && Math.abs(changeX) >= 50 || Math.abs(changeY) < 100 && Math.abs(changeY) >= 50){
                ballSpeedX = (int)changeX / 10;
                ballSpeedY = (int)changeY / 10;
            }
            else if(Math.abs(changeX) >= 100 && Math.abs(changeX) < 300 || Math.abs(changeY) >= 100 && Math.abs(changeY) < 300 ){
                ballSpeedX = (int)changeX / 20;
                ballSpeedY = (int)changeY / 20;
            }
            else{
                ballSpeedX = (int)changeX / 5;
                ballSpeedY = (int)changeY / 5;
            }

            //ball.Update(ballSpeedX, ballSpeedY);

            //invalidate();


        return true;
    }
    /*
    public void createJoystick(){
        //JOYSTICK_1
        //RelativeLayout layout_joystick;
        //Joystickin tausta näytetään imageviewin avulla
        //ImageView image_joystick, image_border;
        //layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);
        bitmapJoystick = BitmapFactory.decodeResource(getResources(), R.drawable.sintausta);
        final Joystick js;
        //js = new Joystick(getContext(), bitmapJoystick, R.drawable.sintatti);
        js = new Joystick(getContext(),attributeSet2,R.drawable.sintatti);
        /*
        //JOYSTICK_2
        RelativeLayout layout_joystick2;
        //Joystickin tausta näytetään imageviewin avulla
        ImageView image_joystick2, image_border2;
        layout_joystick2 = (RelativeLayout)findViewById(R.id.layout_joystick2);
        final Joystick js2;
        js2 = new Joystick(getApplicationContext(), layout_joystick2, R.drawable.puntatti);*/

//JOYSTICK_1
        //JOYSTICKIN ASETUKSET
        //
        //
        /*
        //napin koon määrittäminen | alkup. 150 150
        js.setStickSize(150, 150);
        //Joystickin taustan koko | alkup. 500 500
        js.setLayoutSize(450, 450);
        //                  | alkup. 150
        js.setLayoutAlpha(150);
        //                  | alkup. 100
        js.setStickAlpha(100);
        //Määrittää kuinka paljon sisempi nappi voi mennä ulomman yli | alkup. 90
        js.setOffset(90);
        //Määrittää Centterin koon, 50 säde keskipisteestä eli centterin halkaisija silloin 100 | alkup. 50
        js.setMinimumDistance(50);
        //
        //
        //JOYSTICKIN ASETUSTEN LOPPU
        /*
        //JOYSTICK_2
        //JOYSTICKIN ASETUKSET
        //
        //
        //napin koon määrittäminen | alkup. 150 150
        js2.setStickSize(150, 150);
        //Joystickin taustan koko | alkup. 500 500
        js2.setLayoutSize(450, 450);
        //                  | alkup. 150
        js2.setLayoutAlpha(150);
        //                  | alkup. 100
        js2.setStickAlpha(100);
        //Määrittää kuinka paljon sisempi nappi voi mennä ulomman yli | alkup. 90
        js2.setOffset(90);
        //Määrittää Centterin koon, 50 säde keskipisteestä eli centterin halkaisija silloin 100 | alkup. 50
        js2.setMinimumDistance(50);
        //
        //
        //JOYSTICKIN ASETUSTEN LOPPU
    }*/

}

