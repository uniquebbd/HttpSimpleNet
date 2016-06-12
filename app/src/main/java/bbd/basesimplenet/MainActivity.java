package bbd.basesimplenet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import bbd.basesimplenet.net.base.Request;
import bbd.basesimplenet.net.core.RequestQueue;
import bbd.basesimplenet.net.core.SimpleNet;
import bbd.basesimplenet.net.entity.MultipartEntity;
import bbd.basesimplenet.net.requests.MultipartRequest;
import bbd.basesimplenet.net.requests.StringRequest;

public class MainActivity extends AppCompatActivity {

    // 1、构建请求队列
    RequestQueue mQueue = SimpleNet.newRequestQueue();
    TextView mResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTv = (TextView) findViewById(R.id.result_tv);
        sendStringRequest();
    }

    /**
     * 发送GET请求,返回的是String类型的数据, 同理还有{@see JsonRequest}、{@see MultipartRequest}
     */
    private void sendStringRequest() {
        StringRequest request = new StringRequest(Request.HttpMethod.GET, "http://www.baidu.com",
                new Request.RequestListener<String>() {

                    @Override
                    public void onComplete(int stCode, String response, String errMsg) {
                        mResultTv.setText(Html.fromHtml(response));
                    }
                });
        mQueue.addRequest(request);
    }

    /**
     * 发送MultipartRequest,可以传字符串参数、文件、Bitmap等参数,这种请求为POST类型
     */
    protected void sendMultiRequest() {
        // 2、创建请求
        MultipartRequest multipartRequest = new MultipartRequest("你的url",
                new Request.RequestListener<String>() {
                    @Override
                    public void onComplete(int stCode, String response, String errMsg) {
                        // 该方法执行在UI线程
                    }
                });

        // 3、添加各种参数
        // 添加header
        multipartRequest.addHeader("header-name", "value");

        // 通过MultipartEntity来设置参数
        MultipartEntity multi = multipartRequest.getMultiPartEntity();
        // 文本参数
        multi.addStringPart("location", "模拟的地理位置");
        multi.addStringPart("type", "0");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        // 直接从上传Bitmap
        multi.addBinaryPart("images", bitmapToBytes(bitmap));
        // 上传文件
        multi.addFilePart("imgfile", new File("storage/emulated/0/test.jpg"));

        // 4、将请求添加到队列中
        mQueue.addRequest(multipartRequest);
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onDestroy() {
        mQueue.stop();
        super.onDestroy();
    }
}
