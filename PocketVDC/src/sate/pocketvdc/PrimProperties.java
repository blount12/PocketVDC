package sate.pocketvdc;

public class PrimProperties 
{

	private Vector3d center;
	private Vector3d size;
	//private Quaternian rotation;
	public Vector3d getCenter()
	{
		return this.center;
	}
	public Vector3d getSize()
	{
		return this.size;
	}
	public Vector3d setCenter(Vector3d v)
	{
		this.center = v;
		return this.center;
	}
	public Vector3d setSize(Vector3d w)
	{
		this.size = w;
		return this.size;
	}
}
