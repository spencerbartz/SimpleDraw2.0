package com.spencerbartz.simpledraw;


import java.io.OutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;

public class FileUpload
{
	private final String CrLf = "\r\n";
	private byte [] imgData;
	private String fileName;
	
	public FileUpload(byte [] fileData, String fileName)
	{
		imgData = fileData;
		this.fileName = fileName;
	}
	
	public FileUpload()
	{
		
	}
	
	public void httpConn()
	{
		URLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;

		try
		{
			//URL url = new URL("http://localhost/applets/simpledraw/fileupload.php");
			URL url = new URL("http://www.spencerbartz.com/applets/simpledraw/fileupload.php");
			System.out.println("url:" + url);
			conn = url.openConnection();
			conn.setDoOutput(true);

			//String postData = "";

			//InputStream imgIs = getClass().getResourceAsStream("/test.gif");
			//byte[] imgData = new byte[imgIs.available()];
			//InputStream imgIs = new ByteArrayInputStream(imgData);
			//imgIs.read(imgData);

			String message1 = "";
			message1 += "-----------------------------4664151417711" + CrLf;
			message1 += "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + fileName + ".gif\""
					+ CrLf;
			message1 += "Content-Type: image/gif" + CrLf;
			message1 += CrLf;

			// the image is sent between the messages in the multipart message.

			String message2 = "";
			message2 += CrLf + "-----------------------------4664151417711--"
					+ CrLf;

			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=---------------------------4664151417711");
			// might not need to specify the content-length when sending chunked
			// data.
			conn.setRequestProperty("Content-Length", String.valueOf((message1.length() + message2.length() + imgData.length)));

			System.out.println("open os");
			os = conn.getOutputStream();

			System.out.println(message1);
			os.write(message1.getBytes());

			// SEND THE IMAGE
			int index = 0;
			int size = 1024;
			do
			{
				System.out.println("write:" + index);
				if ((index + size) > imgData.length)
				{
					size = imgData.length - index;
				}
				os.write(imgData, index, size);
				index += size;
			}
			while(index < imgData.length);
			System.out.println("written:" + index);

			System.out.println(message2);
			os.write(message2.getBytes());
			os.flush();

			System.out.println("open is");
			is = conn.getInputStream();

			char buff = 512;
			int len;
			byte[] data = new byte[buff];
			do
			{
				System.out.println("READ");
				len = is.read(data);

				if (len > 0)
				{
					System.out.println(new String(data, 0, len));
				}
			}
			while(len > 0);

			System.out.println("DONE");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Close connection");
			try
			{
				os.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				is.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
