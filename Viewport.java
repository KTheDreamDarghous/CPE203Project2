/*
Viewport ideally helps control what part of the world we are looking at for drawing only what we see
Includes helpful helper functions to map between the viewport and the real world
 */


import java.awt.*;

final class Viewport
{
   private int row;
   private int col;
   private int numRows;
   private int numCols;

   public Viewport(int numRows, int numCols)
   {
      this.numRows = numRows;
      this.numCols = numCols;
   }

   // Getters and Setters
   public int get_row() {
      return row;
   }
   public int get_col() {
      return col;
   }
   public int get_numRows() {
      return numRows;
   }
   public int get_numCols() {
      return numCols;
   }

   public void shift(int col, int row)
   {
      this.col = col;
      this.row = row;
   }

   public boolean contains(Viewport viewport, Point p)
   {
      return p.y >= viewport.row && p.y < viewport.row + viewport.numRows &&
              p.x >= viewport.col && p.x < viewport.col + viewport.numCols;
   }

   public Point viewportToWorld(int col, int row)
   {
      return new Point(col + this.col, row + this.row);
   }

   public Point worldToViewport(int col, int row)
   {
      return new Point(col - this.col, row - this.row);
   }

}
