package si.um.feri.OOgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class PowerUp extends GameObjectDynamic implements Pool.Poolable {
    private static final int SPEED_POWER_UP = 120;
    public static final float POWER_UP_WIDTH = Assets.redBullImage.getWidth();
    public static final float POWER_UP_HEIGHT = Assets.redBullImage.getHeight();
    public static long lastCreated;
    public static long lastPicked;
    public static long CREATE_TIME = 30000; // 30s
    public static long DURATION = 11700; // 11,7s
    public static boolean active;

    public PowerUp(float x, float y){
        super(x, y, POWER_UP_WIDTH, POWER_UP_HEIGHT);
        active = false;
    }

    public PowerUp(){
        super(0, 0, POWER_UP_WIDTH, POWER_UP_HEIGHT);
        active = false;
    }

    public static void setActive(boolean active){
        PowerUp.active = active;
    }

    public static boolean isActive(){
        return TimeUtils.millis() - lastPicked < DURATION;
    }

    public static boolean isTimeToCreateNew(Score gameObjectScore) {
        int score = gameObjectScore.getGirlsRescuedScore();
        return (TimeUtils.millis() - lastCreated > CREATE_TIME) && score >= 20;
    }

    public static void setLastCreated(long lastCreated) {
        PowerUp.lastCreated = lastCreated;
    }

    @Override
    public void update(float deltaTime){
        bounds.y -= SPEED_POWER_UP * deltaTime;
    }

    @Override
    public void render(SpriteBatch batch){
        batch.draw(Assets.redBullImage, bounds.x, bounds.y);
    }

    @Override
    public void updateScore(Score gameObjectScore, Iterator<GameObjectDynamic> it) {
        Assets.astronautSound.play();
        active = true;
        lastPicked = TimeUtils.millis();
        it.remove();
        Assets.powerUpSound.play();
    }

    public void init(float posX, float posY){
        bounds.setPosition(posX, posY);
    }

    @Override
    public void reset(){
        bounds.setPosition(MathUtils.random(0, BeogradGasGame.width - Assets.girlImage3.getWidth()),
                BeogradGasGame.height);
        active = false;
    }
}
