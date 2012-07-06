package sate.pocketvdc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;


public class Line extends Prim 
{
	
	private Vector3d center;
	private Vector3d size;
	public int mBytesPerFloat = 4;
	
		 
		/** How many elements per vertex. */
		private final int mStrideBytes = 7 * mBytesPerFloat;
		 
		/** Offset of the position data. */
		private final int mPositionOffset = 0;
		 
		/** Size of the position data in elements. */
		private final int mPositionDataSize = 3;
		 
		/** Offset of the color data. */
		private final int mColorOffset = 3;
		 
		/** Size of the color data in elements. */
		private final int mColorDataSize = 4;
		
		FloatBuffer mBoxVertices;
		Vector3d point1, point2;
		
		
	public Line(PrimProperties p, Vector3d point1, Vector3d point2) 
	{
		super(p);
		this.point1 = point1;
		this.point2 = point2;
	}
	
	@Override
	public void draw(int mPositionHandle, int mColorHandle, int mNormalHandle, int mTextureHandle) 
	{
		 // Pass in the position information
	    mBoxVertices.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, mBoxVertices);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    mBoxVertices.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, mBoxVertices);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	 
	    GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
	}

	@Override
	public void createVertices()
	{
		
		//this.center = this.prop.getCenter();
		//this.size = this.prop.getSize().scale((float)(1.0/2.0));
		// TODO Auto-generated method stub
		float[] vertices = 
			{
				this.point1.x, this.point1.y, this.point1.z,//0
				1, 0, 0, 1,
				this.point2.x, this.point2.y, this.point2.z,//1
				0, 1, 0, 1
			};

		 this.mBoxVertices = ByteBuffer.allocateDirect(vertices.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		 this.mBoxVertices.put(vertices).position(0);
	}

	@Override
	public Vector3d getCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3d getSize() {
		// TODO Auto-generated method stub
		return null;
	}

}

