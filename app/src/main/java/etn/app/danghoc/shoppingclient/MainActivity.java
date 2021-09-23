package etn.app.danghoc.shoppingclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE = 1121;
    private   FirebaseAuth.AuthStateListener listener;
    private   FirebaseAuth firebaseAuth ;
    private List<AuthUI.IdpConfig> providers;
    FirebaseAuth  mAuth;
    AlertDialog dialog;
    EditText edt_phone_number;

    IMyShoppingAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable=new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initt();
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        // There are no request codes
                        //Intent data = result.getData();
                       // finish(); // neu nhu cai ma so xac nhan nay ma dung thi tra ve day

                    }
                }
            });



    private void initt() {

        mAuth = FirebaseAuth.getInstance();

        edt_phone_number=findViewById(R.id.edit_phone_number);

        providers= Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

        firebaseAuth= FirebaseAuth.getInstance();

        ButterKnife.bind(this);


        listener=firebaseAuth1 -> { // cai nay la lang nghe su kien login
            FirebaseUser user=firebaseAuth1.getCurrentUser();

            if(user!=null) //user really login
            {
                compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY,user.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userModel -> {
                            if(userModel.isSuccess())
                            {
                                Common.currentUser=userModel.getResult().get(0);
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));

                                finish();
                            }
                            else
                            {
                                startActivity(new Intent(MainActivity.this,UpdateInfoActivity.class));
                                finish();
                            }
                        }));
            }

        };

        Paper.init(this);
        dialog= new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(listener!=null && firebaseAuth!=null)
            firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {

        if(listener!=null&&firebaseAuth!=null)
            firebaseAuth.removeAuthStateListener(listener);

        super.onStop();

    }

    @OnClick(R.id.btn_sign_in)
    void loginUser()
    {
        //Intent intent=
//        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
//                .setAvailableProviders(providers).build(),APP_REQUEST_CODE);


// set this to remove reCaptcha web // xoa cai captcha
        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        someActivityResultLauncher.launch(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build());

        if(edt_phone_number.getText().toString()==null){
            Toast.makeText(this, "chưa nhập số điện thoại hoặt nhập sai", Toast.LENGTH_SHORT).show();
            return;
        }

//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(edt_phone_number.getText().toString().trim())       // Phone number to verify
//                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                        .setActivity(this)                 // Activity (for callback binding)
//                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                            @Override
//                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                //no se tu dong vao ,ma k can nhap ma
//                                signInWithPhoneAuthCredential(phoneAuthCredential);
//                            }
//
//                            @Override
//                            public void onVerificationFailed(@NonNull FirebaseException e) {
//                                Toast.makeText(MainActivity.this, "[VerificationFailed]"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                Log.d("adda",e.getMessage());
//                            }
//
//                            @Override
//                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                super.onCodeSent(verificationId, forceResendingToken);
//                                gotoEnterOtpActivity(edt_phone_number.getText().toString().trim(),verificationId);
//                            }
//                        })          // OnVerificationStateChangedCallbacks
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);


    }


//    private void gotoEnterOtpActivity(String phoneNumber, String verificationId) {
//        Intent intent=new Intent(this,EnterOtpActivity.class);
//        intent.putExtra("phone_number",phoneNumber);
//        intent.putExtra("verification_id",verificationId);
//        startActivity(intent);
//    }

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//
//
//                            FirebaseUser user = task.getResult().getUser();
//                            if(user!=null) //user really login
//                            {
//                                compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY,user.getUid())
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(userModel -> {
//                                            if(userModel.isSuccess())
//                                            {
//                                                Common.currentUser=userModel.getResult().get(0);
//                                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
//
//                                                finish();
//                                            }
//                                            else
//                                            {
//                                                startActivity(new Intent(MainActivity.this,UpdateInfoActivity.class));
//                                                finish();
//                                            }
//                                        }));
//                            }
//                            // Update UI
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                                Toast.makeText(MainActivity.this, "ma xac minh khong hop le", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//    }


}
