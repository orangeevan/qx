package com.haipaite.common.event.config;

import com.haipaite.common.event.anno.ReceiverAnno;
import com.haipaite.common.event.core.EventBusManager;
import com.haipaite.common.event.monitor.MonitorEventBusManager;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Element;

public class EventBeanDefinitionParser
        implements BeanDefinitionParser {
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
    private static final String MONITOR_ATTRIBUTE = "monitor";
    private ResourcePatternResolver resourcePatternResolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory = (MetadataReaderFactory) new CachingMetadataReaderFactory((ResourceLoader) this.resourcePatternResolver);


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        registBeanPostProcessor(parserContext);
        registAllReceivers(element, parserContext);
        boolean monitor = Boolean.valueOf(element
                .getAttribute("monitor")).booleanValue();
        Class<?> clz = EventBusManager.class;
        if (monitor) {
            clz = MonitorEventBusManager.class;
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clz);
        AbstractBeanDefinition abstractBeanDefinition = builder.getBeanDefinition();
        parserContext.getRegistry().registerBeanDefinition(
                StringUtils.uncapitalize(clz.getSimpleName()), (BeanDefinition) abstractBeanDefinition);
        return (BeanDefinition) abstractBeanDefinition;
    }

    private void registAllReceivers(Element element, ParserContext parserContext) {
        String[] classNames = finalAllResourceClassNames(element);
        for (String className : classNames) {
            try {
                Class<?> clz = Class.forName(className);
                if (clz.isAnnotationPresent((Class) ReceiverAnno.class)) {

                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clz);
                    parserContext.getRegistry().registerBeanDefinition(
                            StringUtils.uncapitalize(clz.getSimpleName()), (BeanDefinition) builder
                                    .getBeanDefinition());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void registBeanPostProcessor(ParserContext context) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(EventReceiverBeanPostProcessor.class);
        context.getRegistry().registerBeanDefinition(
                StringUtils.uncapitalize(EventReceiverBeanPostProcessor.class
                        .getSimpleName()), (BeanDefinition) builder.getBeanDefinition());
    }

    private String[] finalAllResourceClassNames(Element element) {
        String[] basePackages = StringUtils.tokenizeToStringArray(element
                .getAttribute("base-package"), ",; \t\n");

        List<String> classNameList = new LinkedList<>();
        for (String basePackage : basePackages) {
            classNameList.addAll(findResourceClassNames(basePackage));
        }
        return classNameList.<String>toArray(new String[classNameList.size()]);
    }

    private List<String> findResourceClassNames(String basePackage) {
        List<String> result = new LinkedList<>();

        try {
            String packageSearchPath = "classpath*:" + resolveBasePackage(basePackage) + "/" + "**/*.class";


            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {

                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    result.add(metadataReader.getClassMetadata().getClassName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(
                SystemPropertyUtils.resolvePlaceholders(basePackage));
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\config\EventBeanDefinitionParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */