package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class HistoryMoneyModel {
    private boolean success;
    private String message;
    private List<HistoryMoney> result;

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

    public List<HistoryMoney> getResult() {
        return result;
    }

    public void setResult(List<HistoryMoney> result) {
        this.result = result;
    }

}
