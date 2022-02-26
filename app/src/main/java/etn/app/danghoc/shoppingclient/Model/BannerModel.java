package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class BannerModel {
    String message;
    boolean success;
    List<Banner> result;

    public BannerModel(String message, boolean success, List<Banner> result) {
        this.message = message;
        this.success = success;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Banner> getResult() {
        return result;
    }

    public void setResult(List<Banner> result) {
        this.result = result;
    }
}
