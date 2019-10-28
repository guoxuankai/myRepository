package com.rondaful.cloud.supplier.utils;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: xqq
 * @Date: 2019/6/17
 * @Description:
 */
public class IDUtil {

    // ==============================Fields===========================================
    /** 开始时间截 (2018-01-01) */
    private final Long twepoch = 1514736000000L;

    /** 机器id所占的位数 */
    private final Long workerIdBits = 8L;

    /** 序列在id中占的位数 */
    private final Long sequenceBits = 12L;

    /** 毫秒级别时间截占的位数 */
    private final Long timestampBits = 41L;

    /** 生成发布方式所占的位数 */
    private final Long getMethodBits = 2L;

    /** 支持的最大机器id，结果是255 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final Long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 生成序列向左移8位(8) */
    private final Long sequenceShift = workerIdBits;

    /** 时间截向左移20位(12+8) */
    private final Long timestampShift = sequenceBits + workerIdBits;

    /** 生成发布方式向左移61位(41+12+8) */
    private final Long getMethodShift = timestampBits + sequenceBits  + workerIdBits;

    /** 工作机器ID(0~255) */
    private Long workerId = 0L;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final Long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 毫秒内序列(0~4095) */
    private Long sequence = 0L;

    /** 上次生成ID的时间截 */
    private Long lastTimestamp = -1L;

    /** 2位生成发布方式，0代表嵌入式发布、1代表中心服务器发布模式、2代表rest发布方式、3代表保留未用 */
    private Long getMethod = 0L;

    /** 成发布方式的掩码，这里为3 (0b11=0x3=3) */
    private Long maxGetMethod = -1L ^ (-1L << getMethodBits);
    /** 重入锁*/
    private Lock lock = new ReentrantLock();
    //==============================Constructors=====================================
    /**
     * 构造函数
     * @param getMethod 发布方式 0代表嵌入式发布、1代表中心服务器发布模式、2代表rest发布方式、3代表保留未用 (0~3)
     * @param workerId 工作ID (0~255)
     */
    public IDUtil(Long getMethod, Long workerId) {
        if (getMethod > maxGetMethod || getMethod < 0) {
            throw new IllegalArgumentException(String.format("getMethod can't be greater than %d or less than 0", maxGetMethod));
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.getMethod = getMethod;
        this.workerId = workerId;
    }

    public Long[] nextId(int nums) {
        Long[] ids = new Long[nums];
        for (int i = 0; i < nums; i++) {
            ids[i] = nextId();
        }

        return ids;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public Long nextId() {
        Long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            lock.lock();
            try {
                sequence = (sequence + 1) & sequenceMask;
                //毫秒内序列溢出
                if (sequence == 0) {
                    //阻塞到下一个毫秒,获得新的时间戳
                    timestamp = tilNextMillis(lastTimestamp);
                }
            }finally {
                lock.unlock();
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return  (getMethod << getMethodShift) | ((timestamp - twepoch) << timestampShift)  | (sequence << sequenceShift)  | workerId;
    }

    public String nextString() {
        return Long.toString(nextId());
    }

    public String[] nextString(int nums) {
        String[] ids = new String[nums];
        for (int i = 0; i < nums; i++) {
            ids[i] = nextString();
        }
        return ids;
    }

    public String nextCode(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        Long id = nextId();
        sb.append(id);
        return sb.toString();
    }

    /**
     * 此方法可以在前缀上增加业务标志
     * @param prefix
     * @param nums
     * @return
     */
    public String[] nextCode(String prefix, int nums) {
        String[] ids = new String[nums];
        for (int i = 0; i < nums; i++) {
            ids[i] = nextCode(prefix);
        }
        return ids;
    }

    public String nextHexString() {
        return Long.toHexString(nextId());
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected Long tilNextMillis(Long lastTimestamp) {
        Long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected Long timeGen() {
        return System.currentTimeMillis();
    }


    public static void main(String[] args) {
        IDUtil idGenerate = new IDUtil(0L, 0L);
        int count = 100000;
        final Long[][] times = new Long[count][100];
        System.out.println(idGenerate.nextId());
        Thread[] threads = new Thread[count];
        for (int i = 0; i < threads.length; i++) {
            final int ip = i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j <100; j++) {
                        Long t1 = System.nanoTime();

                        Long aa=idGenerate.nextId();
                        System.out.println(aa);
                        Long t = System.nanoTime() - t1;

                        times[ip][j] = t;
                    }
                }

            };
        }

        Long lastMilis = System.currentTimeMillis();
        //逐个启动线程
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Long time = System.currentTimeMillis() - lastMilis;
        System.out.println("QPS: "+ (1000*count /time));

        Long sum = 0L;
        Long max = 0L;
        for (int i = 0; i < times.length; i++) {
            for (int j = 0; j < times[i].length; j++) {
                sum += times[i][j];

                if (times[i][j] > max)
                    max = times[i][j];
            }
        }
        System.out.println("Sum(ms)"+time);
        System.out.println("AVG(ms): " + sum / 1000000 / (count*100));
        System.out.println("MAX(ms): " + max / 1000000);
    }
}
