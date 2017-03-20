package football.game;

import android.graphics.Canvas;
import android.util.Log;


public class FootballGameThread extends Thread {
    static final long framesPerSecond = 30;
    private FootballGame view;
    private boolean running = false;

    public FootballGameThread(FootballGame view){
        Log.d("thread", "aloitus");
        this.view = view;
    }

    public void setRunning(boolean run){
        running = run;
    }

    @Override
    public void run(){
        Log.d("thread", "runnataan");
        long ticksPS = 1000 / framesPerSecond;

        long startTime;
        long sleepTime;


        while (running) {
            Canvas canvas = null;
            startTime = System.currentTimeMillis();

            view.testi();

            try {
                //Start editing pixels in the surface
                canvas = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    //Kutsuu ondraw eli piirtää kaikki tarvittavat
                    //view.draw(canvas);
                    view.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    //Finish editing pixels in the surface.
                    view.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            //String sleeptime = Long.toString(sleepTime);
            //Log.d("thread", sleeptime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}

        }
    }
}
