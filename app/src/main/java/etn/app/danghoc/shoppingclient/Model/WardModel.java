package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class WardModel {
    int code;
    String message;

    List<Ward>data;


    public WardModel(int code, String message, List<Ward> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public List<Ward> getData() {
        return data;
    }

    public void setData(List<Ward> data) {
        this.data = data;
    }

    public WardModel(int code, String message) {
        this.code = code;
        this.message = message;
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
}
