package me.zhengjie.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.spire.ms.System.Collections.ArrayList;
import me.zhengjie.utils.StringUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mapping通用转换
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Named("TypeConversionWorker")
public class TypeConversionWorker {
    /**
     * 对象转json字符串
     *
     * @param obj
     * @return
     */
    @Named("toJsonString")
    public String toJsonString(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JacksonUtil.toJsonString(obj);
    }

    /**
     * 去空格
     *
     * @param str
     * @return
     */
    @Named("doTrim")
    public String doTrim(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return str.trim();
    }

    /**
     * 字符串转List对象
     *
     * @param str
     * @return
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

    /**
     * 字符串转List对象
     *
     * @param str
     * @return
     */
    @Named("toStrList")
    public List<String> toStrList(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JacksonUtil.jsonToObjByTypeRf(str, new TypeReference<List<String>>() {
        });
    }

    /**
     * json字符串转换为Map
     *
     * @param obj
     * @return
     */
    @Named("toStrObjMap")
    public Object toStrObjMap(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JacksonUtil.jsonToObjByTypeRf(obj.toString(), new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * jsonLsit转换为逗号隔开形式
     *
     * @param obj
     * @return
     */
    @Named("listStr2CommaStr")
    public String listStr2CommaStr(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        List<String> strings = JacksonUtil.jsonToObjByTypeRf(obj.toString(), new TypeReference<List<String>>() {
        });
        if (strings != null) {
            return String.join(",", strings);
        }
        return null;
    }

    /**
     * BsFieldTransMapping生成relatedField内容
     */
    @Named("getParentScope")
    public String getParentScope(String targetScope) {
        String[] split = targetScope.split("\\.");
        if (split.length == 1) {
            return "";
        }
        StringBuilder parentScope = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            parentScope.append(split[i]);
            if (i < split.length - 2) {
                parentScope.append(".");
            }
        }
        return parentScope.toString();
    }
}
