package sputnick.webcamgrabber;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import sputnick.webcamgrabber.util.ImageGrabberVideoListener;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
import vxp.QTLivePixelSource;
import vxp.VideoListener;
import com.idega.util.BrowserLauncher;

/**
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * 
 * A cool class to grab images from your webcam or any supported video input for
 * quicktime and save the image locally or to an ftp server
 */
public class WebCamGrabber extends Frame implements VideoListener, ActionListener, WindowListener {

	static final int width = 320;
	static final int height = 240;
	protected QTLivePixelSource ps;
	protected BufferedImage currentFrameImage;

	WebCamGrabber(String title) {
		super(title);
		setLocation(100, 150);
		setSize(width, height);
		setLayout(null);
		setVisible(true);
		show();
		toFront();
		ps = new QTLivePixelSource(width, height, 24);
		ps.addVideoListener(this);
		//ps.videoSettings();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

	}



	public void newFrame() {
		// currentFrameImage = ps.getImage();
		lookAtAFrame();
	}

	protected void lookAtAFrame() // this is where the action is, where we go
	// pixel by
	// pixel through the video
	{
		//int[] rgb;
		ps.grabFrame(); // grab a frame
		//int[] straightPixels = ps.getPixelArray();
		// REPEAT FOR EACH ROW OF PIXELS
//		for (int row = 0; row < ps.getVideoHeight(); row++) {
//			// REPEAT FOR EACH PIXEL IN THE ROW
//			for (int column = 0; column < ps.getVideoWidth(); column++) {
//				rgb = ps.getPixel(column, row);
				// ps.setPixel(straightPixels, column, row, rgb[1],
				// rgb[2], rgb[3], 255);
				// set the alpha to opaque
//			}// END FOR EACH PIXEL IN A ROW
//		}// END FOR EACH ROW OF PIXELS
		//currentFrameImage = ps.getImage(straightPixels);
		currentFrameImage = ps.getImage();
	}

	public void update(Graphics g) {
		paint(g); // remove flicker
	}

	public void paint(Graphics g) { // this is where we paint
		try {
		//don't know if this is neccesary to could be a chance for the app to "breath"
			Thread.sleep(1);
		}
		catch (InterruptedException e) {
		}
		
		if (currentFrameImage != null) {
			g.drawImage(currentFrameImage, 0, 0, this);// the background video
		}
	}

	public void windowClosing(WindowEvent e) {
		dispose();
	}

	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	
	}

	protected void uploadToServerByFTP(String servername, String folder, String fileName, String userName,
			String password, String imagePathAndFileName) {
		try {
			FtpClient client = new FtpClient();
			client.openServer(servername);
			client.login(userName, password);
			client.binary();
			client.cd(folder);
			// upload
			TelnetOutputStream out = client.put(fileName);
			File image = new File(imagePathAndFileName);
			FileInputStream is = new FileInputStream(image);
			byte[] bytes = new byte[1024];
			int c;
			int totalBytes = 0;
			while ((c = is.read(bytes)) != -1) {
				totalBytes += c;
				out.write(bytes, 0, c);
			}
			is.close();
			out.close();
			client.closeServer();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Main entry point
	static public void main(String[] args) {
		// createHTMLFile();
		// Create the Preview/grabber Window
		WebCamGrabber preview = new WebCamGrabber("WebCamGrabber By Idega");
		try {
			createHTMLFile();
			BrowserLauncher.openURL("http://localhost/~" + System.getProperty("user.name") + "/" + "webcam.html");
			// so the timer calls this methods actionPerformed
			// before starting
			ImageGrabberVideoListener grabber =  new ImageGrabberVideoListener(preview.ps);
			
			
			while(true){
				preview.repaint();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println(System.getProperties().toString());
	}

	/**
	 * 
	 */
	protected static void createHTMLFile() {
		File file = new File(System.getProperty("user.home") + "/Sites/webcam.html");
		if (!file.exists()) {
			try {
				file.createNewFile();
				RandomAccessFile htmlFile;
				htmlFile = new RandomAccessFile(System.getProperty("user.home") + "/Sites/webcam.html", "rw");
				String fileString = "<HTML>\n"
						+ "<HEAD>\n"
						+ "<style type=\"text/css\">\n"
						+ "body { line-height:16px;font-family:Arial,Helvetica,sans-serif;font-size:11px;color:#4a4a4a; }\n"
						+ "A { line-height:16px;font-family:Arial,Helvetica,sans-serif;font-size:11px;color:#FF8008;font-weight:bold; }\n"
						+ "A:hover { line-height:16px;font-family:Arial,Helvetica,sans-serif;font-size:11px;color:#8c8c8c;font-weight:bold; }\n"
						+ ".main{ border: 1px solid #cccccc; background-color: #ffffff; font-size: 10px; font-weight: bold;}\n"
						+ "</style>\n"
						+ "<meta http-equiv=\"refresh\" content=\"4\">\n"
						+ "<TITLE>WebCamGrabber by Idega Software</TITLE>\n"
						+ "</HEAD>\n"
						+ "<BODY BGCOLOR=\"#f5f5f5\">\n"
						+ "<CENTER>\n"
						+ "<p>\n"
						+ "<table class=\"main\" >\n"
						+ "<tr><td>"
						+ "<img src=\"images/grabbedImage.png\" alt=\"Grabbed by eiki's excellent java webcam grabber\"/>"
						+ "</td></tr>"
						+ "<tr><td>"
						+ "<CENTER><p>Made by <a href=\"mailto:eiki@idega.is\">Eirikur Hrafnsson</a>, © Copyright <a href=\"http://www.idega.com/index.jsp?link_from_webcam_grabber\" target=\"_new\">Idega Software</a> 2003."
						+ "</CENTER></td></tr>" + "</table>\n" + "</CENTER>\n" + "</BODY>\n" + "</HTML>";
				htmlFile.writeChars(fileString);
				htmlFile.close();
			}
			catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
