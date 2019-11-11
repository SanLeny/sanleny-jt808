package cn.sanleny.jt808.server.framework.constants;

/**
 * @Author: lg
 * @Date: 2019/10/30
 * @Version: 1.0
 **/
public interface Jt808Constants {

    byte PKG_DELIMITER = 0x7e;
    byte RESP_SUCCESS = 0; //成功/确认
    byte RESP_FAILURE = 1; //失败
    byte RESP_MSG_ERROR = 2; //消息有误
    byte RESP_UNSUPPORTED = 3; //不支持
    byte RESP_WARNNING_MSG_ACK = 4; //报警处理确认(下行支持)

    int ENCTYPTION_TYPE = 0b00;

}
