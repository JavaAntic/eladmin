package me.zhengjie.util;

/**
 * 文件类型枚举
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
public enum FileTypeEnum {
    /**
     * word文档
     */
    WORD("0"),
    /**
     * pdf文档
     */
    PDF("1"),
    /**
     * 表格文档
     */
    EXCEL("2");
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    FileTypeEnum(String code) {
        this.code = code;
    }

    public static String getType(String suffix) throws Exception {
        String code;
        switch (suffix) {
            case "pdf":
                code = PDF.getCode();
                break;
            case "doc":
            case "docx":
                code = WORD.getCode();
                break;
            case "xls":
            case "xlsx":
                code = EXCEL.getCode();
                break;
            default:
                throw new Exception("类型[" + suffix + "]暂不支持");
        }
        return code;
    }
}
