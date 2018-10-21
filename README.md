# simple-cache

simple-cache 是一个为不同种类的缓存提供统一 API 的类库。其目的是方便项目在本地缓存和远程缓存之间切换。

simple-cache 支持以下底层实现：

- 本地缓存：
  - Cache2k
  - caffeine
  - JCS
  - Guava
- 远程缓存：
  - memcached
  - redis

你的项目可以在开发过程中使用本地缓存，而在生产环境上使用远程缓存，无需修改代码，只需修改配置即可。

下面是一个例子：

```java
SimpleCache cache = new SimpleCache(new EhCacheConfiguration());

// 简单存取
cache.put("name", queryName());
System.out.println("name: " + cache.get("name"));

// 惰性存取
String name = cache.get("name", () -> queryName());
```

更多文档在 docs 目录下。
