package com.spencerbartz.simpledraw;

/*
 * TODO
1. Add cut / copy / paste
2. Move oval drawing code to SpencerGraphics
3. Improve Hall of Fame php code
4. Test fill
5. Convert to application / JNLP (Java web start)
6. Add keyboard support
*/

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Toolbar extends JFrame implements ActionListener, ItemListener,
		ChangeListener
{
	private static final long serialVersionUID = 1L;
	public ImageIcon rectangleIcon, ovalIcon, lineIcon, pencilIcon, paintbucketIcon, paintbucketCursor;
	private BufferedImage curColorDisp, fillColorDisp;
	private JLabel rectLabel, ovalLabel, lineLabel, pencilLabel, paintbucketLabel, curColorLabel, fillColorLabel;
	private JRadioButton rectangle, oval, line, pencil, paintbucket;
	private JCheckBox fillCheckBox;
	private Color currentColor = Color.black;
	private Color fillColor = Color.black;
	private JButton clearButton, undoButton, redoButton, saveButton, submitButton, hallButton, testButton, currColorButton, fillColorButton;
	private boolean clearState = false;
	private boolean undoState = false;
	private boolean redoState = false;
	private boolean enableFill = false;
	private Container container;
	private JLabel strokeLabel;
	private JSlider slider;
	private SimpleDrawUI parent;
	private int strokeSize = 2;
	private BufferedImage offScreenImage;
	
	private int width = 550;
	private int height = 200;
	public static int RECTANGLE_TOOL = 0;
	public static int OVAL_TOOL = 1;
	public static int LINE_TOOL = 2;
	public static int PENCIL_TOOL = 3;
	public static int PAINTBUCKET_TOOL = 4;
	private int currentTool = PENCIL_TOOL;
	
	public Toolbar(SimpleDrawUI parent, BufferedImage offScreenImage)
	{
		super("Controls");

		this.parent = parent;
		this.offScreenImage = offScreenImage;
		container = getContentPane();
		container.setLayout(new FlowLayout());

		// initialize button to clear the screen
		clearButton = new JButton("Clear");
		container.add(clearButton);
		clearButton.grabFocus();
		
		//initialize undoButton
		undoButton = new JButton("Undo");
		container.add(undoButton);
		
		//initialize redoButton
		redoButton = new JButton("Redo");
		container.add(redoButton);
		
		//initialize testButton
		testButton = new JButton("Test");
		container.add(testButton);
		testButton.addActionListener(this);
		
		//initialize currColorButton
		currColorButton = new JButton("Current Color");
		container.add(currColorButton);
		
		//create little square that shows the current color
		curColorDisp = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		setColorDisp(curColorDisp, currentColor);
		curColorLabel = new JLabel(new ImageIcon(curColorDisp));
		container.add(curColorLabel);
		
		//initialize fillColorButton
		fillColorButton = new JButton("Fill Color");
		container.add(fillColorButton);
		
		fillColorDisp = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		setColorDisp(fillColorDisp, fillColor);
		fillColorLabel = new JLabel(new ImageIcon(fillColorDisp));
		container.add(fillColorLabel);
		
		fillCheckBox = new JCheckBox("Fill");
		container.add(fillCheckBox);
		
		//Listen for button presses on clear, undo, and redo
		clearButton.addActionListener(this);
		undoButton.addActionListener(this);
		redoButton.addActionListener(this);
		currColorButton.addActionListener(this);
		fillColorButton.addActionListener(this);
		
		//listen for checks
		fillCheckBox.addItemListener(this);
		
		//Initialize drawing tool ImageIcons
		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("rectangle.gif");
			rectangleIcon = new ImageIcon(ImageIO.read(input));
			input = classLoader.getResourceAsStream("oval.gif");
			ovalIcon = new ImageIcon(ImageIO.read(input));
			input = classLoader.getResourceAsStream("line.gif");
			lineIcon = new ImageIcon(ImageIO.read(input));
			input = classLoader.getResourceAsStream("pencil.gif");
			pencilIcon = new ImageIcon(ImageIO.read(input));
			input = classLoader.getResourceAsStream("paintbucket.gif");
			paintbucketIcon = new ImageIcon(ImageIO.read(input));
			input = classLoader.getResourceAsStream("paintbucketcursor.gif");
			paintbucketCursor = new ImageIcon(ImageIO.read(input));
		}
		catch(NullPointerException e)
		{
			System.err.println("ERROR: Could not load icon images.");
			e.printStackTrace();
			System.exit(ERROR);
		}
		catch (IOException e)
		{
			System.err.println("ERROR: Could not load icon images.");
			e.printStackTrace();
			System.exit(ERROR);
		}
		
		//Initialize drawing tool labels
		rectLabel = new JLabel(rectangleIcon);
		ovalLabel = new JLabel(ovalIcon);
		lineLabel = new JLabel(lineIcon);
		pencilLabel = new JLabel(pencilIcon);
		paintbucketLabel = new JLabel(paintbucketIcon);
		
		//Initialize radio buttons for selecting drawing tool
		rectangle = new JRadioButton("", false);
		oval = new JRadioButton("", false);
		line = new JRadioButton("", false);
		pencil = new JRadioButton("", true);
		paintbucket = new JRadioButton("", false);
		
		//Construct button group for tools & associate all shape buttons with the group
		ButtonGroup shapeGroup = new ButtonGroup();
		shapeGroup.add(rectangle);
		shapeGroup.add(oval);
		shapeGroup.add(line);
		shapeGroup.add(pencil);
		shapeGroup.add(paintbucket);
		
		//Add radio buttons to the screen
		container.add(rectangle);
		container.add(rectLabel);
		container.add(oval);
		container.add(ovalLabel);
		container.add(line);
		container.add(lineLabel);
		container.add(pencil);
		container.add(pencilLabel);
		container.add(paintbucket);
		container.add(paintbucketLabel);
		
		//listen for changes in which drawing tool is selected
		rectangle.addItemListener(this);
		oval.addItemListener(this);
		line.addItemListener(this);
		pencil.addItemListener(this);
		paintbucket.addItemListener(this);

		// initialize and add brush size label
		strokeLabel = new JLabel("Brush Size");
		container.add(strokeLabel);

		// initialize JSlider and set up the ticks
		slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 3);
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		container.add(slider);

		saveButton = new JButton("Save Image Locally");
		saveButton.addActionListener(this);
		container.add(saveButton);
		
		submitButton = new JButton("Submit to Hall of Fame");
		submitButton.addActionListener(this);
		container.add(submitButton);
		
		hallButton = new JButton("View Hall of Fame");
		hallButton.addActionListener(this);
		container.add(hallButton);
		
		setSize(width, height);
		setLocation(500, 100);
		setVisible(true);
	}

	public Color getCurrentColor()
	{
		return currentColor;
	}
	
	public Color getFillColor()
	{
		return fillColor;
	}

	public float getStrokeSize()
	{
		return (float) strokeSize;
	}

	public boolean getClearState()
	{
		return clearState;
	}

	public void resetClearState()
	{
		clearState = false;
	}
	
	public boolean getUndoState()
	{
		return undoState;
	}
	
	public void resetUndoState()
	{
		undoState = false;
	}
	
	public boolean getRedoState()
	{
		return redoState;
	}
	
	public void resetRedoState()
	{
		redoState = false;
	}
	
	public int getCurrentTool()
	{
		return currentTool;
	}
	
	public boolean doFillColor()
	{
		return enableFill;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == clearButton)
		{
			clearState = true;
			parent.repaint();
		}
		else if(e.getSource() == undoButton)
		{
			undoState = true;
			parent.repaint();
		}
		else if(e.getSource() == redoButton)
		{
			redoState = true;
			parent.repaint();
		}
		else if(e.getSource() == saveButton)
		{
			//Let the user save the contents of the canvas on the local file system
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("GIF Images", "gif");
			chooser.setFileFilter(filter);
			chooser.setDialogTitle("Save Your Picture!");
			int returnVal = chooser.showSaveDialog(this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				String fileName = chooser.getSelectedFile().getAbsolutePath();
			
				System.err.println("file: " + fileName);
				
				if(!chooser.accept(new File(fileName)))
				{
					System.err.println("No Good");
					fileName = new String(fileName + ".gif");
				}
				
				DisplayPageGenerator dpg = new DisplayPageGenerator(offScreenImage, fileName, parent);
				dpg.saveLocalFile(fileName);
			}
		}
		else if(e.getSource() == submitButton)
		{
			String fileName = JOptionPane.showInputDialog("Submit to Hall of Fame");
			
			//TODO check that fileName is valid
			
			if(fileName != null)
			{
				DisplayPageGenerator dpg = new DisplayPageGenerator(offScreenImage, fileName, parent);
				dpg.uploadFile();
			}
		}
		else if(e.getSource() == hallButton)
		{
			String url = "http://www.spencerbartz.com/applets/simpledraw/halloffame.php"; 
			try
			{
				java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//For checking stack contents
		else if(e.getSource() == testButton)
		{
			new TestWindow(((SimpleDrawUI)parent).undoStack, ((SimpleDrawUI)parent).redoStack);
		}
		else if(e.getSource() == currColorButton)
		{
			Color newColor = JColorChooser.showDialog(this, "Choose a Color", currentColor);
			
			//update the curColorDisp square
			if(newColor != null)
			{
				currentColor = newColor;
				setColorDisp(curColorDisp, currentColor);
				curColorLabel.setIcon(new ImageIcon(curColorDisp));		
			}
		}
		else if(e.getSource() == fillColorButton)
		{
			Color newColor = JColorChooser.showDialog(this, "Choose a Fill Color", currentColor);
			fillColor = newColor;
			setColorDisp(fillColorDisp, fillColor);
			fillColorLabel.setIcon(new ImageIcon(fillColorDisp));	
		}
	}

	public void itemStateChanged(ItemEvent event)
	{
		// check to see which button is clicked

		if(event.getSource() == rectangle)
		{
			currentTool = Toolbar.RECTANGLE_TOOL;
			parent.setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		else if(event.getSource() == oval)
		{
			currentTool = Toolbar.OVAL_TOOL;
			parent.setCursor (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		else if(event.getSource() == line)
		{
			currentTool = Toolbar.LINE_TOOL;
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		else if(event.getSource() == pencil)
		{
			currentTool = Toolbar.PENCIL_TOOL;
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		else if(event.getSource() == paintbucket)
		{
			currentTool = Toolbar.PAINTBUCKET_TOOL;
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = paintbucketCursor.getImage();
			Cursor c = toolkit.createCustomCursor(image , new Point(paintbucketCursor.getIconWidth(), paintbucketCursor.getIconHeight()), "paintbucket");
			parent.setCursor(c);
		}
		else if(event.getSource() == fillCheckBox)
			enableFill = enableFill == false ? true : false;		
	}

	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider) e.getSource();

		if (!source.getValueIsAdjusting())
		{
			strokeSize = source.getValue();
		}
	}

	//Set the color of one of the 2 squares that show the stroke / fill color
	private void setColorDisp(BufferedImage disp, Color c)
	{
		Graphics2D gfx = disp.createGraphics();
		gfx.setColor(c);
		gfx.fillRect(0, 0, 20, 20);
		gfx.setColor(Color.black);
		gfx.drawRect(0, 0, 20, 20);
	}
	
	public void grabClearButtonFocus()
	{
		clearButton.grabFocus();
	}
}