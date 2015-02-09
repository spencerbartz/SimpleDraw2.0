package com.spencerbartz.simpledraw;


import java.awt.Graphics;
import java.awt.Graphics2D;

public class SpencerGraphics
{
	private Graphics2D gfx2d;
	
	public SpencerGraphics(Graphics g)
	{
		gfx2d = (Graphics2D) g;
	}
	
	public SpencerGraphics(Graphics2D g)
	{
		gfx2d = g;
	}
	
	//Draw a rectangle using two sets of coordinates (instead of x, y, width, and height)
	//This method works by drawing four lines and accounts for the cases where the 2nd
	//set of coordinates (x2, y2 or both) contain lesser values than the first set (x1, y1).
	public void drawRectangle(int x1, int y1, int x2, int y2)
	{
		//draw left side
		gfx2d.drawLine(x1, y1, x1, y2);
		
		//draw top
		gfx2d.drawLine(x1, y1, x2, y1);
		
		//draw bottom
		gfx2d.drawLine(x1, y2, x2, y2);
		
		//draw right
		gfx2d.drawLine(x2, y1, x2, y2);
	}
	
	//Fill a rectangle using two sets of coordinates (instead of  x,y, width, and height)
	//This method works by drawing a series of lines and accounts for the cases where the 2nd
	//set of coordinates (x2, y2 or both) contain lesser values than the first set (x1, y1).
	public void fillRectangle(int x1, int y1, int x2, int y2)
	{
		if(y2 < y1)
		{
			for(int i = 0; i < y1 - y2; i++)
				gfx2d.drawLine(x1, y1 - i, x2, y1 - i);
		}
		else
		{
			for(int i = 0; i < y2 - y1; i++)
				gfx2d.drawLine(x1, y1 + i, x2, y1+ i);
		}
	}
}
