package com.chinasofti.oauth2.common.utils;


import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yangkai on 15/5/20.
 */
public final class JSONUtils {
    public JSONUtils() {
    }

    public static String buildJSON(Map<String, Object> params) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Iterator i$ = params.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry param = (Map.Entry)i$.next();
            if(param.getKey() != null && !"".equals(param.getKey()) && param.getValue() != null && !"".equals(param.getValue())) {
                jsonObject.put((String)param.getKey(), param.getValue());
            }
        }

        return jsonObject.toString();
    }

    public static Map<String, Object> parseJSON(String jsonBody) throws JSONException {
        HashMap params = new HashMap();
        JSONObject obj = new JSONObject(jsonBody);
        Iterator it = obj.keys();

        while(it.hasNext()) {
            Object o = it.next();
            if(o instanceof String) {
                String key = (String)o;
                params.put(key, obj.get(key));
            }
        }

        return params;
    }
}
