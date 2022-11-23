package si.um.feri.OOgame;

import static si.um.feri.OOgame.Assets.bulletImage;
import static si.um.feri.OOgame.Assets.font;
import static si.um.feri.OOgame.Assets.girlImage1;
import static si.um.feri.OOgame.Assets.mercedesImage;
import static si.um.feri.OOgame.Assets.policeCarImage;
import static si.um.feri.OOgame.Assets.redBullImage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

import si.um.feri.util.ViewportUtils;
import si.um.feri.util.debug.DebugCameraController;
import si.um.feri.util.debug.MemoryInfo;

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

    //debug
    private DebugCameraController debugCameraController;
    private MemoryInfo memoryInfo;
    private boolean debug = false;

    private ShapeRenderer shapeRenderer;
    public Viewport viewport;


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

        // debug
        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        memoryInfo = new MemoryInfo(500);

        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);


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

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug = !debug;

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
                if (act instanceof Girl){
                    girlPool.free((Girl) act);
                }
                if (act instanceof PoliceCar){
                    policeCarPool.free((PoliceCar) act);
                }
                if (act instanceof PowerUp){
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


        if (debug) {
            debugCameraController.handleDebugInput(Gdx.graphics.getDeltaTime());
            memoryInfo.update();
            debugCameraController.applyTo(camera);
            batch.begin();
            {
                // the average number of frames per second
                GlyphLayout layout = new GlyphLayout(font, "FPS:" + Gdx.graphics.getFramesPerSecond());
                font.setColor(Color.YELLOW);
                font.draw(batch, layout, Gdx.graphics.getWidth() - layout.width, Gdx.graphics.getHeight() - 50);

                // number of rendering calls, ever; will not be reset unless set manually
                font.setColor(Color.YELLOW);
                font.draw(batch, "RC:" + batch.totalRenderCalls, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 20);

                memoryInfo.render(batch, font);
            }
            batch.end();

            batch.totalRenderCalls = 0;
            ViewportUtils.drawGrid(viewport, shapeRenderer, 50);


            // print rectangles
            shapeRenderer.setProjectionMatrix(camera.combined);
            // https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            {
                shapeRenderer.setColor(1, 1, 0, 1);
                for (GameObjectDynamic act : dynamicActors) {
                    if (act instanceof PoliceCar) shapeRenderer.rect(act.bounds.getX(), act.bounds.getY(), policeCarImage.getWidth(), policeCarImage.getHeight());
                    if (act instanceof Girl) shapeRenderer.rect(act.bounds.getX(), act.bounds.getY(), girlImage1.getWidth(), girlImage1.getHeight());
                    if (act instanceof PowerUp) shapeRenderer.rect(act.bounds.getX(), act.bounds.getY(), redBullImage.getWidth(), redBullImage.getHeight());
                }
                for (Bullet bullet : bullets) {
                    shapeRenderer.rect(bullet.bounds.getX(), bullet.bounds.getY(), bulletImage.getWidth(), bulletImage.getHeight());
                }
                shapeRenderer.rect(mercedes.bounds.getX(), mercedes.bounds.getY(), mercedesImage.getWidth(), mercedesImage.getHeight());
            }
            shapeRenderer.end();

        }
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
                if (act instanceof Girl){
                    girlPool.free((Girl) act);
                }
                if (act instanceof PoliceCar){
                    policeCarPool.free((PoliceCar) act);
                }
                if (act instanceof PowerUp){
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
