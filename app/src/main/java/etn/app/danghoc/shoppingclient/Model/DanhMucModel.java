package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class DanhMucModel {
    private boolean success;
    private List<CategoryProduct> result;
    private String message;

    public DanhMucModel(boolean success, List<CategoryProduct> result,String message) {
        this.success = success;
        this.result = result;
        this.message=message;
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

    public List<CategoryProduct> getResult() {
        return result;
    }

    public void setResult(List<CategoryProduct> result) {
        this.result = result;
    }
}
