package etn.app.danghoc.shoppingclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etn.app.danghoc.shoppingclient.Adapter.CategoryProductAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.Model.UpdateModel;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddNewProduct extends AppCompatActivity implements View.OnClickListener {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Spinner spinner;
    CategoryProductAdapter adapter;
    List<CategoryProduct> categoryProducts;
    //ApiService apiService;
    IMyShoppingAPI shoppingAPI;
    Uri picUri;
    ImageButton btn_choose_img;
    Button btn_add_pd;

    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;



    Bitmap mBitmap;
    ImageView image_pd;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.edt_description_pd)
    EditText edt_description_pd;
    @BindView(R.id.edt_name_pd)
    EditText edt_name_pd;
    @BindView(R.id.edt_price_pd)
    EditText edt_price_pd;


    int idDanhMuc = -99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);


        ButterKnife.bind(this);

        btn_add_pd = findViewById(R.id.btn_add_pd);
        btn_add_pd.setOnClickListener(this);


        btn_choose_img = findViewById(R.id.btn_choose_img);
        btn_choose_img.setOnClickListener(this);

        image_pd = findViewById(R.id.image_pd);


        initRetrofitClient();

        loadSpinner();
        progress_bar.setVisibility(View.GONE);
        compositeDisposable = new CompositeDisposable();
    }


    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();


        shoppingAPI = new RetrofitClient().getInstance(Common.API_RESTAURANT_ENDPOINT)
                .create(IMyShoppingAPI.class);


    }

    private void loadSpinner() {
        spinner = findViewById(R.id.spinner_category);
        progress_bar.setVisibility(View.VISIBLE);
        compositeDisposable.add(shoppingAPI.getDanhMuc("1234")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(danhMucModel -> {
                    if (danhMucModel.isSuccess()) {
                        progress_bar.setVisibility(View.GONE);
                        categoryProducts = danhMucModel.getResult();
                        adapter = new CategoryProductAdapter(this,
                                R.layout.item_select_product, categoryProducts);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                idDanhMuc = categoryProducts.get(i).getIdDanhMuc();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    } else{
                        progress_bar.setVisibility(View.GONE);
                        Toast.makeText(this, "load category fail", Toast.LENGTH_SHORT).show();

                    }

                }, throwable -> {
                    progress_bar.setVisibility(View.GONE);
                    Toast.makeText(this, "[LOAD CATEGORY PRODUCT]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==IMAGE_PICK_CODE){

            String filePath = getImageFilePath(data);
            if (filePath != null) {
                mBitmap = BitmapFactory.decodeFile(filePath);
                image_pd.setImageBitmap(mBitmap);
            }
            // mImageView.setImageURI(data.getData());
        }
    }

    private String getImageFromFilePath(Intent data) {


         return getPathFromURI(data.getData());

    }

    private String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private void multipartImageUpload() {

        progress_bar.setVisibility(View.VISIBLE);
        if (edt_description_pd.getText().toString().trim().length() == 0
                || edt_name_pd.getText().toString().trim().length() == 0
                || edt_price_pd.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "chưa nhập đầy đủ thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File filesDir = getApplicationContext().getFilesDir();
            File file = new File(filesDir, "image" + ".png");

            OutputStream os;
            try {
                os = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();


            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "myFile");


            Call<UpdateModel> req = shoppingAPI.postImage2(body, name);
            req.enqueue(new Callback<UpdateModel>() {
                @Override
                public void onResponse(Call<UpdateModel> call, Response<UpdateModel> response) {

                    String tenSp = edt_name_pd.getText().toString();
                    float giaSp = Float.parseFloat(edt_price_pd.getText().toString());
                    String mota = edt_description_pd.getText().toString();

                    if (response.code() == 200) {
                        progress_bar.setVisibility(View.GONE);
                        compositeDisposable.add(shoppingAPI.uploadSanPham(
                                Common.API_KEY,
                                Common.currentUser.getIdUser(),
                                tenSp, giaSp, mota, idDanhMuc,
                                new StringBuilder(Common.API_RESTAURANT_ENDPOINT)
                                        .append(response.body().getMessage()).toString()

                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(uploadSanPhamModel -> {
                                    Toast.makeText(AddNewProduct.this, "them san pham thanh cong", Toast.LENGTH_SHORT).show();
                                }, throwable -> {
                                    Toast.makeText(AddNewProduct.this, "[UPLOAD NEW PRODUCT]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));
                    }
                    else{
                        progress_bar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onFailure(Call<UpdateModel> call, Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                    Log.e("ERROR", t.toString());
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @OnClick(R.id.btn_cancel)
    void cancel(){
              finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {



            case R.id.btn_add_pd:

                if (mBitmap != null) {
                    multipartImageUpload();
                } else {
                    Toast.makeText(getApplicationContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_choose_img:

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[]permission={Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permission,PERMISSION_CODE);

                    }else{
                        pickImageFromGarelly();
                    }
                }
                else{
                    pickImageFromGarelly();
                }
                break;

        }
    }
    private void pickImageFromGarelly() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickImageFromGarelly();
                }
                else{
                    Toast.makeText(this, "permission denied ...", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}