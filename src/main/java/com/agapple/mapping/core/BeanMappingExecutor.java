package com.agapple.mapping.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agapple.mapping.core.config.BeanMappingConfigHelper;
import com.agapple.mapping.core.config.BeanMappingField;
import com.agapple.mapping.core.config.BeanMappingObject;
import com.agapple.mapping.core.helper.BatchObjectHolder;
import com.agapple.mapping.core.introspect.AbstractExecutor;
import com.agapple.mapping.core.introspect.BatchExecutor;
import com.agapple.mapping.core.introspect.GetExecutor;
import com.agapple.mapping.core.introspect.MapGetExecutor;
import com.agapple.mapping.core.introspect.MapSetExecutor;
import com.agapple.mapping.core.introspect.SetExecutor;
import com.agapple.mapping.core.introspect.Uberspector;
import com.agapple.mapping.process.internal.GetProcessInvocation;
import com.agapple.mapping.process.internal.SetProcessInvocation;
import com.agapple.mapping.process.internal.ValueProcessContext;

/**
 * Bean mapping具体的执行器
 * 
 * @author jianghang 2011-5-26 下午04:27:35
 */
public class BeanMappingExecutor {

    /**
     * 根据传递的param，进行mapping处理
     */
    public static void execute(BeanMappingParam param) {
        BeanMappingObject config = param.getConfig();
        BatchObjectHolder holder = null;
        BatchExecutor getBatchExecutor = null;
        BatchExecutor setBatchExecutor = null;
        if (config.isBatch()) { // 执行一次batch get操作，注意batch的获取操作需要放置在doFieldMapping/doBeanMapping之前
            getBatchExecutor = getGetBatchExecutor(param, config);
            setBatchExecutor = getSetBatchExecutor(param, config);
        }
        if (config.isBatch() && getBatchExecutor != null) { // 执行一次batch get操作
            Object[] batchValues = getBatchExecutor.gets(param.getSrcRef());
            holder = new BatchObjectHolder(batchValues);
        }
        List<BeanMappingField> beanFields = config.getBeanFields();
        for (int i = 0, size = beanFields.size(); i < size; i++) {
            BeanMappingField beanField = beanFields.get(i);
            if (beanField.isMapping()) {
                doBeanMapping(param, beanField, holder);
            } else {
                doFieldMapping(param, beanField, holder);
            }
        }
        if (config.isBatch() && setBatchExecutor != null && holder != null) { // 执行一次batch set操作
            setBatchExecutor.sets(param.getTargetRef(), holder.getBatchValues());
        }
    }

    /**
     * 获取set操作的BatchExecutor
     */
    private static BatchExecutor getSetBatchExecutor(BeanMappingParam param, BeanMappingObject config) {
        BatchExecutor executor = config.getSetBatchExecutor();
        if (executor != null) { // 如果已经生成，则直接返回
            return executor;
        }

        // 处理target操作数据搜集
        List<String> targetFields = new ArrayList<String>();
        List<Class> targetArgs = new ArrayList<Class>();
        for (BeanMappingField beanField : config.getBeanFields()) {
            String targetField = beanField.getTargetName();
            Class targetArg = beanField.getTargetClass();
            if (StringUtils.isEmpty(targetField) || targetArg == null) {
                return null; // 直接不予处理
            }
            // 搜集信息
            targetFields.add(targetField);
            targetArgs.add(targetArg);
        }
        // 生成下target批量处理器
        executor = Uberspector.getInstance().getBatchExecutor(param.getTargetRef(),
                                                              targetFields.toArray(new String[targetFields.size()]),
                                                              targetArgs.toArray(new Class[targetArgs.size()]));
        config.setSetBatchExecutor(executor);
        return executor;
    }

    /**
     * 获取get操作的BatchExecutor
     */
    private static BatchExecutor getGetBatchExecutor(BeanMappingParam param, BeanMappingObject config) {
        BatchExecutor executor = config.getGetBatchExecutor();
        if (executor != null) { // 如果已经生成，则直接返回
            return executor;
        }

        List<String> srcFields = new ArrayList<String>();
        List<Class> srcArgs = new ArrayList<Class>();
        // 处理src操作数据搜集
        for (BeanMappingField beanField : config.getBeanFields()) {
            String srcField = beanField.getSrcName();
            Class srcArg = beanField.getSrcClass();
            if (StringUtils.isEmpty(srcField) || srcArg == null) {
                return null; // 直接不予处理
            }
            // 搜集信息
            srcFields.add(srcField);
            srcArgs.add(srcArg);
        }
        // 生成下src批量处理器
        executor = Uberspector.getInstance().getBatchExecutor(param.getSrcRef(),
                                                              srcFields.toArray(new String[srcFields.size()]),
                                                              srcArgs.toArray(new Class[srcArgs.size()]));
        config.setGetBatchExecutor(executor);
        return executor;
    }

    /**
     * 处理下模型的field的mapping动作
     */
    private static void doFieldMapping(BeanMappingParam param, BeanMappingField beanField, BatchObjectHolder holder) {
        // 定义valueContext
        ValueProcessContext valueContext = new ValueProcessContext(param, param.getConfig(), beanField, holder,
                                                                   param.getCustomValueContext());
        // 设置getExecutor
        GetExecutor getExecutor = beanField.getGetExecutor();// 优先从beanField里取
        if (getExecutor == null && StringUtils.isNotEmpty(beanField.getSrcName())) {// 如果不为空,可能存在script
            getExecutor = Uberspector.getInstance().getGetExecutor(param.getSrcRef(), beanField.getSrcName());
            beanField.setGetExecutor(getExecutor);
        }
        // 设置setExecutor
        SetExecutor setExecutor = beanField.getSetExecutor();// 优先从beanField里取
        if (setExecutor == null && StringUtils.isNotEmpty(beanField.getTargetName())) {
            setExecutor = Uberspector.getInstance().getSetExecutor(param.getTargetRef(), beanField.getTargetName(),
                                                                   beanField.getTargetClass());
            beanField.setSetExecutor(setExecutor);

        }

        // 获取get结果
        GetProcessInvocation getInvocation = new GetProcessInvocation(getExecutor, valueContext,
                                                                      param.getGetProcesses());
        Object getResult = getInvocation.proceed();
        // 设置下srcClass
        if (getExecutor != null && beanField.getSrcClass() == null) {
            // 设置为自动提取的targetClasss
            if (getExecutor instanceof MapGetExecutor) {
                if (getResult != null) {
                    beanField.setSrcClass(getResult.getClass());// 优先设置为getResult的class对象
                }
            } else {
                beanField.setSrcClass(getSrcClass(getExecutor)); // 获取getExecutor方法的返回结果类型
            }
        }

        // 设置下targetClass
        if (setExecutor != null && beanField.getTargetClass() == null) {
            // 设置为自动提取的targetClasss
            if (setExecutor instanceof MapSetExecutor) {
                if (getResult != null) {
                    beanField.setTargetClass(getResult.getClass());// 优先设置为getResult的class对象
                }
            } else {
                beanField.setTargetClass(getTargetClass(setExecutor));
            }
        }

        // 执行set
        SetProcessInvocation setInvocation = new SetProcessInvocation(setExecutor, valueContext,
                                                                      param.getSetProcesses());
        setInvocation.proceed(getResult);
    }

    /**
     * 处理下子模型的嵌套mapping动作
     */
    private static void doBeanMapping(BeanMappingParam param, BeanMappingField beanField, BatchObjectHolder holder) {
        // 定义valueContext
        ValueProcessContext valueContext = new ValueProcessContext(param, param.getConfig(), beanField, holder,
                                                                   param.getCustomValueContext());
        // 检查一下targetClass是否有设置，针对bean对象有效
        // 如果目标对象是map，需要客户端强制设定targetClass
        SetExecutor setExecutor = beanField.getSetExecutor();
        if (setExecutor == null && StringUtils.isNotEmpty(beanField.getTargetName())) {// 可能存在为空
            setExecutor = Uberspector.getInstance().getSetExecutor(param.getTargetRef(), beanField.getTargetName(),
                                                                   beanField.getTargetClass());
            beanField.setSetExecutor(setExecutor);
        }
        GetExecutor getExecutor = beanField.getGetExecutor();
        if (getExecutor == null && StringUtils.isNotEmpty(beanField.getSrcName())) {// 可能存在为空
            getExecutor = Uberspector.getInstance().getGetExecutor(param.getSrcRef(), beanField.getSrcName());
            beanField.setGetExecutor(getExecutor);
        }

        // 获取新的srcRef
        // 获取get结果
        GetProcessInvocation getInvocation = new GetProcessInvocation(getExecutor, valueContext,
                                                                      param.getGetProcesses());
        Object srcRef = getInvocation.proceed();
        // 设置下srcClass
        if (getExecutor != null && beanField.getSrcClass() == null) {
            // 设置为自动提取的targetClasss
            if (getExecutor instanceof MapGetExecutor) {
                if (srcRef != null) {
                    beanField.setSrcClass(srcRef.getClass());// 优先设置为getResult的class对象
                }
            } else {
                beanField.setSrcClass(getSrcClass(getExecutor));
            }
        }

        if (setExecutor != null && beanField.getTargetClass() == null) {
            if (setExecutor instanceof MapSetExecutor) {
                beanField.setTargetClass(HashMap.class);// 针对Map处理,嵌套代码的复制默认设置为HashMap.class
            } else {
                beanField.setTargetClass(getTargetClass(setExecutor));
            }
        }

        // 执行set,反射构造一个子Model
        SetProcessInvocation setInvocation = new SetProcessInvocation(setExecutor, valueContext,
                                                                      param.getSetProcesses());
        // 如果嵌套对象为null，则直接略过该对象处理，目标对象也为null,此时srcRef可能为null
        Object value = setInvocation.proceed(srcRef); // 在目标节点对象上，创建一个子节点
        if (srcRef == null) {
            return; // 如果为null，则不做递归处理
        }

        if (beanField.getSrcClass() == null || beanField.getTargetClass() == null) {
            throw new BeanMappingException("srcClass or targetClass is null , " + beanField.toString());
        }
        BeanMappingObject object = BeanMappingConfigHelper.getInstance().getBeanMappingObject(
                                                                                              beanField.getSrcClass(),
                                                                                              beanField.getTargetClass());
        if (object == null) {
            throw new BeanMappingException("no bean mapping config for " + beanField.toString());
        }
        BeanMappingParam newParam = new BeanMappingParam();
        newParam.setTargetRef(value);// 为新创建的子model，注意使用value，可以在SetValueProcess中会创建新对象
        newParam.setSrcRef(srcRef);
        newParam.setConfig(object);
        // 复制并传递
        newParam.setGetProcesses(param.getGetProcesses());
        newParam.setSetProcesses(param.getSetProcesses());
        // 进行递归调用
        execute(newParam);
    }

    /**
     * 根据{@linkplain GetExecutor}获取对应的目标srcClass
     */
    private static Class getSrcClass(GetExecutor getExecutor) {
        Class type = ((AbstractExecutor) getExecutor).getMethod().getReturnType();
        return type;
    }

    /**
     * 根据{@linkplain SetExecutor}获取对应的目标targetClass
     */
    private static Class getTargetClass(SetExecutor setExecutor) {
        Class[] params = ((AbstractExecutor) setExecutor).getMethod().getParameterTypes();
        if (params == null || params.length != 1) {
            throw new BeanMappingException("illegal set method[" + ((AbstractExecutor) setExecutor).getMethodName()
                                           + "] for ParameterType");
        }
        return params[0];
    }

}