# Kafka实战Kerberos

**环境**

```ini
版本：kafka_2.12-2.3.0
主机名：orchome
LSB Version: :core-4.1-amd64:core-4.1-noarch
Distributor ID: CentOS
Description: CentOS Linux release 7.5.1804 (Core)
Release: 7.5.1804
Codename: Core
Linux version 3.10.0-862.el7.x86_64 (builder@kbuilder.dev.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-28) (GCC) ) #1 SMP Fri Apr 20 16:44:24 UTC 2018
```

**kerberos生成principal**

```shell
#创建principal
sudo /usr/sbin/kadmin.local -q 'addprinc -randkey zookeeper/orchome@EXAMPLE.COM'
sudo /usr/sbin/kadmin.local -q 'addprinc -randkey kafka/orchome@EXAMPLE.COM'
sudo /usr/sbin/kadmin.local -q 'addprinc -randkey clients/orchome@EXAMPLE.COM'

sudo /usr/sbin/kadmin.local -q "ktadd -k /etc/security/keytabs/kafka_server.keytab kafka/orchome@EXAMPLE.COM"
sudo /usr/sbin/kadmin.local -q "ktadd -k /etc/security/keytabs/kafka_server.keytab zookeeper/orchome@EXAMPLE.COM"
sudo /usr/sbin/kadmin.local -q "ktadd -k /etc/security/keytabs/kafka_client.keytab clients/orchome@EXAMPLE.COM"
```

```shell
#检查
klist -t -e -k /etc/security/keytabs/kafka_zookeeper.keytab
klist -t -e -k /etc/security/keytabs/kafka_server.keytab
klist -t -e -k /etc/security/keytabs/kafka_client.keytab
```

**各个文件详情**

**more /etc/krb5.conf**

```ini
[logging]
 default = FILE:/var/log/krb5libs.log
 kdc = FILE:/var/log/krb5kdc.log
 admin_server = FILE:/var/log/kadmind.log

[libdefaults]
 default_realm = EXAMPLE.COM
 dns_lookup_realm = false
 dns_lookup_kdc = false
 ticket_lifetime = 24h
 renew_lifetime = 7d
 forwardable = true

[realms]
 EXAMPLE.COM = {
 kdc = orchome
 admin_server = orchome
 }

[domain_realm]
kafka = EXAMPLE.COM
zookeeper = EXAMPLE.COM
clients = EXAMPLE.COM
```

**kadmin.local**

```shell
Authenticating as principal root/admin@EXAMPLE.COM with password.
kadmin.local: listprincs 
K/M@EXAMPLE.COM
admin/admin@EXAMPLE.COM
clients/orchome@EXAMPLE.COM
kadmin/admin@EXAMPLE.COM
kadmin/changepw@EXAMPLE.COM
kadmin/orchome@EXAMPLE.COM
kafka/orchome@EXAMPLE.COM
krbtgt/EXAMPLE.COM@EXAMPLE.COM
krbtgt/orchome@EXAMPLE.COM
zookeeper/orchome@EXAMPLE.COM
```

**klist -t -e -k /var/kerberos/krb5kdc/kafka.keytab**

```shell
Keytab name: FILE:/var/kerberos/krb5kdc/kafka.keytab

KVNO Timestamp     Principal

-----------------------------------------------------------------------------
  3 07/24/16 00:58:30 kafka/orchome@EXAMPLE.COM (aes256-cts-hmac-sha1-96)
  3 07/24/16 00:58:30 kafka/orchome@EXAMPLE.COM (aes128-cts-hmac-sha1-96)
  3 07/24/16 00:58:30 kafka/orchome@EXAMPLE.COM (des3-cbc-sha1)
  3 07/24/16 00:58:30 kafka/orchome@EXAMPLE.COM (arcfour-hmac)
  3 07/24/16 00:58:30 kafka/orchome@EXAMPLE.COM (des-hmac-sha1)
  3 07/24/16 00:58:30 kafka/orchome@EXAMPLE.COM (des-cbc-md5)
  2 07/24/16 12:23:18 zookeeper/orchome@EXAMPLE.COM (aes256-cts-hmac-sha1-96)
  2 07/24/16 12:23:18 zookeeper/orchome@EXAMPLE.COM (aes128-cts-hmac-sha1-96)
  2 07/24/16 12:23:18 zookeeper/orchome@EXAMPLE.COM (des3-cbc-sha1)
  2 07/24/16 12:23:18 zookeeper/orchome@EXAMPLE.COM (arcfour-hmac)
  2 07/24/16 12:23:18 zookeeper/orchome@EXAMPLE.COM (des-hmac-sha1)
  2 07/24/16 12:23:18 zookeeper/orchome@EXAMPLE.COM (des-cbc-md5)
  2 07/25/16 11:31:37 kafka/127.0.0.1@EXAMPLE.COM (aes256-cts-hmac-sha1-96)
  2 07/25/16 11:31:37 kafka/127.0.0.1@EXAMPLE.COM (aes128-cts-hmac-sha1-96)
  2 07/25/16 11:31:37 kafka/127.0.0.1@EXAMPLE.COM (des3-cbc-sha1)
  2 07/25/16 11:31:37 kafka/127.0.0.1@EXAMPLE.COM (arcfour-hmac)
  2 07/25/16 11:31:37 kafka/127.0.0.1@EXAMPLE.COM (des-hmac-sha1)
  2 07/25/16 11:31:37 kafka/127.0.0.1@EXAMPLE.COM (des-cbc-md5)
  3 07/25/16 13:13:31 kafka/orchome@EXAMPLE.COM (aes256-cts-hmac-sha1-96)
  3 07/25/16 13:13:31 kafka/orchome@EXAMPLE.COM (aes128-cts-hmac-sha1-96)
  3 07/25/16 13:13:31 kafka/orchome@EXAMPLE.COM (des3-cbc-sha1)
  3 07/25/16 13:13:31 kafka/orchome@EXAMPLE.COM (arcfour-hmac)
  3 07/25/16 13:13:31 kafka/orchome@EXAMPLE.COM (des-hmac-sha1)
  3 07/25/16 13:13:31 kafka/orchome@EXAMPLE.COM (des-cbc-md5)
  2 07/25/16 15:07:58 zookeeper/127.0.0.1@EXAMPLE.COM (aes256-cts-hmac-sha1-96)
  2 07/25/16 15:07:58 zookeeper/127.0.0.1@EXAMPLE.COM (aes128-cts-hmac-sha1-96)
  2 07/25/16 15:07:58 zookeeper/127.0.0.1@EXAMPLE.COM (des3-cbc-sha1)
  2 07/25/16 15:07:58 zookeeper/127.0.0.1@EXAMPLE.COM (arcfour-hmac)
  2 07/25/16 15:07:58 zookeeper/127.0.0.1@EXAMPLE.COM (des-hmac-sha1)
  2 07/25/16 15:07:58 zookeeper/127.0.0.1@EXAMPLE.COM (des-cbc-md5)
  2 07/25/16 18:47:55 clients@EXAMPLE.COM (aes256-cts-hmac-sha1-96)
  2 07/25/16 18:47:55 clients@EXAMPLE.COM (aes128-cts-hmac-sha1-96)
  2 07/25/16 18:47:55 clients@EXAMPLE.COM (des3-cbc-sha1)
  2 07/25/16 18:47:55 clients@EXAMPLE.COM (arcfour-hmac)
  2 07/25/16 18:47:55 clients@EXAMPLE.COM (des-hmac-sha1)
  2 07/25/16 18:47:55 clients@EXAMPLE.COM (des-cbc-md5)
```

**more /etc/kafka/zookeeper_jaas.conf**

```json
Server{
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  useTicketCache=false
  keyTab="/etc/security/keytabs/kafka_zookeeper.keytab"
  principal="zookeeper/orchome@EXAMPLE.COM";
};
```

**more /etc/kafka/kafka_server_jaas.conf**

```json
KafkaServer {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  keyTab="/etc/security/keytabs/kafka_server.keytab"
  principal="kafka/orchome@EXAMPLE.COM";
};
// Zookeeper client authentication
Client {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  keyTab="/etc/security/keytabs/kafka_server.keytab"
  principal="kafka/orchome@EXAMPLE.COM";
};
```



**more /etc/kafka/kafka_client_jaas.conf**

```javascript
KafkaClient {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  keyTab="/etc/security/keytabs/kafka_client.keytab"
  principal="clients/orchome@EXAMPLE.COM";
};
```



**more config/server.properties**

```properties
listeners=SASL_PLAINTEXT://orchome:9093
security.inter.broker.protocol=SASL_PLAINTEXT
sasl.mechanism.inter.broker.protocol=GSSAPI
sasl.enabled.mechanisms=GSSAPI
sasl.kerberos.service.name=kafka
```



**more start-zk-and-kafka.sh**

```shell
#!/bin/bash
export KAFKA_HEAP_OPTS='-Xmx256M'
export KAFKA_OPTS='-Djava.security.krb5.conf=/etc/krb5.conf -Djava.security.auth.login.config=/etc/kafka/zookeeper_jaas.conf'
bin/zookeeper-server-start.sh config/zookeeper.properties &
sleep 5
export KAFKA_OPTS='-Djava.security.krb5.conf=/etc/krb5.conf -Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf'
bin/kafka-server-start.sh config/server.properties
```



**more config/zookeeper.properties**

```properties
authProvider.1=org.apache.zookeeper.server.auth.SASLAuthenticationProvider
requireClientAuthScheme=sasl
jaasLoginRenew=3600000
```

**more config/producer.properties/consumer.properties**

```properties
security.protocol=SASL_PLAINTEXT
sasl.mechanism=GSSAPI
sasl.kerberos.service.name=kafka
```

**more producer2.sh**

```shell
export KAFKA_OPTS="-Djava.security.krb5.conf=/etc/krb5.conf -Djava.security.auth.login.config=/etc/kafka/kafka_client_jaas.conf"
bin/kafka-console-producer.sh --broker-list orchome:9093 --topic test --producer.config config/producer.properties
```

**more consumer2.sh**

```shell
export KAFKA_OPTS="-Djava.security.krb5.conf=/etc/krb5.conf -Djava.security.auth.login.config=/etc/kafka/kafka_client_jaas.conf"
bin/kafka-console-consumer.sh --bootstrap-server orchome:9093 --topic test --new-consumer --from-beginning --consumer.config config/consumer.properties
```

 

# kafka实战SSL

**生成ca和信任库**

```shell
#!/bin/bash
#Step 1
keytool -keystore server.keystore.jks -alias localhost -validity 365 -genkey
#Step 2
openssl req -new -x509 -keyout ca-key -out ca-cert -days 365
keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert
keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
#Step 3
keytool -keystore server.keystore.jks -alias localhost -certreq -file cert-file
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial -passin pass:test1234
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert
keytool -keystore server.keystore.jks -alias localhost -import -file cert-signed
```

**more /etc/kafka/kafka_server_jaas.conf**

```json
KafkaServer { 
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="admin-secret"
  user_admin="admin-secret"
  user_alice="alice-secret";
};
```

**more config/server.properties**

```properties
listeners=SSL://localhost:9093
ssl.keystore.location=/var/private/ssl/server.keystore.jks
ssl.keystore.password=test1234
ssl.key.password=test1234
ssl.truststore.location=/var/private/ssl/server.truststore.jks
ssl.truststore.password=test1234
security.inter.broker.protocol=SSL
```

**启动kafka**

```shell
export KAFKA_OPTS='-Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf'
bin/kafka-server-start.sh config/server.properties
```

**more client-ssl.properties**

```properties
security.protocol=SSL
ssl.truststore.location=/var/private/ssl/client.truststore.jks
ssl.truststore.password=test1234
```

**消费者和生产者**

```shell
bin/kafka-console-producer.sh --broker-list localhost:9093 --topic test --producer.config client-ssl.properties 
bin/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic test --consumer.config client-ssl.properties 
```

 

# kafka实战SASL/PLAIN认证

**more config/server.properties**

```properties
ssl.keystore.location=/var/private/ssl/server.keystore.jks
ssl.keystore.password=test1234
ssl.key.password=test1234
ssl.truststore.location=/var/private/ssl/server.truststore.jks
ssl.truststore.password=test1234
 
listeners=SASL_SSL://localhost:9093
security.inter.broker.protocol=SASL_SSL
sasl.mechanism.inter.broker.protocol=PLAIN
sasl.enabled.mechanisms=PLAIN
```

**more /etc/kafka/kafka_server_jaas.conf**

```json
KafkaServer { 
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="admin-secret"
  user_admin="admin-secret"
  user_alice="alice-secret";
};
```

**more /etc/kafka/kafka_client_jaas.conf**

```json
KafkaClient {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="alice"
  password="alice-secret";
};
```

**consumer.properties** **和 producer.properties**

```properties
security.protocol=SASL_SSL
sasl.mechanism=PLAIN
ssl.truststore.location=/var/private/ssl/client.truststore.jks
ssl.truststore.password=test1234
```

**启动kafka**

```shell
export KAFKA_OPTS='-Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf'
bin/kafka-server-start.sh config/server.properties
```

**kafka消费者和生产者**

```shell
export KAFKA_OPTS="-Djava.security.auth.login.config=/etc/kafka/kafka_client_jaas.conf"
bin/kafka-console-producer.sh --broker-list localhost:9093 --topic test --producer.config config/producer.properties 
export KAFKA_OPTS="-Djava.security.auth.login.config=/etc/kafka/kafka_client_jaas.conf"
bin/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic test --consumer.config config/consumer.properties
```

 

# kafka实战SASL/SCRAM

**创建证书**

```shell
bin/kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-256=[iterations=8192,password=alice-secret],SCRAM-SHA-512=[password=alice-secret]' --entity-type users --entity-name alice

bin/kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-256=[password=admin-secret],SCRAM-SHA-512=[password=admin-secret]' --entity-type users --entity-name admin
```

**验证证书**

```shell
bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type users --entity-name alice
bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type users --entity-name admin
```

**more /etc/kafka/kafka_server_jaas.conf**

```json
KafkaServer {
  org.apache.kafka.common.security.scram.ScramLoginModule required
  username="admin"
  password="admin-secret"
  user_admin="admin";
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="admin-secret"
  user_admin="admin-secret"
  user_alice="alice-secret";
};
```

**more /etc/kafka/kafka_client_jaas.conf**

```json
KafkaClient {
  org.apache.kafka.common.security.scram.ScramLoginModule required
  username="alice"
  password="alice-secret";
};
```

**consumer.properties** **和 producer.properties**

```properties
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-256

ssl.truststore.location=/var/private/ssl/client.truststore.jks
ssl.truststore.password=test1234
```

**启动zk**

```shell
export KAFKA_OPTS=''
bin/zookeeper-server-start.sh config/zookeeper.properties
```

**启动kafka**

```shell
export KAFKA_OPTS='-Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf'
bin/kafka-server-start.sh config/server.properties
```

**启动生产者和消费者**

```shell
export KAFKA_OPTS="-Djava.security.auth.login.config=/etc/kafka/kafka_client_jaas.conf"
bin/kafka-console-producer.sh --broker-list localhost:9093 --topic test --producer.config config/producer.properties 
export KAFKA_OPTS="-Djava.security.auth.login.config=/etc/kafka/kafka_client_jaas.conf"
bin/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic test --consumer.config config/consumer.properties
```

 
