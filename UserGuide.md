

# XMl配置 #
## 配置内容 ##
## 引入schema ##
```xml

<bean-mappings xmlns="http://mapping4java.googlecode.com/schema/mapping" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://mapping4java.googlecode.com/schema/mapping http://mapping4java.googlecode.com/svn/trunk/src/main/resources/META-INF/mapping.xsd">
....


Unknown end tag for &lt;/bean-mappings&gt;


```
### global-configurations ###
说明：定义行为参数
```xml

<global-configurations debug="false" mappingNullValue="true" mappingEmptyStrings="true" trimStrings="true" />
```
**相关说明：**
  * debug：是否开启详细的mapping过程的日志，可用于问题分析。在真实的产品环境中一般不建议打开，默认为false
  * mappingNullValue：mapping过程中，如果源目标对象get返回的结果为null是否继续对目标对象进行set属性为null的处理，默认为true
  * mappingEmptyStrings：mapping过程中，如果源目标对象get返回的结果为Emptys的字符串对象，是否继续对目标对象进行set属性为null的处理，默认为true
  * trimStrings：mapping过程中，针对字符串类型是否需要进行trim操作，默认为false

### class-alias-configurations ###
说明：类别名定义
```
<class-alias-configurations>
   <classAlias alias="commonClass" class="com.agapple.mapping.process.convetor.CommonAndCommonConvertor$CommonToCommon" />
</class-alias-configurations>
```
**相关说明：**
  * classAlias：可将指定的class定义alias别名，alias的scope为整个jvm实例

### convetors-configurations ###
说明：自定义convertor类
```
<convetors-configurations>
   <convertor alias="custom1" class="com.agapple.mapping.process.convetor.XXX" />
   <convertor alias="custom2" class="commonClass" />
</convetors-configurations>
```
**相关说明：**
  * convertor：可将指定的class注册到convertor容器中，并指定convetor别名为alias。一个class可以对应多个alias
  * convertor所定义的class，可以直接使用[UserGuide#class-alias-configurations](UserGuide#class-alias-configurations.md)中的alias别名

### function-class-configurations ###
说明：定义script中的function支持类
```
<function-class-configurations>
  <functionClass name="customFunction" class="com.agapple.mapping.script.CustomFunctionClass" />
</function-class-configurations>
```
**相关说明：**
  * functionClass：可将指定的class注册到script容器中，并指定别名为alias。一个class可以对应多个alias
  * function所定义的class，可以直接使用[UserGuide#class-alias-configurations](UserGuide#class-alias-configurations.md)中的alias别名
  * 更多关于script/function的使用

### bean-mapping ###
说明：定义mapping的具体行为
```
<bean-mapping srcClass="map" targetClass="java.util.HashMap" reversable="false" mappingNullValue="false" debug="true">
  <field-mapping srcName="id" srcClass="java.lang.String" targetName="id" targetClass="java.lang.String" defaultValue="0" convetor="stringToDate" script="customFunction:sum(src.intValue,src.integerValue)" mappingNullValue="true" trimStrings="true"/>
  <field-mapping mapping="true" srcName="model" targetName="model" mappingNullValue="true" trimStrings="true" />
</bean-mapping>
```
**相关说明：**
  1. bean-mapping属性
    * srcClass：源对象的class类型
    * targetClass：目标对象的class类型
    * reversable：mapping规则是否允许逆序处理
    * debug：覆盖[UserGuide#global-configurations](UserGuide#global-configurations.md)的属性定义
    * mappingNullValue：覆盖[UserGuide#global-configurations](UserGuide#global-configurations.md)的属性定义
    * mappingEmptyStrings：覆盖[UserGuide#global-configurations](UserGuide#global-configurations.md)的属性定义
    * trimStrings：覆盖[UserGuide#global-configurations](UserGuide#global-configurations.md)的属性定义
    * srcKey：script上下文中，对应源object的引用名字，比如可通过srcKey.field获取属性
    * targetKey：script上下文中，对应目标object的引用名字
  1. field-mapping属性
    * srcName：源对象的属性名
    * srcClass：源对象的属性类型
    * srcLocatorClass：指定在该LocatorClass，查找对应的目标数据name的属性方法，解决继承属性
    * srcComponentClass：指定Collection/Array的ComponentType
    * targetName：目标对象的属性名
    * targetClass：目标对象的属性类型
    * targetLocatorClass：指定在该LocatorClass，查找对应的目标数据name的属性方法，解决继承属性
    * targetComponentClass：指定Collection/Array的ComponentType
    * defaultValue：默认值
    * convetor：指定类型转换器
    * script：指定script脚本
    * mapping：是否进行嵌套的mapping映射处理

# Example #
## 基础类型 Example ##
基本类型定义：
  * int,long,short,byte,char,boolean,float,double的8种原型
  * Integer,Long,Short,Byte,Character,Boolean,Float,Double的8中原型对应的基本类型
  * BigInteger,BigDecimal类型

默认支持的几种类型转化：
  1. 基本类型之间的互转
  1. 支持(java.util.Date , java.util.Calendar)和字符串的转化，提供默认的day("yyyy-MM-dd") , time("yyyy-MM-dd HH:mm:ss")两种格式的转化
  1. 支持基础类型的Array,List,Set，允许互相之间的转化
    * List to List
    * List to Array
    * Array to Array
    * Set to Set
    * Set to Array
    * Set to List

### 基础类型转化例子 ###
```
<bean-mapping srcClass="xxx" targetClass="xxxx">
   <field-mapping srcName="intValue" srcClass="int" targetName="bigValue" targetClass="java.math.BigDecimal"/>
</bean-mapping>
```
**相关说明：** 这里src/target属性均为基础类型，所以不需要定义任何convertor，系统能自动的进行mapping转化

### String和Date转化例子 ###
```
<bean-mapping srcClass="xxx" targetClass="xxxx">
   <field-mapping srcName="gmtCreate" srcClass="java.util.Date" targetName="gmtCreate" targetClass="string" convertor="DateDayToString" />
</bean-mapping>
```
**相关说明:** 默认支持的几种Date,Calendar,String转化
  * DateDayToString/StringToDateDay ('yyyy-MM-dd')
  * DateTimeToString/StringToDateTime ('yyyy-MM-dd HH:mm:ss')
  * CalendarDayToString/StringToCalendarDay ('yyyy-MM-dd')
  * CalendarTimeToString/StringToCalendarTime ('yyyy-MM-dd HH:mm:ss')

### array&list转化例子 ###
```
<bean-mapping srcClass="xxx" targetClass="xxxx">
   <field-mapping srcName="array" targetName="list" targetClass="java.util.ArrayList" targetComponentClass="java.util.BigDecimal" />
   <field-mapping srcName="set" srcClass="java.util.HashSet" srcComponentClass="int" targetName="list" targetClass="java.util.ArrayList" targetComponentClass="java.util.BigDecimal" />
</bean-mapping>
```
**相关说明:**
  * 源属性的类型&Component类型都可以自动的进行识别，可以不用配置
  * 如果目标属性为Array类型，也可以自动的进行识别，可以不用配置
  * 如果针对目标类型是Collection，并且没有指定对应的Component类型，则默认会将源类型的对象引用直接复制给目标Collection

### 自定义convertor ###
```
<convetors-configurations>
   <convertor alias="custom1" class="com.agapple.mapping.process.convetor.XXX" />
</convetors-configurations>

<bean-mapping srcClass="xxx" targetClass="xxx">
  <field-mapping srcName="srcValue" targetName="targetValue" convertor="custom1" />
</bean-mapping>
```
**相关说明：**
  1. 实现[http://code.google.com/p/mapping4java/source/browse/trunk/src/main/java/com/agapple/mapping/process/convetor/Convertor.java](Convertor.md)接口
  1. 在[UserGuide#convetors-configurations](UserGuide#convetors-configurations.md)中注册convertor为指定的alias
  1. 在field-mapping中声明使用上一步定义的alias name

## Map&Bean Example ##
说明：目前BeanMapping支持map,bean的mapping处理。
```
<bean-mapping srcClass="java.util.HashMap" targetClass="java.util.HashMap">
   <field-mapping srcName="int" srcClass="int" targetName="integer" targetClass="java.lang.Integer" />
</bean-mapping>
```
**相关说明：**　支持Map&Bean的互相转化，只需指定对应的src/target class类型
  * Map To Map
  * Map To Bean
  * Bean To Map
  * Bean To Bean

## Nested Mapping Example ##
**相关说明：** BeanMapping支持嵌套属性的mapping
  * model下的子model需要进行嵌套属性mapping
  * collection类型的component进行嵌套mapping处理
### 子模型嵌套 ###
```
<bean-mapping srcClass="java.util.HashMap" targetClass="java.util.HashMap">
   <field-mapping srcName="name" targetName="targetName" />
   <field-mapping srcName="subModel" targetName="targetSubModel" mapping="true" />
</bean-mapping>

<bean-mapping srcClass="xxx.SubModel" targetClass="xxx.targetSubModel" >
....
</bean-mapping>
```
**相关说明：**
  1. 首先在field-mapping中定义mapping="true"，声明该属性是个复合属性需要进行递归mapping处理
  1. 定义嵌套复合属性的mapping定义

### collection的component嵌套 ###
```
<bean-mapping srcClass="java.util.HashMap" targetClass="java.util.HashMap">
   <field-mapping srcName="name" targetName="targetName" />
   <field-mapping srcName="subList" srcComponentClass="subModel" targetName="targetList" targetComponentClass="targetSubModel" />
</bean-mapping>

<bean-mapping srcClass="xxx.SubModel" targetClass="xxx.targetSubModel" >
....
</bean-mapping>
```
**相关说明：**
  1. 首先在field-mapping中定义src/target ComponentClass
  1. 定义嵌套复合属性的mapping定义，如果没有mapping定义则默认进行同名属性拷贝
## Default Value Mapping Example ##
说明：支持默认值定义
```
<bean-mapping srcClass="xxx" targetClass="xxx">
  <field-mapping srcName="srcValue" targetName="targetValue" defaultValue="100" />
</bean-mapping>
```
**相关说明：**
  * defaultValue的定义主要是针对null value才生效
  * defautlvalue作用在mappingNullValue/mappingEmptyStrings之前，所以声明了defautlvalue的，mappingNullValue/mappingEmptyStrings都会失效
  * defaultValue默认为string字符串，如果目标类型为非基本类型，则需要自定义convertor

## Inheritance mapping Example ##
说明：支持有继承关系的bean mapping操作
```
<bean-mapping srcClass="xxx" targetClass="xxx">
  <field-mapping srcName="superAttribute" srcLocatorClass="superSrcClass" targetName="superAttribute" targetLocatorClass="superTargetClass" />
</bean-mapping>
```
**相关说明：**
  * srcLocatorClass/targetLocatorClass :可指定操作的目标class类型，比如可以指定当前mapping class的父类中的属性

## 特殊场景mapping Example ##
### no getter/setter method ###
说明：允许src/target属性没有对应的方法，配置方式和正常的一致。

### this symbol name ###
```
<bean-mapping srcClass="xxx" targetClass="xxx" >
  <field-mapping srcName="this" targetName="subModel" />
</bean-mapping>
```
**相关说明：** 允许使用"this"符号，表明将当前的object ref做为另一个模型的子属性

## override global configurations Example ##
```
<bean-mapping srcClass="xx" targetClass="xx" mappingNullValue="false" debug="true">
  <field-mapping srcName="id" targetName="id" defaultValue="0" mappingNullValue="true" trimStrings="true"/>
</bean-mapping>
```
**相关说明：**
  * 参数优先级为field-mapping > bean-mapping -> global
  * 如果没有定义，bean-mapping继承于global，field-mapping继承于bean-mappng

## script Example ##
**相关说明：** 允许使用script个性化定义mapping处理规则。script采用了apache下的JEXL做为解析引擎进行处理
  * 支持四则运算
  * 支持数组属性的处理
  * 支持集合属性的处理
  * 允许自定义function处理，比如json格式，xml格式

### 四则运算 ###
```
<bean-mapping srcClass="xx" targetClass="xx" mappingNullValue="false" debug="true">
  <field-mapping targetName="value" script="src.one + src.two" />
</bean-mapping>
```

### 数组/集合属性的处理 ###
```
<bean-mapping srcClass="xx" targetClass="xx" mappingNullValue="false" debug="true">
  <field-mapping targetName="value" script="src.array[0]" />
  <field-mapping targetName="value" script="src.list[0]" />
</bean-mapping>
```

### 自定义function的处理 ###
```
<function-class-configurations>
  <functionClass name="customFunction" class="com.agapple.mapping.script.CustomFunctionClass" />
</function-class-configurations>

<bean-mapping srcClass="xxx" targetClass="xxx">
  <field-mapping srcName="srcValue" targetName="targetValue" script="customFunction:method(src.srcValue)" />
</bean-mapping>
```
**相关说明：**
  1. 提供任意class实现，需要保证线程安全。无需事先任何接口
  1. 在[UserGuide#function-class-configurations](UserGuide#function-class-configurations.md)中注册该function为指定的alias
  1. 在field-mapping中声明使用该function方式为：alias:methodName(args)。
    * alias为对应在上一步所定义的别名
    * methodName为对应function class中的任一一个method name
    * args可通过srcKey,targetKey进行属性访问。包括数组,Collection,Bean,Map等对象

## reversable Example ##
说明：默认bean-mapping的定义均为单向，从srcClass映射为targetClass。可通过声明reversable=true，表明映射关系可逆，会自动生成targetClass到srcClass的映射
```
<bean-mapping srcClass="xxx" targetClass="xxx" reversable="true">
....
</bean-mapping>
```
**相关说明：**
  * 一旦mapping关系中使用了script脚本定义，则reversable会失效，强制为不可逆

# Mapping API #
## 介绍 ##
基于XML进行mapping行为定义，使用上存在一些限制
  1. 静态定义，不可修改
  1. class和属性的定义都是类名和方法名的复制，在借助于IDE进行重构时容易存在遗漏

## 使用 ##
```
BeanMappingBuilder builder = new BeanMappingBuilder() {

            protected void configure() {
                mapping(HashMap.class, HashMap.class).batch(false).reversable(true).keys("src", "target");
                fields(srcField("one"), targetField("oneOther")).convertor("convertor").defaultValue("ljh");
                fields(srcField("two").clazz(String.class), targetField("twoOther")).script("1+2").convertor(
                                                                                                             StringToCommon.class);
                fields(srcField("three").clazz(ArrayList.class), targetField("threeOther").clazz(HashSet.class)).recursiveMapping(
                                                                                                                                  true);
            }

        };
```
通过builder可以比较方便的构造mapping config，最后需要生成mapping实例，还需要做一步：
```
BeanMapping mapping = new BeanMapping(builder);
mapping.mapping(src, dest);//使用
```
or
```
BeanMappingConfigHelper.getInstance().register(builder); // 进行注册
BeanMappingObject object = BeanMappingConfigHelper.getInstance().getBeanMappingObject(srcClass,targetClass);
mapping.mapping(src, dest);//使用
```