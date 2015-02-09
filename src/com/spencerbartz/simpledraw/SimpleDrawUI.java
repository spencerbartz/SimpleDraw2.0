package com.spencerbartz.simpledraw;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JFrame;

public class SimpleDrawUI extends JFrame implements MouseListener,
		MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	public Toolbar myToolbar;
	private int width = 500;
	private int height = 500;
	private BufferedImage offImage;
	private BufferedImage background;
	private Graphics2D gfx;

	private int lastX = -1;
	private int lastY = -1;
	private SpencerGraphics sg;

	// For drawing shapes
	private int shapeStartX = -1;
	private int shapeStartY = -1;
	private int shapeEndX = -1;
	private int shapeEndY = -1;

	// Stacks for saving undo and redo states
	Stack<BufferedImage> undoStack;
	Stack<BufferedImage> redoStack;

	// set up GUI and register mouse event handler
	public SimpleDrawUI()
	{
		setSize(width, height);

		// create a label and place it in SOUTH of BorderLayout
		getContentPane().add(new Label("Drag the mouse to draw"),
				BorderLayout.SOUTH);
		addMouseListener(this);
		addMouseMotionListener(this);

		// Create offscreen image for double buffering
		offImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		gfx = offImage.createGraphics();
		gfx.setColor(Color.white);
		gfx.fillRect(0, 0, width, height);
		gfx.setStroke(new BasicStroke(12.0f));

		// Use SpencerGraphics for enhanced drawRectangle function.
		sg = new SpencerGraphics(gfx);

		// Create and launch toolbar window
		myToolbar = new Toolbar(this, offImage);
		myToolbar.toFront();

		undoStack = new Stack<BufferedImage>();
		redoStack = new Stack<BufferedImage>();
		
		setVisible(true);
	}
	// draw oval in a 4-by-4 bounding box at the specified
		// location on the window
		public void paint(Graphics g) 
		{
			if(myToolbar.getClearState()) 
			{
				gfx.setColor(Color.white);
				gfx.fillRect(0, 0, offImage.getWidth(), offImage.getHeight());
				myToolbar.resetClearState();
			}
			else if(myToolbar.getUndoState())
			{
				if(!undoStack.isEmpty())
				{
					
					//Before undoing, Save current state of canvas in case we want to redo it
					BufferedImage redoImage = new BufferedImage(offImage.getWidth(), offImage.getHeight(), offImage.getType());
					redoImage.setData(offImage.getData());
					redoStack.push(redoImage);
					
					BufferedImage prevCanvas = undoStack.pop();
					gfx.drawImage(prevCanvas, 0, 0, this);
				}
				
				myToolbar.resetUndoState();
			}
			else if(myToolbar.getRedoState())
			{		
				if(!redoStack.isEmpty())
				{
					
					//Before undoing, Save current state of canvas in case we want to redo it
					BufferedImage undoImage = new BufferedImage(offImage.getWidth(), offImage.getHeight(), offImage.getType());
					undoImage.setData(offImage.getData());
					undoStack.push(undoImage);
					
					BufferedImage nextCanvas = redoStack.pop();
					gfx.drawImage(nextCanvas, 0, 0, this);
				}
				
				myToolbar.resetRedoState();
			}
			
			Graphics2D g2 = ((Graphics2D) g);
			g2.drawImage(offImage, 0, 0, this);
		}

		public BufferedImage getOffscreenImage()
		{
			return offImage;
		}
		
		public synchronized void paintbucketFill(int x, int y, Color target, Color replacement)
		{
			//System.out.println("X " + x + " Y " + y);
			
			Stack <Point> stack = new Stack <Point>();
			Point p = new Point(x, y);
			stack.push(p);
			
			int replace = replacement.getRGB();
			int tgt = target.getRGB();
			
			while(!stack.isEmpty())
			{
				p = stack.pop();
				int pointColor = offImage.getRGB((int)p.getX(), (int)p.getY());
				
				//System.out.println("Point Color " + pointColor);
				
				if(pointColor == tgt)
				{	
					offImage.setRGB((int)p.getX(), (int)p.getY(), replace);
					
					if(p.getX() > 0)
						stack.push(new Point((int)p.getX() - 1, (int)p.getY()));
					if(p.getX() < getWidth() - 1)
						stack.push(new Point((int)p.getX() + 1, (int)p.getY()));
					if(p.getY() > 0)
						stack.push(new Point( (int)p.getX(), (int)p.getY() - 1     ));
					if(p.getY() < getHeight() - 1)
						stack.push(new Point ((int)p.getX() , (int)p.getY() + 1));
				}
			}
			stack.empty();
		}
		
		//We can get rid of this as well as the PixelNode class.
		public void paintbucketFill(PixelNode startNode, Color target, Color replacement)
		{
			//System.out.println("X: " + startNode.getX() + " Y: " + startNode.getY() + " Color: " + startNode.getColor().toString()); 
			Stack <PixelNode> paintbucketStack = new Stack <PixelNode>();
			PixelNode n;
			paintbucketStack.push(startNode);
			int replace = replacement.getRGB();
			
			while(!paintbucketStack.isEmpty())
			{
				n = paintbucketStack.pop();
				if(n.getColor().equals(target))
				{
					offImage.setRGB(n.getX(), n.getY(), replace);
					//west
					if(n.getX() > 1)
						paintbucketStack.push(new PixelNode(n.getX() - 1, n.getY(), new Color(offImage.getRGB(n.getX() - 1, n.getY()))));
					//east
					if(n.getX() < getWidth() - 1)
						paintbucketStack.push(new PixelNode(n.getX() + 1, n.getY(), new Color(offImage.getRGB(n.getX() + 1, n.getY()))));
					//north
					if(n.getY() > 1)
						paintbucketStack.push(new PixelNode(n.getX(), n.getY() - 1, new Color(offImage.getRGB(n.getX(), n.getY() - 1))));
					//south
					if(n.getY() < getHeight() - 1)
						paintbucketStack.push(new PixelNode(n.getX(), n.getY() + 1, new Color(offImage.getRGB(n.getX(), n.getY() + 1))));
				}		
			}
			repaint();
			paintbucketStack.empty();
		}
		
		public void mouseDragged(MouseEvent e) 
		{
			float strokeSize = myToolbar.getStrokeSize();
			gfx.setStroke(new BasicStroke(strokeSize));
		
			if(myToolbar.getCurrentTool() == Toolbar.RECTANGLE_TOOL)
			{
				gfx.setColor(Color.white);
				gfx.fillRect(0, 0, width, height);
				
				shapeEndX = e.getX();
				shapeEndY = e.getY();
				
				//restore the background
				gfx.drawImage(background, 0, 0, this);
				
				//Use drawRectangle from SpencerGraphics instead of gfx.drawRect
				if(myToolbar.doFillColor())
				{
					gfx.setColor(myToolbar.getFillColor());
					sg.fillRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
					gfx.setColor(myToolbar.getCurrentColor());
					sg.drawRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
				}
				else
				{
					gfx.setColor(myToolbar.getCurrentColor());
					sg.drawRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
				}
			}
			else if(myToolbar.getCurrentTool() == Toolbar.OVAL_TOOL)
			{
				gfx.setColor(Color.white);
				gfx.fillRect(0, 0, width, height);
				
				shapeEndX = e.getX();
				shapeEndY = e.getY();
				
				gfx.drawImage(background, 0, 0, this);
				
				//draw bounding box for oval
				gfx.setStroke(new BasicStroke(1.0f));
				gfx.setColor(Color.black);
				//Use drawRectangle from SpencerGraphics instead of gfx.drawRect
				sg.drawRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
				
				gfx.setStroke(new BasicStroke(myToolbar.getStrokeSize()));
				
				int x1 = 0, y1 = 0, w = 0, h = 0;
				
				//TODO put this in SpencerGraphics
				if(shapeStartX < shapeEndX)
				{	
					//Normal case (top left to bottom right)
					if(shapeStartY < shapeEndY)
					{
						x1 = shapeStartX;
						y1 = shapeStartY;
						w = shapeEndX - shapeStartX;
						h = shapeEndY - shapeStartY;
					}
					//bottom left to top right
					else if(shapeStartY > shapeEndY)
					{
						x1 = shapeStartX;
						y1 = shapeEndY;
						w = shapeEndX - shapeStartX;
						h = shapeStartY - shapeEndY;
					}
				}
				else if(shapeStartX > shapeEndX)
				{
					//top right to bottom left
					if(shapeStartY < shapeEndY)
					{
						x1 = shapeEndX;
						y1 = shapeStartY;
						w = shapeStartX - shapeEndX;
						h = shapeEndY - shapeStartY;
					}
					//bottom right to top left
					else if(shapeStartY > shapeEndY)
					{
						x1 = shapeEndX;
						y1 = shapeEndY;
						w = shapeStartX - shapeEndX;
						h = shapeStartY - shapeEndY;
					}
				}
				
				if(myToolbar.doFillColor())
				{
					gfx.setColor(myToolbar.getFillColor());
					gfx.fillOval(x1, y1, w, h);
					gfx.setColor(myToolbar.getCurrentColor());
					gfx.drawOval(x1, y1, w, h);
				}
				else
				{
					gfx.setColor(myToolbar.getCurrentColor());
					gfx.drawOval(x1, y1, w, h);
				}
				
			}
			else if(myToolbar.getCurrentTool() == Toolbar.LINE_TOOL)
			{
				gfx.setColor(Color.white);
				gfx.fillRect(0, 0, width, height);
				
				shapeEndX = e.getX();
				shapeEndY = e.getY();

				//restore background
				gfx.drawImage(background, 0, 0, this);

				gfx.setColor(myToolbar.getCurrentColor());
				gfx.drawLine(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
			}
			else if(myToolbar.getCurrentTool() == Toolbar.PENCIL_TOOL)
			{
			
				int curX = e.getX();
				int curY = e.getY();
				gfx.setColor(myToolbar.getCurrentColor());
			
				gfx.drawLine(lastX, lastY, curX, curY);
			
				lastX = curX;
				lastY = curY;
			}
			
			repaint();
			myToolbar.grabClearButtonFocus();
		}

		public void mousePressed(MouseEvent e) 
		{	
			//Save a snapshot of the current canvas in the "Undo" Stack so we can revert.
			BufferedImage undoImage = new BufferedImage(offImage.getWidth(), offImage.getHeight(), offImage.getType());
			undoImage.setData(offImage.getData());
			undoStack.push(undoImage);
			redoStack.clear();
			
			if(myToolbar.getCurrentTool() == Toolbar.RECTANGLE_TOOL)
			{
				//Before drawing the rectangle or oval, save the contents of the offscreen buffer (offImage)
				//If we don't do this, the "dragging" effect of the rectangle / oval will erase the background
				background = new BufferedImage(offImage.getWidth(), offImage.getHeight(), offImage.getType());
				background.setData(offImage.getData());
				
				shapeStartX = e.getX();
				shapeStartY = e.getY();
			}
			else if(myToolbar.getCurrentTool() == Toolbar.OVAL_TOOL)
			{
				//Before drawing the rectangle or oval, save the contents of the offscreen buffer (offImage)
				//If we don't do this, the "dragging" effect of the rectangle / oval will erase the background
				background = new BufferedImage(offImage.getWidth(), offImage.getHeight(), offImage.getType());
				background.setData(offImage.getData());
				
				shapeStartX = e.getX();
				shapeStartY = e.getY();
			}
			else if(myToolbar.getCurrentTool() == Toolbar.LINE_TOOL)
			{
				//Before drawing the line, save the contents of the offscreen buffer (offImage)
				//If we don't do this, the "dragging" effect of the rectangle / oval will erase the background
				background = new BufferedImage(offImage.getWidth(), offImage.getHeight(), offImage.getType());
				background.setData(offImage.getData());
				
				shapeStartX = e.getX();
				shapeStartY = e.getY();
			}
			else if(myToolbar.getCurrentTool() == Toolbar.PENCIL_TOOL)
			{
				lastX = e.getX();
				lastY = e.getY();
			}
		}

		public void mouseReleased(MouseEvent e)
		{		
			if(myToolbar.getCurrentTool() == Toolbar.RECTANGLE_TOOL)
			{	
				shapeEndX = e.getX();
				shapeEndY = e.getY();
			
				//restore the background
				gfx.drawImage(background, 0, 0, this);
				
				//Use drawRectangle from SpencerGraphics instead of gfx.drawRect
				sg.drawRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
				if(myToolbar.doFillColor())
				{
					gfx.setColor(myToolbar.getFillColor());
					sg.fillRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
					gfx.setColor(myToolbar.getCurrentColor());
					sg.drawRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
				}
				else
				{
					gfx.setColor(myToolbar.getCurrentColor());
					sg.drawRectangle(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
				}
				
				repaint();
			}
			else if(myToolbar.getCurrentTool() == Toolbar.OVAL_TOOL)
			{
				shapeEndX = e.getX();
				shapeEndY = e.getY();
			
				//restore the background
				gfx.drawImage(background, 0, 0, this);
				
				//draw the rectangle being dragged out
				gfx.setColor(myToolbar.getCurrentColor());
				
				int x1 = 0, y1 = 0, w = 0, h = 0;
				
				//TODO put this in SpencerGraphics
				if(shapeStartX < shapeEndX)
				{	
					//Normal case (top left to bottom right)
					if(shapeStartY < shapeEndY)
					{
						x1 = shapeStartX;
						y1 = shapeStartY;
						w = shapeEndX - shapeStartX;
						h = shapeEndY - shapeStartY;
					}
					//bottom left to top right
					else if(shapeStartY > shapeEndY)
					{
						x1 = shapeStartX;
						y1 = shapeEndY;
						w = shapeEndX - shapeStartX;
						h = shapeStartY - shapeEndY;
					}

				}
				else if(shapeStartX > shapeEndX)
				{
					//top right to bottom left
					if(shapeStartY < shapeEndY)
					{
						x1 = shapeEndX;
						y1 = shapeStartY;
						w = shapeStartX - shapeEndX;
						h = shapeEndY - shapeStartY;
					}
					//bottom right to top left
					else if(shapeStartY > shapeEndY)
					{
						x1 = shapeEndX;
						y1 = shapeEndY;
						w = shapeStartX - shapeEndX;
						h = shapeStartY - shapeEndY;
					}
				}
				
				if(myToolbar.doFillColor())
				{
					gfx.setColor(myToolbar.getFillColor());
					gfx.fillOval(x1, y1, w, h);
					gfx.setColor(myToolbar.getCurrentColor());
					gfx.drawOval(x1, y1, w, h);
				}
				else
				{
					gfx.setColor(myToolbar.getCurrentColor());
					gfx.drawOval(x1, y1, w, h);
				}
				
				repaint();
			}
			else if(myToolbar.getCurrentTool() == Toolbar.LINE_TOOL)
			{
				shapeEndX = e.getX();
				shapeEndY = e.getY();
			
				//restore the background
				gfx.drawImage(background, 0, 0, this);
				
				//draw the rectangle being dragged out
				gfx.setColor(myToolbar.getCurrentColor());
				
				//Use drawRectangle from SpencerGraphics instead of gfx.drawRect
				gfx.drawLine(shapeStartX, shapeStartY, shapeEndX, shapeEndY);
				
				repaint();
				
			}
		}
		
		public void mouseMoved(MouseEvent e) 
		{
			
		}
		
		public void mouseClicked(MouseEvent e) 
		{	
			float strokeSize = myToolbar.getStrokeSize();
			gfx.setStroke(new BasicStroke(strokeSize));
			int curX = e.getX();
			int curY = e.getY();

			gfx.setColor(myToolbar.getCurrentColor());
			
			if(myToolbar.getCurrentTool() == Toolbar.PENCIL_TOOL)
				gfx.drawLine(lastX, lastY, curX, curY);
			else if(myToolbar.getCurrentTool() == Toolbar.PAINTBUCKET_TOOL)
			{
				Color startColor = new Color(offImage.getRGB(curX, curY));
				//PixelNode startNode = new PixelNode(curX, curY, startColor);
				//paintbucketFill(startNode, startColor, myToolbar.getCurrentColor());
				
				//make sure the target color and the new color are not the same (otherwise we will enter an infinite loop)
				if(!startColor.equals(myToolbar.getCurrentColor()))
						paintbucketFill(curX, curY, startColor, myToolbar.getCurrentColor());
			}
				
			lastX = curX;
			lastY = curY;

			repaint();
			myToolbar.grabClearButtonFocus();
		}

		public void mouseEntered(MouseEvent arg0) 
		{
		}

		public void mouseExited(MouseEvent arg0) 
		{
		}
}
