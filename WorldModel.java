import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel
{
   private int numRows;
   private int numCols;
   public Background background[][];
   public Entity occupancy[][];
   public Set<Entity> entities;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   // Gettes and Setters
   public int get_numRows() {
      return numRows;
   }
   public int get_numCols() {
      return numCols;
   }

   public void load(Scanner in, WorldModel world, ImageStore imageStore) {
      int lineNumber = 0;
      while (in.hasNextLine()) {
         try {
            if (!processLine(in.nextLine(), world, imageStore)) {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         } catch (NumberFormatException e) {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         } catch (IllegalArgumentException e) {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   public Optional<Point> findOpenAround(WorldModel world, Point pos)
   {
      for (int dy = -Functions.FISH_REACH; dy <= Functions.FISH_REACH; dy++)
      {
         for (int dx = -Functions.FISH_REACH; dx <= Functions.FISH_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (world.withinBounds(newPt) &&
                    !world.isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public boolean processLine(String line, WorldModel world,
                              ImageStore imageStore) {
      String[] properties = line.split("\\s");
      if (properties.length > 0) {
         switch (properties[Functions.PROPERTY_KEY]) {
            case Functions.BGND_KEY:
               return parseBackground(properties, imageStore);
            case Functions.OCTO_KEY:
               return parseOcto(properties, imageStore);
            case Functions.OBSTACLE_KEY:
               return parseObstacle(properties, imageStore);
            case Functions.FISH_KEY:
               return parseFish(properties, imageStore);
            case Functions.ATLANTIS_KEY:
               return parseAtlantis(properties, imageStore);
            case Functions.SGRASS_KEY:
               return parseSgrass(properties, imageStore);
         }
      }

      return false;
   }

   public boolean parseBackground(String[] properties, ImageStore imageStore) {
      if (properties.length == Functions.BGND_NUM_PROPERTIES) {
         Point pt = new Point(Integer.parseInt(properties[Functions.BGND_COL]),
                 Integer.parseInt(properties[Functions.BGND_ROW]));
         String id = properties[Functions.BGND_ID];
         this.setBackground(pt,
                 new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == Functions.BGND_NUM_PROPERTIES;
   }

   public boolean parseAtlantis(String[] properties, ImageStore imageStore) {
      if (properties.length == Functions.ATLANTIS_NUM_PROPERTIES) {
         Point pt = new Point(Integer.parseInt(properties[Functions.ATLANTIS_COL]),
                 Integer.parseInt(properties[Functions.ATLANTIS_ROW]));
         Entity entity = createAtlantis(properties[Functions.ATLANTIS_ID],
                 pt, imageStore.getImageList(Functions.ATLANTIS_KEY));
         this.tryAddEntity(entity);
      }

      return properties.length == Functions.ATLANTIS_NUM_PROPERTIES;
   }

   public boolean parseSgrass(String[] properties, ImageStore imageStore) {
      if (properties.length == Functions.SGRASS_NUM_PROPERTIES) {
         Point pt = new Point(Integer.parseInt(properties[Functions.SGRASS_COL]),
                 Integer.parseInt(properties[Functions.SGRASS_ROW]));
         Entity entity = createSgrass(properties[Functions.SGRASS_ID],
                 pt,
                 Integer.parseInt(properties[Functions.SGRASS_ACTION_PERIOD]),
                 imageStore.getImageList(Functions.SGRASS_KEY));
         this.tryAddEntity(entity);
      }

      return properties.length == Functions.SGRASS_NUM_PROPERTIES;
   }

   public boolean parseOcto(String[] properties, ImageStore imageStore) {
      if (properties.length == Functions.OCTO_NUM_PROPERTIES) {
         Point pt = new Point(Integer.parseInt(properties[Functions.OCTO_COL]),
                 Integer.parseInt(properties[Functions.OCTO_ROW]));
         Entity entity = createOctoNotFull(properties[Functions.OCTO_ID],
                 Integer.parseInt(properties[Functions.OCTO_LIMIT]),
                 pt,
                 Integer.parseInt(properties[Functions.OCTO_ACTION_PERIOD]),
                 Integer.parseInt(properties[Functions.OCTO_ANIMATION_PERIOD]),
                 imageStore.getImageList(Functions.OCTO_KEY));
         this.tryAddEntity(entity);
      }

      return properties.length == Functions.OCTO_NUM_PROPERTIES;
   }

   public boolean parseObstacle(String[] properties, ImageStore imageStore) {
      if (properties.length == Functions.OBSTACLE_NUM_PROPERTIES) {
         Point pt = new Point(
                 Integer.parseInt(properties[Functions.OBSTACLE_COL]),
                 Integer.parseInt(properties[Functions.OBSTACLE_ROW]));
         Entity entity = createObstacle(properties[Functions.OBSTACLE_ID],
                 pt, imageStore.getImageList(Functions.OBSTACLE_KEY));
         this.tryAddEntity(entity);
      }

      return properties.length == Functions.OBSTACLE_NUM_PROPERTIES;
   }

   public Entity createCrab(String id, Point position,
                                   int actionPeriod, int animationPeriod, List<PImage> images)
   {
      return new Entity(EntityKind.CRAB, id, position, images,
              0, 0, actionPeriod, animationPeriod);
   }

   public Entity createObstacle(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OBSTACLE, id, position, images,
              0, 0, 0, 0);
   }

   public boolean parseFish(String[] properties, ImageStore imageStore) {
      if (properties.length == Functions.FISH_NUM_PROPERTIES) {
         Point pt = new Point(Integer.parseInt(properties[Functions.FISH_COL]),
                 Integer.parseInt(properties[Functions.FISH_ROW]));
         Entity entity = createFish(properties[Functions.FISH_ID],
                 pt, Integer.parseInt(properties[Functions.FISH_ACTION_PERIOD]),
                 imageStore.getImageList(Functions.FISH_KEY));
         this.tryAddEntity(entity);
      }
      return properties.length == Functions.FISH_NUM_PROPERTIES;
   }


   public Entity createAtlantis(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.ATLANTIS, id, position, images,
              0, 0, 0, 0);
   }

   public Entity createFish(String id, Point position, int actionPeriod,
                            List<PImage> images)
   {
      return new Entity(EntityKind.FISH, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   public Entity createOctoFull(String id, int resourceLimit,
                                       Point position, int actionPeriod, int animationPeriod,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_FULL, id, position, images,
              resourceLimit, resourceLimit, actionPeriod, animationPeriod);
   }

   public Entity createOctoNotFull(String id, int resourceLimit,
                                          Point position, int actionPeriod, int animationPeriod,
                                          List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_NOT_FULL, id, position, images,
              resourceLimit, 0, actionPeriod, animationPeriod);
   }

   public Entity createSgrass(String id, Point position, int actionPeriod,
                                     List<PImage> images)
   {
      return new Entity(EntityKind.SGRASS, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   public Background getBackgroundCell(Point pos)
   {
      return this.background[pos.y][pos.x];
   }

   public void tryAddEntity(Entity entity)
   {
      if (this.isOccupied(entity.position))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      this.addEntity(entity);
   }

   public boolean withinBounds( Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
              pos.x >= 0 && pos.x < this.numCols;
   }

   public Entity getOccupancyCell(Point pos)
   {
      return this.occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos,
                                       Entity entity)
   {
      this.occupancy[pos.y][pos.x] = entity;
   }

   public void setBackground(Point pos,
                                    Background background)
   {
      if (this.withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public void setBackgroundCell(Point pos,
                                        Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(this.getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public boolean isOccupied(Point pos)
   {
      return this.withinBounds(pos) &&
              this.getOccupancyCell(pos) != null;
   }

   public void addEntity(Entity entity)
   {
      if (this.withinBounds(entity.position))
      {
         this.setOccupancyCell(entity.position, entity);
         this.entities.add(entity);
      }
   }


   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.position;
      if (this.withinBounds(pos) && !pos.equals(oldPos))
      {
         this.setOccupancyCell(oldPos, null);
         this.removeEntityAt(pos);
         this.setOccupancyCell(pos, entity);
         entity.position = pos;
      }
   }

   public void removeEntity(Entity entity)
   {
      this.removeEntityAt(entity.position);
   }

   public void removeEntityAt(Point pos)
   {
      if (this.withinBounds(pos)
              && this.getOccupancyCell(pos) != null)
      {
         Entity entity = this.getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.position = new Point(-1, -1);
         this.entities.remove(entity);
         this.setOccupancyCell(pos, null);
      }
   }

}
