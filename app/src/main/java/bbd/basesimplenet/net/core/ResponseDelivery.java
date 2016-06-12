package bbd.basesimplenet.net.core;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import bbd.basesimplenet.net.base.Request;
import bbd.basesimplenet.net.base.Response;

/**
 * 请求结果投递类,将请求结果投递给UI线程
 *
 * @author Dengb
 * @date 2016-6-2 13:58
 */
public class ResponseDelivery implements Executor {

    /**
     * 主线程的handler
     */
    Handler mResponseHandler = new Handler(Looper.getMainLooper());

    /**
     * 处理请求结果，将其执行在UI线程
     * @param request
     * @param response
     */
    public void deliveryResponse(final Request<?> request, final Response response) {
        Runnable respRunnable = new Runnable() {
            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };
        execute(respRunnable);
    }

    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }
}
