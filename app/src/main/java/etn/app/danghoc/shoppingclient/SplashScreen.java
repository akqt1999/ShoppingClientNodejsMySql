package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import dmax.dialog.SpotsDialog;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SplashScreen extends AppCompatActivity {

    IMyShoppingAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    AlertDialog dialog;


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        init();

/*
 sẽ xin cấp phép cái quyền nếu đc truy cập
 thì sẽ gét cái user hiện tại ,
 trong cái user hiện tại  thì sẽ lất cái number hiện tại
 (trong cái user hiện tại thì sẽ có uid , cái phone number , thì mình sẽ lấy  cái phone number )

 -thì ban đầu nó sẽ kiêm tra cái user co tồn tại hay không , nếu có tồn tại thì sẽ có hai trương hợp :
    +user có tồn tại (user!=null): cái phone number của user đó có tồn tại trên database chưa , nếu có thì sẽ vào home lun ,
      nếu chưa thì sẽ vào phần cập nhập thông tin

    + user chưa tồn tại ( user==null) : thì sẽ vào phần MainActivity (MainActivity để bấm cái nút login )
 */
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        dialog.show();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            compositeDisposable.add(myRestaurantAPI.getUser(etn.app.danghoc.shoppingclient.Common.Common.API_KEY, user.getUid())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(userModel -> {

                                                dialog.dismiss();

                                                if (userModel.isSuccess())//if user available in database
                                                {
                                                /*
                                                co nghia la trong cainay thi vao home lun
                                                 */

                                                    etn.app.danghoc.shoppingclient.Common.Common.currentUser = userModel.getResult().get(0);


                                                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                                                       finish();
                                                        Toast.makeText(SplashScreen.this, "user ton tai trong database", Toast.LENGTH_SHORT).show();
                                                } else //if  user not available in database
                                                {

                                                    Toast.makeText(SplashScreen.this, "user k ton tai trong database", Toast.LENGTH_SHORT);
                                                    startActivity(new Intent(SplashScreen.this, UpdateInfoActivity.class));
                                                    finish();
                                                }
                                            }, throwable -> {
                                                dialog.dismiss();
                                                Toast.makeText(SplashScreen.this, "[get user api]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                    ));

                        } else {
                            //co nghĩa là cái này  là chưa có cái user đăng nhập vào hệ thống
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(SplashScreen.this, "you must accept permission to use our app ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();



        // handler 3s vao app
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SplashScreen.this,MainActivity.class));
//            }
//        },3000);
//    }


    }

    private void init() {

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
    }
}