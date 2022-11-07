package si.um.feri.OOgame;

import static si.um.feri.OOgame.Assets.font;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
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
    static float width;
    static float height;
    public static float BACKGROUND_VELOCITY = 120;
    private static float BACKGROUND_Y = 0;


    private final Pool<PoliceCar> policeCarPool = Pools.get(PoliceCar.class, 10);
    private final Pool<Girl> girlPool = Pools.get(Girl.class, 10);
    private final Pool<PowerUp> powerUpPool = Pools.get(PowerUp.class, 10);
    private final Pool<Bullet> bulletPool = Pools.get(Bullet.class, 10);

    private State state = State.RUN;


    @Override
    public void create() {
        Assets.load();
        //Gdx.app.setLogLevel(Logger.DEBUG);
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
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
        policeCarPool.fill(5);
        girlPool.fill(5);
        powerUpPool.fill(5);
        bulletPool.fill(5);


    }

    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        //clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        // process user input
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) commandPause();
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) commandReset();
        if(gameObjectScore.isEnd()){
            state = State.END;
        }
        draw();
        switch (state){
            case RUN:
                update();
                break;
            case PAUSE:
                batch.begin();
                {
                    font.setColor(Color.RED);
                    font.draw(batch, "PAUSED", Gdx.graphics.getHeight() / 2f + 20, Gdx.graphics.getHeight() / 2f);
                    gameObjectScore.render(batch);
                }
                batch.end();
                break;
            case END:
                batch.begin();
                {
                    //gameObjectEnd.render(batch);
                    font.setColor(Color.RED);
                    font.draw(batch, "The END", Gdx.graphics.getHeight() / 2f + 20, Gdx.graphics.getHeight() / 2f);
                    gameObjectScore.render(batch);
                }
                batch.end();
            default:
                break;
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

    public void update(){
        if (Gdx.input.isTouched()) mercedes.commandTouched(camera); //mouse or touch screen
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) mercedes.commandMoveLeft();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) mercedes.commandMoveRight();
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) commandShoot();

        if(!PowerUp.isActive()){
            BACKGROUND_Y -= BACKGROUND_VELOCITY * Gdx.graphics.getDeltaTime();
            Girl.setSpeedGirl(120);
        } else {
            BACKGROUND_Y -= 240 * Gdx.graphics.getDeltaTime();;
            Girl.setSpeedGirl(240);
        }

        // re-draw background
        if(BACKGROUND_Y + height <= 0){
            BACKGROUND_Y = 0;
        }

        mercedes.update(Gdx.graphics.getDeltaTime());
        for (GameObjectDynamic act : dynamicActors) {
            act.update(Gdx.graphics.getDeltaTime());
        }
        for(Bullet bullet: bullets){
            bullet.update(Gdx.graphics.getDeltaTime());
        }


        if (Girl.isTimeToCreateNew()) spawnGirl();
        if (PoliceCar.isTimeToCreateNew()) spawnPoliceCar();
        if (PowerUp.isTimeToCreateNew(gameObjectScore)) spawnPowerUp();

        for (Iterator<GameObjectDynamic> it = dynamicActors.iterator(); it.hasNext();) {
            GameObjectDynamic act = it.next();
            if (act.bounds.y + act.bounds.height < 0) {
                if (it instanceof Girl){
                    girlPool.free((Girl) act);
                }
                if (it instanceof PoliceCar){
                    policeCarPool.free((PoliceCar) act);
                }
                if (it instanceof PowerUp){
                    powerUpPool.free((PowerUp) act);
                }
                it.remove();
            }
            if (act.bounds.overlaps(mercedes.bounds)) act.updateScore(gameObjectScore, it);
            for (Iterator<Bullet> blIt = bullets.iterator(); blIt.hasNext();) {
                Bullet bl = blIt.next();
                if (bl.bounds.y - bl.bounds.height > height) {
                    blIt.remove();
                    bulletPool.free(bl);
                }
                if (act.bounds.overlaps(bl.bounds) && act instanceof PoliceCar) { it.remove(); blIt.remove();
                    policeCarPool.free((PoliceCar) act);
                    bulletPool.free(bl);
                }
            }
        }
    }

    public void draw(){
        batch.begin();
        {
            batch.draw(Assets.background1, 0, BACKGROUND_Y, width, height);
            batch.draw(Assets.background2, 0, BACKGROUND_Y + height, width, height);
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

    private void spawnGirl() {
        Girl g = girlPool.obtain();
        g.init(
                MathUtils.random(0, width - Assets.girlImage1.getWidth()),
                height
        );
        dynamicActors.add(g);
        Girl.setLastCreated(TimeUtils.nanoTime());
    }


    private void spawnPoliceCar() {
        PoliceCar c = policeCarPool.obtain();
        c.init(
                MathUtils.random(0, width - Assets.policeCarImage.getWidth()),
                height
        );
        dynamicActors.add(c);
        PoliceCar.setLastCreated(TimeUtils.nanoTime());
    }

    private void spawnPowerUp() {
        PowerUp p = powerUpPool.obtain();
        p.init(
                MathUtils.random(0, width - Assets.policeCarImage.getWidth()),
                height
        );
        dynamicActors.add(p);
        PowerUp.setLastCreated(TimeUtils.millis());
    }

    private void commandShoot() {
        Bullet b = bulletPool.obtain();
        b.init(
                mercedes.bounds.x + Assets.mercedesImage.getWidth() / 2f,
                mercedes.bounds.y + Assets.mercedesImage.getHeight()
        );
        bullets.add(b);
        Assets.laserSound.play();
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }

    private void commandPause(){
        if(state == State.PAUSE){
            state = State.RUN;
        } else {state = State.PAUSE; }
    }

    private void commandReset(){
        if (state == State.END){
            gameObjectScore.setMercedesHealth(100);
            gameObjectScore.setGirlsRescuedScore(0);
            for (Iterator<GameObjectDynamic> it = dynamicActors.iterator(); it.hasNext();) {
                GameObjectDynamic act = it.next();
                if (it instanceof Girl){
                    girlPool.free((Girl) act);
                }
                if (it instanceof PoliceCar){
                    policeCarPool.free((PoliceCar) act);
                }
                if (it instanceof PowerUp){
                    powerUpPool.free((PowerUp) act);
                }
                it.remove();
            }
            state = State.RUN;
            PoliceCar.setSpeedPoliceCar(200);
            BACKGROUND_Y = 0;
        }
    }
}
