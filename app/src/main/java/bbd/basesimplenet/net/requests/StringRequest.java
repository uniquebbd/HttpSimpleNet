package bbd.basesimplenet.net.requests;

import bbd.basesimplenet.net.base.Request;
import bbd.basesimplenet.net.base.Response;

public class StringRequest extends Request<String> {

    public StringRequest(HttpMethod method, String url, RequestListener<String> listener) {
        super(method, url, listener);
    }

    @Override
    public String parseResponse(Response response) {
        return new String(response.getRawData());
    }

}