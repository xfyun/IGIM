package com.iflytek.im.demo.common.http;

/**
 * 发送数据包实体类
 *
 * @author ruiding
 * @date 2014/12/19
 */
public abstract class Packet {
    protected final String SEPERATOR = "\r\n";

    public abstract String getHeader();

    public abstract String getBody();

    public String encode(){
    	return getHeader() + getBody();
    }
}
