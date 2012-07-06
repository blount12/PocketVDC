package sate.pocketvdc;

//Basic class representing properties of camera
public class Camera
{
	// Eye position, lookat vector, and up vector
	public Vector3d eye, look, up;
	
	public Camera()
	{
		// have to be careful will all public attributes(not good practice BTW), so initialize here
		this.eye = new Vector3d();
		this.look = new Vector3d();
		this.up = new Vector3d();
	}
}
