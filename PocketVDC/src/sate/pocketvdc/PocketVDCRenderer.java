package sate.pocketvdc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PocketVDCRenderer implements Renderer, OnTouchListener
{

	// Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space; it positions things relative to our eye.
	private float[] mViewMatrix = new float[16];
	
	// Store the projection matrix. This is used to project the scene onto a 2D viewport.
	private float[] mProjectionMatrix = new float[16];
	
	// Store the model matrix. This matrix is used to move models from object space (where each model can be thought of being located at the center of the universe) to world space.
    private float[] mModelMatrix = new float[16];
	
	// Hold Camera properties
	Camera mCamera = new Camera();
	
	// Store our model data in a float buffer
	private final FloatBuffer mGroundVertices;
	private final FloatBuffer mOceanVertices;
	private final FloatBuffer mSkyVertices;
	
	private final FloatBuffer mGroundColors;
	private final FloatBuffer mGroundNormals;
	
	private final FloatBuffer mOceanColors;
	private final FloatBuffer mOceanNormals;
	
	private final FloatBuffer mSkyColors;
	private final FloatBuffer mSkyNormals;
	
	private final FloatBuffer mGroundTextures;
	private final FloatBuffer mOceanTextures;
	private final FloatBuffer mSkyTextures;
	// This will be used to pass in the transformation matrix. 
	private int mMVPMatrixHandle;

	// This will be used to pass in model position information. 
	private int mPositionHandle;

	// This will be used to pass in model color information. 
	private int mColorHandle;
	
	// How many bytes per float
	private final int mBytesPerFloat = 4;
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mMVPMatrix = new float[16];
	 
	
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;	
	
	/** Size of the color data in elements. */
	private final int mColorDataSize = 4;	
	
	/** Size of the normal data in elements. */
	private final int mNormalDataSize = 3;

	
	/** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
	private final float[] mLightPosInEyeSpace = new float[4];
	
	/** This is a handle to our per-vertex cube shading program. */
	private int mPerVertexProgramHandle;
		
	/** This is a handle to our texture data. */
	private int mTextureDataHandle;
	
	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;
	
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;
	
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	
	/** Store our model data in a float buffer. */
	
	/** This will be used to pass in the modelview matrix. */
	private int mMVMatrixHandle;
	
	/** This will be used to pass in the light position. */
	private int mLightPosHandle;

	/** This will be used to pass in model normal information. */
	private int mNormalHandle;
	
	private List<Prim> objects = new ArrayList<Prim>();
	
	private List<Integer> mTextureArray = new ArrayList<Integer>();
	float angleX = 0.0f;
	float angleY = 0.0f;
	float dx = 0.0f;
	float dy = 0.0f;
	float diffX;
    float diffY;
    float width;
    float height;
    float totalDiffX;
    float totalDiffY;
    
    private static final int ZOOM = 2;
	private static final int LOOK = 1;
	private static final int NONE = 0;
	private int mode = NONE;
	private float oldDist;
	private float mPreviousX;
    private float mPreviousY;
    
    private final Context mActivityContext;
    
    public enum Shape 
    {
    	BOX, PRISM, PYRAMID, TETRAHEDRON, CYLINDER, HEMICYLINDER, CONE, HEMICONE, SPHERE, HEMISPHERE, TORUS, TUBE, RING, LINE;
    }

	public PocketVDCRenderer(final Context activityContext) 
	{
		mActivityContext = activityContext;
		
		// This triangle is red, green, and blue.
		final float[] groundVertices = 
			{
			// X,Y,Z
			// R, G, B, A
				 0f,   0f,   -2f,
				 255f, 0f,   -2f,
				 0f,   255f, -2f,
				 255f, 255f, -2f,				 
			};
		final float[] groundColors =
			{
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f
			};
		final float[] groundNormals =
			{
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f
			};
		final float[] oceanVertices = 
			{
				-500f, -500f, -10f,
				 500f, -500f, -10f,
				-500f,  500f, -10f,
				 500f,  500f, -10f,
			};
		final float[] oceanColors = 
			{
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
			};
		final float[] oceanNormals =
			{
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f
			};
		float textureRepeatSize = 100f;
		final float[] groundTextureVertices =
			{
				0.0f, 0.0f, 				
				0.0f, textureRepeatSize,
				textureRepeatSize, 0.0f,
				textureRepeatSize, textureRepeatSize,
			};
		textureRepeatSize = 50;
		final float[] oceanTextureVertices =
			{
				0.0f, 0.0f, 				
				0.0f, textureRepeatSize,
				textureRepeatSize, 0.0f,
				textureRepeatSize, textureRepeatSize,
			};
		final float[] skyVertices = 
			{
				// Front face
				-500.0f, 500.0f, 500.0f,				
				-500.0f, -500.0f, 500.0f,
				500.0f, 500.0f, 500.0f, 
				-500.0f, -500.0f, 500.0f, 				
				500.0f, -500.0f, 500.0f,
				500.0f, 500.0f, 500.0f,
				
				// Right face
				500.0f, 500.0f, 500.0f,				
				500.0f, -500.0f, 500.0f,
				500.0f, 500.0f, -500.0f,
				500.0f, -500.0f, 500.0f,				
				500.0f, -500.0f, -500.0f,
				500.0f, 500.0f, -500.0f,
				
				// Back face
				500.0f, 500.0f, -500.0f,				
				500.0f, -500.0f, -500.0f,
				-500.0f, 500.0f, -500.0f,
				500.0f, -500.0f, -500.0f,				
				-500.0f, -500.0f, -500.0f,
				-500.0f, 500.0f, -500.0f,
				
				// Left face
				-500.0f, 500.0f, -500.0f,				
				-500.0f, -500.0f, -500.0f,
				-500.0f, 500.0f, 500.0f, 
				-500.0f, -500.0f, -500.0f,				
				-500.0f, -500.0f, 500.0f, 
				-500.0f, 500.0f, 500.0f, 
				
				// Top face
				-500.0f, 500.0f, -500.0f,				
				-500.0f, 500.0f, 500.0f, 
				500.0f, 500.0f, -500.0f, 
				-500.0f, 500.0f, 500.0f, 				
				500.0f, 500.0f, 500.0f, 
				500.0f, 500.0f, -500.0f,
				
				// Bottom face
				500.0f, -500.0f, -500.0f,				
				500.0f, -500.0f, 500.0f, 
				-500.0f, -500.0f, -500.0f,
				500.0f, -500.0f, 500.0f, 				
				-500.0f, -500.0f, 500.0f,
				-500.0f, -500.0f, -500.0f,
				
				 
			};
		final float[] skyColors =
			{
				// Front face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				//right face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				//back face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				//left face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				//top face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				//bottom face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
			};
		final float[] skyNormals =
			{
				// Front face
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				
				// Right face 
				1.0f, 0.0f, 0.0f,				
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,				
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				
				// Back face 
				0.0f, 0.0f, -1.0f,				
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,				
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				
				// Left face 
				-1.0f, 0.0f, 0.0f,				
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,				
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				
				// Top face 
				0.0f, 1.0f, 0.0f,			
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,				
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				
				// Bottom face 
				0.0f, -1.0f, 0.0f,			
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,				
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f
			};
		textureRepeatSize = 5;
		final float[] skyTextureVertices =
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

				
		
		// Initialize the buffers.
		// Need to ensure native byte order for underlying C implementation of Opengl es 2.0
		this.mGroundVertices = ByteBuffer.allocateDirect(groundVertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mGroundVertices.put(groundVertices).position(0);
		
		this.mGroundColors = ByteBuffer.allocateDirect(groundColors.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mGroundColors.put(groundColors).position(0);
		
		this.mGroundNormals = ByteBuffer.allocateDirect(groundNormals.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mGroundNormals.put(groundNormals).position(0);	
		
		this.mGroundTextures = ByteBuffer.allocateDirect(groundTextureVertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mGroundTextures.put(groundTextureVertices).position(0);

		this.mOceanVertices = ByteBuffer.allocateDirect(oceanVertices.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mOceanVertices.put(oceanVertices).position(0);
		
		this.mOceanColors = ByteBuffer.allocateDirect(oceanColors.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mOceanColors.put(oceanColors).position(0);
		
		this.mOceanNormals = ByteBuffer.allocateDirect(oceanNormals.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mOceanNormals.put(oceanNormals).position(0);
		
		this.mOceanTextures = ByteBuffer.allocateDirect(oceanTextureVertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mOceanTextures.put(oceanTextureVertices).position(0);
		
		this.mSkyVertices = ByteBuffer.allocateDirect(skyVertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mSkyVertices.put(skyVertices).position(0);
		
		this.mSkyColors = ByteBuffer.allocateDirect(skyColors.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mSkyColors.put(skyColors).position(0);
		
		this.mSkyNormals = ByteBuffer.allocateDirect(skyNormals.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mSkyNormals.put(skyNormals).position(0);
		
		this.mSkyTextures = ByteBuffer.allocateDirect(skyTextureVertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mSkyTextures.put(skyTextureVertices).position(0);
		
		
		
	}

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
		// Set our per-vertex lighting program.
		GLES20.glUseProgram(mPerVertexProgramHandle);
		
		// Set program handles for cube drawing.
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVMatrix"); 
        mLightPosHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Normal"); 
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArray.get(0));
        drawPlane(this.mGroundVertices, this.mGroundColors, this.mGroundNormals, mGroundTextures);
        //bind the next texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArray.get(1));
        drawPlane(this.mOceanVertices, this.mOceanColors, this.mOceanNormals, mOceanTextures);
        //bind the next texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArray.get(3));
        drawCube();
        
        //Get changes in finger positions and add to total for moving and rotating
		totalDiffX += diffX;
		totalDiffY += diffY;
		angleX += dx;
        angleY += dy;
        //to prevent the user from looking more than straight up and less and straight down.
        if (Math.abs(angleY) > 1.5f)
        {
        	angleY -= dy;
        }
        
        //move the model matrix by finger distance changes for pinch zoom movement.
		Matrix.translateM(mModelMatrix, 0, this.diffX, this.diffY, 0);
		
		//rotate camera based on finger movements
		this.mCamera.look.x = (float)Math.cos(angleX);
		this.mCamera.look.y = (float)Math.sin(angleX);
		this.mCamera.look.z = (float)Math.tan(angleY);
		updateLookAt();

		//reset variables so that movement stops after finger movement stops
		this.dx = 0;
		this.dy = 0;
		this.diffX = 0;
		this.diffY = 0;
		

		//update the lookAt coordinates
		updateLookAt();
		//redraw each object in the array.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArray.get(2));
		synchronized (this.objects)
		{
			for(Prim p : this.objects)
			{
				p.draw(this.mPositionHandle, this.mColorHandle, this.mNormalHandle, this.mTextureCoordinateHandle);
			}
		}
		GLES20.glDisable(GLES20.GL_TEXTURE_2D);
	}

	/**
	 * Draws a triangle from the given vertex data.
	 *
	 * @param aTriangleBuffer The buffer containing the vertex data.
	 */
	
	public void addObject(float x, float y, float z, Shape shape)
	{
		PrimProperties a = new PrimProperties(); //new prim properties for size and center
		Prim result = null;
		switch (shape)
		{
		case BOX:
			a.setCenter(new Vector3d(x,y,z));
    	    a.setSize(new Vector3d(.5f,.5f,.5f));
    	    result = new Box(a);//new box object
    	    result.createVertices();
    	    break;
		case SPHERE:
			a.setCenter(new Vector3d(x,y,z));
			a.setSize(new Vector3d(1,5,5));
			result = new Sphere(a);
			result.createVertices();
			//new sphere object goes here
			break;
		case TETRAHEDRON:
			a.setCenter(new Vector3d(x,y,z));
			a.setSize(new Vector3d(1,1,1));
			//new tetrahedron object goes here
			break;
		case LINE:
			a.setCenter(new Vector3d(x,y,z));
			a.setSize(new Vector3d(1,1,1));
		}
		if (result != null)
		{
			synchronized (this.objects)
			{
			objects.add(result);
			}
		}
	}
	private void drawPlane(final FloatBuffer aPlaneBuffer, final FloatBuffer aColorBuffer, final FloatBuffer aNormalBuffer, final FloatBuffer aTextureBuffer)
	{
	    // Pass in the position information
	    aPlaneBuffer.position(0);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	            0, aPlaneBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	 
	    // Pass in the color information
	    aColorBuffer.position(0);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            0, aColorBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	 
	    // Pass in the normal information
        aNormalBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 
        		0, aNormalBuffer);
        
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        if (aTextureBuffer != null)
        {
        	// Pass in the texture coordinate information
        	aTextureBuffer.position(0);
        	GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
        			0, aTextureBuffer);
        
        	GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        }
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	 
	    // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);                
        
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	 
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

	    //mLightPosInEyeSpace[2] = 100;
	    // Pass in the light position in eye space.        
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
        
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
	}
	
	private int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) 
		{
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) 
			{
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	/**
	 * Helper function to compile and link a program.
	 * 
	 * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) 
	{
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			// Bind attributes
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; i++)
				{
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}
			
			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) 
			{				
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{

		// Set the OpenGL viewport to the same size as the surface
		GLES20.glViewport(0, 0, width,height);
		this.width = width;
		this.height = height;
		// Create a new perspective projection matrix. the height will stay the same while the width will vary as per aspect ratio
		final float ratio = (float)width/height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 1000.0f;
		
		Matrix.frustumM(this.mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{
		
		// Set the background clear color to sky blue
		GLES20.glClearColor(135.0f/255.0f, 206.0f/255.0f, 255.0f/255f, 1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		
		// Position the eye behind the origin
		this.mCamera.eye = new Vector3d(0.0f, 0.0f, 0.0f);
		
		// We are looking toward the distance
		this.mCamera.look = new Vector3d(0f, 0f, 0f);
		
		// Set our up vector. This is where our head would be pointing were we holding the camera
		this.mCamera.up = new Vector3d(0.0f, 0.0f, 1.0f);
		
		// Set the view matrix. This matrix can be said to represent the camera position.
	    // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
	    // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(this.mViewMatrix, 0,
				this.mCamera.eye.x, this.mCamera.eye.y,this.mCamera.eye.z, 
				this.mCamera.look.x, this.mCamera.look.y, this.mCamera.look.z,
				this.mCamera.up.x, this.mCamera.up.y, this.mCamera.up.z);
		
		Matrix.setIdentityM(mModelMatrix, 0);
		final String vertexShader = getVertexShader();   		
 		final String fragmentShader = getFragmentShader();			
		
		final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);		
		final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);		
		
		mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});								                                							       

        addTexture(R.drawable.grass_texture_1);
        addTexture(R.drawable.azure_waters);
        addTexture(R.drawable.wood_texture_1);
        addTexture(R.drawable.sky_texture_1);
        
        
	}
	
	protected String getVertexShader()
	{
		// Define our per-pixel lighting shader.
        final String perPixelVertexShader =
			"uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
		  + "uniform mat4 u_MVMatrix;       \n"		// A constant representing the combined model/view matrix.
		  			
		  + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
		  + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
		  + "attribute vec3 a_Normal;       \n"		// Per-vertex normal information we will pass in.
		  + "attribute vec2 a_TexCoordinate; \n"
		  
		  + "varying vec3 v_Position;       \n"		// This will be passed into the fragment shader.
		  + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.
		  + "varying vec3 v_Normal;         \n"		// This will be passed into the fragment shader.
		  + "varying vec2 v_TexCoordinate;  \n"
		  
		// The entry point for our vertex shader.  
		  + "void main()                                                \n" 	
		  + "{                                                          \n"
		// Transform the vertex into eye space.
		  + "   v_Position = vec3(u_MVMatrix * a_Position);             \n"
		// Pass through the texture coordinate.
		  + "	v_TexCoordinate = a_TexCoordinate;						\n"
		// Pass through the color.
		  + "   v_Color = a_Color;                                      \n"
		// Transform the normal's orientation into eye space.
		  + "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));      \n"
		// gl_Position is a special variable used to store the final position.
		// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
		  + "   gl_Position = u_MVPMatrix * a_Position;                 \n"      		  
		  + "}                                                          \n";      
		
		return perPixelVertexShader;
	}
	
	protected String getFragmentShader()
	{
		final String perPixelFragmentShader =
			"precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a 
																	// precision in the fragment shader.
		  + "uniform vec3 u_LightPos;       \n"	    // The position of the light in eye space.
		  + "uniform sampler2D u_Texture;   \n"     //the input texture
		  
		  + "varying vec3 v_Position;		\n"		// Interpolated position for this fragment.
		  + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the 
		  											// triangle per fragment.
		  + "varying vec3 v_Normal;         \n"		// Interpolated normal for this fragment.
		  + "varying vec2 v_TexCoordinate;  \n"    //interpolated texture coord per fragment
		  
		// The entry point for our fragment shader.
		  + "void main()                    \n"		
		  + "{                              \n"
		// Will be used for attenuation.
		  + "   float distance = length(u_LightPos - v_Position);                  \n"
		// Get a lighting direction vector from the light to the vertex.
		  + "   vec3 lightVector = normalize(u_LightPos - v_Position);             \n" 	
		// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
		// pointing in the same direction then it will get max illumination.
		  + "   float diffuse = max(dot(v_Normal, lightVector), 0.0);              \n" 	  		  													  
		// Add attenuation. 
		  + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));  		   \n"
		  
		//Add ambient lighting
		  +"	diffuse = diffuse + 0.7;                                          \n"
		// Multiply the color by the diffuse illumination level and texture value to get final output color.
		  + "	gl_FragColor = (v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate));     \n"		
		  + "}                                                                     \n";	
		
		return perPixelFragmentShader;
	}
	
	private void updateLookAt()
	{
		Matrix.setLookAtM(this.mViewMatrix, 0, 
				this.mCamera.eye.x, this.mCamera.eye.y,this.mCamera.eye.z, 
				this.mCamera.look.x, this.mCamera.look.y, this.mCamera.look.z,
				this.mCamera.up.x, this.mCamera.up.y, this.mCamera.up.z);
	}

	//different version of glu.glunproject
	public static Vector3d unProject(
	        float winx, float winy, float winz,
	        float[] resultantMatrix,
	        float width, float height)
	{
		winy = height-winy;
	    float[] m = new float[16],
	    		in = new float[4],
	    		out = new float[4];
	    Matrix.invertM(m, 0, resultantMatrix, 0);
	    in[0] = (winx / width) * 2 - 1;
	    in[1] = (winy / height) * 2 - 1;
	    in[2] = 2 * winz - 1;
	    in[3] = 1;
	    Matrix.multiplyMV(out, 0, m, 0, in, 0);

	    if (out[3]==0)
	        return null;

	    out[3] = 1/out[3];
	    return new Vector3d(out[0] * out[3], out[1] * out[3], out[2] * out[3]);
	}
	
	public static int loadTexture(final Context context, final int resourceId)
	{
	    final int[] textureHandle = new int[1];
	 
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
	 
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }
	 
	    return textureHandle[0];
	}
	
	public void addTexture(int resourceID)
	{
		mTextureArray.add(loadTexture(mActivityContext, resourceID));
	}
	
	private void drawCube()
	{		
		// Pass in the position information
		mSkyVertices.position(0);		
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
        		0, mSkyVertices);        
                
        GLES20.glEnableVertexAttribArray(mPositionHandle);        
        
        // Pass in the color information
        mSkyColors.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
        		0, mSkyColors);        
        
        GLES20.glEnableVertexAttribArray(mColorHandle);
        
        // Pass in the normal information
        mSkyNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 
        		0, mSkyNormals);
        
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        
        // Pass in the texture coordinate information
        mSkyTextures.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
        		0, mSkyTextures);
        
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
		// This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);   
        
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);                
        
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        
        // Pass in the light position in eye space.        
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
        
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);                               
	}	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		//x and y coordinates of touch location on screen.
		float x = event.getX();
    	float y = event.getY();
    	
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			//Initial pointer down
			mode = LOOK;
			
			Vector3d near = unProject(x, y, 0, mMVPMatrix, this.width, this.height);
			Vector3d far = unProject(x, y, 1, mMVPMatrix, this.width, this.height);
			Vector3d pickingRay = far.subtract(near);
			Vector3d normal = new Vector3d(0,0,1);
			if (normal.dot(pickingRay) != 0 && pickingRay.z < 0)
			{
				Vector3d diff = new Vector3d(-totalDiffX, -totalDiffY, 0);
				float t = (-2f-normal.dot(diff))/(normal.dot(pickingRay));
				pickingRay = diff.add(pickingRay.scale(t));
				boolean b = false;
				synchronized (this.objects)
				{
					int counter = 0; //counter for checking current position.
					List<Integer> positions = new ArrayList<Integer>(); //array for storing object array positions that you've "touched"
					for (Prim p : this.objects)
					{
						Vector3d objectCenter = p.getCenter();
						float d = (((objectCenter.subtract(near)).cross(objectCenter.subtract(far))).length())/((far.subtract(near)).length());
						if (d < p.getSize().x || d < p.getSize().y || d < p.getSize().z)
						{
							positions.add(counter); //add position value to array for checking if closest to camera than others you touched.
							b = true; //stops from creating new objects so you don't erase and create at the same time.
						}
						counter++;
					}
					if (positions.size() > 0)
					{
						Vector3d currentCenter; //variable where current center will be stored.
						currentCenter = objects.get(positions.get(0)).getCenter(); //get the first center for distance checking
						double minDistance = Math.sqrt((double)(Math.pow((double)currentCenter.x-totalDiffX, 2.0)+Math.pow((double)currentCenter.y-totalDiffY, 2.0)+Math.pow((double)currentCenter.z, 2.0))); //distance formula of the first position. the first position is initially set as the smallest.
						int finalMin = 0; //int value where the smallest distance position is stored for removal
						for (int i = 1; i < positions.size(); i++) //starting at one because 0 was already checked before the loop
						{
							currentCenter = objects.get(positions.get(i)).getCenter(); //get the current center of the object for testing distance.
							double currentDistance = Math.sqrt((double)(Math.pow((double)currentCenter.x-totalDiffX, 2.0)+Math.pow((double)currentCenter.y-totalDiffY, 2.0)+Math.pow((double)currentCenter.z, 2.0)));//distance formula for the current position. This is tested to see if smaller than the one before it.
							if (currentDistance < minDistance)
							{
								minDistance = currentDistance; //min becomes the current because current is smaller
								finalMin = i; //current position is stored as smallest distance position.
							}

							
						}
						this.objects.remove((int)positions.get(finalMin)); //remove object closest to camera that you touch.
					}
					counter = 0;
				}
				if (b == false && pickingRay.x < 254.75 && pickingRay.x > .25 && pickingRay.y > .25 && pickingRay.y < 254.75)
				{
					addObject(pickingRay.x, pickingRay.y, pickingRay.z+.25f, Shape.BOX);
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// non-primary pointer down
			oldDist = spacing(event);
			// check distance as one finger may be reported as multiple ones very close together
			if(oldDist > 10f)
			{
				mode = ZOOM;
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			if(event.getPointerCount() >= 2 && this.mode != ZOOM)
			{
				oldDist = spacing(event);
				// check distance as one finger may be reported as multiple ones very close together
				if(oldDist > 10f)
				{
					mode = ZOOM;
				}
			}
			if(mode == ZOOM )
			{
				float newDist = spacing(event);
				float diff = newDist - oldDist;
				if(newDist > 10f && Math.abs(diff) > 20f)
				{
					Vector3d lookAtVector = this.mCamera.look.subtract(this.mCamera.eye);
					lookAtVector = lookAtVector.normalize();
					lookAtVector = lookAtVector.scale(diff*.00025f);
					this.diffX -= lookAtVector.x;
					this.diffY -= lookAtVector.y;
				}
				
			} 
			else if (mode == LOOK)
			{
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				this.dx = dx*.008f;
				this.dy = dy*.008f;

				updateLookAt();
			}
			break;
			
		case MotionEvent.ACTION_POINTER_UP:
			if(event.getPointerCount() == 1)
			{
				mode = LOOK;
			}
			
		case MotionEvent.ACTION_UP:
			mode = NONE;
			break;
		}
		mPreviousX = x;
    	mPreviousY = y;
		return true;
	}
	public void clearObjects()
	{
		synchronized (this.objects)
		{	
		objects.clear();
		}
	}
	
	private float spacing(MotionEvent event)
	{
		if (event.getPointerCount()>=2)
		{
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}
		return this.oldDist;
	}

}
