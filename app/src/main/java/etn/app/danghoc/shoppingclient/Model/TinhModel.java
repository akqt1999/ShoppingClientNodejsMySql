package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class TinhModel {

    int code;
    String message;
    List<Tinh> data;


    public TinhModel(int code, String message, List<Tinh> result) {
        this.code = code;
        this.message = message;
        this.data = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TinhModel(List<Tinh> result) {
        this.data = result;
    }

    public List<Tinh> getResult() {
        return data;
    }

    public void setResult(List<Tinh> result) {
        this.data = result;
    }
}
