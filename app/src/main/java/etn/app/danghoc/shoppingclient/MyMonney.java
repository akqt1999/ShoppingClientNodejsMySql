package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyMonney extends AppCompatActivity {

    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    EditText edt_add_money;
    TextView txt_my_money;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_monney);
        CardForm cardForm = findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(MyMonney.this);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);

        initView();
        initToolbar();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){ // button back
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbar () {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView() {
        unbinder = ButterKnife.bind(this);

        txt_my_money = findViewById(R.id.txt_my_money);
        edt_add_money = findViewById(R.id.edt_add_money);

        //set view
        txt_my_money.setText("số tiền hiện tại:" + Common.currentUser.getAmountMoney());
    }

    @OnClick(R.id.btnUpdateMoney)
    void UpdateMoney() {
        double moneyUpdate = Common.currentUser.getAmountMoney() + Double.parseDouble(edt_add_money.getText().toString());
        compositeDisposable.
                add(myRestaurantAPI.updateMoneyUser(
                        Common.API_KEY,
                        Common.currentUser.getIdUser(),
                        moneyUpdate
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(model -> {
                                    if (model.isSuccess()) {
                                        Toast.makeText(this, "nộp tiền thành công", Toast.LENGTH_SHORT).show();
                                        getCurrentUser();
                                        addHistoryMoney(Double.parseDouble(edt_add_money.getText().toString()));

                                    } else {
                                        Toast.makeText(this, "fail update" + model.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }, throwable -> {
                                    Toast.makeText(this, "[update status]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
    }

    private void addHistoryMoney(double tien) {
        compositeDisposable.add(myRestaurantAPI.postHistoryMoney(Common.API_KEY,
                Common.createCurrentDay(),1,tien,Common.currentUser.getIdUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userModel -> {
                    if (userModel.isSuccess()) {

                    } else {
                        Toast.makeText(this, "fail add history money", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY, user.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userModel -> {
                    if (userModel.isSuccess()) {
                        Common.currentUser = userModel.getResult().get(0);
                        txt_my_money.setText("số tiền hiện tại:" + Common.currentUser.getAmountMoney());
                        edt_add_money.setText("");
                    } else {
                        Toast.makeText(this, "fail get current user", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    ;

}