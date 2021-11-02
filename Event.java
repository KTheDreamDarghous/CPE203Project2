import processing.core.PImage;

import java.util.List;
import java.util.Scanner;

final class Event {
   public Action action;
   private long time;
   public Entity entity;


   public Event(Action action, long time, Entity entity) {
      this.action = action;
      this.time = time;
      this.entity = entity;
   }

   public long get_time() {
      return time;
   }

   public static void updateOnTime(EventScheduler scheduler, long time)
   {
      while (!scheduler.eventQueue.isEmpty() &&
              scheduler.eventQueue.peek().time < time)
      {
         Event next = scheduler.eventQueue.poll();

         scheduler.removePendingEvent(next);

         next.action.executeAction(scheduler);
      }
   }

}