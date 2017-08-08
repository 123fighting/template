/*
 * Timeloit.com Inc.
 * Copyright (c) 2012 时代凌宇物联网数据平台. All Rights Reserved
 */
package com.template.projectName.webapp.controllers.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.HtmlUtils;

/**
 * controller的拦截器，用于进入controller的拦截
 * 
 * @Id $Id: CommonRequestInterceptor.java 3684 2012-01-14 02:46:28Z yulianyu $
 * @author yulianyu
 */
public class CommonRequestInterceptor extends HandlerInterceptorAdapter {


	private static Logger logger = Logger
			.getLogger(CommonRequestInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
       return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			if(request.getAttribute("returnMap") != null){
				Map returnMap = (Map)request.getAttribute("returnMap");
				Map turnMap = new HashMap();
				Iterator it = returnMap.entrySet().iterator();
				while(it.hasNext()){
					Entry entry = (Entry)it.next();
					if(entry.getValue() == null){
						turnMap.put(entry.getKey(), null);
						continue;
					}else{
					if(entry.getValue() instanceof Map){
						Map map = getMapElement(entry.getValue());
						turnMap.put(entry.getKey(), map);
					}else if(entry.getValue() instanceof List){
						List list = getListElement(entry.getValue());
						turnMap.put(entry.getKey(), list);
					}else if(isBaseType(entry.getValue())
							){
						if(entry.getValue() instanceof String){
							turnMap.put(entry.getKey(), HtmlUtils.htmlEscape((String)entry.getValue()));
						}else{
							turnMap.put(entry.getKey(), entry.getValue());
						}
					}else{
						Object object = htmlEncode(entry.getValue());
						turnMap.put(entry.getKey(), object);
					}
				}
				}
				request.setAttribute("returnMap", turnMap);
				return;
			}
			ModelMap modelMap = modelAndView.getModelMap();
			int index = 0;
			if(modelMap != null && !modelMap.isEmpty()){
			Iterator it = modelMap.entrySet().iterator();
			while(it.hasNext()){
				it.next();
				index++;
			}
			it = modelMap.entrySet().iterator();
			index--;
			/*}else{
				Map map = (Map)request.getAttribute("returnMap");
				it = map.entrySet().iterator();
				index = Integer.MAX_VALUE;
			}*/
			Map<String, Object> nMap = new HashMap<String, Object>();
			// Set sets = pMap.entrySet();
			if (modelMap != null) {
				// Iterator iter = sets.iterator();
				while (it.hasNext() && index > 0) {
					index--;
					Entry entry = (Entry) it.next();
					if (entry.getValue() instanceof List) {
						Object list = new Object();
						list = getListElement(entry.getValue());
						modelMap.put((String) entry.getKey(), list);
					} else if (entry.getValue() instanceof Map) {
						Object list = new Object();
						list = getMapElement(entry.getValue());
						modelMap.put((String) entry.getKey(), list);
					} else if (isBaseType(entry.getValue())
							) {
						if (entry.getValue() instanceof String) {
							Object o = entry.getValue();
							String nData = HtmlUtils.htmlEscape((String) entry
									.getValue());
							modelMap.put((String) entry.getKey(), nData);
						} else {
							modelMap.put((String) entry.getKey(),
									entry.getValue());
						}
					} else {
						Object obj = htmlEncode(entry.getValue());
						modelMap.put((String) entry.getKey(), obj);
					}
				}
				System.out.println(modelMap);
				Set set = modelMap.entrySet();
				if (set != null) {
					Iterator iters = set.iterator();
					while (iters.hasNext()) {
						Entry entrys = (Entry) iters.next();
						nMap.put((String) entrys.getKey(), entrys.getValue());
					}
				}
				modelAndView.addObject(nMap);
			}

		}else{
	
			return;
		}

		System.out.println("===================" + 43 + "================");
	}
	}
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	public boolean isBaseType(Object obj) {

		if(obj == null){
			return false;
		}
		if (obj.getClass().equals(Integer.class)
				|| obj.getClass().equals(Double.class)
				|| obj.getClass().equals(String.class)
				|| obj.getClass().equals(Float.class)
				|| obj.getClass().equals(Long.class)
				|| obj.getClass().equals(Boolean.class)
				|| obj.getClass().equals(Short.class)
				|| obj.getClass().equals(Character.class))
			return true;
		return false;
	}

	public Object htmlEncode(Object obj) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		Class clazz = obj.getClass();
		if(obj == null)
			return null;
		try {
			Object newObjec = clazz.newInstance();
			Field[] field = clazz.getDeclaredFields();
			Class[] classes = clazz.getDeclaredClasses();
			for (Field f : field) {
				String methodSet = "set"
						+ f.getName().substring(0, 1).toUpperCase()
						+ f.getName().substring(1);
				String methodGet = "get"
						+ f.getName().substring(0, 1).toUpperCase()
						+ f.getName().substring(1);
				Object newObj = f.getType().newInstance();
				if (newObj instanceof List) {
					// Object newObj = f.getType().newInstance();
					Method mtd = obj.getClass().getMethod(methodGet, null);
					Object obje = mtd.invoke(obj, null);
					if(obje != null){
					Object list = getListElement(obje);
					Method mtdSet = obj.getClass().getMethod(methodSet,
							Object.class);
					mtdSet.invoke(newObjec, newObj);
					}
				} else if (newObj instanceof Map) {
					// Object newObj = f.getType().newInstance();
					Method mtd = obj.getClass().getMethod(methodGet, null);
					Object obje = mtd.invoke(obj, null);
					if(obje != null){
					Object list = getMapElement(obje);
					Method mtdSet = obj.getClass().getMethod(methodSet,
							Object.class);
					mtdSet.invoke(newObjec, newObj);
					}
				}
				boolean bool = isContain(classes, f);

				if (bool) {
					// Object newObj = f.getType().newInstance();
					Method mtd = obj.getClass().getMethod(methodGet, null);
					Object obje = mtd.invoke(obj, null);
					if(obje != null){
					newObj = htmlEncode(obje);
					Method mtdSet = obj.getClass().getMethod(methodSet,
							Object.class);
					mtdSet.invoke(newObjec, newObj);
					}
				} else {
					Method mtdGet = clazz.getMethod(methodGet, null);
					Object valueGet = mtdGet.invoke(obj, null);
					if(valueGet != null){
					Method mtdSet = obj.getClass().getMethod(methodSet,
							Object.class);
					if (obj.getClass().equals(String.class)) {
						Object valEncode = HtmlUtils
								.htmlEscape((String) valueGet);
						mtdSet.invoke(newObjec, valEncode);
					} else {
						mtdSet.invoke(newObjec, valueGet);
					}
					}
				}
			}
			return newObjec;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch
		}
		/*
		 * if (isBaseType(obj)) { if (obj.getClass().equals(String.class))
		 * return HtmlUtils.htmlEscape((String) obj); else return obj; }
		 */
		return null;
	}

	public boolean isContain(Class[] clas, Field field) {

		// int num = 0;
		for (Class cl : clas) {
			if (cl.equals(field.getType()))
				return true;
		}
		return false;
	}

	public List getListElement(Object list) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InvocationTargetException {
		List lis = new ArrayList();
		List listt = (ArrayList) list;
		List li = new ArrayList();
		Map map = new HashMap();
		for (int i = 0; i < listt.size(); i++) {
			Object obj = listt.get(i);
			if (obj instanceof Map) {
				Map ma = (Map)obj;
				if(ma != null && ma.size() != 0){
				map = getMapElement(obj);
				lis.add(map);
				}else{
					if(ma.size() == 0)
					lis.add("");
					lis.add(null);
				}
			} else if (obj instanceof List) {
				List listts = (List)obj;
				if(listts != null && listts.size() != 0){
				li = getListElement(obj);
				lis.add(lis);
				}else{
					if(listts.size() == 0)
					lis.add("");
					lis.add(null);
				}
			} else if (isBaseType(obj)) {
				if (obj instanceof String) {
					if(obj != null){
					lis.add(HtmlUtils.htmlEscape((String) obj));
					//lis.add(obj);
					}else{
						lis.add("");
					}
				}else{
					lis.add(obj);
				}
			} else {
				if(obj != null){
				Object o = htmlEncode(obj);
				lis.add(o);
				}else{
					lis.add(null);
				}
			}
		}
		return lis;
	}

	public Map getMapElement(Object obj) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InvocationTargetException {

		Map map = (HashMap) obj;
		Map mmp = new HashMap();
		Object oo = new Object();
		int i = 0;
		List list = new ArrayList();
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			if (entry.getValue() instanceof List) {
				List li = (List)entry.getValue();
				int length = li.size();
				if(li != null && li.size() != 0){
				list = getListElement(entry.getValue());
				mmp.put(entry.getKey(), list);
				}else{
					if(li.size() == 0)
					mmp.put(entry.getKey(), "");
					mmp.put(entry.getKey(), null);
				}
				
			} else if (entry.getValue() instanceof Map) {
				Map mmap = new HashMap();
				Map mm = (Map)entry.getValue();
				if(mm != null && mm.size() != 0){
				mmap = getMapElement(entry.getValue());
				mmp.put(entry.getKey(), mmap);
				}else{
					if(mm.size() == 0)
					 mmp.put(entry.getKey(), "");
					mmp.put(entry.getKey(), null);
				}
			} else if (isBaseType(entry.getValue())) {
				if (entry.getValue() instanceof String){
					if(entry.getValue() != null){
					mmp.put(entry.getKey(),
							HtmlUtils.htmlEscape((String) entry.getValue()));
				//mmp.put(entry.getKey(), entry.getValue());
					}else{
						if(entry.getValue().equals(""))
						mmp.put(entry.getKey(), "");
						mmp.put(entry.getKey(), null);
					}
				}else{
					mmp.put(entry.getKey(), entry.getValue());
				}
			} else {
				if(entry.getValue() != null){
				oo = htmlEncode(entry.getValue());
				mmp.put(entry.getKey(), oo);
				}else{
					mmp.put(entry.getKey(), null);
				}
			}
		}
		return mmp;
	}
	

}
