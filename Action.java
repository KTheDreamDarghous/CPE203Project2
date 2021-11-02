/*
Action: ideally what our various entities might do in our virutal world
 */

import processing.core.PImage;

import java.util.List;
import java.util.Optional;

final class Action
{
   public ActionKind kind;
   public Entity entity;
   public WorldModel world;
   public ImageStore imageStore;
   private final int repeatCount;

   public Action(ActionKind kind, Entity entity, WorldModel world,
      ImageStore imageStore, int repeatCount)
   {
      this.kind = kind;
      this.entity = entity;
      this.world = world;
      this.imageStore = imageStore;
      this.repeatCount = repeatCount;
   }

   public void executeAction(EventScheduler scheduler)
   {
      switch (this.kind)
      {
         case ACTIVITY:
            executeActivityAction(scheduler);
            break;

         case ANIMATION:
            executeAnimationAction(scheduler);
            break;
      }
   }

   public void executeAnimationAction(EventScheduler scheduler)
   {
      this.entity.nextImage();

      if (this.repeatCount != 1)
      {
         scheduler.scheduleEvent(this.entity,
            scheduler.createAnimationAction(this.entity,
               Math.max(this.repeatCount - 1, 0)),
            this.entity.getAnimationPeriod());
      }
   }

   public void executeActivityAction(EventScheduler scheduler)
   {
      switch (this.entity.kind)
      {
         case OCTO_FULL:
            this.entity.executeOctoFullActivity(world, imageStore, scheduler);
            break;

         case OCTO_NOT_FULL:
            this.entity.executeOctoNotFullActivity(world, imageStore, scheduler);
            break;

         case FISH:
            this.entity.executeFishActivity(world, imageStore, scheduler);
            break;

         case CRAB:
            this.entity.executeCrabActivity(world, imageStore, scheduler);
            break;

         case QUAKE:
            this.entity.executeQuakeActivity(world,imageStore, scheduler);
            break;

         case SGRASS:
            this.entity.executeSgrassActivity(world, imageStore, scheduler);
            break;

         case ATLANTIS:
            this.entity.executeAtlantisActivity(world,imageStore, scheduler );
            break;

         default:
            throw new UnsupportedOperationException(
                    String.format("executeActivityAction not supported for %s",
                            this.entity.kind));
      }
   }

   /*public void scheduleActions( EventScheduler scheduler,
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

*/
 /*
   public void transformFull(EventScheduler scheduler)
   {
      Entity octo = world.createOctoNotFull(entity.id, entity.get_resourceLimit(),
              entity.position, entity.get_actionPeriod(), entity.get_animationPeriod(),
              entity.images);

      entity.removeEntity(world);
      scheduler.unscheduleAllEvents(entity);

      world.addEntity(octo);
      scheduler.scheduleActions(octo, world, imageStore);
   }

   public boolean transformNotFull(EventScheduler scheduler)
   {
      if (entity.get_resourceCount() >= entity.get_resourceLimit())
      {
         Entity octo = world.createOctoFull(entity.id, entity.get_resourceLimit(),
                 entity.position, entity.get_actionPeriod(), entity.get_animationPeriod(),
                 entity.images);

         entity.removeEntity(world);
         scheduler.unscheduleAllEvents(entity);

         world.addEntity(octo);
         scheduler.scheduleActions(octo, world, imageStore);

         return true;
      }

      return false;
   }
   */
/*
   public boolean moveToNotFull(Entity octo, EventScheduler scheduler,
                                Entity target)
   {
      if (octo.position.adjacent(octo.position, target.position))
      {
         octo.set_resourceCount(octo.get_resourceCount() + 1);
         target.removeEntity(world);
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

   public boolean moveToFull(Entity octo, EventScheduler scheduler,
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
*/
}