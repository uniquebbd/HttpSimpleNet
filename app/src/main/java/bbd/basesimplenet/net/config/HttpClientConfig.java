package bbd.basesimplenet.net.config;

/**
 * 这是针对于使用HttpClientStack执行请求时为https请求配置的SSLSocketFactory类
 * 
 * @author Dengb
 */
public class HttpClientConfig extends HttpConfig {
    private static HttpClientConfig sConfig = new HttpClientConfig();
    org.apache.http.conn.ssl.SSLSocketFactory mSslSocketFactory;

    private HttpClientConfig() {

    }

    public static HttpClientConfig getConfig() {
        return sConfig;
    }

    /**
     * 配置https请求的SSLSocketFactory与HostnameVerifier
     * 
     * @param sslSocketFactory
     */
    public void setHttpsConfig(org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory) {
        mSslSocketFactory = sslSocketFactory;
    }

    public org.apache.http.conn.ssl.SSLSocketFactory getSocketFactory() {
        return mSslSocketFactory;
    }
}
