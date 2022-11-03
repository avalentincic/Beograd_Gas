package si.um.feri.OOgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class PoliceCar extends GameObjectDynamic {
    public static long lastCreated;
    public static long CREATE_TIME = 2000000000;//2000000000;// ns
    private static int SPEED_POLICE_CAR = 200;
    public static final float POLICE_CAR_WIDTH = Assets.policeCarImage.getWidth();
    public static final float POLICE_CAR_HEIGHT = Assets.policeCarImage.getHeight();


    public PoliceCar(float x, float y){
        super(x, y, POLICE_CAR_WIDTH, POLICE_CAR_HEIGHT);
    }

    public static void setLastCreated(long lastCreated) {
        PoliceCar.lastCreated = lastCreated;
    }

    public static boolean isTimeToCreateNew(){
        return TimeUtils.nanoTime() - lastCreated > CREATE_TIME;
    }

    @Override
    public void update(float deltaTime){
        bounds.y -= SPEED_POLICE_CAR * deltaTime;
    }

    @Override
    public void render(SpriteBatch batch){
        batch.draw(Assets.policeCarImage, bounds.x, bounds.y);
    }

    @Override
    public void updateScore(Score gameObjectScore, Iterator<GameObjectDynamic> it) {
        gameObjectScore.setMercedesHealth(gameObjectScore.getMercedesHealth() - 1);
        Assets.crashSound.play();
    }

    public static void setSpeedPoliceCar(){
        SPEED_POLICE_CAR += 66;
    }

}
