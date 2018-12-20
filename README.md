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
new SimpleCache(new Cache2kConfiguration());
new SimpleCache(new CaffeineConfiguration());
new SimpleCache(new EhCacheConfiguration());
new SimpleCache(new MemcachedConfiguration());
new SimpleCache(new RedisConfiguration());
// 更多缓存支持添加中...
```

## Spring Boot 中自动初始化

在 Spring Boot 中可以添加多个缓存配置，然后用 `Caches` 类获取各自的 SimpleCache 对象。下面是一个例子：

```properties
# memcached instance
simple-cache.memcached.REMOTE.host=localhost
simple-cache.memcached.REMOTE.port=11211
simple-cache.memcached.REMOTE.time-to-live-seconds=3600

# cache2k instances
simple-cache.cache2k.LOCAL1.entry-capacity=10000
simple-cache.cache2k.LOCAL2.entry-capacity=10000
```

无需额外编码，即可在代码里面使用缓存对象：

```java
import com.hyd.simplecache.springboot.Caches;
import com.hyd.simplecache.SimpleCache;

public class CacheDemo {
    
    @Autowired
    private Caches caches;
    
    public void func() {
        SimpleCache remoteCache = caches.get("REMOTE");
        SimpleCache localCache1 = caches.get("LOCAL1");
        SimpleCache localCache2 = caches.get("LOCAL2");
    }
}
```

当然你也可以在 `@Configuration` 类中定义单独的 SimpleCache：

```java
import com.hyd.simplecache.springboot.Caches;
import com.hyd.simplecache.SimpleCache;

@Configuration
public class Conf {
    
    @Autowired
    private Caches caches;
    
    @Bean
    public SimpleCache remoteCache() {
        return caches.get("REMOTE");
    }
    
    @Bean
    public SimpleCache localCache1() {
        return caches.get("LOCAL1");
    }
    
    @Bean
    public SimpleCache localCache2() {
        return caches.get("LOCAL2");
    }
}
```

