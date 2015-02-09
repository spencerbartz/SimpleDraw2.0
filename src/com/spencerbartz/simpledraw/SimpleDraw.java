package com.spencerbartz.simpledraw;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SimpleDraw 
{
	public static void main(String [] args)
	{
		try 
		{
            // Set System Look & Feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch(UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch(InstantiationException e) 
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
		
		//Start the GUI
		new SimpleDrawUI();
	}
}
