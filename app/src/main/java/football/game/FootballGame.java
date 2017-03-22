package football.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

//implements View.OnTouchListener

public class FootballGame extends SurfaceView implements View.OnTouchListener{

    SurfaceHolder holder;
    FootballGameThread footballGameThread;

    private Bitmap bitmapFootball;
    private Bitmap bitmapPlayer1;
    private Bitmap bitmapPlayer2;
    private Bitmap bitmapBackgroundImage;

    private Bitmap bitmapJoystick1;
    private Bitmap bitmapJoystick2;

    private AttributeSet attributeSet2;

    private int screenWidth = 0;
    private int screenHeight = 0;


    private double ballSpeedX = 0;
    private double ballSpeedY = 0;

    private int player1CenterX = 0;
    private int player1CenterY = 0;

    private double player1SpeedX = 0;
    private double player1SpeedY = 0;

    private int playerRadius = 0;

    private int ballCenterX = 0;
    private int ballCenterY = 0;

    private int ballRadius = 52;

    private int joystick1CenterX = 0;
    private int joystick1CenterY = 0;

    private int joystickRadius = 0;

    double gravity = 0.5;

    float startX = 0;
    float startY = 0;
    float endX = 0;
    float endY = 0;

    float joystickX = 0;
    float joystickY = 0;

    float x = 0;
    float y = 0;

    String teksti = "";
    float tekstiX = 0;
    float tekstiY = 0;

    private long hitTime = 0;
    private long lastHitTimeBallX = 0;
    private long lastHitTimeBallY = 0;

    private long lastHitTimePlayer1X = 0;
    private long lastHitTimePlayer1Y = 0;

    private long lastHitTimeBallAndPlayer1 = 0;
    private long lastHitTimeBallAndPlayer2 = 0;


    Ball ball;
    Joystick joystick;

    View view;
    //AttributeSet attributeSet

    public FootballGame(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //setWillNotDraw(false);
        //Display d = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        //Katsotaan näytön koko
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        attributeSet2 = attributeSet;
        this.setOnTouchListener(this);
        //joystick = new Joystick(context, attributeSet);
        ball = new Ball(context, attributeSet);
        footballGameThread = new FootballGameThread(this);
        //Määritellään pallon aloitus keskipiste
        putStartCordinates();
        //Määritellään kuvat
        createBitmaps();
        holder = getHolder();
        String testi = "";
        testi = Integer.toString(screenWidth);
        testi += " ";
        testi += Integer.toString(screenHeight);
        Log.d("football", testi);
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
    }

    public void putStartCordinates(){

        ballRadius = screenWidth / 40;

        playerRadius = screenWidth / 20;

        joystickRadius = screenWidth / 15;

        player1CenterX = screenWidth / 10;
        //player1CenterX += playerRadius;
        player1CenterY = screenHeight / 2;
        //player1CenterY += playerRadius;


        ballCenterX = screenWidth / 2;
        ballCenterX += ballRadius;
        ballCenterY = screenHeight / 2;
        ballCenterY += ballRadius;

        joystick1CenterX = screenWidth / 8;
        //joystick1CenterX += joystickRadius;
        joystick1CenterY = (int)((double)screenHeight * 0.7);
        //joystick1CenterY += joystickRadius;


    }


    public void createBitmaps(){

        bitmapFootball = BitmapFactory.decodeResource(getResources(), R.drawable.jalkapallo);
        bitmapFootball = Bitmap.createScaledBitmap(bitmapFootball, ballRadius * 2, ballRadius * 2, true);

        bitmapPlayer1 = BitmapFactory.decodeResource(getResources(), R.drawable.pelaaja1);
        bitmapPlayer1 = Bitmap.createScaledBitmap(bitmapPlayer1, playerRadius * 2, playerRadius * 2, true);

        bitmapPlayer2 = BitmapFactory.decodeResource(getResources(), R.drawable.pelaaja2);
        bitmapPlayer2 = Bitmap.createScaledBitmap(bitmapPlayer2, screenWidth / 10, screenWidth / 8, true);

        bitmapBackgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.taustakuva);
        bitmapBackgroundImage = Bitmap.createScaledBitmap(bitmapBackgroundImage, screenWidth, screenHeight, true);

        bitmapJoystick1 = BitmapFactory.decodeResource(getResources(), R.drawable.sintausta);
        //bitmapJoystick1 = Bitmap.createScaledBitmap(bitmapJoystick1, joystickRadius * 2, joystickRadius * 2, true);
        bitmapJoystick1 = Bitmap.createScaledBitmap(bitmapJoystick1, joystickRadius * 2, joystickRadius * 2, true);
    }

    //Päivittää pallon ja pelaajien sijainnit ja katsoo tapahtuuko törmäyksiä
    protected void physics(){
        hitTime = System.currentTimeMillis();
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

        //Log.d("hehehe", "hohoho");
        //Hidastetaan palloa pikku hiljaa X suunnassa
        if(ballSpeedX > 0){
            ballSpeedX -= 0.1;

        }
        if(ballSpeedX < 0){
            ballSpeedX += 0.1;
        }
        //Pallon painovoima
        if(ballSpeedY < 20){
            ballSpeedY += gravity;
        }

        //Hidastetaan pelaajia
        if(player1SpeedX > 0){
            player1SpeedX -= 0.1;
        }
        if(player1SpeedX < 0){
            player1SpeedX += 0.1;
        }
        //Pelaaja1 painovoima
        if(player1SpeedY < 20){
            player1SpeedY += gravity;
        }

        //Muutetaan pallon kohtaa nopeuden mukaan
        ballCenterX += (int)ballSpeedX;
        ballCenterY += (int)ballSpeedY;

        //Muutetaan pelaajien kohta nopeuden mukaan
        player1CenterX += (int)player1SpeedX;
        player1CenterY += (int)player1SpeedY;

        //Katsotaan osuuko pallo reunoille ja muutetaan ja hidastetaan vauhtia
        if(ballCenterX - ballRadius < 0 && hitTime > lastHitTimeBallX+50 || ballCenterX + ballRadius > screenWidth && hitTime > lastHitTimeBallX+50  ){
            lastHitTimeBallX = System.currentTimeMillis();
            ballSpeedX = -(ballSpeedX*0.75);
        }
        //Katsotaan osuuko pallo pelin kattoon/maahan ja muutetaan ja hidastetaan vauhtia
        if( ballCenterY - ballRadius < 0 && hitTime > lastHitTimeBallY+50 || ballCenterY + ballRadius > screenHeight *0.8 && hitTime > lastHitTimeBallY+50){
            lastHitTimeBallY = System.currentTimeMillis();
            ballSpeedY = -(ballSpeedY*0.75);
        }

        //Katsotaan osuuko pelaaja1 reunoille ja muutetaan ja hidastetaan vauhtia
        if(player1CenterX - playerRadius < 0 && hitTime > lastHitTimePlayer1X+50 || player1CenterX + playerRadius > screenWidth && hitTime > lastHitTimePlayer1X+50  ){
            lastHitTimePlayer1X = System.currentTimeMillis();
            player1SpeedX = -(player1SpeedX*0.75);
        }
        //Katsotaan osuuko pelaaja pelin kattoon/maahan ja muutetaan ja hidastetaan vauhtia
        if( player1CenterY - playerRadius < 0 && hitTime > lastHitTimePlayer1Y+50 || player1CenterY + playerRadius > screenHeight *0.8 && hitTime > lastHitTimePlayer1Y+50){
            lastHitTimePlayer1Y = System.currentTimeMillis();
            player1SpeedY = -(player1SpeedY*0.75);
        }

        //Jos peliin tulee bugi ja  pallo menee laitojen yli niin laitetaan pallo keskelle
        if(ballCenterX > screenWidth + 200 || ballCenterY > screenHeight + 200){
            ballCenterX = screenWidth / 2;
            ballCenterY = screenHeight / 2;
            ballSpeedX = 0;
            ballSpeedY = 0;
        }

        //Katsotaan osuuko pelaaja palloon
        if(((((player1CenterX+playerRadius > ballCenterX - ballRadius && player1CenterX +playerRadius < ballCenterX + ballRadius) ||
                (player1CenterX-playerRadius < ballCenterX + ballRadius && player1CenterX -playerRadius > ballCenterX - ballRadius)) &&
                ((player1CenterY + playerRadius > ballCenterY - ballRadius && player1CenterY +playerRadius < ballCenterY + ballRadius) ||
                        (player1CenterY-playerRadius < ballCenterY + ballRadius && player1CenterY -playerRadius > ballCenterY - ballRadius)))
        && hitTime > lastHitTimeBallAndPlayer1+50)){
            lastHitTimeBallAndPlayer1 = System.currentTimeMillis();
            ballSpeedX = player1SpeedX * 2;
            ballSpeedY = player1SpeedY * 2;
        }
        /*
        if(player1CenterX + playerRadius >= ballCenterX - ballRadius && player1CenterX + playerRadius <= ballCenterX + ballRadius &&
        player1CenterY + playerRadius >= ballCenterY - ballRadius && player1CenterY + playerRadius <= ballCenterY + ballRadius
                && hitTime > lastHitTimeBallAndPlayer1 + 50 ){
            lastHitTimeBallAndPlayer1 = System.currentTimeMillis();
            ballSpeedX = player1SpeedX * 2;
            ballSpeedY = player1SpeedY * 2;
        }*/

        /*
        if(player1CenterX - playerRadius <= ballCenterX + ballRadius && player1CenterX - playerRadius >= ballCenterX - ballRadius &&
        player1CenterY - playerRadius <= ballCenterY + ballRadius && player1CenterY - playerRadius >= ballCenterY - ballRadius
                && hitTime > lastHitTimeBallAndPlayer1 + 50){
            lastHitTimeBallAndPlayer1 = System.currentTimeMillis();
            ballSpeedX = player1SpeedX * 2;
            ballSpeedY = player1SpeedY * 2;
        }*/
        /*
        if(player1CenterY + playerRadius >= ballCenterY - ballRadius && player1CenterY + playerRadius <= ballCenterY + ballRadius){
            ballSpeedY = player1SpeedY * 2;
        }

        if(player1CenterY - playerRadius <= ballCenterY + ballRadius && player1CenterY - playerRadius >= ballCenterY - ballRadius){
            ballSpeedY = player1SpeedY * 2;
        }*/


    }



    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);

        canvas.drawBitmap(bitmapBackgroundImage, 0, 0, null);
        //Log.d("football", "piirretaan");
        //ball.invalidate();
        //ball.draw(canvas);
        //canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(38);
        paint.setColor(Color.YELLOW);

        canvas.drawText(teksti, tekstiX, tekstiY, paint);
        /*

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

        canvas.drawCircle(ballCenterX, ballCenterY, ballRadius, paint);*/

        //Piirretään jalkapallo
        canvas.drawBitmap(bitmapFootball, ballCenterX-ballRadius, ballCenterY-ballRadius, null);

        //Piirretään pelaajat
        canvas.drawBitmap(bitmapPlayer1, player1CenterX-playerRadius, player1CenterY-playerRadius, null);
        //canvas.drawBitmap(bitmapPlayer2, ballCenterX+150, ballCenterY, null);

        //Piirretään joystickit
        canvas.drawBitmap(bitmapJoystick1, joystick1CenterX-joystickRadius, joystick1CenterY-joystickRadius, null);
        //canvas.drawBitmap(bitmapJoystick1, player1CenterX, player1CenterY, null);

        //canvas.drawBitmap(bitmapJoystick1, ballCenterX, ballCenterY, null);

    }
    /*
    @Override
    public void onBackPressed(){


        Log.d("football", "painoit back");
    }*/


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //Log.d("football", "kosketeltiin näyttoa");

            switch (event.getAction()){


                case MotionEvent.ACTION_DOWN:

                    x = event.getX();
                    y = event.getY();

                    if(x < joystick1CenterX + joystickRadius  && x > joystick1CenterX && y > joystick1CenterY - joystickRadius && y < joystick1CenterY){
                        Log.d("kosketeltiin", "nopeutetaan");
                        player1SpeedY -= 5;
                        player1SpeedX += 5;
                    }
                    if(x > joystick1CenterX - joystickRadius  && x < joystick1CenterX && y > joystick1CenterY - joystickRadius && y < joystick1CenterY){
                        Log.d("kosketeltiin", "hidastetaan");
                        player1SpeedY -= 5;
                        player1SpeedX -= 5;
                    }
                    if(x > joystick1CenterX - joystickRadius  && x < joystick1CenterX && y < joystick1CenterY + joystickRadius && y > joystick1CenterY){
                        Log.d("kosketeltiin", "nopeutetaan");
                        player1SpeedY += 5;
                        player1SpeedX -= 5;
                    }
                    if(x < joystick1CenterX + joystickRadius  && x > joystick1CenterX && y < joystick1CenterY + joystickRadius && y > joystick1CenterY){
                        Log.d("kosketeltiin", "nopeutetaan");
                        player1SpeedY += 5;
                        player1SpeedX += 5;
                    }


                    tekstiX = event.getX();
                    tekstiY = event.getY();

                    teksti = Float.toString(tekstiX);
                    teksti += " ";
                    teksti += Float.toString(tekstiY);

                    startX = event.getX();
                    startY = event.getY();

                //case MotionEvent.ACTION_POINTER_DOWN:



                case MotionEvent.ACTION_UP:
                    endX = event.getX();
                    endY = event.getY();

            }
            /*
            float changeX = endX - startX;
            float changeY = endY - startY;


            if (Math.abs(changeX) < 100 && Math.abs(changeX) >= 50 || Math.abs(changeY) < 100 && Math.abs(changeY) >= 50){
                player1SpeedX = (int)changeX / 10;
                player1SpeedY = (int)changeY / 10;
            }
            else if(Math.abs(changeX) >= 100 && Math.abs(changeX) < 300 || Math.abs(changeY) >= 100 && Math.abs(changeY) < 300 ){
                player1SpeedX = (int)changeX / 20;
                player1SpeedY = (int)changeY / 20;
            }
            else{
                player1SpeedX = (int)changeX / 5;
                player1SpeedY = (int)changeY / 5;
            }*/

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

/*
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        //realImage = R.drawable.aurinko;
        /*
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

    Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, 1920,
            1080, filter);
        return newBitmap;
                }
 */