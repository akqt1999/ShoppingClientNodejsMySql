package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.MySanPhamAdapter;
import etn.app.danghoc.shoppingclient.Callback.IClickItemSanPham;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ProductByCategory extends AppCompatActivity {


    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.recycler_sp_by_category)
    RecyclerView recycler_sp_by_category;

    MySanPhamAdapter adapter;
    List<SanPham> sanPhams=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_by_category);
        ButterKnife.bind(this);
        myRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        initToolbar();
        displaySanPham();

    }

    private void displaySanPham() {

        compositeDisposable.
                add(myRestaurantAPI.getSanPhamByIdDanhMuc(Common.API_KEY,
                        Common.selectCategprySelect.getIdDanhMuc())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sanPhamModel -> {
                            if(sanPhamModel.isSuccess())
                            {
                                sanPhams=sanPhamModel.getResult();
                                  adapter = new MySanPhamAdapter(this, sanPhams, new IClickItemSanPham() {
                                      @Override
                                      public void onClickItemUser() {
                                          Intent intent=new Intent(ProductByCategory.this,ChiTietSP.class);
                                          startActivity(intent);
                                      }
                                  });

                                recycler_sp_by_category.setLayoutManager(new LinearLayoutManager(this));
                                recycler_sp_by_category.setAdapter(adapter);

                            }
                            else{
                                Toast.makeText(this, sanPhamModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        },throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(Common.selectCategprySelect.getTenDM());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // button back
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}