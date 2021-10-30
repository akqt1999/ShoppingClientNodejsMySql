package etn.app.danghoc.shoppingclient.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Model.Tinh;
import etn.app.danghoc.shoppingclient.Model.User;
import etn.app.danghoc.shoppingclient.R;

public class
Common {
    public static final String API_KEY = "1234";//
    //public static final String API_RESTAURANT_ENDPOINT = "http://10.0.2.2:3000/";
    public static final String API_RESTAURANT_ENDPOINT = "https://nguyenxuantri.xyz/";
    public static final String ORDER_ADDRESS_KEY = "order_address_key";
    public static final String IS_OPEN_ACTIVITY_NEW_ORDER = "IsOpenActivityNewOrder";
    public static User currentUser;
    public static SanPham selectSanPham;
    public static List<Cart> cartList;
    public static List<Order> orderList;
    public static List<Order> orderSellerList;
    public static List<SanPham> sanPhamList;
    public static SanPham productSelectEdit;
    public static CategoryProduct selectCategprySelect;
    public static double totalPriceFromCart;
    public static Order selectOrderBySeller;
    public static Order selectOrderByBuyer;
    public static List<Tinh> provinceList;

    public static String formatPrice(double price) {
        if (price != 0) {
            DecimalFormat df = new DecimalFormat("#,##0");
            df.setRoundingMode(RoundingMode.UP);
            String finalPrice = new StringBuilder(df.format(price)).append(" ₫").toString();
            return finalPrice;
            //.replace(".","");
        } else
            return "0.00";
    }

    //0:đang chờ xác nhận,1:đã xác nhận ,sẽ được nhận hàng trong vài ngày,2:đã hủy đơn hàng

    public static String ConvertOrderStatusToStringBuyer(int trangThai) {
        switch (trangThai) {
            case 0:
                return "đang chờ người bán xác nhận";
            case 1:
                return " người bán đã xác nhận ,sẽ được nhận hàng trong vài ngày";
            case 2:
                return " bạn đã hủy đơn hàng";
            case -2:
                return "người bán đã hủy đơn hàng";
            default:
                return "null";
        }

    }

    //2 cho nguòi mua hủy , -2 do người bán hủy
    public static String ConvertOrderStatusToStringSeller(int trangThai) {
        switch (trangThai) {
            case 0:
                return "đang chờ bạn xác nhận";
            case 1:
                return "bạn đã xác nhận đơn hàng này";
            case 2:
                return " người mua đã hủy đơn hàng";
            case -2:
                return "bạn đã hủy đơn hàng";
            default:
                return "null";
        }

    }

    /*
    //0:đang chờ xác nhận,
    1:đã xác nhận ,sẽ được nhận hàng trong vài ngày,
    2: mua đã hủy đơn hàng
     -2 do người bán hủy
    -1 chua lam gi
     */
    public static String convertStatusMyProduct(int trangthai) {
        switch (trangthai) {
            case 0:
                return "đang chờ bạn xác nhận";
            case 1:
                return "bạn đã xác nhận bán";
            case -1:
                return "đang bán";
            case 2:
                return "đang bán";
            case -2:
                return "bạn đã hủy xác nhận đơn hàng";
            default:
                return "null";
        }
    }

    public static void showNotifiCation(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "edmt_dev_eat_v2";
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Eat It cc", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Eat It cc");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_baseline_restaurant_24));

        if(pendingIntent!=null)
            builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        notificationManager.notify(id,notification);
    }


}
