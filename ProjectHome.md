# Why need mapping #

这里列觉了几种需要使用mapping的场景
  1. model 和 DO的转化 (DO = Data Object , 数据库对象的设计是一种大宽表的设计，domain/model的设计，会有层次结构&具体)。 比如表设计存储会采用json存储动态数据，而在model中会是具体的属性
  1. model 和 VO的转化 (VO = View Object , 公司的产品detail页面，涉及了后端n多个domain/model的组合展示，这时候会进行包装成VO，包装一些页面组装逻辑)
  1. model 和 DTO的转化 (DTO = Data Transfer Object ，公司子系统比较多，系统之间会有比较多的rpc等remote调用)
  1. form -> bean的转化 (现在流行的几个MVC框架，都已经开始支持view层的参数注入，比如@Paramter(name="field")String , @Form("name=xx")Bean)。 提交的form表单数据，基本都是以map+list为主，就会涉及一个mapping

# Why BeanMapping #
  1. 解决BeanUtils, BeanCopier使用上的局限，只能针对同名属性的拷贝
  1. 相比于BeanUtils，性能提升是它的优势
  1. 相比于BeanCopier，类型之间的convertor是它的优势
  1. 支持插件方式的扩展，自身框架的设计也是基于插件扩展。

目前的插件支持：
  1. default value支持
  1. convetor转换
  1. script脚本支持 (EL表达式处理)
  1. bean creator(嵌套对象自动创建)

**性能测试图表：**

![http://dl.iteye.com/upload/attachment/496355/2b29ad34-a6fe-3da1-a3d0-b0d82fd6750c.png](http://dl.iteye.com/upload/attachment/496355/2b29ad34-a6fe-3da1-a3d0-b0d82fd6750c.png)

**相关说明：**
  * 从图中可以直观的看出，BeanMapping的性能相比于beanUtils，PropertyUtils的提升比较明显
  * 详细的性能测试结果可参见：http://code.google.com/p/mapping4java/wiki/Performance

# wiki list #
  * http://code.google.com/p/mapping4java/wiki/GettingStart
  * http://code.google.com/p/mapping4java/wiki/UserGuide
  * http://code.google.com/p/mapping4java/wiki/ProgramGuide
  * http://code.google.com/p/mapping4java/wiki/Performance

# contact #
  1. issue :  http://code.google.com/p/mapping4java/issues/list
  1. mail : [mailto:jianghang115@163.com](mailto:jianghang115@163.com)
  1. blog : http://agapple.iteye.com