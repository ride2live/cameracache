package com.example.camera_action;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraShotMainActivity extends Activity implements SurfaceHolder.Callback, PictureCallback, PreviewCallback {
	Camera mCamera;
	OutputStream os;
	ImageView iv;
	private int sch = 0;
	private byte[] mBuffer;
	Socket clientSock;
	SurfaceHolder mHolder;
	ServerSocket ss;
	private boolean connectionOk= false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_shot_main);
         iv = (ImageView)findViewById(R.id.imageView1);
        mCamera = Camera.open();
    	//mCamera.setParameters(mCamera.getParameters());
    	//Camera.Parameters camParam = mCamera.getParameters();
    	//mCamera.setParameters(camParam);
    	/*List<Camera.Size> sizes = camParam.getSupportedPictureSizes();
    	for (int i = 0; i < sizes.size(); i++) {
			Size size = sizes.get(i);
			System.out.println ("h " +size.height+"w " + size.width);
			
		}*/
    	SurfaceView surfView = (SurfaceView)findViewById(R.id.camsurface);
    	mHolder = surfView.getHolder();
    	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    	mHolder.setFormat(ImageFormat.NV21);
    	mHolder.setFixedSize(272, 272);
    	mHolder.addCallback(this);
    }

    private void runSocket() {
		// TODO Auto-generated method stub
    	new Thread(new Runnable() {
			
			

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (ss == null)
					ss = new ServerSocket(8082);
					System.out.println ("socket created");
					clientSock = ss.accept();
					clientSock.setKeepAlive(true);
					clientSock.setReuseAddress(true);
					System.out.println ("connected");
					connectionOk = true;
					os = clientSock.getOutputStream();
					
					/*FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/dcim/1.jpg");
					if (os !=null)
					{
						String header=
                                "HTTP/1.1 200 OK\n" +
                                "Content-type: image/jpeg"+"\n"+
                                "Content-Length: "+fis.available()+"\n" +
                                "\n";
						String s = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
			                    "Transitional//EN\">\n" +
			"<HTML>\n" +
			"<HEAD><TITLE>Hello WWW</TITLE></HEAD>\n" +
			"<BODY>\n" +
			"<H1>Hello WWW</H1>\n" +
			//"<img scr=\""+uri+ "\">"+
			"</BODY></HTML>\n\n";
						try {
							os.write(header.getBytes());
							int oneByte;
							while ((oneByte = fis.read()) !=-1)
							{
								os.write(oneByte);
							}
							os.close();
							clientSock.close();
							ss.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}*/
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();

	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera_shot_main, menu);
        return true;
    }
    public void takepicture (View V)
    {
		
    	
    	
    	//holder.setFixedSize(500, );
    	
    	
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println ("surf changed");
		System.out.println (format);
		System.out.println (width);
		System.out.println (height);
		Camera.Parameters camParam = mCamera.getParameters();
    	camParam.setAntibanding(Camera.Parameters .ANTIBANDING_OFF);
    	camParam.setColorEffect(Camera.Parameters.EFFECT_NONE);
    	camParam.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
    	camParam.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
    	camParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    	camParam.setPictureFormat(ImageFormat.JPEG);
    	camParam.setJpegQuality(100);
    	camParam.setPreviewFrameRate(1);
    	camParam.setPreviewFormat(ImageFormat.NV21);
    	camParam.setRotation(90);
    	camParam.setPreviewSize(272, 272);
		Size prevSize  = camParam.getPreviewSize();
    	System.out.println ("size prev " +camParam.getPreviewSize().height);
    	int bytsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21);
    	System.out.println ("bytsPerPixel " +bytsPerPixel);
    	int bufferSize= prevSize.height*prevSize.width*bytsPerPixel;
    	if (bufferSize <1)
    		return;
		mBuffer = new byte [bufferSize];
		mCamera.setParameters(camParam);
		mCamera.addCallbackBuffer(mBuffer);
		mCamera.setDisplayOrientation(90);
    	mCamera.setPreviewCallbackWithBuffer(this);
    	
		// TODO Auto-generated method stub

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//takeShot ();
		mCamera.startPreview();
    	//mCamera.stopPreview();
		 runSocket();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		
		System.out.println ("surf created");
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println ("surf destoyed");
	}
	/*public void takeAnotherShot (View V)
	{
		takeShot();
	}*/
	private void takeShot() {
		// TODO Auto-generated method stub
		AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		final Handler handler = new Handler();
		Timer t = new Timer();
		t.schedule(new TimerTask() {
		    public void run() {
		        handler.post(new Runnable() {
		            public void run() {
		            AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		            mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
		                    }
		                });
		            }
		    }, 2000); 
		
    	mCamera.takePicture(null, null, null, this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//mCamSH.removeCallback(this);
		try {
			mHolder.removeCallback(this);
		    mCamera.stopPreview();
		    
		} catch (Exception e) {
			// TODO: handle exception
		}
		mCamera.release();
	   // mCamera.stopPreview();
	    //mCamera.release();
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//mCamSH.removeCallback(this);
		try {
			mHolder.removeCallback(this);
		    mCamera.stopPreview();
		  
		} catch (Exception e) {
			// TODO: handle exception
		}
		mCamera.release();
		super.onDestroy();
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		System.out.println("onpictaken");
		FileOutputStream fos;
		try {
			
			
			fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/StreamGetter/temp/" + String.valueOf(Math.random()*1000)+".jpg");
			fos.write(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sch ++;
		Toast.makeText(this, String.valueOf(sch), Toast.LENGTH_SHORT).show();
		
		//iv.setImageBitmap(null);
		//Bitmap bitmap  = BitmapFactory.decodeByteArray(data, 0, data.length);
		
		//iv.setImageBitmap(bitmap);
		//bitmap=null;
		takeShot();
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		//System.out.println ("frame");
		if (!connectionOk)
		{	
			camera.addCallbackBuffer(data);
			return;
		}
		
		

		//FileOutputStream fos;
		YuvImage rawImage = new YuvImage(data, ImageFormat.NV21, 272, 272, null);
		
		String file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/StreamGetter/temp/" + String.valueOf(Math.random()*1000)+".jpg";
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				FileOutputStream fos = new FileOutputStream(file);
				
			System.out.println("os writing");
			boolean success = rawImage.compressToJpeg(new Rect(0, 0, 272, 272), 50, fos);
			int oneByte;
			try {
				
				
				InputStream is = new FileInputStream(file);
				String header=
		                "HTTP/1.1 200 OK\n" +
		                "Content-type: image/jpeg"+"\n"+
		                "Content-Lenght: "+ String.valueOf(is.available())+"\n"+
		                "\n";
				/*try {
					os.write(header.getBytes());
					//connectionOk = true;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/	
				while ((oneByte = is.read()) !=-1)
				{
					os.write(oneByte);
				}
				os.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			connectionOk = false;
			
			/*if (success)
			{
				try {
				os.close();
				os = null;
				clientSock.close();
				ss.close();
				runSocket();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				//fos.close();
			System.out.println("end write");
			
			//os.write(baos.toByteArray(), 0 , baos.toByteArray().length);
		
		//os=null;
		
		

		
			//YuvImage rawImage = new YuvImage(data, ImageFormat.NV21, 272, 272, null);
			//rawImage.compressToJpeg(new Rect(0, 0, 272, 272), 50, os);
			//System.out.println("os writing");
			/*try {
				
				os.write(data, 0, data.length);
				System.out.println("end write");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			/*try {
				os.close();
				os = null;
				ss.close();
				System.out.println("os closed");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		camera.addCallbackBuffer(data);
		runSocket();
		
	}

	private void runSocketAgain() {
		// TODO Auto-generated method stub
		try {
			clientSock = ss.accept();
			System.out.println ("connected again");
			os = clientSock.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
