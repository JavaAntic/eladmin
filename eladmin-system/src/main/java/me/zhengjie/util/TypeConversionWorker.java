package me.zhengjie.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spire.ms.System.Collections.ArrayList;
import lombok.RequiredArgsConstructor;
import me.zhengjie.config.FileProperties;
import me.zhengjie.modules.system.domain.Document;
import me.zhengjie.modules.system.repository.DocumentRepository;
import me.zhengjie.utils.StringUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Mapping通用转换
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Named("typeConversionWorker")
public class TypeConversionWorker {

    private final DocumentRepository documentRepository;
    private final FileProperties properties;

    /**
     * 对象转json字符串
     *
     * @param obj /
     * @return /
     */
    @Named("toJsonString")
    public String toJsonString(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JacksonUtil.toJsonString(obj);
    }

    @Named("docNum2String")
    public String docNum2String(Integer docNum, String docType) {
        final Document first = documentRepository.findFirstByDocTypeOrderByDocNumDesc(docType);
        final List<String> docNumPrefix = properties.getDocNumPrefix();
        final String prefix = docNumPrefix.get(Integer.parseInt(docType));
        String strDocNum;
        if (first != null) {
            final int targetLen = String.valueOf(first.getDocNum()).length();
            final int sourceLen = String.valueOf(docNum).length();
            final int len = targetLen - sourceLen;
            if (len == 0) {
                strDocNum = prefix + docNum;
            } else {
                String format = "%0" + len + "d";
                strDocNum = prefix + String.format(format, docNum);
            }
        } else {
            strDocNum = prefix + docNum;
        }
        return strDocNum;
    }

    @Named("docNum2Integer")
    public Integer docNum2Integer(String docNum, String docType) {
        final List<String> docNumPrefix = properties.getDocNumPrefix();
        final String prefix = docNumPrefix.get(Integer.parseInt(docType));
        final String strDocNum = docNum.substring(prefix.length());
        return Integer.parseInt(strDocNum);
    }

    /**
     * 字符串转List对象
     *
     * @param str /
     * @return /
     */
    @Named("toStrList")
    public <T> List<T> toStrList(String str, Class<T> elementClasses) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        List<T> ts = null;
        try {
            ts = JacksonUtil.jsonToObjByTypeRf(str, ArrayList.class, elementClasses);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ts;
    }
}
