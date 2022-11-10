package si.um.feri.Dodatna;

import com.badlogic.gdx.math.Circle;
import java.lang.Math;

public class Ball extends Circle {
    public double SPEED;

    public Ball(float x, float y){
        super(x, y, 20);
    }

    public void update(float deltaTime){
        SPEED += 10 * deltaTime;
        this.y -= SPEED;
        if (this.y - this.radius < 0) {
            SPEED = -SPEED * 0.8;
            this.y = this.radius;
        }

    }
}
