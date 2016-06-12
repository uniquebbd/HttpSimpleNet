package bbd.basesimplenet.net.core;

import android.util.Log;

import java.util.concurrent.BlockingQueue;

import bbd.basesimplenet.net.base.Request;
import bbd.basesimplenet.net.base.Response;
import bbd.basesimplenet.net.cache.Cache;
import bbd.basesimplenet.net.cache.LruMemCache;
import bbd.basesimplenet.net.httpstacks.HttpStack;

/**
 * 网络请求Executor,继承自Thread,从网络请求队列中循环读取请求并且执行
 *
 * @author Dengb
 * @date 2016-6-2 11:41
 */
public class NetworkExecutor extends Thread {

    private static final String TAG = "NetworkExecutor";
    /**
     * 网络请求队列
     */
    private BlockingQueue<Request<?>> mRequestQueue;
    /**
     * 网络请求栈
     */
    private HttpStack mHttpStack;
    /**
     * 结果分发器,将结果投递到主线程
     */
    private static ResponseDelivery mResponseDelivery = new ResponseDelivery();
    /**
     * 请求缓存
     */
    private static Cache<String, Response> mReqCache = new LruMemCache();
    /**
     * 是否停止
     */
    private boolean isStop = false;

    public NetworkExecutor(BlockingQueue<Request<?>> mRequestQueue, HttpStack mHttpStack) {
        this.mRequestQueue = mRequestQueue;
        this.mHttpStack = mHttpStack;
    }

    @Override
    public void run() {

        try {
            while (!isStop) {
                final Request<?> request = mRequestQueue.take();
                if (request.isCancel()) {
                    Log.d(TAG, "run: 取消执行了");
                    continue;
                }
                Response response = null;
                if (isUserCache(request)) {
                    //从缓存中取
                    response = mReqCache.get(request.getmUrl());
                } else {
                    Log.e(TAG, "run: 执行网络请求");
                    response = mHttpStack.performRequest(request);
                    if (request.ismShouldCache() && isSuccess(response)) {
                        mReqCache.put(request.getmUrl(), response);
                    }
                }
                //分发请求结果
                mResponseDelivery.deliveryResponse(request, response);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isSuccess(Response response) {
        return response != null && response.getStatusCode() == 200;
    }

    private boolean isUserCache(Request<?> request) {
        return request.ismShouldCache() && mReqCache.get(request.getmUrl()) != null;
    }

    public void quit() {
        isStop = true;
        interrupt();
    }
}
