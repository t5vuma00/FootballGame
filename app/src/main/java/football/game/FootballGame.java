package football.game;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.WindowManager;


public class FootballGame extends SurfaceView implements View.OnTouchListener{

    SurfaceHolder holder;
    FootballGameThread footballGameThread;

    //Alustetaan taulukko, joka sisältää hahmojen kuvat
    private int[] playerSkinArray = {
            R.drawable.character1,
            R.drawable.character2,
            R.drawable.character3,
            R.drawable.character4,
            R.drawable.character5,
            R.drawable.character6,
            R.drawable.character7
    };

    //Alustetaan taulukko, joka sisältää taustojen kuvat 0-1
    private int[] backgroundArray = {
            R.drawable.backgroundsun,
            R.drawable.backgroundcity
    };

    //Alustetaan taulukko, joka sisältää jalkapallojen kuvat 0-1
    private int[] footballArray = {
            R.drawable.football,
            R.drawable.football2
    };


    String skinP1 = null;

    //Pelissä käytetyt kuvat
    private Bitmap bitmapFootball;
    private Bitmap bitmapPlayer1;
    private Bitmap bitmapPlayer2;

    private Bitmap bitmapBackgroundImage;
    private Bitmap bitmapGoalLeft;
    private Bitmap bitmapGoalRight;

    private Bitmap bitmapGoalTextBlue;
    private Bitmap bitmapGoalTextRed;

    private Bitmap bitmapJoystick1;
    private Bitmap bitmapJoystick2;

    //Pelaajan näytön koko
    private int screenWidth = 0;
    private int screenHeight = 0;

    private int gameAreaMinX = 0;
    private int gameAreaMinY = 0;
    private int gameAreaMaxX = 0;
    private int gameAreaMaxY = 0;

    //Hahmojen ja pallon keskipisteet ja nopeudet
    private double ballSpeedX = 0;
    private double ballSpeedY = 1;

    private int player1CenterX = 0;
    private int player1CenterY = 0;

    private int player2CenterX = 0;
    private int player2CenterY = 0;

    private double player1SpeedX = 0;
    private double player1SpeedY = 1;

    private double player2SpeedX = 0;
    private double player2SpeedY = 1;

    //private int player1Angle = 0;

    private int PLAYER_MAX_SPEED = 20;
    private int BALL_MAX_SPEED = 40;
    private int increasePlayerSpeed = 5;
    private double increaseBallSpeed = 0.5;

    private int playerRadiusVertical = 0;
    private int playerRadiusHorizontal = 0;

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
    private int scoreTextSize = 0;

    private int goalTextX = 0;
    private int goalTextY = 0;

    private boolean goalTextRed = false;
    private boolean goalTextBlue = false;

    private int textX = 0;
    private int textY = 0;

    double friction = 0;
    double gravity = 0;

    //private long hitTime = 0;
    private long scoreTime = 0;

    //Ball ball;

    simpleJoystick player1Joystick;
    simpleJoystick player2Joystick;

    //Context testi;

    public FootballGame(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d("football", "aloitustakoskavaanvoin");
        //setWillNotDraw(false);
        //Display d = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //testi = context;
        //Katsotaan näytön koko
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        this.setOnTouchListener(this);

        player1Joystick = new simpleJoystick(context, attributeSet);
        player2Joystick = new simpleJoystick(context, attributeSet);

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
        gameAreaMaxY = (int)(screenHeight * 0.85);

        PLAYER_MAX_SPEED = screenWidth / 75;
        BALL_MAX_SPEED = screenWidth / 50;
        increasePlayerSpeed = screenWidth / 400;
        //increaseBallSpeed = (double)screenWidth / 4000;
        increaseBallSpeed = (double)screenWidth / 8000;

        gravity = (double)screenHeight / 1500;
        friction = (double)screenWidth / 10000;

        ballRadius = screenWidth / 40;
        playerRadiusHorizontal = screenWidth / 23;
        playerRadiusVertical = screenWidth / 18;
        joystickRadius = screenWidth / 15;

        goalWidth = (int)((double)screenWidth * 0.13);
        goalHeight = (int)((double)screenHeight * 0.45);

        scoreTextSize = screenHeight / 14;

    }

    //Laitetaan kaikki esineet ja pelaajat oikealle paikalle
    public void setStartCoordinates(){

        player1CenterX = (int)((double)screenWidth * 0.2);
        player1CenterY = screenHeight / 2;

        player2CenterX = (int)((double)screenWidth * 0.8);
        player2CenterY = screenHeight / 2;

        ballCenterX = screenWidth / 2;
        ballCenterY = screenHeight / 2;

        //joystick1CenterX = (int)((double)screenWidth * 0.2);
        joystick1CenterX = (int)((double)screenWidth * 0.15);
        joystick1CenterY = (int)((double)screenHeight * 0.82);

        //joystick2CenterX = (int)((double)screenWidth * 0.8);
        joystick2CenterX = (int)((double)screenWidth * 0.85);
        joystick2CenterY = (int)((double)screenHeight * 0.82);

        player1Joystick.setJoystickCenterX(joystick1CenterX);
        player1Joystick.setJoystickCenterY(joystick1CenterY);
        player1Joystick.setJoystickRadius(joystickRadius);
        player1Joystick.setColor("RED");
        player1Joystick.setOffset(joystickRadius / 2);

        player2Joystick.setJoystickCenterX(joystick2CenterX);
        player2Joystick.setJoystickCenterY(joystick2CenterY);
        player2Joystick.setJoystickRadius(joystickRadius);
        player2Joystick.setColor("BLUE");
        player2Joystick.setOffset(joystickRadius / 2);

        goalLeftX = gameAreaMinX;
        goalLeftY = (int)((double)screenHeight * 0.4);

        goalRightX = gameAreaMaxX - goalWidth;
        goalRightY = (int)((double)screenHeight * 0.4);

        goalTextX = (screenWidth / 2) - (screenWidth / 10);
        goalTextY = screenHeight / 5;
    }

    public void setText(){
        textX = (int)((double)screenWidth * 0.505);
        textY = (int)((double)screenHeight * 0.095);

        score = Integer.toString(player1Score);
        //Kolme välilyöntiä
        score += "   ";
        score += Integer.toString(player2Score);

        paintText =  new Paint();
        paintText.setStyle(Paint.Style.FILL_AND_STROKE);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(scoreTextSize);
        paintText.setColor(Color.WHITE);
    }

    public void updateScore(){
        score = Integer.toString(player1Score);
        //Kolme välilyöntiä
        score += "   ";
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

        //Otetaan vastaan käyttäjän asettamat hahmot
        SharedPreferences pref = getContext().getSharedPreferences("hahmovalinnat", 0);
        int skinP1 = pref.getInt("character1", 0);
        int skinP2 = pref.getInt("character2", 0);
        int background = pref.getInt("background", 0);
        int football = pref.getInt("football", 0);

        bitmapFootball = BitmapFactory.decodeResource(getResources(), footballArray[football]);
        bitmapFootball = Bitmap.createScaledBitmap(bitmapFootball, ballRadius * 2, ballRadius * 2, true);

        bitmapPlayer1 = BitmapFactory.decodeResource(getResources(), playerSkinArray[skinP1]);
        bitmapPlayer1 = Bitmap.createScaledBitmap(bitmapPlayer1, playerRadiusHorizontal * 2, playerRadiusVertical * 2, true);

        bitmapPlayer2 = BitmapFactory.decodeResource(getResources(), playerSkinArray[skinP2]);
        bitmapPlayer2 = Bitmap.createScaledBitmap(bitmapPlayer2, playerRadiusHorizontal * 2, playerRadiusVertical * 2, true);

        bitmapBackgroundImage = BitmapFactory.decodeResource(getResources(), backgroundArray[background]);
        bitmapBackgroundImage = Bitmap.createScaledBitmap(bitmapBackgroundImage, screenWidth, screenHeight, true);

        bitmapGoalLeft = BitmapFactory.decodeResource(getResources(), R.drawable.goal);
        bitmapGoalLeft = Bitmap.createScaledBitmap(bitmapGoalLeft, goalWidth, goalHeight, true);

        bitmapGoalRight = BitmapFactory.decodeResource(getResources(), R.drawable.goal);
        bitmapGoalRight = Bitmap.createScaledBitmap(bitmapGoalRight, goalWidth, goalHeight, true);

        bitmapJoystick1 = BitmapFactory.decodeResource(getResources(), R.drawable.joystick1);
        bitmapJoystick1 = Bitmap.createScaledBitmap(bitmapJoystick1, joystickRadius * 2, joystickRadius * 2, true);

        bitmapJoystick2 = BitmapFactory.decodeResource(getResources(), R.drawable.joystick2);
        bitmapJoystick2= Bitmap.createScaledBitmap(bitmapJoystick2, joystickRadius * 2, joystickRadius * 2, true);

        bitmapGoalTextBlue = BitmapFactory.decodeResource(getResources(), R.drawable.goaltextblue);
        bitmapGoalTextBlue = Bitmap.createScaledBitmap(bitmapGoalTextBlue, screenWidth / 5, screenHeight / 5, true);

        bitmapGoalTextRed = BitmapFactory.decodeResource(getResources(), R.drawable.goaltextred);
        bitmapGoalTextRed = Bitmap.createScaledBitmap(bitmapGoalTextRed, screenWidth / 5, screenHeight / 5, true);
    }

    //Päivittää pallon ja pelaajien sijainnit ja katsoo tapahtuuko törmäyksiä
    protected void physics(){
        long hitTime = System.currentTimeMillis();

        //Pallon sivut
        double ballRightX = ballCenterX + ballRadius;
        double ballLeftX = ballCenterX - ballRadius;
        double ballTopY = ballCenterY - ballRadius;
        double ballBottomY = ballCenterY + ballRadius;

        //Pelaaja1:n sivut
        double player1RightX = player1CenterX + playerRadiusHorizontal;
        double player1LeftX = player1CenterX - playerRadiusHorizontal;
        double player1TopY = player1CenterY - playerRadiusVertical;
        double player1BottomY = player1CenterY + playerRadiusVertical;

        //Pelaaja2:n sivut
        double player2RightX = player2CenterX + playerRadiusHorizontal;
        double player2LeftX = player2CenterX - playerRadiusHorizontal;
        double player2TopY = player2CenterY - playerRadiusVertical;
        double player2BottomY = player2CenterY + playerRadiusVertical;

        //Pallon sivut + nopeudet
        double ballRightTotal = ballCenterX + ballRadius + ballSpeedX;
        double ballLeftTotal = ballCenterX - ballRadius + ballSpeedX;
        double ballTopTotal = ballCenterY - ballRadius + ballSpeedY;
        double ballBottomTotal = ballCenterY + ballRadius + ballSpeedY;

        //Pelaaja1:n sivut + nopeudet
        double player1RightTotal = player1CenterX + playerRadiusHorizontal + player1SpeedX;
        double player1LeftTotal = player1CenterX - playerRadiusHorizontal + player1SpeedX;
        double player1TopTotal = player1CenterY - playerRadiusVertical + player1SpeedY;
        double player1BottomTotal = player1CenterY + playerRadiusVertical + player1SpeedY;

        //Pelaaja2:n sivut + nopeus
        double player2RightTotal = player2CenterX + playerRadiusHorizontal + player2SpeedX;
        double player2LeftTotal = player2CenterX - playerRadiusHorizontal + player2SpeedX;
        double player2TopTotal = player2CenterY - playerRadiusVertical + player2SpeedY;
        double player2BottomTotal = player2CenterY + playerRadiusVertical + player2SpeedY;

        physicsFriction();


        //Muutetaan pallon kohtaa nopeuden mukaan jos se ei mene reunojen yli
        //Pallo X-akselilla
        if(ballLeftTotal > gameAreaMinX && ballRightTotal < gameAreaMaxX){
            ballCenterX += (int)ballSpeedX;
        }
        else{
            //Log.d("jalkapallo", "osu seinään");
            ballSpeedX = ballSpeedX * -0.75;
            ballCenterX += ballSpeedX;
        }

        //Pallo y-akselilla laitetaan pelaajan säde, jotta pelaajat ja pallot on samalla korkeudella
        //Pallo y-akselilla
        if(ballTopTotal > 0 && ballBottomTotal < gameAreaMaxY){
            //Pallo osuu vasemman maalin ylärimaan
            if(goalLeftX + goalWidth > ballCenterX + ballSpeedX  && goalLeftY < ballBottomTotal
                    && ballBottomTotal < goalLeftY + (int)((double)goalHeight *0.1)){
                ballSpeedY = ballSpeedY * -0.75;
                //ballSpeedY = -ballSpeedY;
                //Otetaan pallo pois maalin ylärimasta
                ballSpeedX += increaseBallSpeed * 15;
                ballCenterY += ballSpeedY;
            }
            //Pallo osuu oikean maalin ylärimaan
            else if (goalRightX < ballCenterX + ballSpeedX  && goalRightY < ballBottomTotal
                    && ballBottomTotal < goalRightY + (int)((double)goalHeight *0.1)){
                ballSpeedY = ballSpeedY * -0.75;
                //ballSpeedY = -ballSpeedY;
                //Otetaan pallo pois maalin ylärimasta
                ballSpeedX -= increaseBallSpeed * 15;
                ballCenterY += ballSpeedY;
            }
            else{
                ballCenterY += (int)ballSpeedY;
            }
        }
        else{
            //Log.d("jalkapallo", "osu kattoon");
            ballSpeedY = ballSpeedY * -0.5;
            ballCenterY += ballSpeedY;
        }


        //
        //Player1
        //Päivitetään pelaajan keskipiste pelaaajan nopeuden mukaan katsotaan myös ettei mene laitojen yli
        if(player1LeftTotal > gameAreaMinX && player1RightTotal < gameAreaMaxX){
            //Pelaaja osuu vasemman maalin ylärimaan x-akselilla
            if(goalLeftX + goalWidth > player1LeftTotal && goalLeftY < player1BottomTotal &&
                    player1CenterY + player1SpeedY < goalLeftY + (int)((double)goalHeight *0.05)){
                player1SpeedX = Math.abs(player1SpeedX);
                player1SpeedX = player1SpeedX * 0.75;
                player1CenterX += (int)player1SpeedX;
            }
            //Pelaaja osuu oikean maalin ylärimaan x-akselilla
            else if(goalRightX < player1RightTotal && goalRightY < player1BottomTotal &&
                    player1CenterY + player1SpeedY < goalRightY + (int)((double)goalHeight *0.05)){
                player1SpeedX = Math.abs(player1SpeedX);
                player1SpeedX = player1SpeedX * -0.75;
                player1CenterX += (int)player1SpeedX;
            }
            else{
                player1CenterX += (int)player1SpeedX;
            }
        }
        else{
            player1SpeedX = player1SpeedX * -0.75;
            player1CenterX += player1SpeedX;
        }
        //Pelaaja1 y-akselilla
        if(player1TopTotal > gameAreaMinY && player1BottomTotal < gameAreaMaxY){
            //Pallo osuu vasemman maalin ylärimaan ylhäältä päin
            if(goalLeftX + goalWidth > player1LeftTotal  && goalLeftY < player1BottomTotal
                    && player1BottomTotal < goalLeftY + (int)((double)goalHeight *0.1)){
                player1SpeedY = player1SpeedY * -0.75;
                player1CenterY += player1SpeedY;
            }
            //Pelaaja osuu vasemman maalin ylärimaan alhaaltapäin
            else if(goalLeftX + goalWidth > player1LeftTotal  && goalLeftY < player1BottomTotal
                    && player1CenterY + player1SpeedY < goalLeftY + (int)((double)goalHeight *0.12)){
                player1SpeedY = player1SpeedY * -0.75;
                player1CenterY += player1SpeedY;
            }
            //Pallo osuu oikean maalin ylärimaan ylhäältäpäin
            else if (goalRightX < player1RightTotal  && goalRightY < player1BottomTotal
                    && player1BottomTotal < goalRightY + (int)((double)goalHeight *0.1)){
                player1SpeedY = player1SpeedY * -0.75;
                player1CenterY += player1SpeedY;
            }
            //Pelaaja osuu oikean maalin ylärimaan alhaaltapäin
            else if(goalRightX < player1RightTotal  && goalLeftY < player1BottomTotal
                    && player1CenterY + player1SpeedY < goalLeftY + (int)((double)goalHeight *0.12)){
                player1SpeedY = player1SpeedY * -0.75;
                player1CenterY += player1SpeedY;
            }
            else{
                player1CenterY += (int)player1SpeedY;
            }
        }
        else{
            player1SpeedY = player1SpeedY * -0.5;
            player1CenterY += player1SpeedY;
        }


        //
        //Player2
        //Päivitetään pelaajan keskipiste pelaaajan nopeuden mukaan katsotaan myös ettei mene laitojen yli
        if(player2LeftTotal > gameAreaMinX && player2RightTotal < gameAreaMaxX){
            //Pelaaja osuu vasemman maalin ylärimaan x-akselilla
            if(goalLeftX + goalWidth > player2LeftTotal && goalLeftY < player2BottomTotal &&
                    player2CenterY + player2SpeedY < goalLeftY + (int)((double)goalHeight *0.05)){
                player2SpeedX = Math.abs(player2SpeedX);
                player2SpeedX = player2SpeedX * 0.75;
                player2CenterX += (int)player2SpeedX;
            }
            //Pelaaja osuu oikean maalin ylärimaan x-akselilla
            else if(goalRightX < player2RightTotal && goalRightY < player2BottomTotal &&
                    player2CenterY + player2SpeedY < goalRightY + (int)((double)goalHeight *0.05)){
                player2SpeedX = Math.abs(player2SpeedX);
                player2SpeedX = player2SpeedX * -0.75;
                player2CenterX += (int)player2SpeedX;
            }
            else{
                player2CenterX += (int)player2SpeedX;
            }
        }
        else{
            player2SpeedX = player2SpeedX * -0.5;
            player2CenterX += player2SpeedX;
        }
        //Pelaaja2 y-akselilla
        if(player2TopTotal > gameAreaMinY && player2BottomTotal < gameAreaMaxY){
            //Pallo osuu vasemman maalin ylärimaan ylhäältä päin
            if(goalLeftX + goalWidth > player2LeftTotal  && goalLeftY < player2BottomTotal
                    && player2BottomTotal < goalLeftY + (int)((double)goalHeight *0.1)){
                player2SpeedY = player2SpeedY * -0.75;
                player2CenterY += player2SpeedY;
            }
            //Pelaaja osuu vasemman maalin ylärimaan alhaaltapäin
            else if(goalLeftX + goalWidth > player2LeftTotal  && goalLeftY < player2BottomTotal
                    && player2CenterY + player2SpeedY < goalLeftY + (int)((double)goalHeight *0.12)){
                player2SpeedY = player2SpeedY * -0.75;
                player2CenterY += player2SpeedY;
            }
            //Pallo osuu oikean maalin ylärimaan ylhäältäpäin
            else if (goalRightX < player2RightTotal  && goalRightY < player2BottomTotal
                    && player2BottomTotal < goalRightY + (int)((double)goalHeight *0.1)){
                player2SpeedY = player2SpeedY * -0.75;
                player2CenterY += player2SpeedY;
            }
            //Pelaaja osuu oikean maalin ylärimaan alhaaltapäin
            else if(goalRightX < player2RightTotal  && goalLeftY < player2BottomTotal
                    && player2CenterY + player2SpeedY < goalLeftY + (int)((double)goalHeight *0.12)){
                player2SpeedY = player2SpeedY * -0.75;
                player2CenterY += player2SpeedY;
            }
            else{
                player2CenterY += (int)player2SpeedY;
            }
        }
        else{
            player2SpeedY = player2SpeedY * -0.5;
            player2CenterY += player2SpeedY;
        }


        //Pelaajien törmäys palloon
        //Lasketaan kulma pallon ja pelaajan välille
        //Katsotaan osuuko pelaaja palloon
        if(((player1RightX >= ballLeftX && player1CenterX <= ballCenterX) || (player1LeftX <= ballRightX && player1CenterX >= ballCenterX)) &&
                ((player1BottomY >= ballTopY && player1CenterY <= ballCenterY) || (player1TopY <= ballBottomY && player1CenterY >= ballCenterY))){
            double angle = calculateAngle(ballCenterX, ballCenterY, player1CenterX, player1CenterY);
            calculateSpeed(angle, player1SpeedX, player1SpeedY);
        }

        //Pelaaja2 ja pallon töyrmäys
        //Pelaaja2
        if(((player2RightX >= ballLeftX && player2CenterX <= ballCenterX) || (player2LeftX <= ballRightX && player2CenterX >= ballCenterX)) &&
                ((player2BottomY >= ballTopY && player2CenterY <= ballCenterY) || (player2TopY <= ballBottomY && player2CenterY >= ballCenterY))){
            double angle = calculateAngle(ballCenterX, ballCenterY, player2CenterX, player2CenterY);
            calculateSpeed(angle, player2SpeedX, player2SpeedY);
        }


        //Pelaajien törmäykset
        //Pelaajien törmäys x- ja y-akselilla
        //Pelaajien töyrmäys x-akselilla
        if((player1RightTotal > player2LeftTotal && player1CenterX + player2SpeedX < player2CenterX + player2SpeedX ||
                player1LeftTotal < player2RightTotal && player1CenterX + player1SpeedX > player2CenterX + player2SpeedX)&&
                (player1TopTotal < player2BottomTotal && player1TopTotal > player2TopTotal ||
                        player1BottomTotal > player2TopTotal && player1BottomTotal < player2BottomTotal)){
            //Pelaaja1 on oikealla ja pelaaja 2 vasemmalla
            if(player1CenterX > player2CenterX){
                player1SpeedX = Math.abs(player1SpeedX);
                player1SpeedX = player1SpeedX * 0.75;

                player2SpeedX = Math.abs(player2SpeedX);
                player2SpeedX = player2SpeedX * -0.75;
            }
            //Pelaaja2 on oikealla ja pelaaja1 vasemmalla
            else if(player1CenterX <= player2CenterX){
                player1SpeedX = Math.abs(player1SpeedX);
                player1SpeedX = player1SpeedX * -0.75;

                player2SpeedX = Math.abs(player2SpeedX);
                player2SpeedX = player2SpeedX * 0.75;
            }
        }

        //Pelaajien töyrmäys y-akselilla
        if(((player1RightTotal > player2LeftTotal && player1LeftTotal < player2RightTotal) ||
                (player2RightTotal > player1LeftTotal && player2LeftTotal < player1RightTotal)) &&
                ((player1TopTotal < player2BottomTotal && player1CenterY + player1SpeedY > player2CenterY + player2SpeedY) ||
                        (player2TopTotal < player1BottomTotal && player2CenterY + player2SpeedY > player1CenterY + player1SpeedY))){
            if(player1CenterY > player2CenterY){
                player1SpeedY = Math.abs(player1SpeedY);
                player1SpeedY = player1SpeedY * 0.75;

                player2SpeedY = Math.abs(player2SpeedY);
                player2SpeedY = player2SpeedY * -0.75;
            }
            //Pelaaja2 on alhapuolella ja pelaaja1 yläpuolella
            else if(player1CenterY <= player2CenterY){
                player1SpeedY = Math.abs(player1SpeedY);
                player1SpeedY = player1SpeedY * -0.75;

                player2SpeedY = Math.abs(player2SpeedY);
                player2SpeedY = player2SpeedY * 0.75;
            }
        }



        //
        //Katsotaan meneekö pallo maaliin
        //Pallo menee vasempaan maaliin
        //if(ballRightX < goalLeftX + goalWidth && ballTopY > goalLeftY && ballCenterY < goalLeftY + goalHeight)
        if(ballRightX < goalLeftX + goalWidth && ballCenterY > goalLeftY && ballCenterY < goalLeftY + goalHeight){
            scoreTime = System.currentTimeMillis();
            setStartCoordinates();
            nullSpeeds();
            player2Score++;
            updateScore();
            goalTextRed = false;
            goalTextBlue = true;
            if(player2Score >= 10){
                endGame();
            }

        }

        //Pallo menee oikeaan maaliin
        if(ballLeftX > goalRightX && ballCenterY > goalRightY && ballCenterY < goalRightY + goalHeight){
            scoreTime = System.currentTimeMillis();
            setStartCoordinates();
            nullSpeeds();
            player1Score++;
            updateScore();
            goalTextRed = true;
            goalTextBlue = false;
            if(player1Score >= 10){
                endGame();
            }
        }

        /*
        //Käännetään pelaajaa
        if(player1SpeedX > 10){
            float angle = 30;
            //bitmapPlayer1 = RotateBitmap(bitmapPlayer1, angle, player1CenterX, player1CenterY);
        }*/


        //
        //Pallon maksiminopeus
        //Katsotaan ettei pallo mene liian nopeaa
        if(ballSpeedX > BALL_MAX_SPEED){
            ballSpeedX = BALL_MAX_SPEED;
        }
        if(ballSpeedX < -BALL_MAX_SPEED){
            ballSpeedX = -BALL_MAX_SPEED;
        }
        if(ballSpeedY > BALL_MAX_SPEED){
            ballSpeedY = BALL_MAX_SPEED;
        }
        if(ballSpeedY < -BALL_MAX_SPEED){
            ballSpeedY = -BALL_MAX_SPEED;
        }

        //Maaliteksti otetaan pois kun se on ollut näytöllä 3 sekunnin ajan
        if(hitTime > scoreTime +3000){
            goalTextRed = false;
            goalTextBlue = false;
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
        if(ballSpeedY < BALL_MAX_SPEED || ballSpeedY != 0){
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
        if(player1SpeedY < PLAYER_MAX_SPEED || player1SpeedY != 0){
            player1SpeedY += gravity;
        }
        //Pelaaja2 painovoima
        if(player2SpeedY < PLAYER_MAX_SPEED || player2SpeedY != 0){
            player2SpeedY += gravity;
        }
    }



    private void endGame(){

        player1Score = 0;
        player2Score = 0;

        updateScore();

        goalTextBlue = false;
        goalTextRed = false;

        footballGameThread.setRunning(false);


        //Muutetaan jaetun arvo todeksi niin tiedetään toisessa activityssä laittaa lopetuslayout
        //SharedPreferences sharedPreferences = getContext().getSharedPreferences("endgame", 0);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("endgame", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("endgame", true);

        editor.apply();
    }


    public double calculateAngle(int ballX, int ballY, int playerX, int playerY){

        double angle;
        double xDistance;
        double yDistance;
        xDistance = (double)Math.abs(ballX - playerX);

        //Lasketaan etäisyys Y- suunnassa keskipisteen Y:n verrattuna, tämä on viereinen kateetti
        yDistance = (double)Math.abs(ballY - playerY);

        //Lasketaan kulma
        angle = Math.toDegrees(Math.atan(yDistance / xDistance));
        /*
        //Pallo on pelaajan oikeassa alakulmassa
        if(ballX > playerX && ballY > playerY){
            //Ei tarvi tehdä mitään
        }*/
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

    public void calculateSpeed(double angle, double playerSpeedX, double playerSpeedY){

        //Pallo on pelaajan oikeassa alakulmassa
        if(angle >= 0 && angle < 90){
            double angle2 = 90 - angle;
            //Katsotaan kuinka nopeaa pelaaja menee ja päätetään pallon nopeus x-akselilla
            if(playerSpeedX > PLAYER_MAX_SPEED / 2){
                ballSpeedX = angle * increaseBallSpeed * 2;
            }
            else{
                ballSpeedX = angle * increaseBallSpeed;
            }
            if(ballCenterY < gameAreaMaxY && ballCenterY > (int)((double)gameAreaMaxY * 0.8)){
                ballSpeedY = angle2 * -increaseBallSpeed;
            }
            else{
                ballSpeedY = angle2 * increaseBallSpeed * 2;
            }

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
            if(playerSpeedX < -PLAYER_MAX_SPEED / 2){
                ballSpeedX = angle * -increaseBallSpeed * 2;
            }
            else{
                ballSpeedX = angle * -increaseBallSpeed;
            }

            ballSpeedY = angle2 * increaseBallSpeed * 2;
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

            if(playerSpeedX < -PLAYER_MAX_SPEED / 2){
                ballSpeedX = angle * -increaseBallSpeed * 2;
            }
            else{
                ballSpeedX = angle * -increaseBallSpeed;
            }

            ballSpeedY = angle2 * -increaseBallSpeed * 2;
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

            if(playerSpeedX > PLAYER_MAX_SPEED / 2){
                ballSpeedX = angle * increaseBallSpeed * 2;
            }
            else{
                ballSpeedX = angle * increaseBallSpeed;
            }

            ballSpeedY = angle2 * -increaseBallSpeed * 2;
            if(ballSpeedX < 5){
                ballSpeedX += 5;
            }
            if(ballSpeedY > -5){
                ballSpeedY -= 5;
            }
        }

    }
    /*
    public static Bitmap RotateBitmap(Bitmap bitmap, float angle, int x, int y){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, x, y, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }*/

    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);

        if(canvas != null){
            //Piirretään taustakuva
            canvas.drawBitmap(bitmapBackgroundImage, 0, 0, null);

        /*
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(38);
        paint.setColor(Color.YELLOW);

        canvas.drawText(teksti, tekstiX, tekstiY, paint);*/

            //Piirretään jalkapallo
            canvas.drawBitmap(bitmapFootball, ballCenterX-ballRadius, ballCenterY-ballRadius, null);

            //Piirretään pelaajat
            canvas.drawBitmap(bitmapPlayer1, player1CenterX-playerRadiusHorizontal, player1CenterY-playerRadiusVertical, null);
            canvas.drawBitmap(bitmapPlayer2, player2CenterX-playerRadiusHorizontal, player2CenterY-playerRadiusVertical, null);

            //Piirretään maalit
            canvas.drawBitmap(bitmapGoalLeft, goalLeftX, goalLeftY, null);
            canvas.drawBitmap(bitmapGoalRight, goalRightX, goalRightY, null);

            //Piirretään joystickit
            canvas.drawBitmap(bitmapJoystick1, joystick1CenterX-joystickRadius, joystick1CenterY-joystickRadius, null);
            canvas.drawBitmap(bitmapJoystick2, joystick2CenterX-joystickRadius, joystick2CenterY-joystickRadius, null);

            //Piirretään tulos
            canvas.drawText(score, textX, textY, paintText);

            //Piirretään maaliteksti jos pelaaja on saanut maalin
            if(goalTextBlue){
                canvas.drawBitmap(bitmapGoalTextBlue, goalTextX, goalTextY, null);
            }
            if (goalTextRed){
                canvas.drawBitmap(bitmapGoalTextRed, goalTextX, goalTextY, null);
            }

            //ball.draw(canvas);
            //canvas.drawBitmap(bitmapJoystick1, ballCenterX, ballCenterY, null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        handleTouch(event);
        return true;
    }

    public void handleTouch(MotionEvent event)
    {
        int pointerCount = event.getPointerCount();
        String actionString = null;

        //Loop, joka käsittelee kaikki kosketukset
        for (int i = 0; i < pointerCount; i++ )
        {
            int x = (int) event.getX(i);
            int y = (int) event.getY(i);
            int action = event.getActionMasked();
            //int actionIndex = event.getActionIndex();
            //int id = event.getPointerId(i);

            switch (action){
                case MotionEvent.ACTION_MOVE:

                    actionString = "MOVE";
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    actionString = "DOWN";
                    break;
                default:
                    actionString = "";
            }

            if(actionString == "MOVE" || actionString == "DOWN")
            {
                //Kosketus on vasemmalla puolella ruutua
                if (x < screenWidth / 2) {

                    //Lähetetään koordinaatit funktioon, joka laskee niiden perusteella kulman
                    player1Joystick.calculateAngle(x, y);

                    //Perinteiset suunnat
                    if (player1Joystick.getDirection() == "UP") {
                        if (player1SpeedY > -PLAYER_MAX_SPEED) {
                            player1SpeedY -= increasePlayerSpeed;
                        }
                    }
                    if (player1Joystick.getDirection() == "DOWN") {
                        if (player1SpeedY < PLAYER_MAX_SPEED) {
                            player1SpeedY += increasePlayerSpeed;
                        }
                    }
                    if (player1Joystick.getDirection() == "LEFT") {
                        if (player1SpeedX > -PLAYER_MAX_SPEED) {
                            player1SpeedX -= increasePlayerSpeed;
                        }
                    }
                    if (player1Joystick.getDirection() == "RIGHT") {
                        if (player1SpeedX < PLAYER_MAX_SPEED) {
                            player1SpeedX += increasePlayerSpeed;
                        }
                    }

                    //Viistottaiset suunnat
                    if (player1Joystick.getDirection() == "UPRIGHT") {
                        if (player1SpeedX < PLAYER_MAX_SPEED) {
                            player1SpeedX += increasePlayerSpeed;
                        }
                        if (player1SpeedY > -PLAYER_MAX_SPEED) {
                            player1SpeedY -= increasePlayerSpeed;
                        }
                    }
                    if (player1Joystick.getDirection() == "DOWNRIGHT") {
                        if (player1SpeedX < PLAYER_MAX_SPEED) {
                            player1SpeedX += increasePlayerSpeed;
                        }
                        if (player1SpeedY < PLAYER_MAX_SPEED) {
                            player1SpeedY += increasePlayerSpeed;
                        }
                    }
                    if (player1Joystick.getDirection() == "DOWNLEFT") {
                        if (player1SpeedX > -PLAYER_MAX_SPEED) {
                            player1SpeedX -= increasePlayerSpeed;
                        }

                        if (player1SpeedY < PLAYER_MAX_SPEED) {
                            player1SpeedY += increasePlayerSpeed;
                        }
                    }
                    if (player1Joystick.getDirection() == "UPLEFT") {
                        if (player1SpeedX > -PLAYER_MAX_SPEED) {
                            player1SpeedX -= increasePlayerSpeed;
                        }

                        if (player1SpeedY > -PLAYER_MAX_SPEED) {
                            player1SpeedY -= increasePlayerSpeed;
                        }
                    }
                }

                //Kosketus on oikealla puolella ruutua
                if (x > screenWidth / 2) {

                    player2Joystick.calculateAngle(x, y);

                    //Perinteiset suunnat
                    if (player2Joystick.getDirection() == "UP") {
                        if(player2SpeedY > -PLAYER_MAX_SPEED){
                            player2SpeedY -= increasePlayerSpeed;
                        }
                    }
                    if (player2Joystick.getDirection() == "DOWN") {
                        if (player2SpeedY < PLAYER_MAX_SPEED) {
                            player2SpeedY += increasePlayerSpeed;
                        }
                    }
                    if (player2Joystick.getDirection() == "LEFT") {
                        if (player2SpeedX > -PLAYER_MAX_SPEED) {
                            player2SpeedX -= increasePlayerSpeed;
                        }
                    }
                    if (player2Joystick.getDirection() == "RIGHT") {
                        if (player2SpeedX < PLAYER_MAX_SPEED) {
                            player2SpeedX += increasePlayerSpeed;
                        }
                    }

                    //Viistottaiset suunnat
                    if (player2Joystick.getDirection() == "UPRIGHT") {
                        if (player2SpeedX < PLAYER_MAX_SPEED) {
                            player2SpeedX += increasePlayerSpeed;
                        }
                        if (player2SpeedY > -PLAYER_MAX_SPEED) {
                            player2SpeedY -= increasePlayerSpeed;
                        }
                    }
                    if (player2Joystick.getDirection() == "DOWNRIGHT") {
                        if (player2SpeedX < PLAYER_MAX_SPEED) {
                            player2SpeedX += increasePlayerSpeed;
                        }
                        if (player2SpeedY < PLAYER_MAX_SPEED) {
                            player2SpeedY += increasePlayerSpeed;
                        }
                    }
                    if (player2Joystick.getDirection() == "DOWNLEFT") {
                        if (player2SpeedX > -PLAYER_MAX_SPEED) {
                            player2SpeedX -= increasePlayerSpeed;
                        }

                        if (player2SpeedY < PLAYER_MAX_SPEED) {
                            player2SpeedY += increasePlayerSpeed;
                        }
                    }
                    if (player2Joystick.getDirection() == "UPLEFT") {
                        if (player2Joystick.getDirection() == "UPLEFT") {
                            if (player2SpeedX > -PLAYER_MAX_SPEED) {
                                player2SpeedX -= increasePlayerSpeed;
                            }

                            if (player2SpeedY > -PLAYER_MAX_SPEED) {
                                player2SpeedY -= increasePlayerSpeed;
                            }
                        }
                    }

                }
            }
        }
    }
}