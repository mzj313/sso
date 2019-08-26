package org.mzj.test;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;

public class PreLoginAction {
	private final Log logger = LogFactory.getLog(getClass());

	public String preLogin(final RequestContext context) {
		logger.info("doPreLogin...");

		// final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
		Object serviceObj = context.getFlowScope().get("service");
		logger.info("service: " + serviceObj);
		if (serviceObj != null) {
			try {
				URL serviceURL = new URL(serviceObj.toString());
				String domain = serviceURL.getHost();
				logger.info("domain：" + domain);
				// 根据域名定制不同的css
				String css = null;
				if ("localhost".equals(domain)) {
					css = ".required {color:red}";
				}
				logger.info("css: " + css);

				css = css == null ? "" : css;
				context.getFlowScope().put("css", css);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "success";
	}

}
