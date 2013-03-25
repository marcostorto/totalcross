/*********************************************************************************
 *  TotalCross Software Development Kit                                          *
 *  Copyright (C) 2000-2012 SuperWaba Ltda.                                      *
 *  All Rights Reserved                                                          *
 *                                                                               *
 *  This library and virtual machine is distributed in the hope that it will     *
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                         *
 *                                                                               *
 *  This file is covered by the GNU LESSER GENERAL PUBLIC LICENSE VERSION 3.0    *
 *  A copy of this license is located in file license.txt at the root of this    *
 *  SDK or can be downloaded here:                                               *
 *  http://www.gnu.org/licenses/lgpl-3.0.txt                                     *
 *                                                                               *
 *********************************************************************************/



package totalcross.ui.chart;

import totalcross.sys.*;
import totalcross.ui.*;
import totalcross.ui.font.Font;
import totalcross.ui.gfx.*;
import totalcross.util.Vector;

/** The base class of all chart class.
 * @see ColumnChart
 * @see LineChart
 * @see PieChart
 * @see XYChart
 * @since TotalCross 1.0
 */
public class Chart extends Control
{
   /** Indicates a 3D chart. Default is 2D.
    * @see #type
    */
   public static final int IS_3D = 1; // guich@tc110_20

   /** Indicates a horizontal gradient.
   * @see #type
   */
   public static final int GRADIENT_HORIZONTAL = 2; // guich@tc110_20

   /** Indicates a vertical gradient.
    * @see #type
    */
   public static final int GRADIENT_VERTICAL = 4; // guich@tc110_20

   /** Indicates a dark gradient. The default is a bright gradient.
    * @see #type
    */
   public static final int GRADIENT_DARK = 8; // guich@tc110_20

   /** Indicates to invert the gradient direction.
    * @see #type
    */
   public static final int GRADIENT_INVERT = 16; // guich@tc110_20

   /** The chart type. The values can be OR'ed together. It applies to Column and Pie charts.
    * For example:
    * <pre>
    * chart.type = Chart.IS_3D | Chart.GRADIENT_HORIZONTAL | Chart.GRADIENT_DARK;
    * </pre>
    * @see #IS_3D
    * @see #GRADIENT_HORIZONTAL
    * @see #GRADIENT_VERTICAL
    * @see #GRADIENT_DARK
    * @see #GRADIENT_INVERT
    */
   public int type;

   /** This chart's title */
   protected String title;

   /** The series that are currently added to this chart */
   public Vector series = new Vector();

   /** The value for the origin of the X axis */
   protected double xAxisMinValue;

   /** The value for the end of the X axis */
   protected double xAxisMaxValue;

   /** The number of subdivisions of the X axis */
   protected int xAxisSteps;

   /** The categories of the X axis (may be null if X is a value axis) */
   protected String[] xAxisCategories;

   /** The value for the origin of the Y axis */
   protected double yAxisMinValue;

   /** The value for the end of the Y axis */
   protected double yAxisMaxValue=100; // guich@tc100b5_56: initialize to something if the user don't call setYAxis

   /** The number of subdivisions of the Y axis */
   protected int yAxisSteps=10; // guich@tc100b5_56

   /** The relative screen position of the X axis origin */
   protected int xAxisX1;

   /** The relative screen position of the X axis end */
   protected int xAxisX2;

   /** The relative screen position of the Y axis origin */
   protected int yAxisY1;

   /** The relative screen position of the Y axis end */
   protected int yAxisY2;

   /** This chart's empty border dimensions */
   protected Insets border = new Insets();

   /** Flag to indicate whether the title must be painted */
   public boolean showTitle;

   /** Flag to indicate whether the legend must be painted */
   public boolean showLegend;

   /** Flag to indicate whether the categories must be painted */
   public boolean showCategories;

   /** Flag to indicate whether the y values must be painted */
   public boolean showYValues; // guich@tc110_75

   /** Flag to indicate whether the X grids must be painted */
   public boolean showVGrids;

   /** Flag to indicate whether the Y grids must be painted */
   public boolean showHGrids;

   /** The number of decimal places to show the data of the X axis. Defaults to 3. */
   public int xDecimalPlaces = 3;

   /** The number of decimal places to show the data of the Y axis. Defaults to 3. */
   public int yDecimalPlaces = 3;

   /** The position of the legend. Can be one of: RIGHT (default), LEFT, TOP, BOTTOM. */
   public int legendPosition = RIGHT;

   /** Perspective distance for the Legend. */
   public int legendPerspective = 3;

   /** Flag that indicates if the axis must be drawn. Defaults to true. */
   protected boolean drawAxis=true;

   /** Values that may be shown with the legend. */
   protected String[] legendValues; // guich@tc110_78

   protected Rect clientRect = new Rect();

   private String[] seriesNames = new String[0];
   private Insets ci = new Insets();
   
   /** Defines a value that will be used as the y-value width.
    * @since TotalCross 2.0 
    */
   public int yValuesSize;

   /**
    * Sets this chart's title
    * @param title the new title
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return this chart's title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * Sets the X axis as a value axis given the minimum (its origin) and
    * the maximum values to be used
    * @param min the minimum value (the axis origin)
    * @param max the maximum value (the axis end)
    * @param steps the number of subdivisions of the axis
    */
   public void setXAxis(double min, double max, int steps)
   {
      xAxisCategories = null; // disable categories
      xAxisMinValue = min;
      xAxisMaxValue = max;
      xAxisSteps = steps;
   }

   /**
    * Sets the X axis as a category axis
    * @param categories the categories' names
    */
   public void setXAxis(String[] categories)
   {
      xAxisCategories = categories;
      xAxisMinValue = 0;
      xAxisMaxValue = categories.length;
      xAxisSteps = categories.length;
   }

   /**
    * Sets the Y axis given the minimum (its origin) and the maximum values
    * to be used
    * @param min the minimum value (the axis origin)
    * @param max the maximum value (the axis end)
    * @param steps the number of subdivisions of the axis
    */
   public void setYAxis(double min, double max, int steps)
   {
      yAxisMinValue = min;
      yAxisMaxValue = max;
      yAxisSteps = steps;
   }

   private int getTotalSize(String []names)
   {
      int t = 0;
      for (int i =0; i < names.length; i++)
         t += fm.stringWidth(names[i]);
      return t;
   }

   protected void getCustomInsets(Insets r)
   {
   }

   /**
    * Draws the chart's basic features.
    * @param g The graphics object.
    */
   protected boolean draw(Graphics g)
   {
      boolean is3d = (type & IS_3D) != 0;
      int sMaxLen = 0;
      int sCount = series.size();
      if (sCount != seriesNames.length)
         seriesNames = new String[sCount];

      for (int i = 0; i < sCount; i ++)
         seriesNames[i] = ((Series) series.items[i]).name;

      boolean drawTitle = showTitle && title != null;
      boolean drawCategories = showCategories && xAxisCategories != null;
      boolean drawLegend = showLegend && sCount > 0;

      int top = border.top;
      int left = border.left;
      int bottom = border.bottom;
      int right = border.right;
      double incY = (yAxisMaxValue - yAxisMinValue) / yAxisSteps;
      boolean lr = legendPosition == LEFT || legendPosition == RIGHT;
      int sqWH = fmH - 6, sqOff = (fmH - sqWH) / 2;

      if (drawTitle)
         top += fmH;

      if (drawLegend)
      {
         sMaxLen += lr ? fm.getMaxWidth(seriesNames, 0, sCount) : (sqWH + 4) * sCount + getTotalSize(seriesNames);
         if (legendValues != null)
               sMaxLen += lr ? fm.getMaxWidth(legendValues, 0, sCount) : getTotalSize(legendValues);
         int ww = sMaxLen + 32;
         if (is3d)
            ww += Math.abs(legendPerspective);
         switch (legendPosition)
         {
            case RIGHT:
               right += ww;
               break;
            case LEFT:
               left += ww;
               break;
            case TOP:
               top += fmH+6;
               if (is3d)
                  top += Math.abs(legendPerspective);
               break;
            case BOTTOM:
               bottom += fmH+6;
               if (is3d)
                  bottom += Math.abs(legendPerspective);
               break;
         }
      }
      ci.top = ci.bottom = ci.left = ci.right = 0;
      getCustomInsets(ci);
      top += ci.top;
      bottom += ci.bottom;
      left += ci.left;
      right += ci.right;

      if (showYValues)
      {
         int yvalW = yValuesSize;
         for (double v = yAxisMinValue; v <= yAxisMaxValue; v += incY)
            yvalW = Math.max(yvalW , fm.stringWidth(Convert.toCurrencyString(v,yDecimalPlaces)));
         left += yvalW;
         top += fm.ascent/2;
         bottom += fm.ascent/2;
         if (drawCategories)
            bottom += fmH/2-1;
      }
      else
      if (drawCategories)
         bottom += fmH - 3;

      xAxisX1 = left + 3;
      if (xAxisX1 < 0 || xAxisX1 >= width) // validate
         return false;

      xAxisX2 = width - right - 1;
      if (xAxisX2 < 0 || xAxisX2 >= width || xAxisX2 <= xAxisX1) // validate
         return false;

      yAxisY1 = height - bottom - 4;
      if (yAxisY1 < 0 || yAxisY1 >= height) // return false;
         return false;

      yAxisY2 = top;
      if (yAxisY2 < 0 || yAxisY2 >= height || yAxisY2 >= yAxisY1) // validate
         return false;

      g.foreColor = Color.BLACK;
      g.backColor = backColor;
      if (!transparentBackground)
         g.fillRect(0, 0, width, height); // clear

      if (drawAxis)
      {
         g.drawLine(xAxisX1, yAxisY1, xAxisX2, yAxisY1); // draw X axis
         g.drawLine(xAxisX1, yAxisY2, xAxisX1, yAxisY1); // draw Y axis
      }

      double inc = (xAxisMaxValue - xAxisMinValue) / xAxisSteps;
      double val = xAxisMinValue;

      for (int i = 0; i <= xAxisSteps; i ++, val += inc)
      {
         int pos = getXValuePos(val);
         if (drawAxis) g.drawLine(pos, yAxisY1, pos, yAxisY1 + 3);

         if (showVGrids && pos != xAxisX1) // draw vertical grids
            g.drawDots(pos, yAxisY1, pos, yAxisY2);

         if (drawCategories && i < xAxisCategories.length) // draw category
         {
            int tW = fm.stringWidth(xAxisCategories[i]);
            pos += ((getXValuePos(val + inc) - pos) - tW) / 2;
            g.drawText(xAxisCategories[i], pos, yAxisY1, textShadowColor != -1, textShadowColor);
         }
      }

      val = yAxisMinValue;
      // adjust the number of steps depending on the number's height
      int ySteps = yAxisSteps;
      while (showYValues && (getYValuePos(incY)-getYValuePos(incY*2)) < fm.ascent)
      {
         ySteps--;
         incY = (yAxisMaxValue - yAxisMinValue) / ySteps;
      }
      for (int i = 0; i <= ySteps; i ++, val += incY)
      {
         int pos = getYValuePos(val);
         if (drawAxis) g.drawLine(xAxisX1, pos, xAxisX1 - 3, pos);
         if (showYValues && pos != yAxisY1)
         {
            String s = Convert.toCurrencyString(val,yDecimalPlaces);
            g.drawText(s, xAxisX1 - fm.stringWidth(s) - 3, pos-fmH/2, textShadowColor != -1, textShadowColor);
         }
         if (showHGrids && pos != yAxisY1) // draw horizontal grids
            g.drawDots(xAxisX1, pos, xAxisX2, pos);
      }

      if (drawTitle)
      {
         Font fp = this.font;
         Font fb = fp.asBold();

         g.setFont(fb);
         g.drawText(title, (width - fb.fm.stringWidth(title)) / 2, 0, textShadowColor != -1, textShadowColor);
         g.setFont(fp); // return to previous font
      }
      if (drawLegend)
      {
         int x,y,w,h;
         if (lr)
         {
            w = sMaxLen + sqWH + 10;
            h = fmH * sCount;
            x = legendPosition == RIGHT ? xAxisX2 + 5 : 0;
            y = yAxisY2 + (yAxisY1 - yAxisY2 - h) / 2;
         }
         else
         {
            w = sMaxLen + 6;
            h = fmH + 2;
            x = (this.width-w)/2;
            if (legendPosition == TOP)
               y = drawTitle ? fmH+3 : 0;
            else
               y = this.height - (fmH + 2 + (is3d ? Math.abs(legendPerspective):0));
         }

         if (is3d)
         {
            if (legendPerspective < 0 && lr)
               x -= legendPerspective;
            g.backColor = Color.interpolate(backColor,0);
            g.fillRect(x + legendPerspective, y + Math.abs(legendPerspective), w, h);
         }
         g.backColor = backColor;
         g.fillRect(x, y, w, h);
         g.drawRect(x, y, w, h);

         x += 3;
         for (int i = 0; i < sCount; i ++)
         {
            g.backColor = ((Series) series.items[i]).color;
            g.fillRect(x, y + sqOff, sqWH, sqWH);
            g.drawRect(x, y + sqOff, sqWH, sqWH);
            String s = seriesNames[i];
            if (legendValues != null)
               s = s.concat(legendValues[i]);

            g.drawText(s, x + sqWH + 2, y, textShadowColor != -1, textShadowColor);
            if (lr)
               y += fmH;
            else
               x += fm.stringWidth(s)+4+sqWH;
         }

         g.backColor = backColor; // back to original back color
      }
      clientRect.set(left, top, this.width - right-left, this.height - top-bottom);

      return true;
   }

   /**
    * Draws a box to display a text in the chart area
    * @param text the text to be displayed
    * @param refX the X position of the anchor point
    * @param refY the Y position of the anchor point
    */
   protected void drawTextBox(Graphics g, int refX, int refY, String text)
   {
      Font f = this.font;

      int boxW = f.fm.stringWidth(text) + 4;
      int boxH = f.fm.height + 2;
      int boxX = refX;
      int boxY = refY - boxH - 2;

      int dif = (boxX + boxW) - width;
      if (dif > 0)
         boxX -= dif;
      dif = (boxY + boxH) - height;
      if (dif > 0)
         boxY -= dif;

      if (boxX < 0)
         boxX = 0;
      if (boxY < 0)
         boxY = 0;

      g.foreColor = Color.BLACK;
      g.backColor = backColor;

      g.fillRect(boxX, boxY, boxW, boxH);
      g.drawRect(boxX, boxY, boxW, boxH);
      g.drawText(text, boxX + 2, boxY + 1, textShadowColor != -1, textShadowColor);
   }

   /**
    * Calculates the screen position of a value in the X axis according to
    * its current minimum and maximum values. This method assumes that the
    * X axis has the same dimensions that it assumed on the last call to
    * <code>draw</code>
    * @param value the value
    * @return the screen position of the value
    * @see #draw(Graphics)
    */
   public int getXValuePos(double value)
   {
      return xAxisX1 + (int)Math.round((value - xAxisMinValue) / (xAxisMaxValue - xAxisMinValue) * (xAxisX2 - xAxisX1));
   }

   /**
    * Calculates the screen position of a value in the Y axis according to
    * its current minimum and maximum values. This method assumes that the
    * Y axis has the same dimensions that it assumed on the last call to
    * <code>draw</code>
    * @param value the value
    * @return the screen position of the value
    * @see #draw(Graphics)
    */
   public int getYValuePos(double value)
   {
      return yAxisY1 - (int)Math.round((value - yAxisMinValue) / (yAxisMaxValue - yAxisMinValue) * (yAxisY1 - yAxisY2));
   }
}