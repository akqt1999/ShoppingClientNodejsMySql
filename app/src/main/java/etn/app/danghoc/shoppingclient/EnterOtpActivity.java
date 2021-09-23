package etn.app.danghoc.shoppingclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EnterOtpActivity extends AppCompatActivity {

    @BindView(R.id.edt_otp)
    TextView edt_otp;
    private String mVerificationId=getIntent().getStringExtra("verification_id");
    private String phoneNumber=getIntent().getStringExtra("phone_number");

    FirebaseAuth mAuth;

    IMyShoppingAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable=new CompositeDisposable();

    PhoneAuthProvider.ForceResendingToken mForceResendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        ButterKnife.bind(this);
        mAuth=FirebaseAuth.getInstance();
        myRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
    }

    @OnClick(R.id.btn_confirm_opt)
    void clickConfirmOtp(){
        String otpCode= edt_otp.getText().toString().trim();
        if(otpCode.length()<6)
        {
            Toast.makeText(this, "nhập chưa đủ số", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,otpCode);
        signInWithPhoneAuthCredential(credential);//credential goi la chung chi de xac minh la code co dung hay khong
    }

    @OnClick(R.id.btn_send_otp_again)
    void clickSendOtpAgain(){

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setForceResendingToken(mForceResendingToken)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                //no se tu dong vao ,ma k can nhap ma
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(EnterOtpActivity.this, "[VerificationFailed]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                mVerificationId=verificationId;
                                mForceResendingToken=forceResendingToken;

                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            FirebaseUser user = task.getResult().getUser();
                            if(user!=null) //user really login
                            {
                                compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY,user.getUid())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(userModel -> {
                                            if(userModel.isSuccess())
                                            {
                                                Common.currentUser=userModel.getResult().get(0);
                                                startActivity(new Intent(EnterOtpActivity.this,HomeActivity.class));

                                                finish();
                                            }
                                            else
                                            {
                                                startActivity(new Intent(EnterOtpActivity.this,UpdateInfoActivity.class));
                                                finish();
                                            }
                                        }));
                            }
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(EnterOtpActivity.this, "ma xac minh khong hop le", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}