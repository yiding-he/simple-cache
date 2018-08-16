# simple-cache

simple-cache 是一个为不同种类的缓存提供统一接口的类库。其目的是方便项目在本地缓存和远程缓存之间切换。

之所以开发这个项目，是因为希望能实现在开发时使用本地缓存，而在生产环境上使用远程缓存，无需修改代码，只需修改配置即可。

下面是一个使用例子：

```java
SimpleCache cache = new SimpleCache(new EhCacheConfiguration());

// 简单存取
cache.put("name", queryName());
System.out.println("name: " + cache.get("name"));

// 惰性存取
String name = cache.get("name", () -> queryName());
```

在 Spring 中，只需要配置为下面的样子：

```xml
<bean id="simpleCache" class="com.hyd.simplecache.SimpleCache" destroy-method="close">
    <constructor-arg ref="memcachedConf"/>   <!-- 修改这里 -->
</bean>

<bean id="ehcacheConf" class="com.hyd.simplecache.EhCacheConfiguration"/>

<bean id="memcachedConf" class="com.hyd.simplecache.MemcachedConfiguration">
    <property name="addresses" value="192.168.1.10:12345"/>
</bean>
```

通过修改 simpleCache 的构造方法参数，可在多种缓存框架之间切换，无需修改 Java 代码。

当然使用这个框架的缺陷就是无法完整使用这些缓存特有的高级功能。
