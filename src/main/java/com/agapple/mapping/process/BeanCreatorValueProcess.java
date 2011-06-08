package com.agapple.mapping.process;

import com.agapple.mapping.core.BeanMappingException;
import com.agapple.mapping.core.config.BeanMappingField;
import com.agapple.mapping.core.helper.ReflectionHelper;
import com.agapple.mapping.process.internal.SetProcessInvocation;
import com.agapple.mapping.process.internal.SetValueProcess;

/**
 * set操作流程中, 尝试创建一下嵌套的bean实例，通过反射newInstance,
 * 
 * @author jianghang 2011-5-28 下午11:32:38
 */
public class BeanCreatorValueProcess implements SetValueProcess {

    @Override
    public Object process(Object value, SetProcessInvocation setInvocation) throws BeanMappingException {
        if (value != null) {
            BeanMappingField currentField = setInvocation.getContext().getCurrentField();
            if (currentField.isMapping()) {
                // 判断下是否在处理嵌套的mapping
                // 这里的value代表从get取出来的嵌套对象，如果有值，说明在目标对象上也需要创建targetClass对象进行复制
                value = ReflectionHelper.newInstance(setInvocation.getContext().getCurrentField().getTargetClass());
            }
        }

        // 继续下一步的调用
        return setInvocation.proceed(value);
    }

}
