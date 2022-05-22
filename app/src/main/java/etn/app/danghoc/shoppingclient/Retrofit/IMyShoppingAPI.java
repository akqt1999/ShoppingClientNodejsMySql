package etn.app.danghoc.shoppingclient.Retrofit;

import java.util.Date;

import etn.app.danghoc.shoppingclient.Model.AddCartModel;
import etn.app.danghoc.shoppingclient.Model.BannerModel;
import etn.app.danghoc.shoppingclient.Model.CartModel;
import etn.app.danghoc.shoppingclient.Model.CreateOrderModel;
import etn.app.danghoc.shoppingclient.Model.DanhMucModel;
import etn.app.danghoc.shoppingclient.Model.DeleteProductModel;
import etn.app.danghoc.shoppingclient.Model.DistrictModel;
import etn.app.danghoc.shoppingclient.Model.HinhAnhModel;
import etn.app.danghoc.shoppingclient.Model.HistoryMoneyModel;
import etn.app.danghoc.shoppingclient.Model.IdNewSanPhamModel;
import etn.app.danghoc.shoppingclient.Model.OrdersModel;
import etn.app.danghoc.shoppingclient.Model.SanPhamModel;
import etn.app.danghoc.shoppingclient.Model.TestModel;
import etn.app.danghoc.shoppingclient.Model.TestModelCha;
import etn.app.danghoc.shoppingclient.Model.TinhModel;
import etn.app.danghoc.shoppingclient.Model.UpdateModel;
import etn.app.danghoc.shoppingclient.Model.UpdateStatusModel;
import etn.app.danghoc.shoppingclient.Model.UpdateUserModel;
import etn.app.danghoc.shoppingclient.Model.UploadSanPhamModel;
import etn.app.danghoc.shoppingclient.Model.UserModel;
import etn.app.danghoc.shoppingclient.Model.WardModel;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IMyShoppingAPI {
    // dùng tương tác (lấy dữ liệu từ talbe user) với api để lấy dữ liệu từ table user database
    @GET("user")
    Observable<UserModel> getUser(@Query("key") String apiKey,
                                  @Query("idUser") String idUser); //cai fbid chu la cai dien thoai

    // user
    // dùng tương tác (chèn dữ liệu vào table user) với api để lấy dữ liệu từ table user database
    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("PhoneUser") String userPhone,
                                               @Field("AddressUser") String userAddress,
                                               @Field("NameUser") String userName,
                                               @Field("IdUser") String fbid);

    @POST("updateMoneyUser")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateMoneyUser(@Field("key") String apiKey,
                                                @Field("IdUser") String idUser,
                                                @Field("AmountMoney") double AmountMoney);

    // san pham
    @GET("sanPham")
    Observable<SanPhamModel> getSanPham(@Query("key") String apiKey,
                                        @Query("IdUser") String iduser);

    @GET("sanPham2")
    Observable<SanPhamModel> getSanPham2(@Query("key") String apiKey, // get san pham kem theo hinh anh
                                         @Query("IdUser") String iduser);

    @GET("sanPham3")
    Observable<SanPhamModel> getSanPham3(@Query("key") String apiKey, // get san pham keo theo hinh anh , page . load more,
                                         @Query("IdUser") String iduser,//
                                         @Query("page") int page
    );

    @GET("sanPhamByIdUser2")
    Observable<SanPhamModel> getSanPhamByUser(@Query("key") String apiKey,
                                              @Query("IdUser") String IdUser);

    @GET("searchSanPham")
    Observable<SanPhamModel> searchSanPham(@Query("key") String apiKey,
                                           @Query("SearchQuery") String NameSanPham);

    @GET("sanPhamByIdDanhMuc")
    Observable<SanPhamModel> getSanPhamByIdDanhMuc(
            @Query("key") String key,
            @Query("IdDanhMuc") int IdDanhMuc,
            @Query("IdUser") String iduser
    );

    @GET("sanPhamByProvinceIdTest3")
    Observable<SanPhamModel> getSanPhamByProvinceId(@Query("key") String apiKey,
                                                    @Query("IdUser") String IdUser,
                                                    @Query("ProvinceId") int ProvinceId,
                                                    @Query("page") int page);


    @GET("idNewSanPham")
    Observable<IdNewSanPhamModel> getIdNewSanPham(@Query("key") String apiKey,
                                                  @Query("IdUser") String IdUser
    );


    @DELETE("sanpham")
    Observable<DeleteProductModel> deleteProduct(@Query("key") String apiKey,
                                                 @Query("IdSP") int IdSP);

    @DELETE("hinhanh")
    Observable<DeleteProductModel> deleteLinkImage(@Query("key") String apiKey,
                                                   @Query("UrlHinhAnh") String UrlHinhAnh);


    @POST("hinhanh")
    @FormUrlEncoded
    Observable<HinhAnhModel> uploadLinkHinhAnh(
            @Field("key") String key,
            @Field("IdSP") int IdSP,
            @Field("UrlHinhAnh") String UrlHinhAnh)

            ;


    @POST("sanpham")
    @FormUrlEncoded
    Observable<UploadSanPhamModel> uploadSanPham(
            @Field("key") String key,
            @Field("IdUser") String IdUser,
            @Field("TenSP") String TenSP,
            @Field("GiaSP") float GiaSP,
            @Field("MoTa") String Mota,
            @Field("IdDanhMuc") int IdDanhMuc,
            @Field("hinh") String hinh,
            @Field("ProvinceId") int privinceid);


    @POST("updatesanpham")
    @FormUrlEncoded
    Observable<UploadSanPhamModel> updateSanPham(
            @Field("key") String key,
            @Field("IdSP") int IdSP,
            @Field("TenSP") String TenSP,
            @Field("GiaSP") float GiaSP,
            @Field("MoTa") String Mota,
            @Field("IdDanhMuc") int IdDanhMuc,
            @Field("hinh") String hinh,
            @Field("ProvinceId") int privinceid);

    @POST("UpdateSanPhamAds")
    @FormUrlEncoded
    Observable<UploadSanPhamModel> UpdateSanPhamAds(
            @Field("key") String key,
            @Field("IdSP") int IdSP,
            @Field("NgayUuTien") String NgayUuTien
    );

    @POST("updateSanPhamBaoCao")
    @FormUrlEncoded
    Observable<UploadSanPhamModel> updateSanPhamBaoCao(
            @Field("key") String key,
            @Field("IdSP") int IdSP,
            @Field("BaoCao")int BaoCao
    );

    //=============
    // gio hang
    //=========
    @GET("giohang")
    Observable<CartModel> getCart(@Query("key") String apiKey,
                                  @Query("IdUser") String idUser);

    //check sp da ton tai trong gio hang hay chua
    @GET("giohangcheck")
    Observable<CartModel> getCartCheck(@Query("key") String apiKey,
                                       @Query("IdUser") String idUser,
                                       @Query("IdSP") int idsp);

    @DELETE("giohang")
    Observable<CartModel> deleteCart(@Query("key") String apiKey,
                                     @Query("IdUser") String idUser,
                                     @Query("IdSP") int IdSP);

    @POST("giohang")
    @FormUrlEncoded
    Observable<AddCartModel> addCart(@Field("key") String apiKey,
                                     @Field("IdSP") int intSP,
                                     @Field("Gia") float gia,
                                     @Field("TenSP") String tensp,
                                     @Field("IdUser") String iduser,
                                     @Field("IdSeller") String idSeller);


    //get province , district, word
    //==================
    @GET("shiip/public-api/master-data/province")
    Observable<TinhModel> getProvince(
            @Header("token") String token,
            @Header("Content-Type") String content
    );

    @GET("shiip/public-api/master-data/district")
    Observable<DistrictModel> getDistrict(
            @Header("token") String token,
            @Header("Content-Type") String content,
            @Query("province_id") int province_id
    );

    @GET("shiip/public-api/master-data/ward")
    Observable<WardModel> getWord(
            @Header("token") String token,
            @Header("Content-Type") String content,
            @Query("district_id") int province_id
    );

    //================
    /// don hang
    @POST("donhang")
    @FormUrlEncoded
    Observable<CreateOrderModel> createOrder(@Field("key") String apiKey,
                                             @Field("DiaChi") String diachi,
                                             @Field("NgayDat") String NgayDat,
                                             @Field("gia") float gia,
                                             @Field("IdUser") String iduser,
                                             @Field("TrangThai") int TrangThai,
                                             @Field("sdt") String sdt,
                                             @Field("IdSP") int IdSP,
                                             @Field("IdSeller") String IdSeller,
                                             @Field("TenUser") String TenUser);

    @GET("donhangByBuyer")
    Observable<OrdersModel> getOrdersByBuyer(@Query("key") String apiKey,
                                             @Query("IdUser") String idUser);

    @GET("donhangBySeller")
    Observable<OrdersModel> getOrdersBySeller(@Query("key") String apiKey,
                                              @Query("IdSeller") String idUser);

    @POST("updatestatusdonhang")
    @FormUrlEncoded
    Observable<UpdateStatusModel> updateStatusOrder(@Field("key") String apiKey,
                                                    @Field("IdDonHang") int IdDonHang,
                                                    @Field("TrangThai") int TrangThai
    );

    @Multipart
    @POST("/uploadfile")
    Call<UpdateModel> postImage2(@Part MultipartBody.Part image, @Part("myFile") RequestBody name);

    @GET("danhmuc")
    Observable<DanhMucModel> getDanhMuc(@Query("key") String apiKey);

    @GET("testselect2")
    Observable<TestModelCha> getTest();

    // banner
    @GET("banner")
    Observable<BannerModel> getBanner(@Query("key") String apiKey);

    //history
    @GET("lichSuNopTien")
    Observable<HistoryMoneyModel> getHistoryMoney(@Query("key") String apiKey,
                                                  @Query("IdUser") String IdUser);

    @POST("lichSuNopTien")
    @FormUrlEncoded
    Observable<UpdateStatusModel> postHistoryMoney(@Field("key") String apiKey,
                                       @Field("DateUpdateMoney") String DateUpdateMoney,
                                       @Field("trangThai") int TrangThai,
                                       @Field("Tien") double Tien,
                                       @Field("IdUser") String IdUser
    );



}
//test update