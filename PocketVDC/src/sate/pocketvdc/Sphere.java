package sate.pocketvdc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;


public class Sphere extends Prim 
{
	
	private Vector3d center;
	private Vector3d size;
	public int mBytesPerFloat = 4;
	
		 
		/** How many elements per vertex. */
		private final int mStrideBytes = 3*mBytesPerFloat;
		 
		/** Offset of the position data. */
		private final int mPositionOffset = 0;
		 
		/** Size of the position data in elements. */
		private final int mPositionDataSize = 3;
		 
		/** Offset of the color data. */
		private final int mColorOffset = 3;
		 
		/** Size of the color data in elements. */
		private final int mColorDataSize = 4;
		

		
		private FloatBuffer sphereVertex;
	    private FloatBuffer sphereNormal;
	    static float sphere_parms[]=new float[3];

	    double mRadius;
	    double mStep=3;
	    float mVertices[];
	    private static double DEG = Math.PI/180;
	    int mPoints;
	    
	    
	public Sphere(PrimProperties p) 
	{
		super(p);
	}
	
	@Override
	public void draw(int mPositionHandle, int mColorHandle, int mNormalHandle, int mTextureHandle) 
	{
		 // Pass in the position information
		GLES20.glFrontFace(GLES20.GL_CW);
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, true,
	            mStrideBytes, sphereVertex);
	 
	    
	    
	    // Pass in the color information
	    /*
	    mBoxVertices.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, mBoxVertices);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	 	*/
	    //GLES20.glDrawElements(GLES20.GL_LINE_LOOP, mPoints*3, GLES20.GL_FLOAT, sphereVertex);
	    GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mPoints);
	    GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	@Override
	public void createVertices() 
	{
		this.center = this.prop.getCenter();
		this.mRadius = this.prop.getSize().x;
		
		double dTheta = mStep * DEG;
        double dPhi = dTheta;
        int points = 0;
        sphereVertex = ByteBuffer.allocateDirect(400000).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(double phi = -(Math.PI); phi <= Math.PI; phi+=dPhi) {
            //for each stage calculating the slices
            for(double theta = 0.0; theta <= (Math.PI * 2); theta+=dTheta) {
                sphereVertex.put((float) (this.mRadius * Math.sin(phi) * Math.cos(theta)) );
                sphereVertex.put((float) (mRadius * Math.sin(phi) * Math.sin(theta)) );
                sphereVertex.put((float) (mRadius * Math.cos(phi)) );
                points++;
            }
        }
        sphereVertex.position(0);
        this.mPoints = points;
	}

	@Override
	public Vector3d getCenter() {
		// TODO Auto-generated method stub
		return this.center;
	}

	@Override
	public Vector3d getSize() {
		// TODO Auto-generated method stub
		return this.size;
	}

}

