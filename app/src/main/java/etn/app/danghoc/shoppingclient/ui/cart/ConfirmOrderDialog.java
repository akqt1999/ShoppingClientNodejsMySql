package etn.app.danghoc.shoppingclient.ui.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.AddNewAddressClick;
import etn.app.danghoc.shoppingclient.R;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClientAddress;
import etn.app.danghoc.shoppingclient.sendNotificationPack.APIService;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Client;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Data;
import etn.app.danghoc.shoppingclient.sendNotificationPack.MyResponse;
import etn.app.danghoc.shoppingclient.sendNotificationPack.NotificationSender;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Token;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrderDialog extends DialogFragment {

    static ConfirmOrderDialog newInstance() {
        return new ConfirmOrderDialog();
    }

    IMyShoppingAPI addressAPI;
    IMyShoppingAPI shoppingAPI;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
    APIService apiService;

    TextView txt_address;
    @BindView(R.id.btn_cancel123)
    Button btn_cancel;
    @BindView(R.id.btn_confirm_buy)
    Button btn_confirm_buy;
    @BindView(R.id.btn_add_new_address)
     Button btn_add_new_address;
    @BindView(R.id.txt_total_price)
      TextView txt_total_price;
    Unbinder unbinder;

    //hien ra thong thanh cong neu hai cai nay bang nhau
    int countOrderSuccess=0;
    int countOrderChoose=0;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        UpdateToken();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_confirm_order, container, false);
        init1(view);
        unbinder = ButterKnife.bind(this, view);


        return view;
    }

    private void init1(View view) {
        addressAPI = RetrofitClientAddress.getInstance("https://dev-online-gateway.ghn.vn/").create(IMyShoppingAPI.class);
        shoppingAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        apiService= Client.getInstance().create(APIService.class);

        txt_address = view.findViewById(R.id.txt_address);
        btn_cancel = view.findViewById(R.id.btn_cancel123);
        btn_confirm_buy = view.findViewById(R.id.btn_confirm_buy);
        txt_total_price=view.findViewById(R.id.txt_total_price);
        txt_total_price.setText(Common.formatPrice(Common.totalPriceFromCart));
        displayAddress();
    }


    @Override
    public void onStop() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart()
    {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        super.onStart();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void addNewAddressClick(AddNewAddressClick event)
    {
        if(event.isSuccess())
        {
            txt_address.setText(Paper.book().read(Common.ORDER_ADDRESS_KEY));
        }
    }

    private void displayAddress() {
        Paper.init(getActivity());
        if(Paper.book().contains(Common.ORDER_ADDRESS_KEY))
        txt_address.setText(Paper.book().read(Common.ORDER_ADDRESS_KEY));
    }

    @OnClick(R.id.btn_cancel123)
    void cancelClick() {
        dismiss();
    }

    @OnClick(R.id.btn_confirm_buy)
    void confirmBuyClick() {


        for (int i = 0; i < Common.cartList.size(); i++) {
            if (Common.cartList.get(i).isChoose()) {
                countOrderChoose++;
                float gia = Common.cartList.get(i).getGia();
                int IdSP = Common.cartList.get(i).getIdSP();
                String IdSeller = Common.cartList.get(i).getIdSeller();
                createOrderToDatabase(gia, IdSP, IdSeller);

                sendNotificationOrderSuccess(IdSeller);

            }
        }

    }

    @OnClick(R.id.btn_add_new_address)
    void addNewAddressClick(){
        DialogFragment dialogFragment=AddAddressOrder.newInstance();
        dialogFragment.show(getParentFragmentManager(),"cailon");
        }

    private void createOrderToDatabase(float gia, int IdSP, String IdSeller) {


        compositeDisposable.add(shoppingAPI.createOrder(
                Common.API_KEY,
                txt_address.getText().toString(),
                createCurrentDay(),
                gia,
                Common.currentUser.getIdUser(),
                0,//o la chua xac nhan 1 la xac nhan
                 Common.currentUser.getPhoneUser(),
                IdSP,
                IdSeller,
                Common.currentUser.getNameUser()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createOrderModel -> {
                            if (createOrderModel.isSuccess()) {
                                countOrderSuccess++;
                                if(countOrderSuccess==countOrderChoose)
                                {
                                    showDialogSuccess();
                                }

                                Toast.makeText(getContext(), "create order success", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getContext(), "create order fail"+createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {

                            Toast.makeText(getContext(), "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                        })
        );

    }

    private void sendNotificationOrderSuccess(String IdSeller) {


        String title="new order";
        String message="you have new order form "+Common.currentUser.getNameUser();
        Data data = new Data(title, message);

        FirebaseDatabase.getInstance().getReference().child("Tokens")
                .child(IdSeller.trim()).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        String usertoken=snapshot.getValue(String.class);
                        NotificationSender sender = new NotificationSender(data, usertoken);


                        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.code()==200)
                                {
                                    if(response.body().success!=1){
                                        Toast.makeText(getContext(), "send Notification Fail", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "send notifi success", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




    }

    private void UpdateToken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful())
                        Toast.makeText(getContext(), "Fetching FCM registration token failed"+task.getException(), Toast.LENGTH_SHORT).show();

                    String refreshToken=task.getResult();
                    Token token= new Token(refreshToken);
                    FirebaseDatabase.getInstance().getReference("Tokens")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
                });
    }

    private String createCurrentDay() {
        long estimatedServerTimeMs = System.currentTimeMillis();
     //   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date resultDate = new Date(estimatedServerTimeMs);
        String date = sdf.format(resultDate);

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
     //   c.add(Calendar.DATE, 1);  // number of days to add trong sql server bi loi cai nay se khong bi loi
        date=sdf.format(c.getTime());
        return date;


    }

    private void showDialogSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("mua hang thanh cong ");
        builder.setNegativeButton("ok", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
/*
bài học rút ra
đó là phải tạo ra cái class khác mới được cái class cũ sẽ không được tại vì nó sẽ trùm với cái
dữ liệu cũ , nó sẽ lấy cái api cũ
 */