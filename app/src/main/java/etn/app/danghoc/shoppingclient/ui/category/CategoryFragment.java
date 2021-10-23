package etn.app.danghoc.shoppingclient.ui.category;

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
import etn.app.danghoc.shoppingclient.Adapter.CategoryAdapter;
import etn.app.danghoc.shoppingclient.Adapter.CategorySanPhamAdapter;
import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.Model.DanhMucModel;
import etn.app.danghoc.shoppingclient.R;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.databinding.FragmentCartBinding;
import etn.app.danghoc.shoppingclient.databinding.FragmentCategoryBinding;
import etn.app.danghoc.shoppingclient.databinding.FragmentGalleryBinding;
import io.reactivex.disposables.CompositeDisposable;

public class CategoryFragment extends Fragment {

    private CategoryViewModel galleryViewModel;
    private FragmentCategoryBinding binding;

    @BindView(R.id.recycler_category)
    RecyclerView recycler_category;

    CategorySanPhamAdapter adapter;

    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(CategoryViewModel.class);
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
     ButterKnife.bind(this,root);


        galleryViewModel.getListCategory().observe(this,categoryProducts -> {
            if(categoryProducts.size()>0){
                displayCategory(categoryProducts);
            }
            else{
                galleryViewModel.getMessageError().observe(this, s -> {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                });
            }
        });

        return root;
    }

    private void displayCategory(List<CategoryProduct>categoryProducts) {
        adapter=new CategorySanPhamAdapter(getActivity(),categoryProducts);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recycler_category.setLayoutManager(linearLayoutManager);
        recycler_category.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}