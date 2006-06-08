package com.idega.games.test.particles;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.Serializable;

/**
 * Simple super-class to create 2D particles animation
 *
 * @author Bruno Augier 
 * @version 1.00 12/01/2005
 */
public class SimpleParticle implements Serializable
{
	private int particleBuffer[];			//particle buffer
	private int particleBuffer2[];			//particle buffer 2
	protected int width;					//width
	protected int height;					//height
	protected int particleBackBuffer[];		//particle back buffer
	protected int particleFrontBuffer[];	//particle front buffer	
	protected int pixels[];					//pixels buffer
	protected int updateCount;				//number of update	
	protected Image image;					//image
	protected MemoryImageSource misImage;	
		
	/**
	 * Set Back/Front buffer
	 */
	private void getBackFrontBuffer()
	{
		//Get back/front buffer
		if(this.updateCount%2==0)
		{
			this.particleBackBuffer=this.particleBuffer;
			this.particleFrontBuffer=this.particleBuffer2;
		}
		else
		{
			this.particleBackBuffer=this.particleBuffer2;
			this.particleFrontBuffer=this.particleBuffer;			
		}		
	}		
	
	/**
	 * Construct a new particle animation
	 * @param width maximum particle animation width (pixel)
	 * @param height maximum particle animation height (pixel)
	 */
	protected SimpleParticle(int width,int height)
	{
		this.setSize(width,height);
	}
	
	/**
	 * Compute particle animation (must be overiden)
	 */
	protected void computeParticle()
	{
		
	}	
		
	/**
	 * Update particle animation 
	 */
	public void update()
	{
		this.getBackFrontBuffer();
		this.computeParticle();
		this.updateCount++;
		if(this.misImage!=null)
			this.misImage.newPixels();
	}
	
	/**
	 * Set the maximum size for this particle animation 
	 * @param width maximum particle animation width (pixel)
	 * @param height maximum particle animation height (pixel)
	 */	
	public void setSize(int width,int height)
	{
		this.width=width;
		this.height=height;	
		this.updateCount=0;
		this.particleBuffer=new int[this.width*this.height];
		this.particleBuffer2=new int[this.width*this.height];		
		this.pixels=new int[this.width*this.height];
		if(this.misImage!=null)	
			this.createImage();
	}		
	
	/**
	 * Set particle area with the specified value (add combustible)
	 * @param x1 min x area
	 * @param y1 min y area
	 * @param x2 max x area
	 * @param y2 max y area
	 * @param value particle value
	 */		
	public void setParticleAt(int x1,int y1,int x2,int y2,int value)
	{
		if(x1<0) x1=0;
		if(x2>=this.width) x2=this.width-1;
		if(y1<0) y1=0;
		if(y2>=this.height) y2=this.height-1;	
		if(x2<x1) return;	
		if(y2<y1) return;
		this.getBackFrontBuffer();
		for(int x=x1;x<=x2;x++)
			for(int y=y1;y<=y2;y++)
				this.particleBackBuffer[x+y*this.width]=value;
	}		
	
	/**
	 * Add particle area width the specified value (add combustible)
	 * @param x1 min x area
	 * @param y1 min y area
	 * @param x2 max x area
	 * @param y2 max y area
	 * @param value particle value
	 */		
	public void addParticleAt(int x1,int y1,int x2,int y2,int value)
	{
		if(x1<0) x1=0;
		if(x2>=this.width) x2=this.width-1;
		if(y1<0) y1=0;
		if(y2>=this.height) y2=this.height-1;	
		if(x2<x1) return;	
		if(y2<y1) return;
		this.getBackFrontBuffer();
		for(int x=x1;x<=x2;x++)
			for(int y=y1;y<=y2;y++)
				this.particleBackBuffer[x+y*this.width]+=value;		
	}	
			
	/**
	 * Create an internal Image of the pixels buffer that can be used to draw on a graphics 
	 */		
	public void createImage()
	{
		this.misImage=new MemoryImageSource(this.getWidth(),this.getHeight(),new DirectColorModel(32,0xFF0000,0xFF00,0xFF,0xFF000000),this.getPixels(),0,this.getWidth());
		this.misImage.setAnimated(true);
		this.misImage.setFullBufferUpdates(true);
		this.image=Toolkit.getDefaultToolkit().createImage(this.misImage);		
	}
	
	/**
	 * Remove the internal Image of the pixels buffer
	 */		
	public void destroyImage()
	{
		this.misImage=null;
		this.image=null;		
	}	
		
	/**
	 * Return pixels array for this particle animation
	 * @return pixels array to draw this particle animation
	 */	
	public int[] getPixels()
	{
		return this.pixels;
	}
	
	/**
	 * Return an image representing this particle animation
	 * @return image representing this particle animation
	 */	
	public Image getImage()
	{
		return this.image;
	}	
	
	/**
	 * Return maximum particle animation width (pixel)
	 * @return maximum width for this particle animation
	 */		
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Return maximum particle animation height (pixel)
	 * @return maximum height for this particle animation
	 */		
	public int getHeight()
	{
		return this.height;
	}		
	
	/**
	 * Implement the necessary method to serialize this object
	 * @param out output stream for serialized data
	 */			
	private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
    {
     	out.writeInt(this.updateCount);
     	out.writeInt(this.width);
     	out.writeInt(this.height);
     	if(image!=null) 
     		out.writeBoolean(true);
     	else 
     		out.writeBoolean(false);
    }

	/**
	 * Implement the necessary method to unserialize this object from a stream
	 * @param in input stream for serialized data
	 */	    
 	private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
    {
    	this.updateCount=in.readInt();
     	int width=in.readInt();
     	int height=in.readInt();
     	this.setSize(width,height);
     	boolean image=in.readBoolean();
     	if(image) 
     		this.createImage();
     	
    }

}