package com.idega.games.test.particles;

/**
 * @(#)TestSimpleParticle.java
 *
 * Demonstration applet for package simpleParticle
 *
 * @author Bruno Augier
 * @version 1.00 06/01/04
 */
 
import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

public class TestSimpleParticle extends Applet implements Runnable
{
	private int width;
	private int height;
	private Image backImage;
	private Graphics backGraphics;
	private boolean pauseProcess;
	private boolean exitProcess;
	private Thread process;
	private int nbLoop;
	
	public void init() 
	{		
		this.process=null;
		this.pauseProcess=false;
		this.exitProcess=false;
		this.nbLoop=0;
		this.setSize(256+256,256);
	}
	
	public void setSize(int width,int height)
	{
		this.width=width;
		this.height=height;
		super.setSize(this.width,this.height);
		this.backImage=this.createImage(this.width,this.height);
		this.backGraphics=this.backImage.getGraphics();
	}
	
	public void start()
	{
		if(this.process==null)
		{
			this.exitProcess=false;
			this.process=new Thread(this);
			this.process.start();		
		}
		this.pauseProcess=false;
	}
	
	public void stop()
	{
		this.pauseProcess=true;
	}
	
	public void destroy()
	{
		if(this.process!=null)
		{
			this.exitProcess=true;
			try
			{
				this.process.join();
			}
			catch(InterruptedException ie)
			{
			}
			
		}
		this.process=null;
	}
	
	public void run()
	{
		this.mainStart();
		while(!this.exitProcess)
		{
			this.nbLoop++;
			this.mainLoop();
			this.update(this.getGraphics());
			Thread.currentThread().yield();
			
			while(this.pauseProcess)
			{
				if(this.exitProcess)
					return;
				try
				{
					Thread.currentThread().sleep(100);
				}
				catch(InterruptedException ie)
				{
					this.exitProcess=true;
					return;
				}
			}
		
		}
	}
	
	public void paint(Graphics g) 
	{
		this.update(g);
	}
	
	public void update(Graphics g) 
	{	
		g.drawImage(this.backImage,0,0,null);
	}
	
	/**
	 * Main prog start here
	 */
	
	SimpleFire fire1;
	SimpleFire fire2;
	SimpleFire fire3;
	SimpleWater water1;
	public void mainStart()
	{
		//Create two fire
		fire1=new SimpleFire(20,100);
		fire1.createImage();
		fire2=new SimpleFire(255,128);
		fire2.createImage();
		fire3=new SimpleFire(160,150);
		fire3.createImage();		
		water1=new SimpleWater(255,128);
		water1.createImage();		
		
		//Erase background
		this.backGraphics.setColor(new Color(0x000000));
		this.backGraphics.fillRect(0,0,this.width,this.height);
	}
	
	public void mainLoop()
	{
		
		//Add random particle on fire1
		int x1=5+(int)(Math.random()*10.0);
		fire1.addParticleAt(x1,fire1.getHeight()-3,x1,fire1.getHeight()-3,2500);
		
		
		//Add multiple random particle on fire2
		for(int n=0;n<8;n++)
		{
			int x2=10+(int)(Math.random()*(fire2.getWidth()-20));
			fire2.addParticleAt(x2,fire2.getHeight()-3,x2,fire2.getHeight()-3,4000);
		}
		
		//Add particle on fire3
		if(nbLoop%20==0)
		{
			int x3=10+(int)(Math.random()*(fire3.getWidth()-20));
			fire3.addParticleAt(x3,fire3.getHeight()-3,x3,fire3.getHeight()-3,100000);		
		}

		//Add particle on water1
		for(int n=0;n<2;n++)
		{
			int x=2+(int)(Math.random()*water1.getWidth()-4);
			int y=2+(int)(Math.random()*water1.getHeight()-4);
			water1.setParticleAt(x,y,x+1,y+1,((int)(Math.random()*10000000))-5000000);		
		}
		
		//Update fire & water
		fire1.update();
		fire2.update();		
		fire3.update();
		water1.update();
		
				
		//Draw some text
		this.backGraphics.setColor(new Color(0xFF0000));
		this.backGraphics.setFont(new Font("Arial",Font.BOLD,40));
		this.backGraphics.drawString("Fire Demo",30,80);
			
		//Draw fire 2
		this.backGraphics.drawImage(this.fire2.getImage(),0,0,null);

		//Draw some text
		this.backGraphics.setColor(new Color(0x0000FF));
		this.backGraphics.setFont(new Font("Arial",Font.BOLD,40));
		this.backGraphics.drawString("Water Demo",10,210);
		this.backGraphics.drawImage(this.water1.getImage(),0,128,null);


		//Draw 15 times fire1
		for(int n=8;n>0;n--)
		{
			this.backGraphics.drawImage(this.fire1.getImage(),256+(int)(120+120.0*Math.cos(Math.PI*((double)n)/15)),(int)(150-50.0*Math.sin(Math.PI*((double)n)/15)),null);
			this.backGraphics.drawImage(this.fire1.getImage(),256+(int)(120-120.0*Math.cos(Math.PI*((double)n)/15)),(int)(150-50.0*Math.sin(Math.PI*((double)n)/15)),null);
		}
		
		//Draw fire 3 		
		this.backGraphics.drawImage(this.fire3.getImage(),256+60,100,null);		
		
		
		//Uncomment to test serialisation	
		/*	
		if(nbLoop%100==0) //Serialize after 200 loops
		{
			try
			{
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				ObjectOutputStream oos=new ObjectOutputStream(bos);
				oos.writeObject(fire2);
				oos.close();
				b=bos.toByteArray();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				System.out.println(ioe.getMessage());
			}
			
		}

	
	
		if(nbLoop%200==0)	//Unserialize after 200 loops
		{
			try
			{
				ByteArrayInputStream bis=new ByteArrayInputStream(b);
				ObjectInputStream ois=new ObjectInputStream(bis);
				try
				{
					fire2=(SimpleFire)ois.readObject();
					ois.close();
				}
				catch(ClassNotFoundException cnfe)
				{
				}
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				System.out.println(ioe.getMessage());
			}			
		}
	*/
	}
	byte[] b;	//Used for serialization test
}
