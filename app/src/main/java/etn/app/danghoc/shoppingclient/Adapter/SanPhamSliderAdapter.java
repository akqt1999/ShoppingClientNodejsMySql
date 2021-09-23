package etn.app.danghoc.shoppingclient.Adapter;

import java.util.List;

import etn.app.danghoc.shoppingclient.Model.SanPham;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class SanPhamSliderAdapter extends SliderAdapter {
    List<SanPham> sanPhamList;

    public SanPhamSliderAdapter(List<SanPham> sanPhamList) {
        this.sanPhamList = sanPhamList;
    }

    @Override
    public int getItemCount() {
        return sanPhamList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(sanPhamList.get(position).getHinh());
    }
}
