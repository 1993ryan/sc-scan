package nuaa.dp.hole.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.UUID;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/8/5 10:38 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/8/5      Dapeng Yan        v1.0.0
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String UTF_8 = "UTF-8";
    public static final String GBK = "GBK";

    public static final Charset CHARSET_UTF8 = Charset.forName(UTF_8);
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    public static String serialize(Object obj) {
        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT_DEFAULT).create();
        return gson.toJson(obj);
    }

    public static <T> T deserialize(String strJson, Class<T> clazz) {
        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT_DEFAULT).create();
        return gson.fromJson(strJson, clazz);
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    /**
     * 将对象toString().欢迎扩展
     *
     * @param o
     * @return
     * @date 2011-11-2 下午12:23:43
     * @author jiaxiao
     */
    public static String toString(Object o) {
        return toString(o, ",");
    }

    public static String toString(Object o, String spliter) {
        if (null == o) {
            return "";
        } else {
            if (o instanceof Integer || o instanceof Double || o instanceof Boolean || o instanceof Float
                    || o instanceof Long || o instanceof Short || o instanceof StringBuffer
                    || o instanceof StringBuilder) {
                return o.toString();
            } else if (o.getClass().isArray()) {
                StringBuffer buffer = new StringBuffer();
                int len = Array.getLength(o);
                for (int index = 0; index < len; index++) {
                    buffer.append(Array.get(o, index));
                    if (index < len - 1) {
                        buffer.append(spliter);
                    }
                }
                return buffer.toString();
            } else if (o instanceof Collection) {
                StringBuffer buffer = new StringBuffer();
                int len = CollectionUtils.size(o);
                for (int index = 0; index < len; index++) {
                    buffer.append(CollectionUtils.get(o, index));
                    if (index < len - 1) {
                        buffer.append(spliter);
                    }
                }
                return buffer.toString();
            } else {
                return o.toString();
            }
        }
    }

    public static String convertCamelNameToColumnName(String fieldName){
        StringBuffer buffer = new StringBuffer();
        for(int index=0; index<fieldName.length(); index ++){
            char ch = fieldName.charAt(index);
            if(Character.isUpperCase(ch)){
                buffer.append("_").append(Character.toLowerCase(ch));
            }else{
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    public static String convertColumnNameToCamelName(String columnName){
        StringBuffer buffer = new StringBuffer();
        String text = columnName.toLowerCase();

        boolean bUpper = false;
        for(int index=0; index< text.length(); index ++){
            char ch = text.charAt(index);
            if('_' == ch){
                bUpper = true;
            }else{
                buffer.append(bUpper?Character.toUpperCase(ch):ch);
                bUpper = false;
            }
        }
        return buffer.toString();
    }

}
