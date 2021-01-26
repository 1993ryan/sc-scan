package nuaa.dp.hole.config;

import nuaa.dp.hole.utils.DateUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @Copyright ZhengRen
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/7/9 2:09 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/7/9      Dapeng Yan         v1.0.0
 */
@Component
public class StringToUtilDateConverter implements Converter<String, Date> { // 这里的<String, Date>表示从string转换到Date

    @Override
    public Date convert(String timeStr) {
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
            return DateUtils.parseDate(timeStr, format);
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", timeStr));
        }
    }


}