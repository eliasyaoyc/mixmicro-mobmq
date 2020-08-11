# Mixmicro-MobMQ

<img src="document/mixmicro-chaos-logo.png" weight="150px" height="150px"/>

<b>Mixmicro Mob MQ Component</b> based membership and failure detection .

### Overview

> Design Architecture

![HA Design Architecture](document/ha-design.jpg)

![HA Design Architecture](document/Mixmicro-HA.jpg)

### Features
* Group & Topic message model
* Pub/Sub messaging model
* Message retroactivity by time or offset
* Flexible distributed scale-out deployment architecture
* Lightning-fast batch message exchange system
* Efficient pull&push consumption model
* Docker images for isolated testing adn cloud isolated clusters
* Scheduled message delivery
* Log collection for streaming
* Big data integration
* Reliable FIFO and strict ordered messaging in the same queue
* Million-level message accumulation capacity in a single queue
* Feature-rich administrative dashboard for configuration,metrics and monitoring

### Requirements

YunLSP+ Maven Repository Configuration

> [Configuration Reference](https://github.com/misselvexu/Acmedcare-Maven-Nexus/blob/master/README.md)

### Quick Start

You can download binaries from [Release Repo](http://git.hgj.net/elve.xu/Mixmicro-Components) or [repo.hgj.net](http://nexus.hgj.net/).

*First* : unzip release package

```bash
$ tar -zxvf *.tar.gz
```

*Second* : startup & shutdown

```bash
$ sh ./bin/startup.sh -p production  
```

*Third* : check the application log

```bash
$ tail -f logs/start.log
```

> param: -p (optional): `production` | `dev`


### Building from Source

You don’t need to build from source to use `Mix Micro Chaos Components` (binaries in [repo.hgj.net](http://nexus.hgj.net)), 
but if you want to try out the latest and greatest, 
`Mix Micro Chaos Components` can be easily built with the maven wrapper. You also need JDK 1.8.

*First* : git clone source from gitlab
 
```bash
$ git clone http://git.hgj.net/elve.xu/Mixmicro-Chaos.git
```

*Second* : build

```bash
$ mvn clean install
```

If you want to build with the regular `mvn` command, you will need [Maven v3.5.0 or above](https://maven.apache.org/run-maven/index.html).


### Document

#### How to Use

```xml
<dependencyManagement>
   <dependencies>
        <dependency>
            <groupId>com.yunlsp.framework.components</groupId>
            <artifactId>mixmicro-chaos-dependencies</artifactId>
            <version>1.0.2.BUILD-SNAPSHOT</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>               
    </dependencies>
</dependencyManagement>

```


### License
 
```
Copyright (c) 2020 YunLSP+

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```