package main.java.com.shike.id;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;

/**
 * Id生成器
 * 构成:
 * 1 ms级时间 17位 20160329201902024
 * 2 IDC标志位 2位 12
 * 3 服务器标志位 3位 002
 * 4 流水码 4位 0-4095 仿Twitter snowflake ms内最多产生4096的seq
 * 5 用户Id埋点 4位 (userId: 23423423454) 2345 交易场景下根据userId查询较多,方便分表
 * Created by shike on 16/3/29.
 */
public class IdGenerator {

    //private final static Logger logger = Logger.getLogger(IdGenerator.class);
    //private final static Logger idlogger = Logger.getLogger("getIdLog");
    private long sequence = 0L; //流水号
    private final long seqBit = 12L; //左移控制位
    private final long seqMask = -1L ^ -1L << this.seqBit; //掩码, 111111111111, 保证流水号在0-4095之间
    private StringBuilder lastTime = new StringBuilder("20160328232228180"); //格式化时间戳,初始一个
    private final String idc = getIdc();
    private final String server = getServerId();

    private volatile static IdGenerator idGenerator;
    private IdGenerator() {} //私有化构造函数

    /**
     * 单例工具, 保证IdGenerator有唯一实例
     * @return
     */
    public static IdGenerator getIdGenerator() {
        if (idGenerator == null) {
            synchronized (IdGenerator.class) {
                if (idGenerator == null) {
                    idGenerator = new IdGenerator();
                }
            }
        }
        return idGenerator;
    }

    /**
     * 获取Id
     * @param userId 用户Id
     * @return id
     */
    public synchronized StringBuilder getId(String userId) throws Exception{
        if (userId == null) {
            //idlogger.info("userId is null");
            return null;
        }
        //获取时间
        StringBuilder time = getTime();
        if (time == null) {
            return null;
        }
        if (this.lastTime.toString().equals(time.toString())) { //同一ms内, 增加流水号
            this.sequence = this.sequence + 1 & this.seqMask;
            if (this.sequence == 0) { //ms内流水超过4095,等待到下一毫秒
                time = this.waitNextMs(this.lastTime);
            }
        } else { //初始化sequence
            this.sequence = 0;
        }

        if (time.toString().compareTo(this.lastTime.toString()) < 0) {
            //logger.error("clock error!"); //需要监控,致命错误
            throw new Exception("clock error!");
        }

        this.lastTime = time;

        StringBuilder id = new StringBuilder();
        StringBuilder uid = getUserIdTracking(userId);
        String seqStr = String.valueOf(this.sequence);
        seqStr = org.apache.commons.lang3.StringUtils.leftPad(seqStr,4,"0"); //补齐4位

        id = id.append(time).append(this.idc).append(this.server).append(seqStr).append(uid);
        uid = null; //help GC
        seqStr = null;
        return id;

    }

    /**
     * 获取当前时间
     * @return
     */
    private StringBuilder getTime() {
        Long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmssSSS");
        return new StringBuilder(sdf.format(time));
    }

    /**
     * 获取Idc标志,可以判断ip/MAC等方法
     * @return
     */
    private String getIdc() {
        if (true) {
            return "11";
        } else {
            //logger.error("idc unknow!"); //日志需要监控
            return "99";
        }
    }

    /**
     * 获取服务器Id, 可以根据MAC等标志
     * @return
     */
    private String getServerId() {
        if (true) {
            return "022";
        } else {
            //logger.error("server unknow!"); //日志需要监控
            return "999";
        }
    }

    /**
     * userId埋点, 获取userId倒数第2\3\4\5位
     * @param userId
     * @return
     */
    private StringBuilder getUserIdTracking(String userId) {
        if (userId == null) {
            return null;
        }
        int len = userId.length();
        return new StringBuilder(userId.substring(len - 5, len - 1));
    }

    /**
     * 获取下一个ms
     * @param lastTime
     * @return time当前时间
     */
    private StringBuilder waitNextMs(StringBuilder lastTime) {
        StringBuilder time = this.getTime();
        while (time.toString().compareTo(lastTime.toString()) <= 0) {
            time = this.getTime();
        }
        return time;
    }
}
