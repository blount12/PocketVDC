package sate.pocketvdc;


//Very basic implementation for a 3d Vector/point
// Possibly use this(http://objectclub.jp/download/vecmath_e) for vector later?
public class Vector3d
{
	public float x, y, z;
	
	public Vector3d()
	{
		x = y = z = 0.0f;
	}
	
	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector3d(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d add(Vector3d v) 
	{
		
		Vector3d result = new Vector3d();
		result.x = this.x;
		result.y = this.y;
		result.z = this.z;
		if(v != null){
			result.x += v.x;
			result.y += v.y;
			result.z += v.z;
		}
		
		return result;
	}
	
	
	public Vector3d subtract(Vector3d v)
	{
		Vector3d result = new Vector3d();
		result.x = this.x;
		result.y = this.y;
		result.z = this.z;
		if (v != null)
		{
			result.x -= v.x;
			result.y -= v.y;
			result.z -= v.z;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return this cross product vVector
	 */
	public Vector3d cross(Vector3d vVector)
	{
		Vector3d result = new Vector3d();
		// Cross product
		result.x = (this.y * vVector.z) - (this.z * vVector.y);
		result.y = (this.z * vVector.x) - (this.x * vVector.z);
		result.z = (this.x * vVector.y) - (this.y * vVector.x);
		return result;
	}
	
	/**
	 * 
	 * @param vVector
	 * @return dot product between this and vVector
	 */
	public float dot(Vector3d vVector)
	{
		float result;
		
		result = (this.x * vVector.x) + (this.y * vVector.y) + (this.z * vVector.z);

		return result;
	}
	
	public Vector3d scale(float scale)
	{
		Vector3d result = new Vector3d();
		result.x = this.x * scale;
		result.y = this.y * scale;
		result.z = this.z * scale;
		
		return result;
	}
	
	public Vector3d normalize()
	{
		float length = this.length();
		Vector3d result = new Vector3d();
		result.x = this.x/length;
		result.y = this.y/length;
		result.z = this.z/length;
		
		return result;
	}
	
	public float length()
	{
		float value = (this.x * this.x) + (this.y * this.y) + (this.z + this.z);
		if(value < 0){
			value *= -1;
		}
		return (float) Math.sqrt(value );
	}
	
	@Override
	public String toString()
	{
		String result;
		
		result = this.x + "," + this.y + "," + this.z;
		
		return result;
		
	}
}


