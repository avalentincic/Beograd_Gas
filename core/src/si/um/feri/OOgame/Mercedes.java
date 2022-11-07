package si.um.feri.OOgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class Mercedes extends GameObjectDynamic {
    private static final int SPEED = 600;

    public Mercedes (float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void render(SpriteBatch batch){
        batch.draw(Assets.mercedesImage, bounds.x, bounds.y);
    }

    @Override
    public void update(float deltaTime){
    }

    @Override
    public void updateScore(Score gameObjectScore, Iterator<GameObjectDynamic> it){
    }

    public void commandMoveLeft(){
        bounds.x -= SPEED * Gdx.graphics.getDeltaTime();
        if (bounds.x < 0) bounds.x = 0;
    }

    public void commandMoveRight(){
        bounds.x += SPEED * Gdx.graphics.getDeltaTime();
        if (bounds.x > Gdx.graphics.getWidth() - bounds.width)
            bounds.x = Gdx.graphics.getWidth() - bounds.width;
    }

    public void commandTouched(OrthographicCamera camera) {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        bounds.x = touchPos.x - bounds.width / 2f;
        if (bounds.x < 0) bounds.x = 0;
        if (bounds.x > Gdx.graphics.getWidth() - bounds.width) bounds.x = Gdx.graphics.getWidth() - bounds.width;

    }

    private void commandMoveLeftCorner() {
        position.x = 0;
    }

    private void commandMoveRightCorner() {
        position.x = Gdx.graphics.getWidth() - bounds.width;
    }
}
