package bbd.basesimplenet.net.httpstacks;

import bbd.basesimplenet.net.base.Request;
import bbd.basesimplenet.net.base.Response;

/**
 * 执行网络请求的接口
 *
 * @author Dengb
 * @date 2016-6-2 11:44
 */
public interface HttpStack {
    /**
     * 执行Http请求
     *
     * @param request 待执行的请求
     * @return
     */
    public Response performRequest(Request<?> request);
}
