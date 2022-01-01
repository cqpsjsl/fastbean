# fastbean

#### 介绍
> fastbean基于cglib。一个快速的基于字节码的映射，它不依赖反射来操作Bean。
> 
> 默认支持同名同类型(含包装类)属性赋值,不同名需要设置nameMapping，字段忽略需要设置ignoreSet，
> 类型不同需要设置类型转换链(默认set NULL)。
> 详细用法请查看example
## 特点
- 基于字节码技术。
- 属性名映射,属性忽略。

- 支持自定义转换链，当前只支持类型不一致才会调用。
- 泛型安全检查  
- 代理类缓存,多次调用效率更高。
- 支持基本类型与包装类型之间转换。
- 支持lambda表达式
## 使用方式
### maven
```xml
<dependency>
    <groupId>io.github.cqpsjsl</groupId>
    <artifactId>fastbean</artifactId>
    <version>1.3</version>
</dependency>

```
> UserDO

```java
@Data
public class UserDO {
    private Integer id;
    private String username;
    private String address;
    private List<Long> ids;
    private Double price;
    private BigDecimal marketPrice;
    private LocalDateTime time;
}
```
> UserDTO

```java
@Data
public class UserDTO {
    private Integer id;
    private String name;
    private String address;
    private List<Long> ids;
    private Double price;
    private BigDecimal marketPrice;
    private Long time;
}
```
 ### 常规使用
 ```java
UserDO source = new UserDO();
UserDTO target = new UserDTO();
FastBeanCopier copier = FastBeanUtils.create(source.getClass(), target.getClass());
copier.copy(source,target,null);
```
### List
```java
FastBeanCopier copier = FastBeanUtils.create(source.getClass(), target.getClass()); // 此步耗时,不建议放到循环
for (UserDO userDO : UserDOS) {
UserDTO target = new UserDTO();
copier.copy(source,target,null);
}
```
### 属性映射、属性忽略
> UserDO中username需要赋值到UserDTO中的name上。
```java
// 属性映射
HashMap<String, String> map = new HashMap<>();
map.put("name","username");
// 字段忽略 忽略UserDTO中id赋值
HashSet<String> set = new HashSet<>();
set.add("id");
FastBeanCopier copier = FastBeanUtils.create(source.getClass(), target.getClass(), map, set); 

```
### 自定义转换器
> UserDO中是LocalDateTime,UserDO中是Long,如果未定义属性转换器,将会set NULL。

```java
DefaultConverterChain converterChain = new DefaultConverterChain();
converterChain.add(new TypeConverter());
copier.copy(source,target,converterChain);

```
> TypeConverter

```java
public class TypeConverter
    implements Converter
{
    public Object convert(Object value, Class target) {
        if (value == null) return null; 
        if (value.getClass().equals(LocalDateTime.class) && target.equals(Long.class)) {
            // 返回你需要的处理
            return Long.valueOf(System.currentTimeMillis());
        }
        return null;
    }
}
```
### lambda支持
> 习惯了lambda,此操作较耗时
```java
// 属性映射
LambdaNameMappingWrapper<UserDTO, UserDO> mappingWrapper = new LambdaNameMappingWrapper<>();
mappingWrapper.add(UserDTO::getName, UserDO::getUsername);
// 字段忽略 忽略UserDTO中id赋值
LambdaIgnoreWrapper<UserDTO> ignoreWrapper = new LambdaIgnoreWrapper<>();
ignoreWrapper.add(UserDTO::getId);
FastBeanCopier copier = FastBeanUtils.create(source.getClass(), target.getClass(), mappingWrapper, ignoreWrapper);
```
# 性能比较
> 基于spring stopWatch 进行性能比较，mapstruct肯定比不过。
![输入图片说明](https://images.gitee.com/uploads/images/2021/1212/235044_f4dd9d77_7650717.png "屏幕截图.png")
