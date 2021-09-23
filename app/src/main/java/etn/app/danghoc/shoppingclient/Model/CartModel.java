package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class CartModel {
    private boolean success;
    private String message;
    private List<Cart> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Cart> getResult() {
        return result;
    }

    public void setResult(List<Cart> result) {
        this.result = result;
    }
}
