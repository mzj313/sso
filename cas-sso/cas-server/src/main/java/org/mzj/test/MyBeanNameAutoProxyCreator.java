package org.mzj.test;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;

public class MyBeanNameAutoProxyCreator  extends BeanNameAutoProxyCreator {
	private static final long serialVersionUID = -6453424598767548384L;

	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName, TargetSource targetSource) {
//		System.out.println("======== " + beanClass + " -- " + beanName);
		return super.getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
	}
}
