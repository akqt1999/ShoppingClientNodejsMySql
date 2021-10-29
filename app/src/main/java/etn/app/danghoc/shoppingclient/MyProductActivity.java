package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.CartAdapter;
import etn.app.danghoc.shoppingclient.Adapter.MyProductAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.MyProductItemDelete;
import etn.app.danghoc.shoppingclient.EventBus.MyProductItemEdit;
import etn.app.danghoc.shoppingclient.Model.LinkImageModel;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyProductActivity extends AppCompatActivity {

    private List<SanPham> sanPhamList ;
    @BindView(R.id.recycler_my_product)
    RecyclerView recycler_my_product;


    MyProductAdapter adapter;

    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);
        ButterKnife.bind(this);
        myRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);

        initToolbar();
    }
    @Override
    protected void onResume() {
        super.onResume();
        displayMyProduct(); // de cho no cap nhap lai san pham sau khi load vo lai
    }

    private void displayMyProduct() {
        compositeDisposable.
                add(myRestaurantAPI.getSanPhamByUser(Common.API_KEY,
                        Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(productModel -> {
                            if(productModel.isSuccess())
                            {
                                Common.sanPhamList = productModel.getResult();
                                sanPhamList=productModel.getResult();
                                Collections.reverse(sanPhamList);

                                // add list link image
                                for(int i=0;i<sanPhamList.size();i++){

                                    List<LinkImageModel>listLinkImage=new ArrayList<>();
                                    String jsonListImage=sanPhamList.get(i).getListImage();
                                    JSONArray jsonArray=new JSONArray(jsonListImage);

                                    for (int j=0;j<jsonArray.length();j++) {
                                        JSONObject jsonObjectImage=jsonArray.getJSONObject(j);
                                        String UrlHinhAnh=jsonObjectImage.getString("UrlHinhAnh");
                                        listLinkImage.add(new LinkImageModel(UrlHinhAnh));
                                    }
                                    sanPhamList.get(i).setListLinkImage(listLinkImage);
                                }

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                                recycler_my_product.setLayoutManager(linearLayoutManager);
                                adapter = new MyProductAdapter(this, sanPhamList);
                                recycler_my_product.setAdapter(adapter);
                            }
                            else{
                                Toast.makeText(this, "[empty]", Toast.LENGTH_SHORT).show();
                            }

                        },throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );    }




    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        EventBus.getDefault().postSticky(new MyProductItemDelete(false,-99));
        EventBus.getDefault().postSticky(new MyProductItemEdit(false,-99));
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        Log.d("dfknsoif","stop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("dfknsoif","pause");
        EventBus.getDefault().postSticky(new MyProductItemEdit(false,-98));//no se xoa trang thai nay de khong khoi dong lai
        super.onPause();
    }





    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onDeleteItem(MyProductItemDelete event)
    {
        if(event.isSuccess())
        {
            int position =event.getPosition();

            compositeDisposable.add(myRestaurantAPI.deleteProduct(
                    Common.API_KEY,
                    Common.sanPhamList.get(position).getIdSP()
            ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(deleteProductModel -> {
                Toast.makeText(this, ""+sanPhamList.get(position).getIdSP(), Toast.LENGTH_SHORT).show();
                        if(deleteProductModel.isSuccess()){
                            sanPhamList.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "delete success", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, deleteProductModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }

            },throwable -> {
                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }));
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onEditItem(MyProductItemEdit event){
            if(event.isSuccess()){
                Common.productSelectEdit=sanPhamList.get(event.getPosition());
                startActivity(new Intent(MyProductActivity.this,UpdateProductActivity.class));
            }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}