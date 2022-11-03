package si.um.feri.OOgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;

public class Assets {
    public static Texture girlImage1;
    public static Texture girlImage2;
    public static Texture girlImage3;
    public static Texture mercedesImage;
    public static Texture policeCarImage;
    public static Texture bulletImage;
    public static Texture background1;
    public static Texture background2;

    public static Array<Texture> girlImages;

    public static Sound astronautSound;
    public static Sound laserSound;
    public static Sound crashSound;

    public static BitmapFont font;

    public static void load() {

        mercedesImage = new Texture(Gdx.files.internal("mercedes-benz.png"));
        girlImage1 = new Texture(Gdx.files.internal("girl1.png"));
        girlImage2 = new Texture(Gdx.files.internal("girl2.png"));
        girlImage3 = new Texture(Gdx.files.internal("girl3.png"));
        policeCarImage = new Texture(Gdx.files.internal("police-car.png"));
        bulletImage = new Texture(Gdx.files.internal("bullet.png"));

        girlImages = new Array<Texture>();
        girlImages.add(girlImage1);
        girlImages.add(girlImage2);
        girlImages.add(girlImage3);

        background1 = new Texture(Gdx.files.internal("background.png"));
        background2 = new Texture(Gdx.files.internal("background.png"));

        astronautSound = Gdx.audio.newSound(Gdx.files.internal("pick.wav"));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser.wav"));
        crashSound = Gdx.audio.newSound(Gdx.files.internal("crash.wav"));

        font = new BitmapFont();
        font.getData().setScale(2);
    }

    public static void dispose() {
        girlImage1.dispose();
        girlImage2.dispose();
        girlImage3.dispose();
        policeCarImage.dispose();
        mercedesImage.dispose();
        astronautSound.dispose();
        background1.dispose();
        background2.dispose();
        font.dispose();
    }

}

