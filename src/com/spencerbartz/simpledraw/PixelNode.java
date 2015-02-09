package com.spencerbartz.simpledraw;

import java.awt.Color;

public class PixelNode
{
	private int x = -1;
	private int y = -1;
	private Color c;
	
	public PixelNode(int x, int y, Color c)
	{
		this.x = x;
		this.y = y;
		this.c = c;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public Color getColor()
	{
		return c;
	}
	
	public void setColor(Color newColor)
	{
		c = newColor;
	}
}
