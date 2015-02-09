package com.spencerbartz.simpledraw;


import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JApplet;

public class DisplayPageGenerator
{
	File displayFile;
	File dir;
	SimpleDrawUI parent;
	BufferedImage imgToSave;
	String fileName;
	
	public DisplayPageGenerator(BufferedImage saveImage, String fileName, SimpleDrawUI parent)
	{
		imgToSave = saveImage;
		this.fileName = fileName;
		this.parent = parent;
	}
	
	//Convert the contents of the canvas (stored in a BufferedImage) to a ByteArrayOutputStream which can
	//be sent to the web server. The web Server will then save them on its file system in a .gif file.
	public void uploadFile()
	{
		try
		{
			//convert the the BufferedImage of the canvas into a ByteArrayOutputStream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imgToSave, "gif", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			
			//Upload the ByteArrayOutputStream to the web server
			FileUpload fu = new FileUpload(imageInByte, fileName);
			fu.httpConn();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}		
	}
	
	
	public void saveLocalFile(String path)
	{
		try
		{
			ImageIO.write(imgToSave, "gif", new File(path));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveLocalFileX()
	{
		String timeStamp;
		
		//Create a unique folder name to store the image and the web page in
		Date date = new Date();
		timeStamp = new Timestamp(date.getTime()).toString();
		timeStamp = convertTimeStamp(timeStamp);	
		
		try
		{
			//Attempt to create the directory with the name "timeStamp"
			//System.err.println("Current Directory: " + System.getProperty("user.dir"));
			dir = new File(System.getProperty("user.dir") + "/" + timeStamp);
			boolean result = dir.mkdir();
			
			if(!result)
				System.err.println("Failed to create directory " + System.getProperty("user.dir") + "/" + timeStamp + ". Aborting...");
			else
			{
				System.err.println("Created directory " + dir.getAbsolutePath());
				ImageIO.write(imgToSave, "gif", new File(dir.getAbsolutePath() + "/" + fileName + ".gif"));
				displayFile = new File(dir.getAbsolutePath() + "/imagedisplay.html");	
				
				if(displayFile.exists())
					System.err.println("Created: " + displayFile.getAbsolutePath());
				else
					System.err.println("Failed to create " + dir.getAbsolutePath() + "/imagedisplay.html");
				
				//parent.getAppletContext().showDocument(new URL("http://" + dir.getName() + "/imagedisplay.html"), "_blank");
			}
			
		}
		catch(IOException exception)
		{
			exception.printStackTrace();
		}
	}
	
	//Remove all non numeric characters from a Java timestamp and return as string
	public String convertTimeStamp(String timeStamp)
	{
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < timeStamp.length(); i++)
		{
			char c = timeStamp.charAt(i);
			if(c != '-' && c != ' ' && c != ':' && c != '.')
				buf.append(c);
		}
		
		return buf.toString();
		
	}
	
	public void generatePage(File f, String imageName)
	{
		//Write to the html file
		try 
		{
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
			bw.write("<html>\n");
			bw.write("<head>\n");
			bw.write("<title>" + imageName.substring(0, imageName.length() - 4) + "</title>\n");
			bw.write("</head>\n");
			bw.write("<body>\n");
			bw.write("<div align=\"left\">");
			bw.write("<img src=\"" + imageName + "\">\n");
			bw.write("</div>");
			bw.write("<div align=\"left\">");
			bw.write("<a href=\"../processImage.php?action=1\">Add To Hall of Fame</a><br>");
			bw.write("<a href=\"../processImage.php?action=2\">Delete Image</a>\n");
			bw.write("</div>");
			bw.write("</body\n");
			bw.write("</html>\n");
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
