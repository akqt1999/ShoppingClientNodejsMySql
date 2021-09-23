package etn.app.danghoc.shoppingclient.sendNotificationPack;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAb5CScEk:APA91bGcs5lUHth4rZInrnUBXlGEps1d64GDPVTMfKqL0LPiaE78dbs7v68DQYlpUANsL4LjiMA1xxjZLEC_Zu6NZdu0_XXvGaARy-f20Ls-QBclcASEJCNGv_w_bTR_9g4CHwrJ4u7I"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);


}

