package nuaa.dp.hole.config;

import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/20 6:19 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/20      Dapeng Yan          v1.0.0
 */
@ControllerAdvice
public class DefaultControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        GenericConversionService genericConversionService = (GenericConversionService) binder.getConversionService();
        if (genericConversionService != null) {
            genericConversionService.addConverter(new StringToUtilDateConverter());
        }
    }
}
