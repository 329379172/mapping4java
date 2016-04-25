# 性能测试 #
一个产品不管功能说的如何天花乱坠，整个工具的性能相比也是大家比较会关注的一个点。<br />
性能测试case：构造一个CopyBean，基本涵盖了普通类型，对象处理等，进行批量处理
```
public class CopyBean {  
  
        private int        intValue;  
        private boolean    boolValue;  
        private float      floatValue;  
        private double     doubleValue;  
        private long       longValue;  
        private char       charValue;  
        private byte       byteValue;  
        private short      shortValue;  
        private Integer    integerValue;  
        private Boolean    boolObjValue;  
        private Float      floatObjValue;  
        private Double     doubleObjValue;  
        private Long       longObjValue;  
        private Short      shortObjValue;  
        private Byte       byteObjValue;  
        private BigInteger bigIntegerValue;  
        private BigDecimal bigDecimalValue;  
        private String     stringValue;  
} 
```

BeanCopy性能测试，对比的内容：
  1. BeanCopy.copy
  1. !Method.invoke
  1. FastMethod.invoke
  1. BulkBean
  1. BeanCopier
  1. HardCode (硬编码，直接手工挨个复制属性)
  1. PropertyUtils  (不做类型转化)
  1. BeanUtils

**测试结果：**
![http://dl.iteye.com/upload/attachment/496355/2b29ad34-a6fe-3da1-a3d0-b0d82fd6750c.png](http://dl.iteye.com/upload/attachment/496355/2b29ad34-a6fe-3da1-a3d0-b0d82fd6750c.png)

**相关说明：**
> 因为beanUtils，PropertyUtils的性能太差，基本上其他的柱状图都看不清楚。

![http://dl.iteye.com/upload/attachment/496357/a8752fbc-d83b-3fb9-a50a-8eee7c9ac151.png](http://dl.iteye.com/upload/attachment/496357/a8752fbc-d83b-3fb9-a50a-8eee7c9ac151.png)

**相关说明：**
> 排除了PropertyUtils/BeanUtils的性能对比图，发现BeanMapping提供的copy方法可以比直接Method的反射调用略快一点，不过和cglib的BeanCopier还是有点差距，毕竟人家的功能支持的也很简单，很裸露的get/set调用。性能上基本也和直接硬编码写copy操作一样。

**具体测试数据：**
| |**开启batch优化(200w次):单位ns**|**纯解释执行(排除JIT优化)(10w次):单位ns**|
|:|:------------------------|:----------------------------|
|BeanMapping.copy|1189                     |72780                        |
|!Method|1322                     |25882                        |
|FastMethod|533                      |15961                        |
|BulkBean|108                      |4420                         |
|BeanCopier|18                       |1566                         |
|HardCopy|17                       |1376                         |
|PropertyUtils|22143                    |1037770                      |
|BeanUtils|43980                    |1766392                      |

**相关说明：**
  * 首先注意一下，单位是ns。　1s=1000ms=1\*10^9 ns
  * BeanCopier和手工编码写的copy性能基本接近
  * BeanMapping的copy和simpleCopy的区别，copy可以支持类型转化，simpleCopy不会处理类型转化。
  * BeanMapping的性能是BeanUtils的近40倍左右，不过和BeanCopier还是有些差距，大概在50倍左右。 (换另一个概念，就是执行100w次，BeanCopier可以比BeanMapping节省1秒，可以比BeanUtils节省40多秒，比较直观吧)