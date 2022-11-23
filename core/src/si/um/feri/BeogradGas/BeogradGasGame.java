package si.um.feri.BeogradGas;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class BeogradGasGame extends ApplicationAdapter {
    private Texture girl1Image;
    private Texture girl2Image;
    private Texture girl3Image;
    private Texture carImage;
    private Texture policeCarImage;
    private Texture bulletImage;
    private Texture background1;
    private Texture background2;

    private Sound pickSound;
    private Sound laserSound;
    private Sound crashSound;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle mercedes;
    private Array<Rectangle> girls;    // special LibGDX Array
    private Array<Rectangle> policeCars;
    private Array<Rectangle> bullets;
    private long lastGirlTime;
    private long lastPoliceCarTime;
    private int girlsPickedScore;
    private int mercedesHealth;    // starts with 100

    public BitmapFont font;

    // all values are set experimental
    private static final int SPEED = 600;    // pixels per second
    private static final int SPEED_GIRL = 120; // pixels per second
    private static int SPEED_POLICE_CAR = 200;    // pixels per second
    private static final long CREATE_GIRL_TIME = 1000000000;    // ns
    private static final long CREATE_POLICE_CAR_TIME = 2000000000;    // ns
    private static float BACKGROUND_VELOCITY = 2;
    private static float BACKGROUND_Y = 0;

    private Viewport viewport;
    private Viewport hudViewport;

    private float WORLD_HEIGHT = 600f;
    private float WORLD_WIDTH = 800f;


    @Override
    public void create() {

        font = new BitmapFont();
        font.getData().setScale(2);
        girlsPickedScore = 0;
        mercedesHealth = 100;

        // default way to load a texture
        carImage = new Texture(Gdx.files.internal("mercedes-benz.png"));
        policeCarImage = new Texture(Gdx.files.internal("police-car.png"));
        bulletImage = new Texture(Gdx.files.internal("bullet.png"));
        girl1Image = new Texture(Gdx.files.internal("girl1.png"));
        girl2Image = new Texture(Gdx.files.internal("girl2.png"));
        girl3Image = new Texture(Gdx.files.internal("girl3.png"));

        pickSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser.wav"));
        crashSound = Gdx.audio.newSound(Gdx.files.internal("crash.wav"));

        background1 = new Texture(Gdx.files.internal("background.png"));
        background2 = new Texture(Gdx.files.internal("background.png"));



        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        hudViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch = new SpriteBatch();

        // create a Rectangle to logically represents the car
        mercedes = new Rectangle();
        mercedes.x = viewport.getWorldWidth() / 2f - carImage.getWidth() / 2f;    // center the mercedes horizontally
        mercedes.y = 20;    // bottom left corner of mercedes is 20 pixels above the bottom screen edge
        mercedes.width = carImage.getWidth();
        mercedes.height = carImage.getHeight();

        girls = new Array<Rectangle>();
        policeCars = new Array<Rectangle>();
        bullets = new Array<Rectangle>();
        // add first mercedes and policecar
        spawnGirl();
        spawnPoliceCar();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        // clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw background
        batch.begin();
        batch.draw(background1, 0, BACKGROUND_Y, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.draw(background2, 0, BACKGROUND_Y+ viewport.getWorldHeight(), viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        // move background vertically
        BACKGROUND_Y -= BACKGROUND_VELOCITY;

        // re-draw background
        if(BACKGROUND_Y + viewport.getWorldHeight() == 0){
            BACKGROUND_Y = 0;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();

        // check if we need to create a new girl/police car
        if (TimeUtils.nanoTime() - lastGirlTime > CREATE_GIRL_TIME) spawnGirl();
        if (TimeUtils.nanoTime() - lastPoliceCarTime > CREATE_POLICE_CAR_TIME) spawnPoliceCar();

        if (mercedesHealth > 0) {    // is game end?

            // process user input
            //if (Gdx.input.isTouched()) commandTouched();    // mouse or touch screen
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) commandMoveLeft();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) commandMoveRight();
            if (Gdx.input.isKeyPressed(Input.Keys.A)) commandMoveLeftCorner();
            if (Gdx.input.isKeyPressed(Input.Keys.S)) commandMoveRightCorner();
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) commandShoot();

            // move and remove any that are beneath the bottom edge of
            // the screen or that hit the car
            for (Iterator<Rectangle> it = policeCars.iterator(); it.hasNext(); ) {
                Rectangle policeCar = it.next();
                policeCar.y -= SPEED_POLICE_CAR * Gdx.graphics.getDeltaTime();
                if (policeCar.y + policeCarImage.getHeight() < 0) it.remove();
                if (policeCar.overlaps(mercedes)) {
                    crashSound.play();
                    mercedesHealth--;
                }
            }

            for (Iterator<Rectangle> it = girls.iterator(); it.hasNext(); ) {
                Rectangle girl = it.next();
                girl.y -= SPEED_GIRL * Gdx.graphics.getDeltaTime();
                if (girl.y + girl1Image.getHeight() < 0) it.remove();    // from screen
                if (girl.overlaps(mercedes)) {
                    pickSound.play();
                    girlsPickedScore++;
                    if (girlsPickedScore % 10 == 0) SPEED_POLICE_CAR += 66;    // speeds up
                    it.remove();    // smart Array enables remove from Array
                }
            }

            // shooting mechanics
            if(!bullets.isEmpty()){
                for (Iterator<Rectangle> it = bullets.iterator(); it.hasNext(); ) {
                    Rectangle bullet = it.next();
                    bullet.y += SPEED_GIRL * Gdx.graphics.getDeltaTime();
                    if (bullet.y + bulletImage.getHeight() > WORLD_HEIGHT) it.remove();
                    //collision detection between bullets and policeCars
                    for (Iterator<Rectangle> it1 = policeCars.iterator(); it1.hasNext();){
                        if (bullet.overlaps(it1.next())) {
                            pickSound.play();
                            it.remove();
                            it1.remove();
                        }
                    }
                }
            }

        } else {    // health of mercedes is 0 or less
            batch.begin();
            {
                font.setColor(Color.RED);
                font.draw(batch, "The END", hudViewport.getWorldWidth()  / 2f - 50, hudViewport.getWorldHeight() / 2f);
                BACKGROUND_VELOCITY = 0;
            }
            batch.end();
        }

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the mercedes, girls, policeCars
        batch.begin();
        {    // brackets added just for indent
            batch.draw(carImage, mercedes.x, mercedes.y);
            for (Rectangle policeCar : policeCars) {
                batch.draw(policeCarImage, policeCar.x, policeCar.y);
            }
            for (Rectangle girl : girls) {
                batch.draw(girl2Image, girl.x, girl.y);
                // TODO: different images
            }
            if(!bullets.isEmpty()){
                for (Rectangle bullet : bullets) {
                    batch.draw(bulletImage, bullet.x, bullet.y);
                }
            }

            font.setColor(Color.YELLOW);
            font.draw(batch, "" + girlsPickedScore, hudViewport.getWorldWidth() - 50, hudViewport.getWorldHeight() - 20);
            font.setColor(Color.GREEN);
            font.draw(batch, "" + mercedesHealth, 20, hudViewport.getWorldHeight() - 20);
        }
        batch.end();
    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {
        girl1Image.dispose();
        policeCarImage.dispose();
        carImage.dispose();
        pickSound.dispose();
        crashSound.dispose();
        laserSound.dispose();
        batch.dispose();
        font.dispose();
        background1.dispose();
        background2.dispose();
        bulletImage.dispose();
    }

    private void spawnGirl() {
        Rectangle girl = new Rectangle();
        girl.x = MathUtils.random(0, viewport.getWorldWidth() - girl1Image.getWidth());
        girl.y = viewport.getWorldHeight();
        girl.width = girl1Image.getWidth();
        girl.height = girl1Image.getHeight();
        girls.add(girl);
        lastGirlTime = TimeUtils.nanoTime();
    }

    private void spawnPoliceCar() {
        Rectangle policeCar = new Rectangle();
        policeCar.x = MathUtils.random(0, viewport.getWorldWidth() - policeCarImage.getWidth());
        policeCar.y = viewport.getWorldHeight();
        policeCar.width = policeCarImage.getWidth();
        policeCar.height = policeCarImage.getHeight();
        policeCars.add(policeCar);
        lastPoliceCarTime = TimeUtils.nanoTime();
    }

    private void commandMoveLeft() {
        mercedes.x -= SPEED * Gdx.graphics.getDeltaTime();
        if (mercedes.x < 0) mercedes.x = 0;
    }

    private void commandMoveRight() {
        mercedes.x += SPEED * Gdx.graphics.getDeltaTime();
        if (mercedes.x > viewport.getWorldWidth() - carImage.getWidth())
            mercedes.x = viewport.getWorldWidth() - carImage.getWidth();
    }

    private void commandMoveLeftCorner() {
        mercedes.x = 0;
    }

    private void commandMoveRightCorner() {
        mercedes.x = viewport.getWorldWidth() - carImage.getWidth();
    }

    private void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        mercedes.x = touchPos.x - carImage.getWidth() / 2f;
        // TODO: add screen width limit
    }

    private void commandShoot() {
        Rectangle bullet = new Rectangle();
        bullet.x = mercedes.x + carImage.getWidth() / 2f;
        bullet.y = mercedes.y + carImage.getHeight();
        bullet.width = bulletImage.getWidth();
        bullet.height = bulletImage.getHeight();
        bullets.add(bullet);
        laserSound.play();
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
