# simple-cache

simple-cache 是一个为不同种类的缓存提供统一 API 的类库。其目的是方便项目在本地缓存和远程缓存之间切换。

simple-cache 支持以下底层实现：

- 本地缓存：
  - EhCache
  - Cache2k
  - caffeine
  - JCS （开发中）
  - Guava （开发中）
- 远程缓存：
  - memcached
  - redis

你的项目可以在开发过程中使用本地缓存，而在生产环境上使用远程缓存，无需修改代码，只需修改配置即可。

下面是一个使用例子：

```java
// 创建一个 SimpleCache 对象
SimpleCache cache = new SimpleCache(new EhCacheConfiguration());

// 简单存取
cache.put("name", queryName());
System.out.println("name: " + cache.get("name"));

// 取缓存时指定查询方法，当缓存没有时调用该方法填充缓存并返回
User user = cache.get("user", () -> queryUser());

// 异步获取缓存
// 这个例子中当 60 秒超时后，再次查询缓存会触发后台异步获取，
// 获取期间仍然返回当前的缓存内容，获取成功后再次查询会返回新值。
int pageDataExpirySeconds = 60;
PageData pageData = cache.getAsync(
        "page-data", pageDataExpirySeconds, () -> queryPageData());
```

## 创建 `SimpleCache` 对象

通过下面几种方式之一来创建：

```java
new SimpleCache(new EhCacheConfiguration());
new SimpleCache(new MemcachedConfiguration());
new SimpleCache(new RedisConfiguration());
// 更多缓存支持添加中...
```

## Spring Boot 中自动初始化

在 Spring Boot 中只需要添加配置即可直接使用 SimpleCache 对象。下面是一个例子：

```properties
# application.properties
simple-cache.memcached.cache1.host=localhost
simple-cache.memcached.cache1.port=11211
```

就可以直接在代码里面引用了：

```java
@Autowired
private SimpleCache simpleCache;
```

更多文档在 docs 目录下。
