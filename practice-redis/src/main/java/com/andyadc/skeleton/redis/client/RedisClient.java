package com.andyadc.skeleton.redis.client;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Redis client interface providing a unified API for both Jedis and Lettuce.
 */
public interface RedisClient extends AutoCloseable {

    // ==================== String Operations ====================

    /**
     * Sets a key-value pair.
     */
    void set(String key, String value);

    /**
     * Sets a key-value pair with expiration.
     */
    void set(String key, String value, Duration ttl);

    /**
     * Sets a key only if it doesn't exist.
     */
    boolean setNx(String key, String value);

    /**
     * Sets a key only if it doesn't exist, with expiration.
     */
    boolean setNx(String key, String value, Duration ttl);

    /**
     * Gets a value by key.
     */
    Optional<String> get(String key);

    /**
     * Gets multiple values by keys.
     */
    List<String> mget(String... keys);

    /**
     * Sets multiple key-value pairs.
     */
    void mset(Map<String, String> keyValues);

    /**
     * Increments a key's integer value.
     */
    long incr(String key);

    /**
     * Increments a key's integer value by the given amount.
     */
    long incrBy(String key, long amount);

    /**
     * Decrements a key's integer value.
     */
    long decr(String key);

    /**
     * Decrements a key's integer value by the given amount.
     */
    long decrBy(String key, long amount);

    /**
     * Appends value to existing value.
     */
    long append(String key, String value);

    // ==================== Key Operations ====================

    /**
     * Deletes a key.
     */
    boolean del(String key);

    /**
     * Deletes multiple keys.
     */
    long del(String... keys);

    /**
     * Checks if a key exists.
     */
    boolean exists(String key);

    /**
     * Sets expiration on a key.
     */
    boolean expire(String key, Duration ttl);

    /**
     * Gets the TTL of a key.
     */
    Duration ttl(String key);

    /**
     * Gets keys matching pattern.
     */
    Set<String> keys(String pattern);

    /**
     * Scans keys matching pattern (preferred over keys() for production).
     */
    Set<String> scan(String pattern, int count);

    /**
     * Renames a key.
     */
    void rename(String oldKey, String newKey);

    /**
     * Gets the type of a key.
     */
    String type(String key);

    // ==================== Hash Operations ====================

    /**
     * Sets a hash field.
     */
    void hset(String key, String field, String value);

    /**
     * Sets multiple hash fields.
     */
    void hset(String key, Map<String, String> fieldValues);

    /**
     * Gets a hash field.
     */
    Optional<String> hget(String key, String field);

    /**
     * Gets all hash fields and values.
     */
    Map<String, String> hgetAll(String key);

    /**
     * Gets multiple hash fields.
     */
    List<String> hmget(String key, String... fields);

    /**
     * Deletes hash fields.
     */
    long hdel(String key, String... fields);

    /**
     * Checks if hash field exists.
     */
    boolean hexists(String key, String field);

    /**
     * Gets all hash field names.
     */
    Set<String> hkeys(String key);

    /**
     * Gets hash size.
     */
    long hlen(String key);

    /**
     * Increments hash field integer value.
     */
    long hincrBy(String key, String field, long amount);

    // ==================== List Operations ====================

    /**
     * Pushes values to the left of a list.
     */
    long lpush(String key, String... values);

    /**
     * Pushes values to the right of a list.
     */
    long rpush(String key, String... values);

    /**
     * Pops from the left of a list.
     */
    Optional<String> lpop(String key);

    /**
     * Pops from the right of a list.
     */
    Optional<String> rpop(String key);

    /**
     * Gets a range of list elements.
     */
    List<String> lrange(String key, long start, long stop);

    /**
     * Gets list length.
     */
    long llen(String key);

    /**
     * Gets element at index.
     */
    Optional<String> lindex(String key, long index);

    /**
     * Sets element at index.
     */
    void lset(String key, long index, String value);

    /**
     * Blocking pop from left (waits for element).
     */
    Optional<String> blpop(String key, Duration timeout);

    /**
     * Blocking pop from right (waits for element).
     */
    Optional<String> brpop(String key, Duration timeout);

    // ==================== Set Operations ====================

    /**
     * Adds members to a set.
     */
    long sadd(String key, String... members);

    /**
     * Removes members from a set.
     */
    long srem(String key, String... members);

    /**
     * Gets all set members.
     */
    Set<String> smembers(String key);

    /**
     * Checks if member exists in set.
     */
    boolean sismember(String key, String member);

    /**
     * Gets set size.
     */
    long scard(String key);

    /**
     * Gets random members from set.
     */
    Set<String> srandmember(String key, int count);

    /**
     * Pops random member from set.
     */
    Optional<String> spop(String key);

    /**
     * Gets set intersection.
     */
    Set<String> sinter(String... keys);

    /**
     * Gets set union.
     */
    Set<String> sunion(String... keys);

    /**
     * Gets set difference.
     */
    Set<String> sdiff(String... keys);

    // ==================== Sorted Set Operations ====================

    /**
     * Adds member with score to sorted set.
     */
    long zadd(String key, double score, String member);

    /**
     * Adds multiple members with scores.
     */
    long zadd(String key, Map<String, Double> scoreMembers);

    /**
     * Removes members from sorted set.
     */
    long zrem(String key, String... members);

    /**
     * Gets member score.
     */
    Optional<Double> zscore(String key, String member);

    /**
     * Gets member rank (0-based, ascending).
     */
    Optional<Long> zrank(String key, String member);

    /**
     * Gets member rank (0-based, descending).
     */
    Optional<Long> zrevrank(String key, String member);

    /**
     * Gets range by rank (ascending).
     */
    List<String> zrange(String key, long start, long stop);

    /**
     * Gets range by rank (descending).
     */
    List<String> zrevrange(String key, long start, long stop);

    /**
     * Gets range by score.
     */
    List<String> zrangeByScore(String key, double min, double max);

    /**
     * Gets sorted set size.
     */
    long zcard(String key);

    /**
     * Counts members in score range.
     */
    long zcount(String key, double min, double max);

    /**
     * Increments member score.
     */
    double zincrBy(String key, double increment, String member);

    // ==================== Pub/Sub Operations ====================

    /**
     * Publishes message to channel.
     */
    long publish(String channel, String message);

    /**
     * Subscribes to channels.
     */
    void subscribe(Consumer<Message> messageHandler, String... channels);

    /**
     * Subscribes to pattern.
     */
    void psubscribe(Consumer<Message> messageHandler, String... patterns);

    /**
     * Unsubscribes from channels.
     */
    void unsubscribe(String... channels);

    // ==================== Transaction/Pipeline Operations ====================

    /**
     * Executes commands in pipeline.
     */
    List<Object> pipeline(Consumer<RedisClient> commands);

    /**
     * Executes commands in transaction.
     */
    List<Object> transaction(Consumer<RedisClient> commands);

    // ==================== Script Operations ====================

    /**
     * Evaluates Lua script.
     */
    Object eval(String script, List<String> keys, List<String> args);

    /**
     * Evaluates Lua script by SHA.
     */
    Object evalsha(String sha, List<String> keys, List<String> args);

    /**
     * Loads script and returns SHA.
     */
    String scriptLoad(String script);

    // ==================== Async Operations ====================

    /**
     * Async get operation.
     */
    CompletableFuture<Optional<String>> getAsync(String key);

    /**
     * Async set operation.
     */
    CompletableFuture<Void> setAsync(String key, String value);

    /**
     * Async set with TTL operation.
     */
    CompletableFuture<Void> setAsync(String key, String value, Duration ttl);

    // ==================== Server Operations ====================

    /**
     * Pings the server.
     */
    boolean ping();

    /**
     * Gets server info.
     */
    String info();

    /**
     * Gets specific server info section.
     */
    String info(String section);

    /**
     * Flushes current database.
     */
    void flushDb();

    /**
     * Gets database size.
     */
    long dbSize();

    /**
     * Gets client type name.
     */
    String getClientType();

    // ==================== Connection Management ====================

    /**
     * Checks if connected.
     */
    boolean isConnected();

    /**
     * Closes the client.
     */
    @Override
    void close();

    // ==================== Message Record ====================

    /**
     * Represents a pub/sub message.
     */
    record Message(String channel, String pattern, String content) {
        public Message(String channel, String content) {
            this(channel, null, content);
        }
    }

}
