package etn.app.danghoc.shoppingclient.Adapter;

import java.util.List;

import etn.app.danghoc.shoppingclient.Model.Banner;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class SanPhamSliderAdapter extends SliderAdapter {
    List<Banner> list;

    public SanPhamSliderAdapter(List<Banner> bannerList) {
        this.list    = bannerList;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(list.get(position).getUrlHinhAnh());
    }
}
