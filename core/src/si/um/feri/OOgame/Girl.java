package si.um.feri.OOgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Girl extends GameObjectDynamic {
    public static long lastCreated;
    public boolean rescued;
    public Texture girlImage;
    public static long CREATE_TIME = 1000000000; //ns
    private static final int SPEED_GIRL = 120;
    public static final float GIRL_WIDTH = Assets.girlImage1.getWidth();
    public static final float GIRL_HEIGHT = Assets.girlImage1.getHeight();

    public Girl(float x, float y) {
        super(x, y, GIRL_WIDTH, GIRL_HEIGHT);
        rescued = false;
        girlImage = Assets.girlImages.random();
    }

    public static void setLastCreated(long lastCreated) {
        Girl.lastCreated = lastCreated;
    }

    public static boolean isTimeToCreateNew() {
        return TimeUtils.nanoTime() - lastCreated > CREATE_TIME;
    }

    @Override
    public void update(float deltaTime){
        bounds.y -= SPEED_GIRL * deltaTime;
    }

    @Override
    public void render(SpriteBatch batch){
        batch.draw(girlImage, bounds.x, bounds.y);
    }

    @Override
    public void updateScore(Score gameObjectScore, Iterator<GameObjectDynamic> it) {
        if (!rescued) {
            gameObjectScore.setGirlsRescuedScore(gameObjectScore.getGirlsRescuedScore() + 1);
            Assets.astronautSound.play();
            rescued = true;
            it.remove();
            if(gameObjectScore.getGirlsRescuedScore()%10==0 && gameObjectScore.getGirlsRescuedScore()!=0){
                PoliceCar.setSpeedPoliceCar();
            }
        }
    }

}
