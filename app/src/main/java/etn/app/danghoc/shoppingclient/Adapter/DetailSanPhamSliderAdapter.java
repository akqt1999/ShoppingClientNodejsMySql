package etn.app.danghoc.shoppingclient.Adapter;

import java.util.List;

import etn.app.danghoc.shoppingclient.Model.LinkImageModel;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class DetailSanPhamSliderAdapter  extends SliderAdapter {

    private List<LinkImageModel>listLinkImage;

    public DetailSanPhamSliderAdapter(List<LinkImageModel> listLinkImage) {
        this.listLinkImage = listLinkImage;
    }

    @Override
    public int getItemCount() {
        return listLinkImage.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(listLinkImage.get(position).getLink());
    }
}
