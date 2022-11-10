package si.um.feri.Dodatna;

import com.badlogic.gdx.math.Circle;

public class Wheel extends Circle {
    public double SPEED = 200;

    public Wheel(float x, float y){
        super(x, y, 50);
    }

    public void update(float deltaTime, float width){
        this.x += SPEED * deltaTime;
        if (this.x +2*this.radius >= width){
            SPEED = -SPEED;

        }
        if (this.x <= 0 && SPEED < 0){
            SPEED = -SPEED;
        }
    }
}
