package org.jasig.cas.support.pac4j.plugin.wechat;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

/**
 * 用于定义获取微信返回的code与access_token
 * Created by Jeng on 16/2/21.
 */
public class WeiXinApi20 extends DefaultApi20 {
	// 报错：对实体 "redirect_uri" 的引用必须以 ';' 分隔符结尾。解决： ; -> $amp;
    private static final String WEIXIN_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&amp;redirect_uri=%s&amp;response_type=code&amp;scope=snsapi_login#wechat_redirect";

    @Override
    public AccessTokenExtractor getAccessTokenExtractor()
    {
        return new JsonTokenExtractor();
    }

    @Override
    public Verb getAccessTokenVerb()
    {
        return Verb.POST;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.weixin.qq.com/sns/oauth2/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return String.format(WEIXIN_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
}
