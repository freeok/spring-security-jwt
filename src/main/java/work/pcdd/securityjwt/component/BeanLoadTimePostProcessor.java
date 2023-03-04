package work.pcdd.securityjwt.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监控 Bean 加载耗时
 *
 * @author pcdd
 * @date 2023/03/05 01:27
 */
@Slf4j
@Component
public class BeanLoadTimePostProcessor implements BeanPostProcessor {

    private final Map<String, Long> loadTimeMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        loadTimeMap.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Long loadTime = loadTimeMap.computeIfPresent(beanName, (k, v) -> {
            long end = System.currentTimeMillis() - v;
            loadTimeMap.put(k, end);
            return end;
        });
        log.info("beanName: {}, loadTime: {} ms", beanName, loadTime);
        return bean;
    }

}
