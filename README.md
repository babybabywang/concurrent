>并发:同时拥有两个或者多个线程，如果程序在单核处理器上运行，多个线程将交替地换入或者换出内存这些线程同时“存在”的，
每个线程都处于执行过程中的某个状态，如果运行在多核处理器上，此时，程序中的每个线程都将分配到一个处理器核上，因此可以同时运行。


>高并发:高并发(High Concurrency)是互联网分布式系统架构设计中必须考虑的因素之一，它通常是指，通过设计保证系统能够同时并行处理很多请求。


```并发:多个线程操作相同的资源，保证线程安全，合理使用资源```
```高并发:服务能同时处理很多请求，提高程序性能```

# 总结:
1. CPU多级缓存:缓存一致性、乱序执行优化
2. Java内存模型：JMM规定、抽象结构、同步八种操作及规则


# 并发模拟测试
1. postman 通过Collections方式
2. ab压测 
3. apache-jmeter
4. 代码并发模拟 参考test包下ConcurrencyTest类

# 线程安全性
**定义**
```
当多个线程访问某个类时，不管运行时环境采用何种调度发那个是或者这些进程将如何交替执行，
并且主调代码中不需要任何额外的同步或者协同，这个类都能表现出正确的行为吗，那么就称这个类是线程安全的。
```

>原子性:提供了互斥访问，同一时刻只能一个线程来对它进行操作
>可见性:一个线程对内存的修改可以及时的被其他线程观察到
>有序性:一个线程观察其他线程中的指令执行顺序，由于指令重排序的存在，该观察结果一般乱序无章

**LongAdder类和AtomicLong类的区别**
(LongAdder详解)[https://juejin.im/entry/5a5b7e8a51882573443ca7ee]

**CAS的ABA问题:**
>通过CAS修改A的时候，被其他线程修改成B后然后有修改成A，这时候在修改就产生了ABA问题，通过AtomicStampReference每次修改添加会将版本号加1 Stamp+1

**原子性-对比**
>synchronized:不可中断锁，适合竞争不激烈，可读性好
>Lock:可中断锁，多样化同步，竞争激烈时能维持常态
>Atomic:竞争激烈时能维持常态，比Lock性能好，缺点每次只能同步一个值

**可见性**
>导致共享变量在线程间不可见的原因
1. 线程交叉执行
2. 重排序结合线程交叉执行
3. 共享变量更新后的值没有在工作内存和主存之间及时更新

**volatile**
`通过加入内存屏障和禁止指令重排序优化来实现`
1. 对volatile变量写操作时，会在写操作后加入一条store屏障指令，将本地内存中的共享变量值刷新到主内存
2. 对volatile变量操作读操作时，会在读操作前加入一条load屏障质量，从主内存中读取共享变量

**有序性**
1. Java内存模型中，运行编译器和处理器对指令进行重排序，但是重排序过程不会影响到单线程程序的执行，却会影响到多线程并发执行的正确性
2. volatile、synchronized、Lock

**happens-before原则**
1. 程序次序规则：一个线程内，按照代码顺序，书写在前面的操作先行发生于书写在后面的操作
2. 锁定规则：一个unLock操作先行发生于后面对同一个锁的lock操作
3. volatile变量规则:对一个变量的写操作先行发生于后面对这个变量的读操作
4. 传递规则：如果操作A先行发生于操作B，而操作B又先行发生于操作C，那么操作A先行发生于操作C
5. 线程启动规则：Thread对象的start()方法先行发生于此线程的每一个动作
6. 线程中断规则：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生
7. 线程终结规则：线程中所有的操作都先行发生于线程的终止检测，我们可以通过Thread.join()方法结束、Thread.isAlive()方法的返回值手段检测到线程已经终止执行
8. 对象终止规则：一个对象的初始化完成先行发生于他的finalize()方法的开始

# 安全发布对象
* 发布对象:使一个对象能够被当前范围之外的代码所使用的
* 对象逸出:一种错误的发布。当一个对象还没有构造完成时，就使它被其他线程所见

**安全发布对象**
1. 在静态初始化函数中初始化一个对象引用
2. 将对象的引用保存到volatile类型域或则AtomicReference对象中
3. 将对象的引用保存到某个正确构造对象的final类型域中
4. 将对象的引用保存到一个由锁保护的域中

# 线程安全策略讲解
* final修饰方法:1.锁定方法不被继承类修改；2.效率
* final修饰变量:基本类型变量(初始化之后不能在被修改)、引用类型变量(初始化之后不能在指向另一个对象)

**不可变对象**
1. Collections.unmodifiableXXX:Collection、List、Set、Map...
2. Guava:ImmutableXXX:Collection、List、Set、Map...

**线程封闭**
1. Ad-hoc线程封闭:程序控制实现，最糟糕，忽略
2. 堆栈封闭:使用局部变量，无并发问题，因为局部变量会被拷贝到栈中，因此不是线程共享的
3. ThreadLocal线程封闭:推荐的封闭方法

**ThreadLocal**
>内部维护一个Map，Map的key是线程的名称，value是我们所存储的对象