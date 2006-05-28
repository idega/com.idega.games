package sputnick.webcamgrabber.computervision;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import vxp.PixelSource;
import vxp.QTLivePixelSource;
import vxp.VideoListener;

/*
 * Created on Oct 1, 2004
 *
 * */

/**
 * @author Dan O'Sullivan
 * 
 * A Simple Example for showing Video
 */
public class SimpleVideoImage extends JFrame implements KeyListener, VideoListener { //try MouseListener
	static int kHeight = 240;
    static int kWidth = 320;
    static JFrame frame;
    PixelSource ps;
    BufferedImage videoImage;

    public static void main(String[] args) { //this is always the first method
        // called by java when you program is run, like set up in processing
        frame = new SimpleVideoImage();
        //you are allow to say all of the below because you extended JFrame, look up the rest of the possibilities
        frame.setTitle(frame.getClass().getName());
        //frame.setUndecorated(true); //lose the menu bar, actually you might like menu bars, so implement "window listener" to get the close button working
        frame.setSize(kWidth, kHeight);
        frame.setVisible(true);
    
    }

    SimpleVideoImage() { //constructor of this class, called when the "new" is used for this class
        ps = new QTLivePixelSource(kWidth, kHeight, 50); //30 frames/second, make it bigger if you are getting lag
        ps.videoSettings();  //in case you want to pick the camera etc...
        ps.addVideoListener(this);
        addKeyListener(this);
    }

    public void newFrame() { //called for me by pixel source when I made myself a video listener
        ps.grabFrame();
        videoImage = ps.getImage();
        repaint();
    }

    public void update(Graphics g) { //overide java's clean up of the screenbecaus you are fill the screen anyway, faster, less flicker
        paint(g);
    }

    public void paint(Graphics g) { //this is where we paint
        if (videoImage != null) {
            g.drawImage(videoImage, 0, 0, null);
        }
    }
    public void shutDown() {
        ps.killSession();
        System.out.println("Kill Application");
        frame.setVisible(false);
        frame.dispose();
        System.exit(0);
    }
  
    /////All of these are methods that I promised to put in when I said I was a KeyLister, as you can see most of them are empty promises
    //actually "WindowListener" would be better, save mouseClicked for something else

    public void keyPressed(KeyEvent e) {
        String what = KeyEvent.getKeyText(e.getKeyCode());
        if (what.toUpperCase().equals("Q") || what.equals("Escape")){
            shutDown();
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) { }

    /*code for not having all these empty stubs hanging around
    addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                   shutDown();
            }
    });
    */



}


