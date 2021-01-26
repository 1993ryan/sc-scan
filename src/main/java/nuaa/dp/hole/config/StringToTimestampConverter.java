package nuaa.dp.hole.config;

import nuaa.dp.hole.utils.DateUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * @Copyright ZhengRen
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/7/9 4:14 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/7/9      Dapeng Yan         v1.0.0
 */
public class StringToTimestampConverter implements Converter<String, Timestamp> { // 这里的<String, Timestamp>表示从string转换到timestamp

    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    static final String REG_YYYY_MM_DD_HH_MM_SS_SSS = "[0-9]{4}[-]{1}[0-9]{2}[-]{1}[0-9]{2}[ ]{1}[0-9]{2}[:]{1}[0-9]{2}[:]{1}[0-9]{2}[.]{1}[0-9]{3}";

    static final String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";
    static final String REG_YYYY_MM_DD_HH_MM_SS_S = "[0-9]{4}[-]{1}[0-9]{2}[-]{1}[0-9]{2}[ ]{1}[0-9]{2}[:]{1}[0-9]{2}[:]{1}[0-9]{2}[.]{1}[0-9]{1}";

    static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    static final String REG_YYYY_MM_DD_HH_MM_SS = "[0-9]{4}[-]{1}[0-9]{2}[-]{1}[0-9]{2}[ ]{1}[0-9]{2}[:]{1}[0-9]{2}[:]{1}[0-9]{2}";

    static final String YYYY_MM_DD = "yyyy-MM-dd";
    static final String REG_YYYY_MM_DD = "[0-9]{4}[-]{1}[0-9]{2}[-]{1}[0-9]{2}";
    static final String REG_NUMBER = "[0-9]{1,}";

    @Override
    public Timestamp convert(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        timeStr = timeStr.trim();

        try {
            String format;
            if (Pattern.matches(StringToTimestampConverter.REG_YYYY_MM_DD_HH_MM_SS_SSS, timeStr)) {
                format = StringToTimestampConverter.YYYY_MM_DD_HH_MM_SS_SSS;
            } else if (Pattern.matches(StringToTimestampConverter.REG_YYYY_MM_DD_HH_MM_SS_S, timeStr)) {
                format = StringToTimestampConverter.YYYY_MM_DD_HH_MM_SS_S;
            } else if (Pattern.matches(StringToTimestampConverter.REG_YYYY_MM_DD_HH_MM_SS, timeStr)) {
                format = StringToTimestampConverter.YYYY_MM_DD_HH_MM_SS;
            } else if (Pattern.matches(StringToTimestampConverter.REG_YYYY_MM_DD, timeStr)) {
                format = StringToTimestampConverter.YYYY_MM_DD;
            } else if (Pattern.matches(StringToTimestampConverter.REG_NUMBER, timeStr)) {
                Long lDate = Long.parseLong(timeStr);
                return new Timestamp(lDate);
            } else {
                throw new RuntimeException(String.format("parser %s to Date fail", timeStr));
            }
            return DateUtils.parseTimestamp(timeStr, format);
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", timeStr));
        }
    }


}