package org.mzj.test;

import java.lang.reflect.Method;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

public class LogInterceptor implements MethodBeforeAdvice, AfterReturningAdvice {

	// 在服务中得方法执行之前加入before方法中得如下逻辑，stopWatch开始统计
	public void before(Method method, Object[] args, Object target) throws Throwable {
		String completeMethodName = getCompleteMethodName(target, method);
		System.out.println("========" + completeMethodName + "->");
	}

	// 在服务执行完毕，返回值之前加入如下afterReturning逻辑，stopWatch结束统计
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		String completeMethodName = getCompleteMethodName(target, method);
		System.out.println("========" + completeMethodName + "-|");
	}

	private String getCompleteMethodName(Object target, Method method) {
		String className = "";
		if (target != null) {
			className = target.toString();
			int loc = className.indexOf("@");
			if (loc >= 0) {
				className = className.substring(0, loc);
			}
		}
		return className + "." + method.getName();
	}
}