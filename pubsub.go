package main

import (
	"fmt"
	"strings"
	"sync"
	"time"
)

type (
	subscribe chan interface{}
	topicFunc func(v interface{}) bool
)

type Publisher struct {
	m          sync.RWMutex
	buffer     int
	subscribes map[subscribe]topicFunc
	timeout    time.Duration
}

func NewPublisher(buffer int, timeout time.Duration) *Publisher {
	return &Publisher{
		buffer:     buffer,
		subscribes: make(map[subscribe]topicFunc),
		timeout:    timeout,
	}
}

func (p *Publisher) Subscribe() chan interface{} {
	return p.SubscribeFunc(nil)
}

func (p *Publisher) SubscribeFunc(topic topicFunc) chan interface{} {
	ch := make(chan interface{}, p.buffer)
	p.m.Lock()
	defer p.m.Unlock()
	p.subscribes[ch] = topic
	return ch
}

func (p *Publisher) Publish(v interface{}) {
	p.m.Lock()
	defer p.m.Unlock()

	var wg sync.WaitGroup
	for sub, topic := range p.subscribes {
		wg.Add(1)
		go p.sendMessage(sub, topic, v, &wg)
	}
	wg.Wait()
}

func (p *Publisher) sendMessage(sub subscribe, topic topicFunc, v interface{}, wg *sync.WaitGroup) {
	defer wg.Done()
	if topic != nil && !topic(v) {
		return
	}
	select {
	case sub <- v:
	case <-time.After(p.timeout):

	}
}

func (p *Publisher) Close() {
	p.m.Lock()
	defer p.m.Unlock()

	for sub := range p.subscribes {
		delete(p.subscribes, sub)
		close(sub)
	}
}

func main() {
	p := NewPublisher(10, 100*time.Millisecond)
	all := p.Subscribe()
	goland := p.SubscribeFunc(func(v interface{}) bool {
		if s, ok := v.(string); ok {
			return strings.Contains(s, "goland")
		}
		return false
	})
	p.Publish("hello world")
	p.Publish("hello goland")

	go func() {
		for msg := range all {
			fmt.Println("all", msg)
		}
	}()

	go func() {
		for msg := range goland {
			fmt.Println("goland", msg)
		}
	}()

	time.Sleep(3 * time.Second)
}
