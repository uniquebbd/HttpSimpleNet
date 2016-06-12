package bbd.basesimplenet.net.base;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求类，注意GET和delete不能传递参数，因为其请求的性质所致，用户可以将参数构建到url后传递进来到Request中
 *
 * @param <T>
 * @author Dengb
 * @date 2016-6-2 09:38
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * http request method enum.
     *
     * @author mrsimple
     */
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private String mHttpMethod = "";

        HttpMethod(String mHttpMethod) {
            this.mHttpMethod = mHttpMethod;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /**
     * 优先级枚举
     */
    public static enum Priority {
        LOW,
        NORMAL,
        HIGN,
        IMMEDIATE
    }

    /**
     * Default encoding for POST or PUT parameters. See
     */
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    /**
     * Default Content-type
     */
    public final static String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * 请求序列号
     */
    protected int mSerialNum = 0;
    /**
     * 优先级默认设置为Normal
     */
    protected Priority mPriority = Priority.NORMAL;
    /**
     * 是否取消该请求
     */
    protected boolean isCancel = false;

    /**
     * 该请求是否应该缓存
     */
    private boolean mShouldCache = true;
    /**
     * 请求的url
     */
    private String mUrl = "";
    /**
     * 请求的方法
     */
    HttpMethod mHttpMethod = HttpMethod.GET;

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    private RequestListener mRequestLinstener;

    /**
     * 请求的header
     */
    private Map<String, String> mHeaders = new HashMap<>();

    /**
     * 请求参数
     */
    private Map<String, String> mBodyParams = new HashMap<>();

    /**
     * @param method
     * @param url
     * @param listener
     */
    public Request(HttpMethod method, String url, RequestListener<T> listener) {
        mHttpMethod = method;
        mUrl = url;
        mRequestLinstener = listener;
    }

    public void addHeader(String name, String value) {
        mHeaders.put(name, value);
    }

    /**
     * 从原生的网络请求中解析结果
     *
     * @param response
     * @return
     */
    public abstract T parseResponse(Response response);

    /**
     * 处理Response,该方法运行在UI线程
     *
     * @param response
     */
    public final void deliveryResponse(Response response) {
        T result = parseResponse(response);
        if (mRequestLinstener != null) {
            int stCode = response != null ? response.getStatusCode() : -1;
            String msg = response != null ? response.getMessage() : "unkown error";
            Log.e("", "### 执行回调：stCode=" + stCode + ",result:" + result);
            mRequestLinstener.onComplete(stCode, result, msg);
        }
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public boolean ismShouldCache() {
        return mShouldCache;
    }

    public void setmShouldCache(boolean mShouldCache) {
        this.mShouldCache = mShouldCache;
    }

    public HttpMethod getmHttpMethod() {
        return mHttpMethod;
    }

    public void setmHttpMethod(HttpMethod mHttpMethod) {
        this.mHttpMethod = mHttpMethod;
    }

    public Map<String, String> getParams() {
        return mBodyParams;
    }

    public void setmParams(Map<String, String> mBodyParams) {
        this.mBodyParams = mBodyParams;
    }

    public RequestListener getmRequestLinstener() {
        return mRequestLinstener;
    }

    public void setmRequestLinstener(RequestListener mRequestLinstener) {
        this.mRequestLinstener = mRequestLinstener;
    }

    public boolean isHttps() {
        return mUrl.startsWith("https");
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public void setmHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void cancel() {
        isCancel = true;
    }

    public Priority getmPriority() {
        return mPriority;
    }

    public void setmPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    public int getmSerialNum() {
        return mSerialNum;
    }

    public void setmSerialNum(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public static String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {

        StringBuilder encodeParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodeParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodeParams.append('=');
                encodeParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodeParams.append('&');
            }
            return encodeParams.toString().getBytes(getParamsEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
        }
    }

    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority = this.getmPriority();
        Priority anotherPriority = another.getmPriority();
        //如果优先级相等，那么按照添加到队列的序列号顺序来执行
        return myPriority.equals(anotherPriority) ? this.getmSerialNum()
                - another.getmSerialNum() : myPriority.ordinal() - anotherPriority.ordinal();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mHeaders == null) ? 0 : mHeaders.hashCode());
        result = prime * result + ((mHttpMethod == null) ? 0 : mHttpMethod.hashCode());
        result = prime * result + ((mBodyParams == null) ? 0 : mBodyParams.hashCode());
        result = prime * result + ((mPriority == null) ? 0 : mPriority.hashCode());
        result = prime * result + (mShouldCache ? 1231 : 1237);
        result = prime * result + ((mUrl == null) ? 0 : mUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Request<?> other = (Request<?>) o;
        if (mHeaders == null) {
            if (other.mHeaders != null) return false;
        } else if (!mHeaders.equals(other.mHeaders)) {
            return false;
        }
        if (mHttpMethod != other.mHttpMethod) return false;
        if (mBodyParams == null) {
            if (other.mBodyParams != null)
                return false;
        } else if (!mBodyParams.equals(other.mBodyParams))
            return false;
        if (mPriority != other.mPriority)
            return false;
        if (mShouldCache != other.mShouldCache)
            return false;
        if (mUrl == null) {
            if (other.mUrl != null)
                return false;
        } else if (!mUrl.equals(other.mUrl))
            return false;
        return true;
    }

    public static interface RequestListener<T> {
        void onComplete(int stCode, T response, String errMsg);
    }
}
