package ships.y2022.noah;

import asteroidsfw.Vector2d;
import asteroidsfw.ai.*;

public class NoahShip implements ShipMind {
    private ShipControl control;
    private final double MAX_SEE_AHEAD = 2;

    @Override
    public void init(ShipControl control) {
        this.control = control;
        this.state = State.AGGRESSIVE;
        this.Deciede();
    }

    @Override
    public void think(Perceptions perceptions, double v) {
        CollisionAvoidance(perceptions, v);
    }

    public enum State {
        AGGRESSIVE,
        AVOID
    }

   private State state;

    public void Deciede(){
        switch (state) {
            case AGGRESSIVE:
                control.shooting(true);
                control.thrustBackward(false);
                control.thrustForward(true);
                control.rotateRight(false);
                break;
            case AVOID:
                control.shooting(true);
                control.thrustForward(false);
                control.thrustBackward(true);
                control.rotateRight(true);
                break;
        }
    }


    public AsteroidPerception CheckClosestAsteroid(Perceptions perceptions, double v, Vector2d ahead, Vector2d ahead2, Vector2d shipPos){
        AsteroidPerception currentAsteroid;
        AsteroidPerception closestAsteroid = null;

        for(int i = 0; i < perceptions.asteroids().length; i++){
            currentAsteroid = perceptions.asteroids()[i];
            boolean collision = LineIntersectsCircle(ahead, ahead2, currentAsteroid);

            if(collision && (closestAsteroid == null || (DistanceToShip(shipPos, currentAsteroid) < DistanceToShip(shipPos, closestAsteroid)))){
                closestAsteroid = currentAsteroid;
            }
        }

        return closestAsteroid;
    }

    public boolean LineIntersectsCircle(Vector2d ahead, Vector2d ahead2, AsteroidPerception currentAsteroid){
        return ((Distance(currentAsteroid, ahead) <= currentAsteroid.radius() * 10) || (Distance(currentAsteroid, ahead2) <= currentAsteroid.radius() * 10));
    }


    public double Distance(AsteroidPerception currentAsteroid, Vector2d ahead){
        double distanceTo;
        double x;
        double y;

        x = currentAsteroid.pos().x() - ahead.x();
        y = currentAsteroid.pos().y() - ahead.y();

        distanceTo = Math.sqrt(((x * x) + (y * y)));
        return distanceTo;
    }

    public double DistanceToShip(Vector2d shipPos, AsteroidPerception currentAsteroid) {
        double distanceTo;
        double x;
        double y;

        x = shipPos.x() - currentAsteroid.pos().x();
        y = shipPos.y() - currentAsteroid.pos().y();

        distanceTo = Math.sqrt(((x * x) + (y * y)));
        return distanceTo;
    }

    public void CollisionAvoidance(Perceptions perceptions, double v){
        Vector2d shipPos = control.pos();
        Vector2d velocity = control.v().normalize();
        Vector2d ahead = shipPos.$plus(velocity).$times(MAX_SEE_AHEAD);
        Vector2d ahead2 = shipPos.$plus(velocity).$times(MAX_SEE_AHEAD * 0.5);
        AsteroidPerception closestAsteroid = CheckClosestAsteroid(perceptions, v, ahead, ahead2, shipPos);


        if(closestAsteroid != null){
            state = State.AVOID;
            this.Deciede();
        } else {
            state = State.AGGRESSIVE;
            this.Deciede();
        }
    }
}
