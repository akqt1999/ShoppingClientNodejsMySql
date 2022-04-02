package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etn.app.danghoc.shoppingclient.Adapter.DetailSanPhamSliderAdapter;
import etn.app.danghoc.shoppingclient.Adapter.SanPhamSliderAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.LinkImageModel;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.chat.specificchat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class ChiTietSP extends AppCompatActivity {

     IMyShoppingAPI myRestaurantAPI;
     CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    String urlImage="https://scontent.fdad3-4.fna.fbcdn.net/v/t1.15752-9/274838229_4952544314871195_2682715600486588086_n.png?_nc_cat=105&ccb=1-5&_nc_sid=ae9488&_nc_ohc=Vc2QC7GhfqUAX8-WLet&_nc_ht=scontent.fdad3-4.fna&oh=03_AVLB8Xmmds7koDkuwqSewj3Ww6MsVHBvQVfljPEZWbH_VA&oe=626F70BE";

    @BindView(R.id.txtFoodName)
    TextView txtFoodName;
    @BindView(R.id.txtFoodPrice)
    TextView txtFoodPrice;
    @BindView(R.id.txtFoodDescription)
    TextView txtFoodDescription;
    @BindView(R.id.txt_phone)
    TextView txt_phone;

    @BindView(R.id.btn_cart)
    CounterFab btn_cart;
    @BindView(R.id.btn_report)
    Button btn_report;
    @BindView(R.id.banner_slider)
    Slider banner_slider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_sp);

        ButterKnife.bind(this);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        initToolbar();
        displayDetail();

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void displayBanner(List<LinkImageModel> imageModels) {
        banner_slider.setAdapter(new DetailSanPhamSliderAdapter(imageModels));
    }

    private void displayDetail() {

       SanPham sanPham= Common.selectSanPham;
      //  Glide.with(this).load(Common.selectSanPham.getHinh()).into(imgFood);
        txtFoodName.setText(sanPham.getTenSP());
        txtFoodPrice.setText(Common.formatPrice(sanPham.getGiaSP()));
        txtFoodDescription.setText(sanPham.getMoTa());
        txt_phone.setText(sanPham.getPhoneUser());
        getSupportActionBar().setTitle(sanPham.getTenSP());

        displayBanner(sanPham.getListLinkImage());


    }

    @OnClick(R.id.btn_cart)
    public void addCart ()
    {


        compositeDisposable.add(myRestaurantAPI.getCartCheck(Common.API_KEY, Common.currentUser.getIdUser(),Common.selectSanPham.getIdSP())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartModel -> {
                    if(cartModel.isSuccess()){
                        Toast.makeText(this, "sản phẩm này bạn đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        compositeDisposable.add(myRestaurantAPI.addCart(
                                Common.API_KEY,
                                Common.selectSanPham.getIdSP(),
                                Common.selectSanPham.getGiaSP(),
                                Common.selectSanPham.getTenSP(),
                                Common.currentUser.getIdUser(),
                                Common.selectSanPham.getIdUser()//id of seller
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(cartModel2 -> {
                                    if(cartModel2.isSuccess())
                                    {
                                        Toast.makeText(this, "add cart success", Toast.LENGTH_SHORT).show();
                                    }else {
                                    }

                                },throwable -> {
                                    Toast.makeText(this, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));
                    }
                },throwable -> {
                    Toast.makeText(this, "check sp exits cart"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));


    }

    @OnClick(R.id.btn_call)
    public void clickCall(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_DIAL,
                                Uri.fromParts("tel", txt_phone.getText().toString(), null));
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(ChiTietSP.this, "you must accept permission to use our app", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();
    }

    @OnClick(R.id.btn_report)
    public void clickReport()
    {
        compositeDisposable.add(myRestaurantAPI.updateSanPhamBaoCao(Common.API_KEY,Common.selectSanPham.getIdSP(),1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    if(model.isSuccess()){
                        Toast.makeText(this, "Cảm ơn bạn đã báo cáo sản phẩm này, chúng tôi sẽ xem xét lại ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,model.getMessage() , Toast.LENGTH_SHORT).show();
                    }

                },throwable -> {
                    Toast.makeText(this, "check sp exits cart"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    @OnClick(R.id.btn_chat)
    public void clickChat()
    {


        DocumentReference documentReference=firebaseFirestore.collection("Users").document(Common.currentUser.getIdUser());
        Map<String , Object> userdata=new HashMap<>();
        userdata.put("name",Common.currentUser.getNameUser());
        userdata.put("image",urlImage);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Data on Cloud Firestore send success",Toast.LENGTH_SHORT).show();

                DocumentReference documentReference2=firebaseFirestore.collection("Users").document(Common.selectSanPham.getIdUser());
                Map<String , Object> userdata2=new HashMap<>();
                userdata.put("name",Common.selectSanPham.getTenSP());
                userdata.put("image",urlImage);
                userdata.put("uid",Common.selectSanPham.getIdUser());
                userdata.put("status","Online");

                documentReference2.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent=new Intent(ChiTietSP.this, specificchat.class);
                        intent.putExtra("name",Common.selectSanPham.getTenSP());
                        intent.putExtra("receiveruid",Common.selectSanPham.getIdUser());
                        intent.putExtra("imageuri",urlImage);
                        startActivity(intent);
                    }
                });

            }
        });
        /////


    }


    // back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}