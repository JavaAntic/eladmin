package me.zhengjie.util;

/**
 * 安全类型枚举
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum SafeTypeEnum {
    /**
     * 通付盾
     */
    PAY_EGIS("0"),
    /**
     * 梆梆
     */
    BANG_CLE("1"),
    /**
     * 爱加密
     */
    I_JIAMI("2");
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    SafeTypeEnum(String code) {
        this.code = code;
    }

    public static String getType(String safeType) throws Exception {
        String code;
        switch (safeType) {
            case "通付盾":
                code = PAY_EGIS.getCode();
                break;
            case "梆梆":
                code = BANG_CLE.getCode();
                break;
            case "爱加密":
                code = I_JIAMI.getCode();
                break;
            default:
                throw new Exception("安全类型[" + safeType + "]暂不支持");
        }
        return code;
    }
}
