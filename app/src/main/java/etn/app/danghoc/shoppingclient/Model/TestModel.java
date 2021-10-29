package etn.app.danghoc.shoppingclient.Model;

import java.util.ArrayList;
import java.util.List;

public class TestModel {
   String IdSeller;
   String list;

    public TestModel(String idSeller, String list) {
        IdSeller = idSeller;
        this.list = list;
    }

    public String getIdSeller() {
        return IdSeller;
    }

    public void setIdSeller(String idSeller) {
        IdSeller = idSeller;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }
}
