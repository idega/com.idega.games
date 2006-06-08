package com.idega.games.test.particles;


/**
 * Simple class to create water effect
 *
 * @author Bruno Augier 
 * @version 1.00 01/12/2005
 */
public class SimpleWater extends SimpleParticle
{


	/**
	 * Construct a new water
	 * @param width maximum water width (pixel)
	 * @param height maximum water height (pixel)
	 */
	public SimpleWater(int width,int height)
	{
		super(width,height);
	}
	
	
	/**
	 * Compute particle buffer and pixels
	 */
	protected void computeParticle()
	{
		//Update front particle buffer using back buffer
		for(int y=1;y<this.height-1;y++)
		{
			int ofsYX=y*this.width+1;
			int ofsYXEnd=y*this.width+this.width-1;
			int ofsY1X=ofsYX+this.width;
			int ofsY1XN=ofsYX-this.width;
			
			while(ofsYX<ofsYXEnd)
			{
				int c0=this.particleBackBuffer[ofsY1XN-1];
				int c1=this.particleBackBuffer[ofsY1XN];
				int c2=this.particleBackBuffer[ofsY1XN+1];
				int c3=this.particleBackBuffer[ofsYX-1];
				int c4=this.particleBackBuffer[ofsYX];
				int c5=this.particleBackBuffer[ofsYX+1];
				int c6=this.particleBackBuffer[ofsY1X-1];
				int c7=this.particleBackBuffer[ofsY1X];
				int c8=this.particleBackBuffer[ofsY1X+1];			
				int total=(c0+c1+c2+c3+c5+c6+c7+c8+c4);				
				total/=9;
			
				
				int particle=total;
				//particle--;
				this.particleFrontBuffer[ofsYX]=particle;
				
				if(particle<0)
				particle=0;
				particle>>=4;
				
				//Update pixels buffer
				int r=particle-150;
				int v=particle-150;
				int b=particle+50;
				int a=32+particle;
				
				//int a=255;
				
				if(a>240) a=240;
				if(r<0) r=0;
				if(v<0) v=0;
				if(b<0) b=0;
				if(r>200) r=200;
				if(v>200) v=200;
				if(b>255) b=255;
				this.pixels[ofsYX]=a<<24|r<<16|v<<8|b;		
				
				
				//Increment offset
				ofsYX++;
				ofsY1X++;
				ofsY1XN++;
			}
		}
	}	
	
}