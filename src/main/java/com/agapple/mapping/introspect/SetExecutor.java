package com.agapple.mapping.introspect;

import com.agapple.mapping.BeanMappingException;

/**
 * 属性的Set方法操作接口
 * 
 * @author jianghang 2011-5-25 上午11:14:07
 */
public interface SetExecutor {

    Object invoke(Object key, Object value) throws BeanMappingException;
}
