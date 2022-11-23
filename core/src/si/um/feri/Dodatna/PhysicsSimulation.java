package si.um.feri.Dodatna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class PhysicsSimulation extends ApplicationAdapter {
    private Texture wheelImage;
    private SpriteBatch batch;
    private Sprite sprite;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    static float width;
    static float height;
    private Array<Ball> balls;
    private float SPEED = 100;
    private float ROTATION = 2;
    private Wheel wheel;

    @Override
    public void create(){
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        wheelImage = new Texture(Gdx.files.internal("wheel.png"));
        sprite = new Sprite(wheelImage);

        balls = new Array<Ball>();
        //wheel = new Wheel(0,0);
    }

    @Override
    public void render(){ //runs every frame
        //clear screen
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.justTouched()) commandTouched(camera);

        for (Ball ball : balls) {
            ball.update(Gdx.graphics.getDeltaTime());
        }

        //wheel.update(Gdx.graphics.getDeltaTime(), width);

        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        {
            for (Ball ball : balls){
                shapeRenderer.setColor(Color.SKY);
                shapeRenderer.circle(ball.x, ball.y, ball.radius);

            }
        }
        shapeRenderer.end();

        batch.begin();
        sprite.draw(batch);
        batch.end();
        rotateSprite();
    }

    @Override
    public void dispose(){
        shapeRenderer.dispose();
        batch.dispose();
    }

    public void spawnBall(float x, float y){
        Ball ball = new Ball(x,y);
        balls.add(ball);
    }

    public void commandTouched(OrthographicCamera camera) {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        spawnBall(touchPos.x, touchPos.y);

    }

    private void rotateSprite(){
        float x = sprite.getX();
        float rotation = sprite.getRotation();
        rotation -= ROTATION;
        sprite.setRotation(rotation);
        x+= SPEED*Gdx.graphics.getDeltaTime();
        sprite.setX(x);
        if(sprite.getX() + sprite.getWidth() >= width){
            SPEED = -SPEED;
            ROTATION = -ROTATION;
        }
        if (sprite.getX() <= 0 && SPEED < 0){
            SPEED = -SPEED;
            ROTATION = -ROTATION;
        }
    }
}
