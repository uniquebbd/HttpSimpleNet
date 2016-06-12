package bbd.basesimplenet.net.requests;

import org.json.JSONException;
import org.json.JSONObject;

import bbd.basesimplenet.net.base.Request;
import bbd.basesimplenet.net.base.Response;

/**
 * 返回的数据类型为Json的请求, Json对应的对象类型为JSONObject
 *
 * @author Dengb
 * @date 2016-6-2 15:19
 */
public class JsonRequest extends Request<JSONObject> {
    public JsonRequest(HttpMethod method, String url, RequestListener<JSONObject> listener) {
        super(method, url, listener);
    }

    @Override
    public JSONObject parseResponse(Response response) {
        String jsonString = new String(response.getRawData());
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
