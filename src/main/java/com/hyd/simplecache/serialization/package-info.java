/**
 * 本机缓存通常不需要序列化，但远程缓存（例如 memcached/redis）需要将对象序列化和反序列化。
 * 为了避免某些缓存框架（如 memcached）要求缓存的对象一定要实现 Serializable 接口，simple-cache
 * 统一将对象转为 byte[] 对象，然后保存到缓存当中。
 *
 * simple-cache 支持可选的序列化方法，每个缓存都可以配置各自不同的序列化方法。
 *
 * 对象被序列化为字节串之后，会在前面加上一个额外字节，以标识该对象是用哪种方式序列化的。在读取的时候可以
 * 通过该字节来决定如何反序列化。所以理论上 simple-cache 支持 256 种不同的序列化/反序列化方式。
 *
 *
 */
package com.hyd.simplecache.serialization;