# MobMQ

<img src="document/logo.jpg" weight="300px" height="300px"/>

<b>Mob MQ</b> Distributed messaging and streaming platform with low-latency, high performance and reliability,
 trillion-level capacity and flexible scalability.

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

Go version 1.5+
```
go version
```

### Quick Start

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


### Document

#### How to Use

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
