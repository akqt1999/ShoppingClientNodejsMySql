package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class IdNewSanPhamModel {
   private boolean success;
    List <IdnewSP> result;
    private  String message;

    public IdNewSanPhamModel(boolean success, List<IdnewSP> result, String message) {
        this.success = success;
        this.result = result;
        this.message = message;
    }

    public List<IdnewSP> getResult() {
        return result;
    }

    public void setResult(List<IdnewSP> result) {
        this.result = result;
    }

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
}
