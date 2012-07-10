package sate.pocketvdc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;


public class Box extends Prim 
{
	
	private Vector3d center;
	private Vector3d size;
	public int mBytesPerFloat = 4;
	
		 
		/** How many elements per vertex. */
		//private final int mStrideBytes = 3 * mBytesPerFloat;
		 
		/** Offset of the position data. */
		private final int mPositionOffset = 0;
		 
		/** Size of the position data in elements. */
		private final int mPositionDataSize = 3;
		 
		/** Offset of the color data. */
		private final int mColorOffset = 3;
		 
		/** Size of the color data in elements. */
		private final int mColorDataSize = 4;
		
		/** Size of the normal data in elements. */
		private final int mNormalDataSize = 3;
		
		/** Size of the texture coordinate data in elements. */
		private final int mTextureCoordinateDataSize = 2;
		
		FloatBuffer mBoxVertices;
		FloatBuffer mBoxNormals;
		FloatBuffer mBoxColors;
		FloatBuffer mBoxTextures;
	public Box(PrimProperties p) 
	{
		super(p);
	}
	
	@Override
	public void draw(int mPositionHandle, int mColorHandle, int mNormalHandle, int mTextureHandle) 
	{
		// Pass in the position information
				mBoxVertices.position(0);		
		        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
		        		0, mBoxVertices);        
		                
		        GLES20.glEnableVertexAttribArray(mPositionHandle);        
		        
		        // Pass in the color information
		        mBoxColors.position(0);
		        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
		        		0, mBoxColors);        
		        
		        GLES20.glEnableVertexAttribArray(mColorHandle);
		        
		        // Pass in the normal information
		        mBoxNormals.position(0);
		        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 
		        		0, mBoxNormals);
		        
		        GLES20.glEnableVertexAttribArray(mNormalHandle);
		        
		        
		        // Pass in the texture coordinate information
		        mBoxTextures.position(0);
		        GLES20.glVertexAttribPointer(mTextureHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
		        		0, mBoxTextures);
		        
		        GLES20.glEnableVertexAttribArray(mTextureHandle);
				
		        // Draw the cube.
		        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
	}

	@Override
	public void createVertices() 
	{
		
		this.center = this.prop.getCenter();
		this.size = this.prop.getSize().scale((float)(1.0/2.0));
		// TODO Auto-generated method stub
		final float[] cubePositionData =
			{
					// In OpenGL counter-clockwise winding is default. This means that when we look at a triangle, 
					// if the points are counter-clockwise we are looking at the "front". If not we are looking at
					// the back. OpenGL has an optimization where all back-facing triangles are culled, since they
					// usually represent the backside of an object and aren't visible anyways.
					
					// Front face
					center.x-size.x, center.y+size.y, center.z+size.z,				
					center.x-size.x, center.y-size.y, center.z+size.z,
					center.x+size.x, center.y+size.y, center.z+size.z, 
					center.x-size.x, center.y-size.y, center.z+size.z, 				
					center.x+size.x, center.y-size.y, center.z+size.z,
					center.x+size.x, center.y+size.y, center.z+size.z,
					
					// Right face
					center.x+size.x, center.y+size.y, center.z+size.z,				
					center.x+size.x, center.y-size.y, center.z+size.z,
					center.x+size.x, center.y+size.y, center.z-size.z,
					center.x+size.x, center.y-size.y, center.z+size.z,				
					center.x+size.x, center.y-size.y, center.z-size.z,
					center.x+size.x, center.y+size.y, center.z-size.z,
					
					// Back face
					center.x+size.x, center.y+size.y, center.z-size.z,				
					center.x+size.x, center.y-size.y, center.z-size.z,
					center.x-size.x, center.y+size.y, center.z-size.z,
					center.x+size.x, center.y-size.y, center.z-size.z,				
					center.x-size.x, center.y-size.y, center.z-size.z,
					center.x-size.x, center.y+size.y, center.z-size.z,
					
					// Left face
					center.x-size.x, center.y+size.y, center.z-size.z,				
					center.x-size.x, center.y-size.y, center.z-size.z,
					center.x-size.x, center.y+size.y, center.z+size.z, 
					center.x-size.x, center.y-size.y, center.z-size.z,				
					center.x-size.x, center.y-size.y, center.z+size.z, 
					center.x-size.x, center.y+size.y, center.z+size.z, 
					
					// Top face
					center.x-size.x, center.y+size.y, center.z-size.z,				
					center.x-size.x, center.y+size.y, center.z+size.z, 
					center.x+size.x, center.y+size.y, center.z-size.z, 
					center.x-size.x, center.y+size.y, center.z+size.z, 				
					center.x+size.x, center.y+size.y, center.z+size.z, 
					center.x+size.x, center.y+size.y, center.z-size.z,
					
					// Bottom face
					center.x+size.x, center.y-size.y, center.z-size.z,				
					center.x+size.x, center.y-size.y, center.z+size.z, 
					center.x-size.x, center.y-size.y, center.z-size.z,
					center.x+size.x, center.y-size.y, center.z+size.z, 				
					center.x-size.x, center.y-size.y, center.z+size.z,
					center.x-size.x, center.y-size.y, center.z-size.z,
			};	
			
			// R, G, B, A
			final float[] cubeColorData =
			{				
					// Front face (w)
					1.0f, 1.0f, 1.0f, 1.0f,				
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					
					// Right face (w)
					1.0f, 1.0f, 1.0f, 1.0f,				
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					
					// Back face (w)
					1.0f, 1.0f, 1.0f, 1.0f,				
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					
					// Left face (w)
					1.0f, 1.0f, 1.0f, 1.0f,				
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					
					// Top face (w)
					1.0f, 1.0f, 1.0f, 1.0f,				
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					
					// Bottom face (w)
					1.0f, 1.0f, 1.0f, 1.0f,				
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
			};
			
			// X, Y, Z
			// The normal is used in light calculations and is a vector which points
			// orthogonal to the plane of the surface. For a cube model, the normals
			// should be orthogonal to the points of each face.
			final float[] cubeNormalData =
			{												
					// Front face
					-1.0f, 0.0f, 0.0f,				
					-1.0f, 0.0f, 0.0f,
					-1.0f, 0.0f, 0.0f,
					-1.0f, 0.0f, 0.0f,				
					-1.0f, 0.0f, 0.0f,
					-1.0f, 0.0f, 0.0f,
					
					
					// Right face 
					0.0f, -1.0f, 0.0f,			
					0.0f, -1.0f, 0.0f,
					0.0f, -1.0f, 0.0f,
					0.0f, -1.0f, 0.0f,				
					0.0f, -1.0f, 0.0f,
					0.0f, -1.0f, 0.0f,
					
					
					// Back face 
					1.0f, 0.0f, 0.0f,				
					1.0f, 0.0f, 0.0f,
					1.0f, 0.0f, 0.0f,
					1.0f, 0.0f, 0.0f,				
					1.0f, 0.0f, 0.0f,
					1.0f, 0.0f, 0.0f,
					
					// Left face 
					0.0f, 1.0f, 0.0f,			
					0.0f, 1.0f, 0.0f,
					0.0f, 1.0f, 0.0f,
					0.0f, 1.0f, 0.0f,				
					0.0f, 1.0f, 0.0f,
					0.0f, 1.0f, 0.0f,
					
					
					// Top face 
					0.0f, 0.0f, 1.0f,				
					0.0f, 0.0f, 1.0f,
					0.0f, 0.0f, 1.0f,
					0.0f, 0.0f, 1.0f,				
					0.0f, 0.0f, 1.0f,
					0.0f, 0.0f, 1.0f,
					
					// Bottom face 
					0.0f, 0.0f, -1.0f,				
					0.0f, 0.0f, -1.0f,
					0.0f, 0.0f, -1.0f,
					0.0f, 0.0f, -1.0f,				
					0.0f, 0.0f, -1.0f,
					0.0f, 0.0f, -1.0f,
					
					
			};
			
			final float[] cubeTextureData =
				{												
						// Front face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,				
						
						// Right face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Back face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Left face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Top face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	
						
						// Bottom face 
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f
				};
			

		 this.mBoxVertices = ByteBuffer.allocateDirect(cubePositionData.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		 this.mBoxVertices.put(cubePositionData).position(0);
		 
		 this.mBoxColors = ByteBuffer.allocateDirect(cubeColorData.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		 this.mBoxColors.put(cubeColorData).position(0);
		 
		 this.mBoxNormals = ByteBuffer.allocateDirect(cubeNormalData.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		 this.mBoxNormals.put(cubeNormalData).position(0);
		 
		 this.mBoxTextures = ByteBuffer.allocateDirect(cubeTextureData.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		 this.mBoxTextures.put(cubeTextureData).position(0);
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

