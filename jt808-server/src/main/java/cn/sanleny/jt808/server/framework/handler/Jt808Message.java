package cn.sanleny.jt808.server.framework.handler;

import cn.sanleny.jt808.server.framework.constants.Jt808MessageType;
import lombok.Data;

import java.util.Date;

/**
 * @Author: LG
 * @Date: 2019/11/2
 * @Version: 1.0
 **/
@Data
public class Jt808Message {

    protected Jt808FixedHeader header;//消息头数据
    protected byte[] msgBodyBytes;  // 消息体字节数组
    protected byte checkSum;  // 校验码

    /**
     * 0：成功∕确认<br>
     * 1：失败<br>
     * 2：消息有误<br>
     * 3：不支持<br>
     * 4：报警处理确认<br>
     *
     *  注册
     * 0：成功；
     * 1：车辆已经注册；
     * 2：数据库中无该车辆；
     * 3：终端已经被注册；
     * 4：数据库中无该终端；
     */
    protected byte replyCode;               //通用应答code
    protected Date replayTime;              //通用应答时间
    protected Jt808MessageType replayType = Jt808MessageType.RESPONSE_COMMON_DOWN;;  //通用应答type
    protected String replayToken;           //鉴权码

    private String hex;   //去除标志位后的转义16进制字符

    private Throwable throwable;  //返回给终端的异常信息

    public Jt808Message() {
    }

    public Jt808Message(Jt808FixedHeader header, String hex, Throwable throwable) {
        this.header = header;
        this.hex = hex;
        this.throwable = throwable;
    }

    public Jt808Message(Jt808FixedHeader header, byte[] msgBodyBytes, byte checkSum, String hex) {
        this.header = header;
        this.msgBodyBytes = msgBodyBytes;
        this.checkSum = checkSum;
        this.hex = hex;
    }

}
