package com.haipaite.common.resource.schema;

import com.haipaite.common.resource.StorageManagerFactory;
import com.haipaite.common.resource.other.FormatDefinition;
import com.haipaite.common.resource.other.ResourceDefinition;
import com.haipaite.common.resource.reader.ExcelReader;
import com.haipaite.common.resource.reader.JsonReader;
import com.haipaite.common.resource.reader.ReaderHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ConfigDefinitionParser extends AbstractBeanDefinitionParser {
    private static final Logger logger = LoggerFactory.getLogger(ConfigDefinitionParser.class);

    protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private ResourcePatternResolver resourcePatternResolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory = (MetadataReaderFactory) new CachingMetadataReaderFactory((ResourceLoader) this.resourcePatternResolver);

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        register(parserContext);

        FormatDefinition format = parseFormat(element);


        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(StorageManagerFactory.class);
        ManagedList<BeanDefinition> resources = new ManagedList();

        NodeList child = element.getChildNodes();
        for (int i = 0; i < child.getLength(); i++) {
            Node node = child.item(i);
            if (node.getNodeType() == 1) {

                String name = node.getLocalName();

                if (name.equals("package")) {

                    String packageName = ((Element) node).getAttribute("name");
                    String[] names = getResources(packageName);
                    for (String resource : names) {
                        Class<?> clz = null;
                        try {
                            clz = Class.forName(resource);
                        } catch (ClassNotFoundException e) {
                            FormattingTuple message = MessageFormatter.format("无法获取的资源类[{}]", resource);
                            logger.error(message.getMessage());
                            throw new RuntimeException(message.getMessage(), e);
                        }
                        BeanDefinition beanDefinition = parseResource(clz, format);
                        resources.add(beanDefinition);
                    }
                }

                if (name.equals("class")) {

                    String className = ((Element) node).getAttribute("name");
                    Class<?> clz = null;
                    try {
                        clz = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        FormattingTuple message = MessageFormatter.format("无法获取的资源类[{}]", className);
                        logger.error(message.getMessage());
                        throw new RuntimeException(message.getMessage(), e);
                    }
                    BeanDefinition beanDefinition = parseResource(clz, format);
                    resources.add(beanDefinition);
                }
            }
        }
        factory.addPropertyValue("definitions", resources);
        AbstractBeanDefinition definition = factory.getBeanDefinition();

        return definition;
    }


    private void register(ParserContext parserContext) {
        registerDefaultReader(parserContext);
        registerReaderHolder(parserContext);
        registerStaticInject(parserContext);
    }


    private FormatDefinition parseFormat(Element element) {
        NodeList child = element.getChildNodes();
        for (int i = 0; i < child.getLength(); i++) {
            Node node = child.item(i);
            if (node.getNodeType() == 1) {


                String name = node.getLocalName();
                if (name.equals("format")) {


                    Element e = (Element) node;
                    String type = e.getAttribute("type");
                    String suffix = e.getAttribute("suffix");
                    String location = e.getAttribute("location");
                    if (StringUtils.endsWith(location, File.pathSeparator)) {
                        location = StringUtils.substringAfterLast(location, File.pathSeparator);
                    }
                    return new FormatDefinition(location, type, suffix);
                }
            }
        }
        throw new RuntimeException("XML文件缺少[format]元素定义");
    }


    private void registerStaticInject(ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String name = StringUtils.uncapitalize(StaticInjectProcessor.class.getSimpleName());
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(StaticInjectProcessor.class);
        registry.registerBeanDefinition(name, (BeanDefinition) factory.getBeanDefinition());
    }


    private void registerReaderHolder(ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String name = StringUtils.uncapitalize(ReaderHolder.class.getSimpleName());
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ReaderHolder.class);
        registry.registerBeanDefinition(name, (BeanDefinition) factory.getBeanDefinition());
    }


    private void registerDefaultReader(ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();


        String name = StringUtils.uncapitalize(ExcelReader.class.getSimpleName());
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ExcelReader.class);
        registry.registerBeanDefinition(name, (BeanDefinition) factory.getBeanDefinition());


        name = StringUtils.uncapitalize(JsonReader.class.getSimpleName());
        factory = BeanDefinitionBuilder.rootBeanDefinition(JsonReader.class);
        registry.registerBeanDefinition(name, (BeanDefinition) factory.getBeanDefinition());
    }


    private BeanDefinition parseResource(Class<?> clz, FormatDefinition format) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ResourceDefinition.class);
        builder.addConstructorArgValue(clz);
        builder.addConstructorArgValue(format);
        return builder.getBeanDefinition();
    }


    private String[] getResources(String packageName) {
        try {
            String packageSearchPath = "classpath*:" + resolveBasePackage(packageName) + "/" + "**/*.class";
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);

            Set<String> result = new HashSet<>();
            String name = com.haipaite.common.resource.anno.Resource.class.getName();
            for (Resource resource : resources) {
                if (resource.isReadable()) {


                    MetadataReader metaReader = this.metadataReaderFactory.getMetadataReader(resource);
                    AnnotationMetadata annoMeta = metaReader.getAnnotationMetadata();
                    if (annoMeta.hasAnnotation(name)) {


                        ClassMetadata clzMeta = metaReader.getClassMetadata();
                        result.add(clzMeta.getClassName());
                    }
                }
            }
            return result.<String>toArray(new String[0]);
        } catch (IOException e) {
            String message = "无法读取资源信息";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\schema\ConfigDefinitionParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */