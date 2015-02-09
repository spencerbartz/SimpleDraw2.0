package com.spencerbartz.simpledraw;


import java.util.Vector;

import javax.swing.JFrame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class TestWindow extends JFrame
{
	int width = 1000;
	int height = 500;
	Vector <BufferedImage> images;
	Stack <BufferedImage> undo;
	Stack <BufferedImage> redo;
	
	public TestWindow(Vector <BufferedImage> images)
	{
		this.images = images;
		setSize(width, height);
		setLocation(0, 0);
		setVisible(true);
	}
	
	public TestWindow(Stack <BufferedImage> undo, Stack <BufferedImage> redo)
	{
		this.undo = undo;
		this.redo = redo;
		setSize(width, height);
		setLocation(0, 300);
		setVisible(true);
	}
	
	public void paint(Graphics g)
	{
		Graphics2D gfx = (Graphics2D) g;
		int x = 0;
		
		gfx.fillRect(0, 0, width, height);
		
		if(images != null)
		{
			for(int i = 0; i < images.size(); i++)
			{
				gfx.drawImage(images.get(i), x, 0, this);
				x += 250;
			}
		}
		
		if(undo != null && redo != null)
		{
			Object undoAry [] = undo.toArray();
			for(int i = 0; i < undoAry.length; i++)
			{
				gfx.drawImage(((BufferedImage)undoAry[i]), x, 0, this);
				x += 250;
			}
			
			x = 0;
			
			Object redoAry [] = redo.toArray();
			for(int i = 0; i < redoAry.length; i++)
			{
				gfx.drawImage(((BufferedImage)redoAry[i]), x, 250, this);
				x += 250;
			}
		}
	}
}
