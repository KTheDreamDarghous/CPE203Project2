import java.awt.*;
import java.util.List;
import java.util.Optional;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


final class Entity
{
   public EntityKind kind;
   public String id;
   public Point position;
   public List<PImage> images;
   private int imageIndex;
   private int resourceLimit;
   private int resourceCount;
   private int actionPeriod;
   private int animationPeriod;

   public Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
   }
   // Getters and Setters
   public int get_imageIndex() {
      return imageIndex;
   }
   public int get_resourceLimit() {
      return resourceLimit;
   }
   public int get_resourceCount() {
      return resourceCount;
   }
   public void set_resourceCount(int val) {
      resourceCount = val;
   }
   public int get_actionPeriod() {
      return actionPeriod;
   }
   public int get_animationPeriod() {
      return animationPeriod;
   }

   public void nextImage()
   {
      this.imageIndex = (this.imageIndex + 1) % this.images.size();
   }

public int getAnimationPeriod()
{
   switch (this.kind)
   {
      case OCTO_FULL:
      case OCTO_NOT_FULL:
      case CRAB:
      case QUAKE:
      case ATLANTIS:
         return this.animationPeriod;
      default:
         throw new UnsupportedOperationException(
                 String.format("getAnimationPeriod not supported for %s",
                         this.kind));
   }
}

   public void executeOctoFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> fullTarget = EventScheduler.findNearest(world, this.position,
              EntityKind.ATLANTIS);

      if (fullTarget.isPresent() &&
              this.moveToFull(this, world, scheduler, fullTarget.get()))
      {
         //at atlantis trigger animation
         scheduleActions(fullTarget.get(), scheduler, world, imageStore);

         //transform to unfull
         this.transformFull( world, scheduler, imageStore);
      }
      else
      {
         scheduler.scheduleEvent(this,
                 scheduler.createActivityAction(this, world, imageStore),
                 this.get_actionPeriod());
      }
   }

   public void executeOctoNotFullActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> notFullTarget = EventScheduler.findNearest(world, this.position,
              EntityKind.FISH);

      if (!notFullTarget.isPresent() ||
              !this.moveToNotFull(this, world, scheduler, notFullTarget.get()) ||
              !this.transformNotFull(world, scheduler, imageStore))
      {
         scheduler.scheduleEvent(this,
                 scheduler.createActivityAction(this, world, imageStore),
                 this.get_actionPeriod());
      }
   }
   public boolean transformNotFull(WorldModel world,
                                          EventScheduler scheduler, ImageStore imageStore)
   {
      if (this.resourceCount >= this.resourceLimit)
      {
         Entity octo = world.createOctoFull(this.id, this.resourceLimit,
                 this.position, this.actionPeriod, this.animationPeriod,
                 this.images);

         world.removeEntity(this);
         scheduler.unscheduleAllEvents(this);

         world.addEntity(octo);
         scheduleActions(octo, scheduler, world, imageStore);

         return true;
      }

      return false;
   }

   public void transformFull( WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      Entity octo = world.createOctoNotFull(this.id, this.resourceLimit,
         this.position, this.actionPeriod, this.animationPeriod,
         this.images);

      world.removeEntity(this);
      scheduler.unscheduleAllEvents(this);

      world.addEntity(octo);
      scheduleActions(octo, scheduler, world, imageStore);
   }


   public void executeFishActivity(WorldModel world,ImageStore imageStore, EventScheduler scheduler)
   {
      Point pos = this.position;  // store current position before removing

      world.removeEntity(this);
      scheduler.unscheduleAllEvents(this);

      Entity crab = world.createCrab(this.id + Functions.CRAB_ID_SUFFIX,
              pos, this.get_actionPeriod() / Functions.CRAB_PERIOD_SCALE,
              Functions.CRAB_ANIMATION_MIN +
                      Functions.rand.nextInt(Functions.CRAB_ANIMATION_MAX - Functions.CRAB_ANIMATION_MIN),
              imageStore.getImageList(Functions.CRAB_KEY));

      world.addEntity(crab);
      scheduleActions(crab, scheduler, world, imageStore);
   }

   public void executeCrabActivity(WorldModel world,ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> crabTarget = EventScheduler.findNearest(world,
              this.position, EntityKind.SGRASS);
      long nextPeriod = this.get_actionPeriod();

      if (crabTarget.isPresent())
      {
         Point tgtPos = crabTarget.get().position;

         if (scheduler.moveToCrab(this, world, crabTarget.get()))
         {
            Entity quake = scheduler.createQuake(tgtPos,
                    imageStore.getImageList(Functions.QUAKE_KEY));

            world.addEntity(quake);
            nextPeriod += this.get_actionPeriod();
            scheduleActions(quake, scheduler, world, imageStore);
         }
      }

      scheduler.scheduleEvent(this,
              scheduler.createActivityAction(this, world, imageStore),
              nextPeriod);
   }

   public void executeQuakeActivity( WorldModel world, ImageStore imageStore,
                                            EventScheduler scheduler)
   {
      scheduler.unscheduleAllEvents( this);
      world.removeEntity(this);
   }


   public void executeAtlantisActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      scheduler.unscheduleAllEvents(this);
      world.removeEntity(this);
   }

   public void executeSgrassActivity( WorldModel world,
                                            ImageStore imageStore, EventScheduler scheduler) {
      Optional<Point> openPt = world.findOpenAround(world, this.position);

      if (openPt.isPresent()) {
         Entity fish = world.createFish(Functions.FISH_ID_PREFIX + this.id,
                 openPt.get(), Functions.FISH_CORRUPT_MIN +
                         Functions.rand.nextInt(Functions.FISH_CORRUPT_MAX - Functions.FISH_CORRUPT_MIN),
                 imageStore.getImageList(Functions.FISH_KEY));
         world.addEntity(fish);
         scheduleActions(fish, scheduler, world, imageStore);
      }
      scheduler.scheduleEvent( this,
              scheduler.createActivityAction(this, world, imageStore),
              this.actionPeriod);
   }
      public void scheduleActions(Entity entity, EventScheduler scheduler,
           WorldModel world, ImageStore imageStore)
      {
         switch (entity.kind)
         {
            case OCTO_FULL:
               scheduler.scheduleEvent(entity,
                       scheduler.createActivityAction(entity, world, imageStore),
                       entity.get_actionPeriod());
               scheduler.scheduleEvent(entity, scheduler.createAnimationAction(entity, 0),
                       entity.getAnimationPeriod());
               break;

            case OCTO_NOT_FULL:
               scheduler.scheduleEvent( entity,
                       scheduler.createActivityAction(entity, world, imageStore),
                       entity.get_actionPeriod());
               scheduler.scheduleEvent( entity,
                       scheduler.createAnimationAction(entity, 0), entity.getAnimationPeriod());
               break;

            case FISH:
               scheduler.scheduleEvent( entity,
                       scheduler.createActivityAction(entity, world, imageStore),
                       entity.get_actionPeriod());
               break;

            case CRAB:
               scheduler.scheduleEvent( entity,
                       scheduler.createActivityAction(entity, world, imageStore),
                       entity.get_actionPeriod());
               scheduler.scheduleEvent( entity,
                       scheduler.createAnimationAction(entity, 0), entity.getAnimationPeriod());
               break;

            case QUAKE:
               scheduler.scheduleEvent(entity,
                       scheduler.createActivityAction(entity, world, imageStore),
                       entity.get_actionPeriod());
               scheduler.scheduleEvent(entity,
                       scheduler.createAnimationAction(entity, Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                       entity.getAnimationPeriod());
               break;

            case SGRASS:
               scheduler.scheduleEvent(entity,
                       scheduler.createActivityAction(entity, world, imageStore),
                       entity.get_actionPeriod());
               break;
            case ATLANTIS:
               scheduler.scheduleEvent( entity,
                       scheduler.createAnimationAction(entity, Functions.ATLANTIS_ANIMATION_REPEAT_COUNT),
                       entity.getAnimationPeriod());
               break;

            default:
         }
      }


   public boolean moveToNotFull(Entity octo, WorldModel world,  EventScheduler scheduler,
                                Entity target)
   {
      if (octo.position.adjacent(octo.position, target.position))
      {
         octo.set_resourceCount(octo.get_resourceCount() + 1);
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = octo.nextPositionOcto(world,target.position);

         if (!octo.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(octo, nextPos);
         }
         return false;
      }
   }

   public boolean moveToFull(Entity octo, WorldModel world, EventScheduler scheduler,
                             Entity target)
   {
      if (octo.position.adjacent(octo.position, target.position))
      {
         return true;
      }
      else
      {
         Point nextPos = octo.nextPositionOcto(world, target.position);

         if (!octo.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(octo, nextPos);
         }
         return false;
      }
   }
//#######
 /*  public Point nextPositionCrab(WorldModel world, Point destPos)
   {
      int horiz = Integer.signum(destPos.x - this.position.x);
      Point newPos = new Point(this.position.x + horiz,
              this.position.y);

      Optional<Entity> occupant = world.getOccupant(newPos);

      if (horiz == 0 ||
              (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
      {
         int vert = Integer.signum(destPos.y - this.position.y);
         newPos = new Point(this.position.x, this.position.y + vert);
         occupant = world.getOccupant(newPos);

         if (vert == 0 ||
                 (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
         {
            newPos = this.position;
         }
      }

      return newPos;
   }
*/
   public Point nextPositionOcto(WorldModel world, Point destPos)
   {
      int horiz = Integer.signum(destPos.x - this.position.x);
      Point newPos = new Point(this.position.x + horiz,
              this.position.y);

      Optional<Entity> occupant = world.getOccupant(newPos);

      if (horiz == 0 || world.isOccupied(newPos))
      {
         int vert = Integer.signum(destPos.y - this.position.y);
         newPos = new Point(this.position.x,
                 this.position.y + vert);

         if (vert == 0 || world.isOccupied(newPos))
         {
            newPos = this.position;
         }
      }

      return newPos;
   }

}