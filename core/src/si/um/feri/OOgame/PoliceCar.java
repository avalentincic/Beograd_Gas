package si.um.feri.OOgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class PoliceCar extends GameObjectDynamic implements Pool.Poolable{
    public static long lastCreated;
    public static long CREATE_TIME = 2000000000;//2000000000;// ns
    private static int SPEED_POLICE_CAR = 200;
    public static final float POLICE_CAR_WIDTH = Assets.policeCarImage.getWidth();
    public static final float POLICE_CAR_HEIGHT = Assets.policeCarImage.getHeight();
    private boolean alive;

    public PoliceCar(float x, float y){
        super(x, y, POLICE_CAR_WIDTH, POLICE_CAR_HEIGHT);
    }

    public PoliceCar(){
        super(0,0, POLICE_CAR_WIDTH, POLICE_CAR_HEIGHT);
        setLastCreated(TimeUtils.nanoTime());
    }

    public static void setLastCreated(long lastCreated) {
        PoliceCar.lastCreated = lastCreated;
    }

    public static boolean isTimeToCreateNew(){
        return TimeUtils.nanoTime() - lastCreated > CREATE_TIME;
    }

    public boolean isAlive(){
        return alive;
    }

    @Override
    public void update(float deltaTime){
        bounds.y -= SPEED_POLICE_CAR * deltaTime;
        if (isOutOfScreen()) alive = false;
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

    public static void setSpeedPoliceCar(int speedPoliceCar){
        SPEED_POLICE_CAR = speedPoliceCar;
    }

    public static void incrementSpeedPoliceCar(){
        SPEED_POLICE_CAR += 66;
    }

    public void init(float posX, float posY){
        bounds.setPosition(posX, posY);
    }

    @Override
    public void reset(){
        bounds.setPosition(MathUtils.random(0, BeogradGasGame.width - Assets.policeCarImage.getWidth()),
                BeogradGasGame.height);
    }

    private boolean isOutOfScreen() {
        return bounds.y >= BeogradGasGame.height + Assets.policeCarImage.getHeight();
    }

}
