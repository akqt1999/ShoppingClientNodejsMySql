package etn.app.danghoc.shoppingclient.Model;

public class CategoryProduct {
    int IdDanhMuc;
    String TenDM;

    public CategoryProduct(int idDanhMuc, String tenDM) {
        IdDanhMuc = idDanhMuc;
        TenDM = tenDM;
    }

    public int getIdDanhMuc() {
        return IdDanhMuc;
    }

    public void setIdDanhMuc(int idDanhMuc) {
        IdDanhMuc = idDanhMuc;
    }

    public String getTenDM() {
        return TenDM;
    }

    public void setTenDM(String tenDM) {
        TenDM = tenDM;
    }
}
