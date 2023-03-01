/*
 * Copyright (c) kanxiaoshuo-cloud author. 2021-2022. All rights reserved.
 */

package work.pcdd.securityjwt.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Servlet工具类
 * UtilityClass注解：将类标记为final，并且类、内部类中的方法、字段都标记为static
 *
 * @author pcdd
 * @date 2022/3/1
 */
@UtilityClass
public class ServletUtils {

    /**
     * 获取当前上下文HttpServletRequest对象
     */
    public HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes requestAttributes = getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getRequest();
        }
        return null;
    }

    private ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

}
