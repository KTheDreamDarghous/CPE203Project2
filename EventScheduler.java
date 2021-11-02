import processing.core.PImage;

import java.util.*;

/*
EventScheduler: ideally our way of controlling what happens in our virtual world
 */

final class EventScheduler {
   public PriorityQueue<Event> eventQueue;
   public Map<Entity, List<Event>> pendingEvents;
   private final double timeScale;

   public EventScheduler(double timeScale) {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }

   public void removePendingEvent(Event event)
   {
      List<Event> pending = this.pendingEvents.get(event.entity);

      if (pending != null)
      {
         pending.remove(event);
      }
   }


   public Action createAnimationAction(Entity entity, int repeatCount)
   {
      return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
   }

   public Action createActivityAction(Entity entity, WorldModel world,
                                      ImageStore imageStore)
   {
      return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
   }

/*
   public void scheduleActions(Entity entity, WorldModel world, ImageStore imageStore)
   {
      switch (entity.kind)
      {
         case OCTO_FULL:
            scheduleEvent(entity,
                    createActivityAction(entity, world, imageStore),
                    entity.get_actionPeriod());
            scheduleEvent(entity, createAnimationAction(entity, 0),
                    entity.getAnimationPeriod());
            break;

         case OCTO_NOT_FULL:
            scheduleEvent(entity,
                    createActivityAction(entity, world, imageStore),
                    entity.get_actionPeriod());
            scheduleEvent(entity,
                    createAnimationAction(entity, 0), entity.getAnimationPeriod());
            break;

         case FISH:
            scheduleEvent(entity,
                    createActivityAction(entity, world, imageStore),
                    entity.get_actionPeriod());
            break;

         case CRAB:
            scheduleEvent(entity,
                    createActivityAction(entity, world, imageStore),
                    entity.get_actionPeriod());
            scheduleEvent(entity,
                    createAnimationAction(entity, 0), entity.getAnimationPeriod());
            break;

         case QUAKE:
            scheduleEvent(entity,
                    createActivityAction(entity, world, imageStore),
                    entity.get_actionPeriod());
            scheduleEvent(entity,
                    createAnimationAction(entity, Functions.QUAKE_ANIMATION_REPEAT_COUNT), // Functions. used
                    entity.getAnimationPeriod());
            break;

         case SGRASS:
            scheduleEvent(entity,
                    createActivityAction(entity, world, imageStore),
                    entity.get_actionPeriod());
            break;
         case ATLANTIS:
            scheduleEvent(entity,
                    createAnimationAction(entity, Functions.ATLANTIS_ANIMATION_REPEAT_COUNT), // Functions. used
                    entity.getAnimationPeriod());
            break;

         default:
      }
   }
*/
   public Entity createQuake(Point position, List<PImage> images) // Functions. used below
   {
      return new Entity(EntityKind.QUAKE, Functions.QUAKE_ID, position, images,
              0, 0, Functions.QUAKE_ACTION_PERIOD, Functions.QUAKE_ANIMATION_PERIOD);
   }


   public static Optional<Entity> findNearest(WorldModel world, Point pos,
                                              EntityKind kind) {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : world.entities) {
         if (entity.kind == kind) {
            ofType.add(entity);
         }
      }
      return pos.nearestEntity(ofType, pos);
   }

   public void scheduleEvent(Entity entity, Action action, long afterPeriod)
   {
      long time = System.currentTimeMillis() +
              (long)(afterPeriod * this.timeScale);
      Event event = new Event(action, time, entity);

      this.eventQueue.add(event);

      // update list of pending events for the given entity
      List<Event> pending = this.pendingEvents.getOrDefault(entity,
              new LinkedList<>());
      pending.add(event);
      this.pendingEvents.put(entity, pending);
   }

   public boolean moveToCrab(Entity crab, WorldModel world,
                                    Entity target)
   {
      if (crab.position.adjacent(crab.position, target.position))
      {
         world.removeEntity(target);
         unscheduleAllEvents(target);
         return true;
      }
      else
      {
         Point nextPos = crab.nextPositionCrab(world, target.position);

         if (!crab.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(crab, nextPos);
         }
         return false;
      }
   }

   public void unscheduleAllEvents(Entity entity)
   {
      List<Event> pending = this.pendingEvents.remove(entity);

      if (pending != null)
      {
         for (Event event : pending)
         {
            this.eventQueue.remove(event);
         }
      }
   }

}
