package si.um.feri.OOgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

import java.util.Iterator;

public class Bullet extends GameObjectDynamic implements Pool.Poolable {
    private static final int SPEED_BULLET = 120;

    public Bullet(float x, float y, float width, float height){
        super(x, y, Assets.bulletImage.getWidth(), Assets.bulletImage.getHeight());
    }

    public Bullet(){
        super(0, 0, Assets.bulletImage.getWidth(), Assets.bulletImage.getHeight());
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

    @Override
    public void reset(){
        bounds.setPosition(MathUtils.random(0, BeogradGasGame.width - Assets.policeCarImage.getWidth()),
                BeogradGasGame.height);
    }

    public void init(float posX, float posY){
        bounds.setPosition(posX, posY);
    }

}
