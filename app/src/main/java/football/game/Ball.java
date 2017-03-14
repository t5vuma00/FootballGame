package football.game;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class Ball extends View implements View.OnTouchListener{

    int ballCenterX = 200;
    int ballCenterY = 1000;
    int ballSpeedX = 0;
    int ballSpeedY = 0;
    int ballRadius = 62;

    float startX = 0;
    float startY = 0;
    float endX = 0;
    float endY = 0;

    boolean win =false;

    float previousX = 0;
    float previousY = 0;

    public Ball(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setOnTouchListener(this);
        Paint paint = new Paint();
    }
    /*
    public void move(){

    }*/

    protected void Update(){
        ballCenterX += ballSpeedX;
        ballCenterY += ballSpeedY;

        /*
        if(ballCenterX + ballRadius > 350 && ballCenterY + ballRadius < 75 && ballCenterX - ballRadius < 750){
            ballSpeedY = -ballSpeedY;
        }
        if(ballCenterX + ballRadius > 540 && ballCenterY + ballRadius > 350 && ballCenterX + ballRadius <580 && ballCenterY + ballRadius < 1920 ){
            ballSpeedX = -ballSpeedX;
        }

        if (ballCenterY + ballRadius > 350 && ballCenterX - ballRadius > 540 && ballCenterX + ballRadius < 580){
            ballSpeedY = - ballSpeedY;
        }

        if (ballCenterX + ballRadius > 825 && ballCenterX - ballRadius < 875 && ballCenterY + ballRadius > 975 && ballCenterY - ballRadius < 1025){
            ballSpeedX = 0;
            ballSpeedY = 0;
            win = true;
        }

        if(ballCenterX+ ballRadius<0 || ballCenterX + ballRadius > 1180 || ballCenterY + ballRadius < 0 || ballCenterY + ballRadius > 2020){
            ballCenterX = 200;
            ballCenterY = 1000;
            ballSpeedX = 0;
            ballSpeedY = 0;
        }*/
    }

    @Override
    protected void onDraw ( Canvas canvas){
        Log.d("ball", "2");
        super.onDraw(canvas);
        /*
        if(win == true){
            Paint paintti = new Paint();
            paintti.setColor(Color.GREEN);
            paintti.setTextSize(62);
            canvas.drawText("Voitit pelin", 400, 200, paintti);
        }*/
        /*
        Paint paintRect = new Paint();
        paintRect.setStyle(Paint.Style.FILL);
        paintRect.setColor(Color.BLACK);
        canvas.drawRect( 350, 0, 750, 25, paintRect);
        canvas.drawRect( 540, 350, 580, 1920, paintRect);
        Paint paintRectNew = new Paint();
        paintRectNew.setStyle(Paint.Style.FILL);
        paintRectNew.setColor(Color.GREEN);
        //canvas.drawRect(800, 950, 900, 1050, paintRectNew);
        canvas.drawRect(825, 975, 875, 1025, paintRectNew);
        */

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

        canvas.drawCircle(ballCenterX, ballCenterY, ballRadius, paint);

        Paint paintOutLine = new Paint();
        paintOutLine.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(ballCenterX, ballCenterY, ballRadius, paintOutLine);

        Update();

        invalidate();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(ballSpeedX == 0 && ballSpeedY == 0){

            float change = 0;
            String xTesti = "";
            String yTesti = "";

            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    xTesti = Float.toString(startX);
                    yTesti = Float.toString(startY);

                    Log.d("painoit X", xTesti);
                    Log.d("painoit Y", yTesti);

                case MotionEvent.ACTION_UP:
                    endX = event.getX();
                    endY = event.getY();
                    xTesti = Float.toString(endX);
                    yTesti = Float.toString(endY);
                    Log.d("Nostit X", xTesti);
                    Log.d("Nostit Y", yTesti);

            }
            float changeX = endX - startX;
            float changeY = endY - startY;

            String muutosX = Float.toString(endX);
            String muutosY = Float.toString(endY);

            Log.d("Muutos X", muutosX);
            Log.d("Muutos Y", muutosY);


            if (Math.abs(changeX) < 100 && Math.abs(changeX) >= 50 || Math.abs(changeY) < 100 && Math.abs(changeY) >= 50){
                ballSpeedX = (int)changeX / 10;
                ballSpeedY = (int)changeY / 10;
            }
            else if(Math.abs(changeX) >= 100 && Math.abs(changeX) < 300 || Math.abs(changeY) >= 100 && Math.abs(changeY) < 300 ){
                ballSpeedX = (int)changeX / 20;
                ballSpeedY = (int)changeY / 20;
            }
            else if(Math.abs(changeX) >= 300 && Math.abs(changeX) < 600 || Math.abs(changeY) >= 300 && Math.abs(changeY) < 600 ){
                ballSpeedX = (int)changeX / 45;
                ballSpeedY = (int)changeY / 45;
            }
            else if(Math.abs(changeX) >= 600 && Math.abs(changeX) < 900 || Math.abs(changeY) >= 600 && Math.abs(changeY) < 900 ){
                ballSpeedX = (int)changeX / 75;
                ballSpeedY = (int)changeY / 75;
            }
            else if(Math.abs(changeX) >= 900 || Math.abs(changeY) >= 900 ){
                ballSpeedX = (int)changeX / 100;
                ballSpeedY = (int)changeY / 100;
            }
            else{
                ballSpeedX = (int)changeX / 5;
                ballSpeedY = (int)changeY / 5;
            }

            invalidate();
        }

        return true;
    }
}
