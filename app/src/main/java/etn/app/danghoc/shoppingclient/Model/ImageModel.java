package etn.app.danghoc.shoppingclient.Model;

import android.net.Uri;

public class ImageModel {
   private String imagename;
   private Uri image;
   private String linkImage;
    public ImageModel() {
    }


    public ImageModel(String imagename, Uri image) {
        this.imagename = imagename;
        this.image = image;
    }

    public ImageModel( String linkImage) {
        this.linkImage = linkImage;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }
}
