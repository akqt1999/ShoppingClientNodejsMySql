package etn.app.danghoc.shoppingclient.ui.sanpham_by_category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.MySanPhamAdapter;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.R;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.databinding.FragmentGalleryBinding;
import etn.app.danghoc.shoppingclient.databinding.FragmentSanphamByCategoryBinding;
import io.reactivex.disposables.CompositeDisposable;

public class SanPhamBuyCategoryFragment extends Fragment {

    private SanPhamCategoryViewModel sanPhamCategoryViewModel;
    private FragmentSanphamByCategoryBinding binding;

    @BindView(R.id.recycler_sp_by_category)
    RecyclerView recycler_sp_by_category;
    Unbinder unbinder;

    MySanPhamAdapter adapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyShoppingAPI shoppingAPI;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sanPhamCategoryViewModel =
                new ViewModelProvider(this).get(SanPhamCategoryViewModel.class);

        binding = FragmentSanphamByCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sanPhamCategoryViewModel.getListSanPham().observe(this, sanPhams -> {
            if (sanPhams.size() != 0) {
                displaySanPham(sanPhams);
            } else
                sanPhamCategoryViewModel.getMessageError().observe(this, error -> {
                    Toast.makeText(getContext(), "[Load  restaurant ]" + error, Toast.LENGTH_SHORT).show();
                });

        });

        unbinder= ButterKnife.bind(this,root);
        return root;
    }

    private void displaySanPham(List<SanPham> sanPhams) {
        Toast.makeText(getContext(), sanPhams.get(0).getTenSP(), Toast.LENGTH_SHORT).show();
        adapter = new MySanPhamAdapter(getContext(), sanPhams);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recycler_sp_by_category.setLayoutManager(linearLayoutManager);
        recycler_sp_by_category.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}