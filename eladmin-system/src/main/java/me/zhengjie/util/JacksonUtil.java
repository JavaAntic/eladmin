package me.zhengjie.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JacksonUtil
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class JacksonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 对象转json
     *
     * @param object
     * @return
     */
    public static String toJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("...Err...Jackson转换字符串（String）过程失败：：：", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json转对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("...Err...Jackson转换对象（Object）过程失败：：：", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转换为List
     *
     * @param listStr
     * @param typeReference new TypeReference<List<Object>>() {}
     * @param <T>
     * @return
     */
    public static <T> T jsonToObjByTypeRf(String listStr, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(listStr, typeReference);
        } catch (JsonProcessingException e) {
            log.error("...Err...Jackson转换Object过程失败：：：", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param listStr         json
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    @SafeVarargs
    public static <T> List<T> jsonToObjByTypeRf(String listStr, Class<?> collectionClass, Class<T>... elementClasses) throws JsonProcessingException {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        return mapper.readValue(listStr, javaType);
    }
}
