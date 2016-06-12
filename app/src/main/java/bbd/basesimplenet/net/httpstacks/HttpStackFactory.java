package bbd.basesimplenet.net.httpstacks;

import android.os.Build;

/**
 * 根据api版本选择HttpClient或者HttpURLConnection
 *
 * @author Dengb
 * @date 2016-6-2 11:52
 */
public class HttpStackFactory  {
    private static final int GINGERBREAD_SDK_NUM = 9;

    public static HttpStack createHttpStack(){
        int runtimeSDKApi = Build.VERSION.SDK_INT;
        /**
         * 根据SDK版本号来创建不同的Http执行器,即SDK 9之前使用HttpClient,之后则使用HttlUrlConnection,
         * 两者之间的差别请参考 :
         * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
         *
         * @return
         */
        if (runtimeSDKApi>=GINGERBREAD_SDK_NUM){
            return new HttpUrlConnStack();
        }
        return new HttpClientStack();
    }
}
