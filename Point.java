import java.util.List;
import java.util.Optional;

final class Point
{
   public final int x;
   public final int y;

   public Point(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   public String toString()
   {
      return "(" + x + "," + y + ")";
   }

   public Optional<Entity> nearestEntity(List<Entity> entities,
                                         Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = distanceSquared(nearest.position, pos);

         for (Entity other : entities)
         {
            int otherDistance = distanceSquared(other.position, pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   public int distanceSquared(Point p1, Point p2)
   {
      int deltaX = p1.x - p2.x;
      int deltaY = p1.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }
   public boolean adjacent(Point p1, Point p2)
   {
      return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
              (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
   }

   public boolean equals(Object other)
   {
      return other instanceof Point &&
         ((Point)other).x == this.x &&
         ((Point)other).y == this.y;
   }

   public int hashCode()
   {
      int result = 17;
      result = result * 31 + x;
      result = result * 31 + y;
      return result;
   }
}


