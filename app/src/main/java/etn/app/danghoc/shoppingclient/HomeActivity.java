package etn.app.danghoc.shoppingclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.DanhMucItemClick;
import etn.app.danghoc.shoppingclient.EventBus.HideFABCart;
import etn.app.danghoc.shoppingclient.EventBus.SanPhamItemClick;
import etn.app.danghoc.shoppingclient.databinding.ActivityHomeBinding;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Token;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    TextView txt_user_name, txt_user_phone;

    NavController navController;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //FloatingActionButton fab = findViewById(R.id.fab);

        ButterKnife.bind(this);
        fab.setOnClickListener(view -> navController.navigate(R.id.nav_cart));

        setSupportActionBar(binding.appBarHome.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_sign_out)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //setup navigationView
        navigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txt_user_name = headerView.findViewById(R.id.txt_name);
        txt_user_phone = headerView.findViewById(R.id.txt_phone);

        txt_user_phone.setText(Common.currentUser.getPhoneUser());
        txt_user_name.setText(Common.currentUser.getNameUser());
        showDialogLock();
        UpdateToken();
    }

    private void showDialogLock() {
        if(Common.currentUser.getTrangThai()==1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("tai khoan bi khoa");
            builder.setMessage("lien he mail: bexiu.1964@gmail.com de giai quyet");
            builder.setCancelable(false);
            builder.setNegativeButton("thoat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        EventBus.getDefault().postSticky(new DanhMucItemClick(false,-99));
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);



        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sign_out) {
            xacNhanDangXuat();
        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Common.currentUser = null;
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void foodSelect(SanPhamItemClick event) {
        if (event.isSuccess()) {
            navController.navigate(R.id.nav_detail_sanpham);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void hideFab(HideFABCart event) {
        if (event.isHidden()) {
            //fab.hide();
            fab.setVisibility(View.GONE);
        } else {
            //  fab.show();
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void danhMucItemClick(DanhMucItemClick event){
        if(event.isSuccess()){
            navController.navigate(R.id.nav_sanpham_by_id);
        }
        else{

        }
    }

    public void xacNhanDangXuat(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signOut();
            }
        });
        b.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        b.setTitle("ban co muon dang xuat khong??");
        AlertDialog dialog = b.create();
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {


        int id = item.getItemId();


        if (id == R.id.nav_sign_out) {
            xacNhanDangXuat();
        }
         else if (id == R.id.nav_home) {
            navController.navigate(R.id.nav_home);
        } else if (id == R.id.nav_view_order) {
            navController.navigate(R.id.nav_view_order);
        } else if (id == R.id.nav_view_order_seller) {
            navController.navigate(R.id.nav_view_order_seller);
        } else if (id == R.id.nav_view_add_new_product) {
            startActivity(new Intent(HomeActivity.this, AddNewProduct.class));
        }
        else if (id == R.id.nav_view_my_product) {
            startActivity(new Intent(HomeActivity.this, MyProductActivity.class));
        }
        else if(id==R.id.nav_category)
        {
             navController.navigate(R.id.nav_category);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void UpdateToken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful())
                        Toast.makeText(this, "Fetching FCM registration token failed"+task.getException(), Toast.LENGTH_SHORT).show();

                    String refreshToken=task.getResult();
                    Token token= new Token(refreshToken);
                    FirebaseDatabase.getInstance().getReference("Tokens")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
                });
    }

}