package football.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
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
    private Bitmap bitmapGoalLeft;
    private Bitmap bitmapGoalRight;

    private Bitmap bitmapJoystick1;
    private Bitmap bitmapJoystick2;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private int gameAreaMinX = 0;
    private int gameAreaMinY = 0;

    private int gameAreaMaxX = 0;
    private int gameAreaMaxY = 0;


    private double ballSpeedX = 0;
    private double ballSpeedY = 0;

    private int player1CenterX = 0;
    private int player1CenterY = 0;

    private int player2CenterX = 0;
    private int player2CenterY = 0;

    private double player1SpeedX = 0;
    private double player1SpeedY = 0;

    private double player2SpeedX = 0;
    private double player2SpeedY = 0;

    private int playerMaxSpeed = 20;
    private int increasePlayerSpeed = 5;
    //private int playerMaxSpeed = 20;


    private int playerRadius = 0;

    private int ballCenterX = 0;
    private int ballCenterY = 0;

    private int ballRadius = 52;

    private int joystick1CenterX = 0;
    private int joystick1CenterY = 0;

    private int joystick2CenterX = 0;
    private int joystick2CenterY = 0;

    private int joystickRadius = 0;

    private int goalWidth = 0;
    private int goalHeight = 0;

    private int goalLeftX = 0;
    private int goalLeftY = 0;
    private int goalRightX = 0;
    private int goalRightY = 0;

    private int player1Score = 0;
    private int player2Score = 0;

    private Paint paintText;
    private String score;

    private int textX = 0;
    private int textY = 0;

    double friction = 0;
    double gravity = 0;

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

    private long lastHitTimePlayer2X = 0;
    private long lastHitTimePlayer2Y = 0;

    private long lastHitTimeBallAndPlayer1 = 0;
    private long lastHitTimeBallAndPlayer2 = 0;

    private static PointF touchOnScreen[] = new PointF[10];


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

        //attributeSet2 = attributeSet;
        this.setOnTouchListener(this);
        //joystick = new Joystick(context, attributeSet);
        ball = new Ball(context, attributeSet);
        footballGameThread = new FootballGameThread(this);
        //Määritellään arvot näytölle sopiviksi
        setScaledValues();
        //Määritellään pallon aloitus keskipiste
        setStartCoordinates();
        //Määritellään teksti joka tulostaa tulokset näytölle
        setText();
        //Määritellään kuvat
        createBitmaps();

        holder = getHolder();
        /*
        String testi = "";
        testi = Integer.toString(screenWidth);
        testi += " ";
        testi += Integer.toString(screenHeight);
        Log.d("football", testi);*/
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

    //Skaalataan kaikki arvot sopiviksi pelaajan näytölle
    public void setScaledValues(){
        gameAreaMinX = 0;
        gameAreaMinY = 0;

        gameAreaMaxX = screenWidth;
        gameAreaMaxY = (int)(screenHeight * 0.8);

        gravity = (double)screenHeight / ((double)screenHeight * 2);
        friction = (double)screenWidth / ((double)screenHeight * 6);

        ballRadius = screenWidth / 40;
        playerRadius = screenWidth / 20;
        joystickRadius = screenWidth / 15;

        goalWidth = (int)((double)screenWidth * 0.13);
        goalHeight = (int)((double)screenHeight * 0.45);
    }

    //Laitetaan kaikki esineet ja pelaajat oikealle paikalle
    public void setStartCoordinates(){

        player1CenterX = (int)((double)screenWidth * 0.2);
        player1CenterY = screenHeight / 2;

        player2CenterX = (int)((double)screenWidth * 0.8);
        player2CenterY = screenHeight / 2;

        ballCenterX = screenWidth / 2;
        ballCenterY = screenHeight / 2;

        joystick1CenterX = (int)((double)screenWidth * 0.2);
        joystick1CenterY = (int)((double)screenHeight * 0.78);

        joystick2CenterX = (int)((double)screenWidth * 0.8);
        joystick2CenterY = (int)((double)screenHeight * 0.78);

        goalLeftX = gameAreaMinX;
        goalLeftY = (int)((double)screenWidth * 0.2);

        goalRightX = gameAreaMaxX - goalWidth;
        goalRightY = (int)((double)screenWidth * 0.2);
    }

    public void setText(){
        textX = (int)((double)screenWidth * 0.5);
        textY = (int)((double)screenHeight * 0.2);

        score = Integer.toString(player1Score);
        score += " - ";
        score += Integer.toString(player2Score);

        paintText =  new Paint();
        paintText.setStyle(Paint.Style.FILL_AND_STROKE);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(158);
        paintText.setColor(Color.BLACK);
    }

    public void updateScore(){
        score = Integer.toString(player1Score);
        score += " - ";
        score += Integer.toString(player2Score);
    }

    public void nullSpeeds(){
        ballSpeedX = 0;
        ballSpeedY = 0;

        player1SpeedX = 0;
        player1SpeedY = 0;

        player2SpeedX = 0;
        player2SpeedY = 0;
    }


    //Luodaan kaikki kuvat joita aletaan piirtämään näytölle
    public void createBitmaps(){

        bitmapFootball = BitmapFactory.decodeResource(getResources(), R.drawable.jalkapallo);
        bitmapFootball = Bitmap.createScaledBitmap(bitmapFootball, ballRadius * 2, ballRadius * 2, true);

        bitmapPlayer1 = BitmapFactory.decodeResource(getResources(), R.drawable.pelaaja1);
        bitmapPlayer1 = Bitmap.createScaledBitmap(bitmapPlayer1, playerRadius * 2, playerRadius * 2, true);

        bitmapPlayer2 = BitmapFactory.decodeResource(getResources(), R.drawable.pelaaja2);
        bitmapPlayer2 = Bitmap.createScaledBitmap(bitmapPlayer2, playerRadius * 2, playerRadius * 2, true);

        bitmapBackgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
        bitmapBackgroundImage = Bitmap.createScaledBitmap(bitmapBackgroundImage, screenWidth, screenHeight, true);

        bitmapGoalLeft = BitmapFactory.decodeResource(getResources(), R.drawable.goalleft);
        bitmapGoalLeft = Bitmap.createScaledBitmap(bitmapGoalLeft, goalWidth, goalHeight, true);

        bitmapGoalRight = BitmapFactory.decodeResource(getResources(), R.drawable.goalright);
        bitmapGoalRight = Bitmap.createScaledBitmap(bitmapGoalRight, goalWidth, goalHeight, true);

        bitmapJoystick1 = BitmapFactory.decodeResource(getResources(), R.drawable.joystick1);
        bitmapJoystick1 = Bitmap.createScaledBitmap(bitmapJoystick1, joystickRadius * 2, joystickRadius * 2, true);

        bitmapJoystick2 = BitmapFactory.decodeResource(getResources(), R.drawable.joystick2);
        bitmapJoystick2= Bitmap.createScaledBitmap(bitmapJoystick2, joystickRadius * 2, joystickRadius * 2, true);

    }

    //Päivittää pallon ja pelaajien sijainnit ja katsoo tapahtuuko törmäyksiä
    protected void physics(){
        hitTime = System.currentTimeMillis();

        //Pallon sivut
        double ballRightX = ballCenterX + ballRadius;
        double ballLeftX = ballCenterX - ballRadius;
        double ballTopY = ballCenterY - ballRadius;
        double ballBottomY = ballCenterY + ballRadius;

        //Pelaaja1:n sivut
        double player1RightX = player1CenterX + playerRadius;
        double player1LeftX = player1CenterX - playerRadius;
        double player1TopY = player1CenterY - playerRadius;
        double player1BottomY = player1CenterY + playerRadius;

        //Pelaaja2:n sivut
        double player2RightX = player2CenterX + playerRadius;
        double player2LeftX = player2CenterX - playerRadius;
        double player2TopY = player2CenterY - playerRadius;
        double player2BottomY = player2CenterY + playerRadius;

        physicsFriction();
        //moveBallAndPlayers();
        //physicsWallCollisions();
        //physicsPlayerCollisions();


        //Muutetaan pallon kohtaa nopeuden mukaan jos se ei mene reunojen yli
        //Pallo X-akselilla
        if(ballLeftX + ballSpeedX > gameAreaMinX && ballRightX + ballSpeedX < gameAreaMaxX){
            ballCenterX += (int)ballSpeedX;
        }
        else{
            //Log.d("jalkapallo", "osu seinään");
            ballSpeedX = ballSpeedX * -0.75;
            ballCenterX += ballSpeedX;
        }

        //Pallo y-akselilla laitetaan pelaajan säde, jotta pelaajat ja pallot on samalla korkeudella
        //Pallo y-akselilla
        if(ballTopY + ballSpeedY > 0 && ballBottomY + ballSpeedY < gameAreaMaxY){
            //Pallo osuu vasemman maalin ylärimaan
            if(goalLeftX + goalWidth > ballCenterX + ballSpeedX  && goalLeftY < ballBottomY + ballSpeedY
                     && ballBottomY + ballSpeedY < goalLeftY + (int)((double)goalHeight *0.1)){
                ballSpeedY = ballSpeedY * -0.75;
                //ballSpeedY = -ballSpeedY;
                ballSpeedX += 8;
                ballCenterY += ballSpeedY;
            }
            //Pallo osuu oikean maalin ylärimaan
            else if (goalRightX < ballCenterX + ballSpeedX  && goalRightY < ballBottomY + ballSpeedY
                    && ballBottomY + ballSpeedY < goalRightY + (int)((double)goalHeight *0.1)){
                ballSpeedY = ballSpeedY * -0.75;
                //ballSpeedY = -ballSpeedY;
                ballSpeedX -= 8;
                ballCenterY += ballSpeedY;
            }
            else{
                ballCenterY += (int)ballSpeedY;
            }
        }
        else{
            //Log.d("jalkapallo", "osu kattoon");
            ballSpeedY = ballSpeedY * -0.75;
            ballCenterY += ballSpeedY;
        }


        //
        //Player1
        //Päivitetään pelaajan keskipiste pelaaajan nopeuden mukaan katsotaan myös ettei mene laitojen yli
        if(player1LeftX + player1SpeedX > gameAreaMinX && player1RightX + player1SpeedX < gameAreaMaxX){
            player1CenterX += (int)player1SpeedX;
        }
        else{
            player1SpeedX = player1SpeedX * -0.75;
            player1CenterX += player1SpeedX;
        }
        //Pelaaja1 y-akselilla
        if(player1TopY + player1SpeedY > gameAreaMinY && player1BottomY + player1SpeedY < gameAreaMaxY){
            player1CenterY += (int)player1SpeedY;
        }
        else{
            player1SpeedY = player1SpeedY * -0.75;
            player1CenterY += player1SpeedY;
        }


        //
        //Player2
        //Pelaaja2 x-akselilla
        if(player2LeftX + player2SpeedX > gameAreaMinX && player2RightX + player2SpeedX < gameAreaMaxX){
            player2CenterX += (int)player2SpeedX;
        }
        else{
            player2SpeedX = player2SpeedX * -0.75;
            player2CenterX += player2SpeedX;
        }

        //Pelaaja2 y-akselilla
        if(player2TopY + player2SpeedY > gameAreaMinY && player2BottomY + player2SpeedY < gameAreaMaxY){
            player2CenterY += (int)player2SpeedY;
        }
        else{
            player2SpeedY = player2SpeedY * -0.75;
            player2CenterY += player2SpeedY;
        }

        //Katsotaan osuuko pelaaja palloon
        if(((player1RightX > ballLeftX && player1RightX < ballRightX) || (player1LeftX < ballRightX && player1LeftX > ballLeftX)) &&
                ((player1BottomY > ballTopY && player1BottomY < ballBottomY) || (player1TopY < ballBottomY && player1TopY > ballTopY))){
            //lastHitTimeBallAndPlayer1 = System.currentTimeMillis();
            double angle = calculateAngle(ballCenterX, ballCenterY, player1CenterX, player1CenterY);
            calculateSpeed(angle);
            //ballSpeedX = angle;
        }
        /*
        //Pelaaja2 ja pallon töyrmäys
        //Pelaaja2
        if(((player2RightX > ballLeftX && player2RightX < ballRightX) || (player2LeftX < ballRightX && player2LeftX > ballLeftX)) &&
                ((player2BottomY > ballTopY && player2BottomY < ballBottomY) || (player2TopY < ballBottomY && player2TopY > ballTopY))){
            //lastHitTimeBallAndPlayer2 = System.currentTimeMillis();
            //&& hitTime > lastHitTimeBallAndPlayer2+100)
            ballSpeedX = player2SpeedX * 2;
            ballSpeedY = player2SpeedY * 2;
        }*/


        //
        //Katsotaan meneekö pallo maaliin
        //Pallo menee vasempaan maaliin
        if(ballRightX < goalLeftX + goalWidth && ballTopY > goalLeftY && ballCenterY < goalLeftY + goalHeight){
            setStartCoordinates();
            nullSpeeds();
            player2Score++;
            updateScore();
            if(player2Score >= 10){
                //player1Score = 0;
                //player2Score = 0;
                footballGameThread.setRunning(false);
            }
        }

        //Pallo menee oikeaan maaliin
        if(ballLeftX > goalRightX && ballTopY > goalRightY && ballCenterY < goalRightY + goalHeight){
            setStartCoordinates();
            nullSpeeds();
            player1Score++;
            updateScore();
            if(player1Score >= 10){
                //player1Score = 0;
                //player2Score = 0;
                footballGameThread.setRunning(false);
            }
        }
    }


    //
    //Otetaan huomioon ilmanvastus ja painovoima
    public void physicsFriction(){
        //Hidastetaan palloa pikku hiljaa X suunnassa
        if(ballSpeedX > 0){
            ballSpeedX -= friction;
        }
        else if(ballSpeedX < 0){
            ballSpeedX += friction;
        }

        //Pallon painovoima
        if(ballSpeedY < 20){
            ballSpeedY += gravity;
        }

        //Hidastetaan pelaajia
        if(player1SpeedX > 0){
            player1SpeedX -= friction;
        }
        else if(player1SpeedX < 0){
            player1SpeedX += friction;
        }

        //Pelaaja2
        if(player2SpeedX > 0){
            player2SpeedX -= friction;
        }
        else if(player2SpeedX < 0){
            player2SpeedX += friction;
        }

        //Pelaaja1 painovoima
        if(player1SpeedY < 20){
            player1SpeedY += gravity;
        }
        //Pelaaja2 painovoima
        if(player2SpeedY < 20){
            player2SpeedY += gravity;
        }
        /*
        if(ballCenterX < goalLeftX + goalWidth && ballCenterY < goalLeftY && ballSpeedX < 20){
            ballSpeedX += 0.5;
        }
        if(ballCenterX > goalRightX && ballCenterY < goalRightY && ballSpeedX > -20){
            ballSpeedX -= 0.5;
        }*/
    }

    public void moveBallAndPlayers(){

    }

    public void physicsWallCollisions(){

    }

    public void physicsPlayerCollisions(){

    }

    public double calculateAngle(int ballX, int ballY, int playerX, int playerY){

        double angle;
        double anglePlus;
        double xDistance;
        double yDistance;
        xDistance = (double)Math.abs(ballX - playerX);

        //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
        yDistance = (double)Math.abs(ballY - playerY);

        //Lasketaan kulma
        angle = (double) Math.toDegrees(Math.atan(yDistance / xDistance));

        //Pallo on pelaajan oikeassa alakulmassa
        if(ballX > playerX && ballY > playerY){
            //Ei tarvi tehdä mitään
        }
        //Pallo on pelaajan vasemmassa alakulmassa
        if(ballX < playerX && ballY > playerY){
            angle += 90;
        }
        //Pallo on pelaajan vasemmassa yläkulmassa
        if(ballX < playerX && ballY < playerY){
            angle += 180;
        }
        //Pallo on pelaajan oikeassa yläkulmassa
        if(ballX > playerX && ballY < playerY){
            angle += 270;
        }
        return angle;
    }

    public void calculateSpeed(double angle){

        //Pallo on pelaajan oikeassa alakulmassa
        if(angle >= 0 && angle < 90){
            double angle2 = 90 - angle;
            ballSpeedX = angle * 0.3;
            ballSpeedY = angle2 * 0.3;
            if(ballSpeedX < 5){
                ballSpeedX += 5;
            }
            if(ballSpeedY < 5){
                ballSpeedY += 5;
            }
        }
        //Pallo on pelaajan vasemmassa alakulmassa
        if(angle >= 90 && angle < 180){
            double angle2 = 180 - angle;
            ballSpeedX = angle * -0.3;
            ballSpeedY = angle2 * 0.3;
            if(ballSpeedX > -5){
                ballSpeedX += 5;
            }
            if(ballSpeedY < 5){
                ballSpeedY += 5;
            }
        }
        //Pallo on pelaajan vasemmassa yläkulmassa
        if(angle >= 180 && angle < 270){
            double angle2 = 270 - angle;
            ballSpeedX = angle * -0.3;
            ballSpeedY = angle2 * -0.3;
            if(ballSpeedX > -5){
                ballSpeedX -= 5;
            }
            if(ballSpeedY > -5){
                ballSpeedY -= 5;
            }
        }
        //Pallo on pelaajan oikeassa yläkulmassa
        if(angle >= 270 && angle < 360){
            double angle2 = 360 - angle;
            ballSpeedX = angle * 0.3;
            ballSpeedY = angle2 * -0.3;
            if(ballSpeedX < 5){
                ballSpeedX += 5;
            }
            if(ballSpeedY > -5){
                ballSpeedY -= 5;
            }
        }

    }


    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);

        //Piirretään taustakuva
        canvas.drawBitmap(bitmapBackgroundImage, 0, 0, null);

        //Log.d("football", "piirretaan");
        //ball.invalidate();
        //ball.draw(canvas);
        /*
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(38);
        paint.setColor(Color.YELLOW);

        canvas.drawText(teksti, tekstiX, tekstiY, paint);*/

        //Piirretään jalkapallo
        canvas.drawBitmap(bitmapFootball, ballCenterX-ballRadius, ballCenterY-ballRadius, null);

        //Piirretään pelaajat
        canvas.drawBitmap(bitmapPlayer1, player1CenterX-playerRadius, player1CenterY-playerRadius, null);

        //canvas.drawBitmap(bitmapPlayer2, player2CenterX-playerRadius, player2CenterY-playerRadius, null);
        //canvas.drawBitmap(bitmapPlayer2, ballCenterX+150, ballCenterY, null);

        //Piirretään maalit
        canvas.drawBitmap(bitmapGoalLeft, goalLeftX, goalLeftY, null);
        canvas.drawBitmap(bitmapGoalRight, goalRightX, goalRightY, null);

        //Piirretään joystickit
        canvas.drawBitmap(bitmapJoystick1, joystick1CenterX-joystickRadius, joystick1CenterY-joystickRadius, null);
        //canvas.drawBitmap(bitmapJoystick1, player1CenterX, player1CenterY, null);

        canvas.drawBitmap(bitmapJoystick2, joystick2CenterX-joystickRadius, joystick2CenterY-joystickRadius, null);



        canvas.drawText(score, textX, textY, paintText);

        //ball.draw(canvas);
        //canvas.drawBitmap(bitmapJoystick1, ballCenterX, ballCenterY, null);

    }

    /*
    @Override
    public void onBackPressed(){


        Log.d("football", "painoit back");
    }*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float js1X = 0;
        float js1Y = 0;
        double js1Angle = 0;
        float distance = 0;
        float xDistance = 0;
        float yDistance = 0;


        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:

                x = event.getX();
                y = event.getY();

                //Joystickin alueen määrittely
                if(x > joystick1CenterX - joystickRadius
                        && x < joystick1CenterX + joystickRadius
                        && y > joystick1CenterY - joystickRadius
                        && y < joystick1CenterY + joystickRadius)
                {
                    //Log.d("JS1", "Kosketellaan joystick 1");

                    //Joystickin vasen yläkulma on joystickCenterX ja joystickCenterY
                    //Joystickin keskusta on joystickCenterX + joystickRadius ja joystickCenterY + joystickRadius

                    //Jos kosketus joystickin alueella otetaan x ja y talteen
                    js1X = x;
                    js1Y = y;
                    Log.d("X ", String.valueOf(js1X) );
                    Log.d("Y" , String.valueOf(js1Y));

                    if(js1X == joystick1CenterX && js1Y == joystick1CenterY)
                    {
                        Log.d("JS1", "JS1 keskipiste");
                    }

                    //Joystickin oikea yläkulma
                    if(js1X > joystick1CenterX && js1Y < joystick1CenterY)
                    {
                        //Log.d("JS1", "JS1 oikea yläkulma");

                        //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                        xDistance = Math.abs(js1X - joystick1CenterX);

                        //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                        yDistance = Math.abs(js1Y - joystick1CenterY);

                        //Lasketaan hypotenuusa
                        distance = (float) Math.abs(Math.sqrt(Math.abs(xDistance) + Math.abs(yDistance)));

                        //Lasketaan kulma
                        js1Angle = Math.toDegrees(Math.atan(xDistance / yDistance)) + 270;

                        //Log.d("JS1 kulma on", String.valueOf(js1Angle));

                    }

                    //Joystickin oikea alakulma
                    if(js1X > joystick1CenterX && js1Y > joystick1CenterY)
                    {
                        //Log.d("JS1", "JS1 oikea alakulma");

                        //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                        xDistance = Math.abs(js1X - joystick1CenterX);

                        //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                        yDistance = Math.abs(js1Y - joystick1CenterY);

                        //Lasketaan kulma
                        js1Angle = (float) Math.toDegrees(Math.atan(yDistance / xDistance));

                        //Log.d("JS1 kulma on", String.valueOf(js1Angle));

                    }

                    //Joystickin vasen alakulma
                    if(js1X < joystick1CenterX && js1Y > joystick1CenterY)
                    {
                        //Log.d("JS1", "JS1 vasen alakulma");

                        //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                        xDistance = Math.abs(js1X - joystick1CenterX);

                        //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                        yDistance = Math.abs(js1Y - joystick1CenterY);

                        //Lasketaan kulma
                        js1Angle =  Math.toDegrees(Math.atan(xDistance / yDistance)) + 90;

                        //Log.d("JS1 kulma on", String.valueOf(js1Angle));
                    }

                    //Joystickin vasen yläkulma
                    if(js1X < joystick1CenterX && js1Y < joystick1CenterY)
                    {
                        //Log.d("JS1", "JS1 vasen yläkulma");

                        //Lasketaan etäisyys X- suunnassa keskipisteen X:n verrattuna, tämä on vastakkainen kateetti
                        xDistance = Math.abs(js1X - joystick1CenterX);

                        //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
                        yDistance = Math.abs(js1Y - joystick1CenterY);

                        //Lasketaan kulma
                        js1Angle = Math.toDegrees(Math.atan(yDistance / xDistance)) + 180;

                        //Log.d("JS1 kulma on", String.valueOf(js1Angle));
                    }



                }

                //Hahmon liikuttaminen
                //Oikealle 45 asteen sektori
                if(js1Angle > 0 && js1Angle < 22.5 || js1Angle > 337.5 && js1Angle < 360)
                {
                    Log.d("Suunta", "Oikealle");
                    //Jos hahmon nopeus on vähemmän kuin maksiminopeus, nopeutetaan
                    if(player1SpeedX < playerMaxSpeed)
                    {
                        player1SpeedX += increasePlayerSpeed;
                    }

                }
                //Oikealle alas
                if(js1Angle > 22.5 && js1Angle < 67.5)
                {
                    Log.d("Suunta", "Oikealle alaviistoon");
                    //Jos hahmon nopeus on  vähemmän kuin maksiminopeus, nopeutetaan
                    if(player1SpeedX < playerMaxSpeed)
                    {
                        player1SpeedX += increasePlayerSpeed;
                    }

                    //Jos hahmon nopeus on vähemmän kuin maksiminopeus, nopeutetaan
                    if(player1SpeedY < playerMaxSpeed)
                    {
                        player1SpeedY += increasePlayerSpeed;
                    }
                }
                //Alas
                if(js1Angle > 67.5 && js1Angle < 112.5)
                {
                    Log.d("Suunta", " Alas");
                    //Jos hahmon nopeus on vähemmän kuin maksiminopeus, nopeutetaan
                    if(player1SpeedY < playerMaxSpeed)
                    {
                        player1SpeedY += increasePlayerSpeed;
                    }
                }
                //Vasemmalle alas
                if(js1Angle > 112.5 && js1Angle < 157.5)
                {
                    Log.d("Suunta", "Vasemmalle alaviistoon");
                    //Jos hahmon nopeus on enemmän kuin negatiivinen maksiminopeus, lisätää
                    if(player1SpeedX > -playerMaxSpeed)
                    {
                        player1SpeedX -= increasePlayerSpeed;
                    }

                    if(player1SpeedY < playerMaxSpeed)
                    {
                        player1SpeedY += increasePlayerSpeed;
                    }
                }
                //Vasemmalle
                if(js1Angle > 157.5 && js1Angle < 202.5)
                {
                    Log.d("Suunta", "Vasemmalle");

                    if(player1SpeedX > -playerMaxSpeed)
                    {
                        player1SpeedX -= increasePlayerSpeed;
                    }
                }
                //Vasemmalle ylös
                if(js1Angle > 202.5 && js1Angle < 247.5)
                {
                    Log.d("Suunta", "Vasemmalle yläviistoon");
                    if(player1SpeedX > -playerMaxSpeed)
                    {
                        player1SpeedX -= increasePlayerSpeed;
                    }

                    if(player1SpeedY > -playerMaxSpeed)
                    {
                        player1SpeedY -= increasePlayerSpeed;
                    }
                }
                //Ylös
                if(js1Angle > 247.5 && js1Angle < 292.5)
                {
                    Log.d("Suunta", "Ylös");
                    if(player1SpeedY > -playerMaxSpeed)
                    {
                        player1SpeedY -= increasePlayerSpeed;
                    }
                }
                //Oikealle ylös
                if(js1Angle > 292.5 && js1Angle <  337.5)
                {
                    Log.d("Suunta", "Oikealle yläviistoon");
                    if(player1SpeedX < playerMaxSpeed)
                    {
                        player1SpeedX += increasePlayerSpeed;
                    }
                    if(player1SpeedY > -playerMaxSpeed)
                    {
                        player1SpeedY -= increasePlayerSpeed;
                    }

                }
        }
        return true;
    }

}