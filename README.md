# spring-rdb-redis-testcontainers

## WIP:

## setup

- run redis container

```shell
docker compose up -d
```

## run local

- access [http://localhost:8081/swagger-ui/](http://localhost:8081/swagger-ui/)
    - you can test api using Swagger UI

---
## redis playground

```shell
docker compose up -d
```

```shell
$ docker compose exec redis bash -c redis-cli
127.0.0.1:6379> 
```

- string

```
> set name taro
> get name
"taro"

> keys *
1) "name"

> flushall
> keys *
(empty array)
```

- list

```
> lpush country India
> lpush country Japan
> lrange country 0 -1
1) "Japan"
2) "India"

> lpush country USA
> lrange country 0 -1
1) "USA"
2) "Japan"
3) "India"

> lrange country 0 1
1) "USA"
2) "Japan"

> llen country
(integer) 3

> lpop country
"USA"

> lrange country 0 -1
1) "Japan"
2) "India"

> sort country desc ALPHA
1) "Japan"
2) "India"
```

- set

```
> sadd technology Java
> sadd technology Redis k8s AWS GCP
> smembers technology
1) "Redis"
2) "AWS"
3) "k8s"
4) "Java"
5) "GCP"

# set はユニークな要素のみ保持
> sadd technology Java
(integer) 0

> smembers technology
1) "AWS"
2) "k8s"
3) "Java"
4) "Redis"
5) "GCP"

> sadd frontend JavaScript TypeScript AWS
> sdiff technology frontend
1) "Redis"
2) "k8s"
3) "GCP"
4) "Java"

> sdiffstore not-frontend technology frontend
> smembers not-frontend
1) "Redis"
2) "k8s"
3) "GCP"
4) "Java"

> sinter technology frontend
1) "AWS"

> sinter frontend not-frontend
(empty array)

> sunionstore newunion technology frontend
(integer) 7

> smembers newunion
1) "Redis"
2) "JavaScript"
3) "k8s"
4) "GCP"
5) "AWS"
6) "TypeScript"
7) "Java"
```

- sorted sets

```
> zadd users 1 taro
> zadd users 2 jiro 3 ichiro 4 yonro
> zrange users 0 -1
1) "taro"
2) "jiro"
3) "ichiro"
4) "yonro"

> zrange users 0 -1 withscores
1) "taro"
2) "1"
3) "jiro"
4) "2"
5) "ichiro"
6) "3"
7) "yonro"
8) "4"

> zscore users taro
"1"
```

- hashes

```
> hset myhash name taro
> hset myhash email taro@example.com

> hkeys myhash
1) "name"
2) "email"
> hvals myhash
1) "taro"
2) "taro@example.com"

> hgetall myhash
1) "name"
2) "taro"
3) "email"
4) "taro@example.com"

> hexists myhash name
(integer) 1

> hmset myhash country Japan Phone 0120-444-444
OK
> hkeys myhash
1) "name"
2) "email"
3) "country"
4) "Phone"
```

- transactions

```
> multi
OK

(TX)> set name ichiro
QUEUED
(TX)> get name
QUEUED
(TX)> set a 1
QUEUED
(TX)> set b 2
QUEUED

(TX)> exec
1) OK
2) "ichiro"
3) OK
4) OK
```

- pub sub

```
> subscribe news
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "news"
3) (integer) 1
1) "message"
2) "news"
3) "new news"
1) "message"
2) "news"
3) "new news2"

> publish news "new news"
> publish news "new news2"

# 複数 subscribe することも可能
> subscribe news broadcast
1) "message"
2) "broadcast"
3) "broadcast 1"
1) "message"
2) "news"
3) "new news3"

> publish broadcast "broadcast 1"
(integer) 1
> publish news "new news3"
(integer) 2
```
