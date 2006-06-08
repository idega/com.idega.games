package com.idega.games.test.particles;


/**
 * Simple class to create fire effect
 *
 * @author Bruno Augier 
 * @version 1.00 01/12/2005
 */
public class SimpleFire extends SimpleParticle
{


	/**
	 * Construct a new fire
	 * @param width maximum fire width (pixel)
	 * @param height maximum fire height (pixel)
	 */
	public SimpleFire(int width,int height)
	{
		super(width,height);
	}
	
	
	/**
	 * Compute particle buffer and pixels
	 */
	protected void computeParticle()
	{
		//Update front particle buffer using back buffer
		for(int y=0;y<this.height-2;y++)
		{
			int ofsYX=y*this.width+2;
			int ofsYXEnd=y*this.width+this.width-2;
			int ofsY1X=ofsYX+this.width;
			int ofsY2X=ofsY1X+this.width;
			
			while(ofsYX<ofsYXEnd)
			{
				int c0=this.particleBackBuffer[ofsYX];
				int c3=this.particleBackBuffer[ofsY1X-1];
				int c4=this.particleBackBuffer[ofsY1X+1];
				int c5=this.particleBackBuffer[ofsY1X];
				int c6= this.particleBackBuffer[ofsY2X-1];
				int c7= this.particleBackBuffer[ofsY2X+1];
				int c8= this.particleBackBuffer[ofsY2X];
				int c9= this.particleBackBuffer[ofsY2X-2];
				int c10=this.particleBackBuffer[ofsY2X+2];			
				int c345=(((c3+c4)>>1)+c5)>>1;				
				int c678=(((c6+c7)>>1)+c8)>>1;
				int c910c678=(((c9+c10)>>1)+c678)>>1;
				int c345c910c678=(c345+c910c678)>>1;
				int particle=(c345c910c678+c0)>>1;
				if(particle>0)	particle--;
				this.particleFrontBuffer[ofsYX]=particle;
				
				//Update pixels buffer
				int r=particle<<1;
				int v=particle;
				int b=particle>>1;
				int a=particle<<1;
				
				if(a>255) a=255;
				if(r>255) r=255;
				if(v>255) v=255;
				if(b>255) b=255;
				this.pixels[ofsYX]=a<<24|r<<16|v<<8|b;		
				
				//Increment offset
				ofsYX++;
				ofsY1X++;
				ofsY2X++;
			}
		}
	}	
	
}