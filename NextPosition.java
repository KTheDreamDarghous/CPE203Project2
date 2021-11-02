import java.util.Optional;

public abstract class NextPosition {
    public Point nextPosition(Entity entity, WorldModel world,
                              Point destPos)
    {
        int horiz = Integer.signum(destPos.x - entity.position.x);
        Point newPos = new Point(entity.position.x + horiz,
                entity.position.y);

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 ||
                (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
        {
            int vert = Integer.signum(destPos.y - entity.position.y);
            newPos = new Point(entity.position.x, entity.position.y + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 ||
                    (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
            {
                newPos = entity.position;
            }
        }

        return newPos;
    }

}
