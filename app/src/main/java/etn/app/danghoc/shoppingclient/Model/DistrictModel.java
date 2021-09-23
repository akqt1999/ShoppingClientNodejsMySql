package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class DistrictModel {
    int code;
    String message;
    List<District>data ;

    public DistrictModel(int code, String message, List<District> data) {
        this.code = code;
        this.message = message;
        this.data = data;
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

    public List<District> getData() {
        return data;
    }

    public void setData(List<District> data) {
        this.data = data;
    }
}
