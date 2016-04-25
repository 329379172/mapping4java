

# 介绍 #
iteye相关文章：
  * http://agapple.iteye.com/blog/1075671
  * http://agapple.iteye.com/blog/1101502

# 总体设计 #
![http://dl.iteye.com/upload/attachment/496320/482a8f78-9acc-35d6-be77-9a1fe03e8349.jpg](http://dl.iteye.com/upload/attachment/496320/482a8f78-9acc-35d6-be77-9a1fe03e8349.jpg)
**相关说明：**
  * 将属性mapping的动作抽象成Get/ Se两个操作。 Get操作针对数据源对象，Set操作针对数据目标对象
  * 在Get/Set中间，定义了一个ValueProcess处理插件的概念，允许扩展相关的功能插件 （自认为相比于BeanUtils/BeanCopier的非常好的亮点，扩展性良好）
  * 整个BeanMapping是采用微内核的设计，自身的一些功能点也是通过ValueProcess插件.

## Get/Set设计 ##
  1. Get操作抽象为GetExecutor
    * FieldGetExecutor
    * MapGetExecutor
    * PropertyGetExecutor
    * ThisSymbolGetExecutor
  1. Set操作抽象为SetExecutor
    * FieldSetExecutor
    * MapSetExecutor
    * PropertySetExecutor
**说明：**
  1. GetExecutor/SetExecutor的选择处理，客户端可通过Uberspector进行获取
```
Uberspector.getInstance().getGetExecutor(Class locatorClass, Object identifier)
Uberspector.getInstance().getSetExecutor(Class locatorClass, Object identifier, Class arg)
```
  1. Executor的抽象，允许自定义新的Get/Set操作处理，比如Factory模式的支持等，扩展相对比较容易
### 实现自定义Get/SetExecutor ###
TODO

**相关类图设计**
![http://dl.iteye.com/upload/attachment/510028/4c121cbd-7c5e-3fa3-b073-6c44a72e68d8.png](http://dl.iteye.com/upload/attachment/510028/4c121cbd-7c5e-3fa3-b073-6c44a72e68d8.png)

## ValueProcess设计 ##
> ValueProcess是本工具设计的核心点,也是相应的功能扩展点. 它在这个mapping过程中，所处的位置就是Get,Set的当中，允许动态的进行修改。
默认支持的几个ValueProcess插件:
  * DefaultValueValueProcess (支持[Value Mapping Example](UserGuide#Default.md))
  * ConvertorValueProcess (支持[UserGuide#基础类型 Example])
  * ScriptValueProcess (支持[Example](UserGuide#script.md))
  * DebugValueProcess (打印debug信息)
  * BehaviorValueProcess (支持[UserGuide#global-configurations](UserGuide#global-configurations.md)功能)

**相关类图设计：**
![http://dl.iteye.com/upload/attachment/510030/23dde9a4-582a-3567-b4b1-a416234f95ec.png](http://dl.iteye.com/upload/attachment/510030/23dde9a4-582a-3567-b4b1-a416234f95ec.png)

### 实现自定义ValueProcess ###
**ValueProcess接口:**
```
public interface ValueProcess {

    public Object process(Object value, ValueProcessInvocation invocation) throws BeanMappingException;

}
```

**CustomValueProcess:**
```
public class CustomValueProcess implements ValueProcess {

    public Object process(Object value, ValueProcessInvocation invocation) throws BeanMappingException {
		ValueProcessContext context = invocation.getContext();
		if(context xxxx) {
			// do with something
		} 
		
		return invocation.proceed(value); // 继续传递
    }
}
```
**相关说明：**
  1. ValueProcessContext为整个ValueProcess处理过程中的上下文相关数据
    * BeanMappingParam: 当前需要进行mapping操作的相关参数对象
    * BeanMappingObject: 当前需要进行mapping操作的相关配置对象(基于XML or Mapping API)后的解析结果
    * BeanMappingField: 当前进行mapping操作的field属性配置对象(比如src/target field)
  1. ValueProcessInvocation: 为ValueProcess的调度控制器
    * invocation.getContext(): 可获取ValueProcessContext对象
    * invocation.proceed(value)： 传递给chian ValueProcess的下一个节点

## 客户端设计 ##
![http://dl.iteye.com/upload/attachment/510072/6e837a9c-94d7-3ed1-8c1f-9c5d1b59e9ac.png](http://dl.iteye.com/upload/attachment/510072/6e837a9c-94d7-3ed1-8c1f-9c5d1b59e9ac.png)
  * BeanMapping :  本轮子的核心功能，通过基于配置方式的mapping映射，支持convetor,defaultValue,script的所有功能。要求使用之前必须提前配置bean-mapping映射。
  * BeanCopy 和 BeanMap ： 都是一些扩展功能，基于本轮子的核心架构不变的基础上，开发了BeanUtils.copyProperties() , BeanUtils.describe,  BeanUtils.populate()的功能。 使用该api，可以不需要配置映射文件，会进行自动扫描，就是基于同名属性的处理前提。
  * BeanMappingUtil ： 提供了BeanMapping , BeanCopy , BeanMap的所有方法，提供静态的util方法处理。每次都会构造对应的BeanMapping等对象，注意：每次进行构造BeanCopy,BeanMap，解析的属性结果会有cache。所以使用该util不会有很明显的性能下降，无非就是多一些临时对象。