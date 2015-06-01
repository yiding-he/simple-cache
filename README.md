#simple-cache

simple-cache 是一个为不同种类的缓存提供统一 API 的类库。其目的是方便项目在本地缓存和远程缓存之间切换。

你的项目可以在开发过程中使用本地缓存，而在生产环境上使用远程缓存，无需修改代码，只需修改配置即可。下面是一个例子：

```java
SimpleCache cache = new SimpleCache(new EhCacheConfiguration());
cache.put("name", "simple-cache");
System.out.println("name: " + cache.get("name"));
```