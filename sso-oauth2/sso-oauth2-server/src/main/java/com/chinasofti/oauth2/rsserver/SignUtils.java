package com.chinasofti.oauth2.rsserver;

import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.ResourceRequest;
import com.chinasofti.oauth2.key.Base64;
import com.chinasofti.oauth2.key.RSACoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by liuzhuo on 2015/4/29.
 */
public class SignUtils {
    /**
     * 根据资源请求生成访问签名
     *
     * @param request 访问请求
     * @param publicKey    应用公钥
     * @return
     */
//    public static String createSign(ResourceRequest request, String publicKey) throws Exception {
//        return new String(Base64.encode(RSACoder.encryptByPrivateKey(request.toString().getBytes(), publicKey)));
//    }

    /**
     * 验证签名
     * @param srcSign 参数中传过来的sign
     * @param request 从servletrequest中获得的request参数
     * @param privateKey 用于解密sign的私钥
     * @return 利用私钥解密的sign原文，与从request中获取的参数组织的签名原文进行对比，一致则没有被拦截修改true，不一致则被拦截修改返回false。
     * @throws Exception
     */
    public static boolean verifySign(String srcSign, ResourceRequest request, String  privateKey) throws Exception {
        String srcSignStr = new String(RSACoder.decryptByPrivateKey(Base64.decode(srcSign).getBytes(), privateKey));
        return request.toString().equals(srcSignStr);
    }

    /**
     * 验证签名
     * @param srcSign 参数中传过来的sign
     * @param request 从servletrequest中获得的request参数
     * @return 利用私钥解密的sign原文，与从request中获取的参数组织的签名原文进行对比，一致则没有被拦截修改true，不一致则被拦截修改返回false。
     * @throws Exception
     */
    public static boolean verifySign(String srcSign, ResourceRequest request) throws Exception {
        String srcSignStr = new String(Base64.decode(srcSign).getBytes());
        return request.toString().equals(srcSignStr);
    }

    /**
     * 根据网络请求生成资源请求对象
     * @param httpRequest
     * @return
     */
    public static ResourceRequest getResourceRequest(HttpServletRequest httpRequest){
        ResourceRequest request = new ResourceRequest(httpRequest.getRequestURI(), httpRequest.getMethod());
        Set<String> keys = httpRequest.getParameterMap().keySet();
        for(String name : keys){
            if(!name.equals(Constant.HTTP_REQUEST_PARAM_SIGN)){
                request.setParameter(name, httpRequest.getParameter(name));
            }
        }
        return request;
    }
}
