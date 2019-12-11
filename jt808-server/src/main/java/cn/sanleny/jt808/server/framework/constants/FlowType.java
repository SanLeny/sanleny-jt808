package cn.sanleny.jt808.server.framework.constants;

/**
 * @Author: sanleny
 * @Date: 2019-12-11
 * @Version: 1.0
 */
public enum FlowType {

    INITIATIVE("initiative_flow")    //主动

    ,PASSIVE("passive_flow");  //被动

    private String value;

    FlowType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
