package si.um.feri.OOgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;


public class Score extends GameObject{
    private int girlsRescuedScore;
    private int mercedesHealth; //Starts with 100

    public Score(float x, float y, float width, float height, int girlsRescuedScore, int mercedesHealth)  {
        super(x, y, width, height);
        this.girlsRescuedScore = girlsRescuedScore;
        this.mercedesHealth = mercedesHealth;
    }

    public int getGirlsRescuedScore() {
        return girlsRescuedScore;
    }

    public void setGirlsRescuedScore(int girlsRescuedScore) {
        this.girlsRescuedScore = girlsRescuedScore;
    }

    public int getMercedesHealth() {
        return mercedesHealth;
    }

    public void setMercedesHealth(int mercedesHealth) {
        this.mercedesHealth = mercedesHealth;
    }

    //@Override
    public void render(SpriteBatch batch) {
        Assets.font.setColor(Color.YELLOW);
        Assets.font.draw(batch, "" + getGirlsRescuedScore(), bounds.width - 50, bounds.height - 20);
        Assets.font.setColor(Color.GREEN);
        Assets.font.draw(batch, "" + getMercedesHealth(), 20, bounds.height - 20);
    }

    public boolean isEnd() {
        return (mercedesHealth <=0);
    }
}

