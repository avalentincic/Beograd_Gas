package si.um.feri.OOgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;

public class Bullet extends GameObjectDynamic {
    private static final int SPEED_BULLET = 120;

    public Bullet(float x, float y, float width, float height){
        super(x, y, width, height);
    }

    @Override
    public void update(float deltaTime){
        //super.update(deltaTime);
        bounds.y += SPEED_BULLET * deltaTime;
    }

    @Override
    public void render(SpriteBatch batch){
        //super.render(batch);
        batch.draw(Assets.bulletImage, bounds.x, bounds.y);
    }

    @Override
    public void updateScore(Score gameObjectScore, Iterator<GameObjectDynamic> it){

    }
}
