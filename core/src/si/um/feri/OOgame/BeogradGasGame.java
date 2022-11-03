package si.um.feri.OOgame;

import static si.um.feri.OOgame.Assets.font;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class BeogradGasGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Mercedes mercedes;
    private Array<GameObjectDynamic> dynamicActors;
    private Array<Bullet> bullets;
    private Score gameObjectScore;
    //private GameObjectEnd gameObjectEnd;
    float width, height;
    private static float BACKGROUND_VELOCITY = 2;
    private static float BACKGROUND_Y = 0;


    @Override
    public void create() {
        Assets.load();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        Gdx.app.setLogLevel(Logger.DEBUG);
        gameObjectScore = new Score(0,0, width, height, 0, 100);
        //gameObjectEnd = new GameObjectEnd(0,0, width, height);
        bullets = new Array<Bullet>();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        // create a Rectangle to logically represents Mercedes
        mercedes = new Mercedes(
                width / 2f - Assets.mercedesImage.getWidth() / 2f ,
                20, Assets.mercedesImage.getWidth(), Assets.mercedesImage.getHeight()
        );

        dynamicActors = new Array<GameObjectDynamic>();
        //add first girl and police car
        spawnGirl();
        spawnPoliceCar();


    }

    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        //clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(Assets.background1, 0, BACKGROUND_Y, width, height);
        batch.draw(Assets.background2, 0, BACKGROUND_Y+ height, width, height);
        batch.end();

        // move background vertically
        BACKGROUND_Y -= BACKGROUND_VELOCITY;

        // re-draw background
        if(BACKGROUND_Y + height == 0){
            BACKGROUND_Y = 0;
        }

        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        // process user input
        if (Gdx.input.isTouched()) mercedes.commandTouched(camera); //mouse or touch screen
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) mercedes.commandMoveLeft();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) mercedes.commandMoveRight();
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) commandShoot();
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
        if (gameObjectScore.isEnd()) {
            batch.begin();
            {
                //gameObjectEnd.render(batch);
                font.setColor(Color.RED);
                font.draw(batch, "The END", Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 2f);
                gameObjectScore.render(batch);
                BACKGROUND_VELOCITY = 0;
            }
            batch.end();
        } else {
            mercedes.update(Gdx.graphics.getDeltaTime());
            for (GameObjectDynamic act : dynamicActors) {
                act.update(Gdx.graphics.getDeltaTime());
            }
            for(Bullet bullet: bullets){
                bullet.update(Gdx.graphics.getDeltaTime());
            }


            if (Girl.isTimeToCreateNew()) spawnGirl();
            if (PoliceCar.isTimeToCreateNew()) spawnPoliceCar();

            for (Iterator<GameObjectDynamic> it = dynamicActors.iterator(); it.hasNext();) {
                GameObjectDynamic act = it.next();
                if (act.bounds.y + act.bounds.height < 0) it.remove();
                if (act.bounds.overlaps(mercedes.bounds)) act.updateScore(gameObjectScore, it);
                for (Iterator<Bullet> blIt = bullets.iterator(); blIt.hasNext();) {
                    Bullet bl = blIt.next();
                    if (act.bounds.overlaps(bl.bounds) && act instanceof PoliceCar) { it.remove(); blIt.remove(); }
                }

            }
            batch.begin();
            {
                mercedes.render(batch);
                for (GameObjectDynamic act : dynamicActors) {
                    act.render(batch);
                }

                for(Bullet bullet: bullets){
                    bullet.render(batch);
                }

                gameObjectScore.render(batch);
            }
            batch.end();
        }
    }


    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {
        Assets.dispose();
        batch.dispose();
    }

    private void spawnGirl() {
        dynamicActors.add(new Girl(
                MathUtils.random(0, width - Assets.girlImage1.getWidth()),
                height
        ));
        Girl.setLastCreated(TimeUtils.nanoTime());
    }


    private void spawnPoliceCar() {
        dynamicActors.add(new PoliceCar(
                MathUtils.random(0, width - Assets.policeCarImage.getWidth()),
                height
        ));
        PoliceCar.setLastCreated(TimeUtils.nanoTime());
    }

    private void commandShoot() {
        bullets.add(new Bullet(
                mercedes.bounds.x + Assets.mercedesImage.getWidth() / 2f,
                mercedes.bounds.y + Assets.mercedesImage.getHeight(),
                Assets.bulletImage.getWidth(),
                Assets.bulletImage.getHeight()
        ));
        Assets.laserSound.play();
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
