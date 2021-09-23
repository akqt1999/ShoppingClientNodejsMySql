package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class OrdersModel {
    boolean success;
    List<Order>result;
    String message;

    public OrdersModel(boolean success, List<Order> result, String message) {
        this.success = success;
        this.result = result;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Order> getResult() {
        return result;
    }

    public void setResult(List<Order> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
