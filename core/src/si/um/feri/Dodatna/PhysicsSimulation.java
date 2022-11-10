package si.um.feri.Dodatna;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    static float width;
    static float height;
    private Array<Ball> balls;
    private int SPEED = 200;

    @Override
    public void create(){
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        balls = new Array<Ball>();
    }

    @Override
    public void render(){ //runs every frame
        //clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.justTouched()) commandTouched(camera);

        for (Ball ball : balls) {
            ball.update(Gdx.graphics.getDeltaTime());
        }

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
    }

    @Override
    public void dispose(){
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
}
