package de.uni_ulm.ismm.stolperpfad.scanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;

/**
 * The scanner activity that uses the camera api and camera api example from the android developer
 * site: https://developer.android.com/guide/topics/media/camera#java
 */
public class ScannerActivity extends StolperpfadeAppActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    protected CameraDevice camera_device;
    protected CameraCaptureSession camera_capture_sessions;
    protected CaptureRequest.Builder capture_request_builder;
    private TextureView texture_view;
    private Size image_dimension;
    private ImageReader image_reader;
    private Handler background_handler;
    private HandlerThread background_thread;
    private AlertDialog dialog;
    private AsyncTask<Object, Object, Object> scan_task;

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { /*---*/ }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) { /*---*/ }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            camera_device = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera_device.close();
        }
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera_device.close();
            camera_device = null;
        }
    };

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_scanner);
        aq.id(R.id.scan_button).visible().clicked(my_click_listener);

        texture_view = (TextureView) aq.id(R.id.camera_preview).getView();
        assert texture_view != null;
        texture_view.setSurfaceTextureListener(textureListener);
    }

    /**
     * This method prepares the taking, saving and scanning of the picture captured with the camera
     */
    public void takePicture() {
        if(null == camera_device) {
            return;
        }
        createAndShowScanInfoDialog();
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert manager != null;
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera_device.getId());
            Size[] jpegSizes = Objects.requireNonNull(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(ImageFormat.JPEG);
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(texture_view.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = camera_device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(StolperpfadeApplication.DATA_FILES_PATH + "/img/last_scanned_stone.jpg");

            /*
             * This reader listener receives the image if it has been captured, therefor
             * the saving and scanning of the image will take place here
             */
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    try (Image image = reader.acquireLatestImage()) {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                        scan();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                /**
                 * Save the captured image to the external storage
                 *
                 * @param bytes the image as bytes
                 * @throws IOException if the image could not be saved
                 */
                private void save(byte[] bytes) throws IOException {
                    try (OutputStream output = new FileOutputStream(file)) {
                        output.write(bytes);
                    }
                }

                /**
                 * Calls the scan method for the captured image and tries to redirect the user
                 * to the corresponding information page
                 */
                @SuppressLint("StaticFieldLeak")
                private void scan() {
                    scan_task = new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            String result = scanImage();
                            tryToRedirect(result);
                            // TODO: try to find a person in this text and open their info page
                            //for p in persons if string contains entire name
                            return null;
                        }
                    };
                    scan_task.execute();
                }
            };

            reader.setOnImageAvailableListener(readerListener, background_handler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };

            camera_device.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, background_handler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) { /*---*/ }
            }, background_handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls the Tesseract API to scan text from an image
     *
     * @return the recognized text from that image
     */
    private String scanImage() {
        Bitmap image_bitmap = BitmapFactory.decodeFile(StolperpfadeApplication.DATA_FILES_PATH + "/img/last_scanned_stone.jpg");
        if(StolperpfadeApplication.getInstance().fileTreeIsNotReady()) {
            StolperpfadeApplication.getInstance().setupFileTree();
        }
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(StolperpfadeApplication.DATA_FILES_PATH, "deu");
        baseApi.setImage(image_bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();
        return recognizedText;
    }

    /**
     * Displays and information dialog letting the user know, that the sacnning of the image
     * is currently in progress
     */
    private void createAndShowScanInfoDialog() {
        if(dialog != null) {
            dialog.cancel();
            dialog = null;
        }
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Scanner aktiviert");
        builder.setMessage("Bild wird nach Namen durchsucht, bitte haben Sie Geduld...");
        builder.setOnCancelListener(dialogInterface -> createCameraPreview());
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * Displays an error dialog if something went wrong while scanning the image
     */
    public void error(){
        if(dialog != null) {
            dialog.cancel();
            dialog = null;
        }
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Kein Ergebnis");
        builder.setMessage("Es konnte kein Name erkannt werden...");
        builder.setNegativeButton("Abbrechen", (dialogInterface, i) -> {
            createCameraPreview();
            if(scan_task != null) {
                scan_task.cancel(true);
            }
            if(search_tag_and_redirect_task != null) {
                search_tag_and_redirect_task.cancel(true);
            }
            dialogInterface.cancel();
        });
        builder.setPositiveButton("Liste anzeigen", (dialogInterface, i) -> {
            dialogInterface.cancel();
            if(scan_task != null) {
                scan_task.cancel(true);
            }
            if(search_tag_and_redirect_task != null) {
                search_tag_and_redirect_task.cancel(true);
            }
            startActivity(new Intent(this, StoneListActivity.class));
        });
        builder.setOnCancelListener(dialogInterface -> createCameraPreview());
        dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates the preview for the camera as a simple display on the screen
     */
    public void createCameraPreview() {
        try {
            SurfaceTexture texture = texture_view.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(image_dimension.getWidth(), image_dimension.getHeight());
            Surface surface = new Surface(texture);
            capture_request_builder = camera_device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            capture_request_builder.addTarget(surface);
            camera_device.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == camera_device) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    camera_capture_sessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) { /*---*/ }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the camera, meaning it checks for permission to use the camera and makes it ready
     * to use for the preview and capture methods
     */
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = Objects.requireNonNull(manager).getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            image_dimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the camera preview on the scanner screen
     */
    protected void updatePreview() {
        if(camera_device == null) {
            return;
        }
        capture_request_builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            camera_capture_sessions.setRepeatingRequest(capture_request_builder.build(), null, background_handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the camera session
     */
    private void closeCamera() {
        if (null != camera_device) {
            camera_device.close();
            camera_device = null;
        }
        if (null != image_reader) {
            image_reader.close();
            image_reader = null;
        }
    }

    // FURTHER UTILITY METHODS

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            }
        }
    }

    protected void startBackgroundThread() {
        background_thread = new HandlerThread("Camera Background");
        background_thread.start();
        background_handler = new Handler(background_thread.getLooper());
    }

    protected void stopBackgroundThread() {
        background_thread.quitSafely();
        try {
            background_thread.join();
            background_thread = null;
            background_handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (texture_view.isAvailable()) {
            openCamera();
        } else {
            texture_view.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    public void endDialog() {
        if(dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }
}
