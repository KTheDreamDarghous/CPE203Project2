import processing.core.PApplet;
import processing.core.PImage;

import java.awt.*;
import java.util.Optional;

/*
WorldView ideally mostly controls drawing the current part of the whole world
that we can see based on the viewport
*/

final class WorldView
{
   public PApplet screen;
   public WorldModel world;
   private final int tileWidth;
   private final int tileHeight;
   public Viewport viewport;

   public WorldView(int numRows, int numCols, PApplet screen, WorldModel world,
      int tileWidth, int tileHeight)
   {
      this.screen = screen;
      this.world = world;
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.viewport = new Viewport(numRows, numCols);
   }

   public void drawViewport()
   {
      this.drawBackground();
      this.drawEntities();
   }

   public void drawBackground()
   {
      for (int row = 0; row < this.viewport.get_numRows(); row++)
      {
         for (int col = 0; col < this.viewport.get_numCols(); col++)
         {
            Point worldPoint = viewport.viewportToWorld(col, row);
            Optional<PImage> image = getBackgroundImage(this.world,
                    worldPoint);
            if (image.isPresent())
            {
               this.screen.image(image.get(), col * this.tileWidth,
                       row * this.tileHeight);
            }
         }
      }
   }

   public void drawEntities()
   {
      for (Entity entity : this.world.entities)
      {
         Point pos = entity.position;

         if (viewport.contains(this.viewport, pos))
         {
            Point viewPoint = viewport.worldToViewport(pos.x, pos.y);
            this.screen.image(getCurrentImage(entity),
                    viewPoint.x * this.tileWidth, viewPoint.y * this.tileHeight);
         }
      }
   }

   public void shiftView(int colDelta, int rowDelta)
   {
      int newCol = Functions.clamp(this.viewport.get_col() + colDelta, 0,
              this.world.get_numCols() - this.viewport.get_numCols());
      int newRow = Functions.clamp(this.viewport.get_row() + rowDelta, 0,
              this.world.get_numRows() - this.viewport.get_numRows());

      this.viewport.shift(newCol, newRow);
   }

   public Optional<PImage> getBackgroundImage(WorldModel world, Point pos)
   {
      if (world.withinBounds(pos))
      {
         return Optional.of(getCurrentImage(world.getBackgroundCell(pos)));
      }
      else
      {
         return Optional.empty();
      }
   }

   public static PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Background)
      {
         return ((Background)entity).images
                 .get(((Background)entity).get_imageIndex());
      }
      else if (entity instanceof Entity)
      {
         return ((Entity)entity).images.get(((Entity)entity).get_imageIndex());
      }
      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         entity));
      }
   }

}
