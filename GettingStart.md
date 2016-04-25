# 下载 #
http://code.google.com/p/mapping4java/downloads/list

代码已提交到mvn仓库，可设置mvn dependency进行依赖：
```
<dependency>
  <groupId>com.googlecode.mapping4java</groupId>
  <artifactId>mapping</artifactId>
  <version>1.0.0</version>
</dependency>
```
# 第一个例子 #

## 默认的属性拷贝 ##
```
public BeanCopy srcCopyer    = BeanCopy.create(SrcMappingObject.class, TargetMappingObject.class);  
public BeanCopy targetCopyer = BeanCopy.create(TargetMappingObject.class , SrcMappingObject.class);  
  
@Test  
public void testCopy_ok() {  
    SrcMappingObject srcRef = new SrcMappingObject();  
    srcRef.setIntegerValue(1);  
    srcRef.setIntValue(1);  
    srcRef.setName("ljh");  
    srcRef.setStart(true);  
  
    TargetMappingObject targetRef = new TargetMappingObject();// 测试一下mapping到一个Object对象  
    srcCopyer.copy(srcRef, targetRef);  
  
    SrcMappingObject newSrcRef = new SrcMappingObject();// 反过来再mapping一次  
    targetCopyer.copy(targetRef, newSrcRef);  
}  
```

  1. 使用方式上是否觉得比较眼熟，类似于cglib的BeanCopier。
  1. 本例子的演示主要是将SrcMappingObject的属性拷贝到TargetMappingObject，自动扫描对应的同名属性进行拷贝

代码例子： [BeanCopyTest.java http://code.google.com/p/mapping4java/source/browse/trunk/src/test/java/com/agapple/mapping/BeanCopyTest.java]

## 自定义属性拷贝 ##
```
<bean-mappings xmlns="http://mapping4java.googlecode.com/schema/mapping" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xsi:schemaLocation="http://mapping4java.googlecode.com/schema/mapping http://mapping4java.googlecode.com/svn/trunk/src/main/resources/META-INF/mapping.xsd">  
    <!--  (bean-bean) mapping 测试 -->  
    <bean-mapping batch="true" srcClass="com.agapple.mapping.object.SrcMappingObject" targetClass="com.agapple.mapping.object.TargetMappingObject" reversable="true">  
        <field-mapping srcName="intValue" targetName="intValue" />  
        <field-mapping targetName="integerValue" script="src.intValue + src.integerValue" /> <!-- 测试script -->  
        <field-mapping srcName="start" targetName="start" />  
        <field-mapping srcName="name" targetName="targetName" /> <!--  注意不同名 -->  
        <field-mapping srcName="mapping" targetName="mapping" mapping="true" />  
    </bean-mapping>  
      
    <bean-mapping batch="true" srcClass="com.agapple.mapping.object.NestedSrcMappingObject" targetClass="com.agapple.mapping.object.NestedTargetMappingObject" reversable="true">  
        <field-mapping srcName="name" targetName="name" defaultValue="ljh" /> <!-- 测试default value -->  
        <field-mapping srcName="bigDecimalValue" targetName="value" targetClass="string" defaultValue="10" /> <!-- 测试不同名+不同类型+default value  -->  
    </bean-mapping>  
  
</bean-mappings>  
```

  1. 复杂的mapping关系可以通过外部xml的方式进行定义
  1. 将mapping配置注册到框架中

代码例子： [BeanMappingTest.java http://code.google.com/p/mapping4java/source/browse/trunk/src/test/java/com/agapple/mapping/BeanMappingTest.java]