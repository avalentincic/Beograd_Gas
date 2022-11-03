package si.um.feri.OOgame;

import com.badlogic.gdx.math.Vector2;

import java.util.Iterator;

public abstract class GameObjectDynamic extends GameObject {
    public final Vector2 velocity;
    public final Vector2 accel;

    public GameObjectDynamic (float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity = new Vector2();
        accel = new Vector2();
    }

    public abstract void update (float deltaTime);

    public abstract void updateScore(Score gameObjectScore, Iterator<GameObjectDynamic> it);
}