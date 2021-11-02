import java.util.Comparator;

final class EventComparator
   implements Comparator<Event>
{
   public int compare(Event lft, Event rht)
   {
      return (int)(lft.get_time() - rht.get_time());
   }
}
