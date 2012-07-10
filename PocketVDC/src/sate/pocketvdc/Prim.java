package sate.pocketvdc;

public abstract class Prim 
{
	
	protected PrimProperties prop;
	
	
	public Prim(PrimProperties p)
	{
		this.prop = p;
		//createVertices();
	}

	public abstract void createVertices();

	public abstract void draw(int mPositionHandle, int mColorHandle, int mNormalHandle, int mTextureHandle);
	
	public abstract Vector3d getCenter();
	
	public abstract Vector3d getSize();
}
