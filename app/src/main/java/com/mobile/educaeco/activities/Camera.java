package com.mobile.educaeco.activities;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.adapters.AdapterPratica;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Camera extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 1;
    private Map<String, String> docData = new HashMap<>();
    private Database database = new Database();
    private ExecutorService cameraExecutor;
    private androidx.camera.view.PreviewView viewFinder;
    private ImageCapture imageCapture;
    private ImageView foto;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private static final String TAG = "CameraXGaleria";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String[] REQUIRED_PERMISSIONS;
    static {
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(android.Manifest.permission.CAMERA);
//        requiredPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        requiredPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS = requiredPermissions.toArray(new String[0]);
    }

    TextView ok, cancelar;
    ImageView btnTakePhoto, flipCamera;
    AdapterPratica adapterPratica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        viewFinder = findViewById(R.id.viewFinder);
        foto = findViewById(R.id.foto);
        ok = findViewById(R.id.ok);
        cancelar = findViewById(R.id.cancelar);
        cameraExecutor = Executors.newSingleThreadExecutor();
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        flipCamera = findViewById(R.id.flipCamera);

        if (allPermissionsGranted()) {
            startCamera();
            Log.d(TAG, "Permissão de leitura do armazenamento concedida");
        } else {
            requestPermissions();
            Log.d(TAG, "Permissão de leitura do armazenamento negada");
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences2 = getSharedPreferences("pratica", 0);
                String pratica = sharedPreferences2.getString("id_pratica", "");
                Log.d("pratica na Camera", pratica);
                database.uploadFoto(getBaseContext(), foto, docData, pratica);
                Bundle bundle = getIntent().getExtras();
                String status = "", imgStorage = "", id_aluno = "";
                Date dataEntrega = null;

                SharedPreferences sharedPreferences = getSharedPreferences("aluno", MODE_PRIVATE);
                id_aluno = sharedPreferences.getString("id_aluno", "");
                status = "Ver imagem em anexo";
                dataEntrega = new Date();
                imgStorage = "galeria/" + sharedPreferences2.getString("imageUri", "");
                docData.put("pratica", pratica);
                docData.put("status", status);
                docData.put("imgStorage", imgStorage);
                docData.put("id_aluno", id_aluno);
                docData.put("dataEntrega", String.valueOf(dataEntrega));

                Log.d("dataEntrega", String.valueOf(dataEntrega));
                database.registroPratica(pratica, id_aluno, imgStorage, dataEntrega);
                Intent intent = new Intent(getBaseContext(), Main.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("tela", "pratica");
                startActivity(intent);
                finish();
            }
        });

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                } else {
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                }
                startCamera();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("pratica", MODE_PRIVATE);
                takeFoto(sharedPreferences.getString("id_pratica", ""));
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String action = bundle.getString("ação");
            if (action != null) {
                if (action.equals("galeria")) {
                    btnTakePhoto.setVisibility(View.GONE);
                    flipCamera.setVisibility(View.GONE);
                    loadImage();
                } else {
                    ok.setVisibility(View.INVISIBLE);
                    cancelar.setVisibility(View.INVISIBLE);
                }
            }
        }

        // Verifique se a permissão de leitura do armazenamento foi concedida
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            loadImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage(); // Carregar a imagem se a permissão for concedida
            } else {
                Log.e("Camera", "Permissão de leitura do armazenamento negada");
            }
        }
    }

    private void loadImage() {
        // Receber a URI da imagem
        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            foto.setImageURI(imageUri); // Exibir a imagem
            Log.d("Camera", "URI da imagem: " + imageUriString);
        } else {
            Log.e("Camera", "URI da imagem nulo");
        }

        ImageView btnTakePhoto = findViewById(R.id.btnTakePhoto);
        ImageView flipCamera = findViewById(R.id.flipCamera);

        foto.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);
        cancelar.setVisibility(View.VISIBLE);
        viewFinder.setVisibility(View.INVISIBLE);
        btnTakePhoto.setVisibility(View.GONE);
        flipCamera.setVisibility(View.GONE);
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if ( ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ok.setVisibility(View.INVISIBLE);
        cancelar.setVisibility(View.INVISIBLE);
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Obtenha a instância do CameraProvider
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Configure o Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                // Configure o ImageCapture
                imageCapture = new ImageCapture.Builder().build();

                // Desvincule qualquer uso anterior da câmera
                cameraProvider.unbindAll();

                // Vincule o Preview e o ImageCapture à câmera
                cameraProvider.bindToLifecycle(
                        this, // Ciclo de vida da atividade
                        cameraSelector, // Câmera escolhida (frontal ou traseira)
                        preview, // Vincula o Preview à câmera
                        imageCapture // Vincula a captura de imagem
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Falha ao acessar a câmera", e);
            }
        }, ContextCompat.getMainExecutor(this)); // Certifique-se de executar no thread principal
    }


    private void requestPermissions() {
        // Verificar permissões ainda não concedidas
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // Se houver permissões a serem solicitadas, faça a solicitação
        if (!permissionsToRequest.isEmpty()) {
            activityResultLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            // Todas as permissões já foram concedidas
            startCamera();
        }
    }


    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    if (!entry.getValue()) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    // Se todas as permissões foram concedidas
                    startCamera();
                } else {
                    // Se alguma permissão foi negada
                    Toast.makeText(getApplicationContext(), "Permissão negada. Tente de novo!", Toast.LENGTH_SHORT).show();
                }
            }
    );


    private void takeFoto(String tema) {
        if ( imageCapture == null ) {
            return;
        }

        //Definindo nome e caminho da imagem
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
        tema = tema.replace(" ", "-");
        String turma = getSharedPreferences("aluno", MODE_PRIVATE).getString("turma", "");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, tema + "-" + name + "-" + turma + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/CameraXGaleria");

        //Carregando imagem
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation = Surface.ROTATION_0; // Rotação padrão

                if (orientation >= 45 && orientation < 135) {
                    // Se o dispositivo está de cabeça para baixo
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 135 && orientation < 225) {
                    // Se o dispositivo está virado para a esquerda
                    rotation = Surface.ROTATION_90;
                } else if (orientation >= 225 && orientation < 315) {
                    // Se o dispositivo está virado para a direita
                    rotation = Surface.ROTATION_270;
                }

                if (imageCapture != null) {
                    imageCapture.setTargetRotation(rotation);
                }
            }
        };
        orientationEventListener.enable();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults results) {
                Uri savedUri = results.getSavedUri();

                // Checa se a imagem foi salva corretamente
                if (savedUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), savedUri);
                        Matrix matrix = new Matrix();

                        // Verifica a orientação da câmera e aplica a rotação
                        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            // Para a câmera traseira, rotaciona a imagem 90 graus
                            matrix.postRotate(90);
                        } else if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                            // Para a câmera frontal, rotaciona a imagem 270 graus
                            matrix.postRotate(270);
                        }

                        // Aplica a transformação
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        foto.setImageBitmap(rotatedBitmap); // Exibe a imagem rotacionada

                        // Salva a imagem rotacionada
                        saveRotatedImage(rotatedBitmap, savedUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ImageView btnTakePhoto = findViewById(R.id.btnTakePhoto);
                ImageView flipCamera = findViewById(R.id.flipCamera);

                foto.setVisibility(View.VISIBLE);
                ok.setVisibility(View.VISIBLE);
                cancelar.setVisibility(View.VISIBLE);
                viewFinder.setVisibility(View.INVISIBLE);
                btnTakePhoto.setVisibility(View.GONE);
                flipCamera.setVisibility(View.GONE);

                orientationEventListener.disable();
            }

            private void saveRotatedImage(Bitmap bitmap, Uri uri) {
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Salva a imagem rotacionada
                } catch (IOException e) {
                    Log.e(TAG, "Erro ao salvar imagem rotacionada: " + e.getMessage());
                }
            }


            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Erro ao salvar imagem" + exception.getMessage());
            }
        });
    }

}
