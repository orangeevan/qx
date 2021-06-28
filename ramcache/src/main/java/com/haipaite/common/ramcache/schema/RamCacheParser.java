package com.haipaite.common.ramcache.schema;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.aop.LockAspect;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.persist.PersisterConfig;
import com.haipaite.common.ramcache.persist.PersisterType;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.springframework.beans.factory.support.ManagedMap;
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
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class RamCacheParser extends AbstractBeanDefinitionParser {
    private static final Logger logger = LoggerFactory.getLogger(RamCacheParser.class);


    protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory((ResourceLoader) this.resourcePatternResolver);

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        registerInjectProcessor(parserContext);

        if (Boolean.valueOf(element.getAttribute("lockAspect")).booleanValue()) {
            registerLockAspect(parserContext);
        }


        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServiceManagerFactory.class);


        builder.addPropertyReference("accessor", getAccessorBeanName(element));

        builder.addPropertyReference("querier", getQuerierBeanName(element));

        parseConstants2Bean(builder, DomUtils.getChildElementByTagName(element, "constants"), parserContext);


        Map<String, PersisterConfig> persisterConfigs = new HashMap<>();
        Element persisterElement = DomUtils.getChildElementByTagName(element, "persist");
        PersisterType type = PersisterType.valueOf(persisterElement.getAttribute("type"));
        String value = persisterElement.getAttribute("config");
        persisterConfigs.put("30s", new PersisterConfig(type, value));
        for (Element e : DomUtils.getChildElementsByTagName(persisterElement, "persister")) {
            String name = e.getAttribute("name");
            type = PersisterType.valueOf(e.getAttribute("type"));
            value = e.getAttribute("config");
            persisterConfigs.put(name, new PersisterConfig(type, value));
        }
        builder.addPropertyValue("persisterConfig", persisterConfigs);


        Set<Class<? extends IEntity>> classes = new HashSet<>();
        NodeList child = DomUtils.getChildElementByTagName(element, "entity").getChildNodes();
        for (int i = 0; i < child.getLength(); i++) {
            Node node = child.item(i);
            if (node.getNodeType() == 1) {


                String name = node.getLocalName();

                if (name.equals("package")) {

                    String packageName = ((Element) node).getAttribute("name");
                    String[] names = getResources(packageName);
                    for (String resource : names) {
                        Class<? extends IEntity> clz = null;
                        try {
                            clz = (Class) Class.forName(resource);
                        } catch (ClassNotFoundException e) {
                            FormattingTuple message = MessageFormatter.format("无法获取的资源类[{}]", resource);
                            logger.error(message.getMessage());
                            throw new ConfigurationException(message.getMessage(), e);
                        }
                        classes.add(clz);
                    }
                }

                if (name.equals("class")) {

                    String className = ((Element) node).getAttribute("name");
                    Class<? extends IEntity> clz = null;
                    try {
                        clz = (Class) Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        FormattingTuple message = MessageFormatter.format("无法获取的资源类[{}]", className);
                        logger.error(message.getMessage());
                        throw new ConfigurationException(message.getMessage(), e);
                    }
                    classes.add(clz);
                }
            }
        }
        builder.addPropertyValue("entityClasses", classes);

        return builder.getBeanDefinition();
    }

    private void parseConstants2Bean(BeanDefinitionBuilder builder, Element element, ParserContext parserContext) {
        String ref = element.getAttribute("ref");

        if (StringUtils.isNotBlank(ref)) {
            builder.addPropertyReference("constants", ref);

            return;
        }

        ManagedMap<String, Integer> constants = new ManagedMap();
        for (Element e : DomUtils.getChildElementsByTagName(element, "constant")) {
            String name = e.getAttribute("name");
            Integer value = Integer.valueOf(Integer.parseInt(e.getAttribute("size")));
            constants.put(name, value);
        }
        builder.addPropertyValue("constants", constants);
    }


    private void registerLockAspect(ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String name = StringUtils.uncapitalize(LockAspect.class.getSimpleName());
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LockAspect.class);
        registry.registerBeanDefinition(name, (BeanDefinition) factory.getBeanDefinition());
    }


    private void registerInjectProcessor(ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String name = StringUtils.uncapitalize(InjectProcessor.class.getSimpleName());
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(InjectProcessor.class);
        registry.registerBeanDefinition(name, (BeanDefinition) factory.getBeanDefinition());
    }


    private String getQuerierBeanName(Element element) {
        element = ParserHelper.getUniqueChildElementByTagName(element, "querier");

        if (element.hasAttribute("ref")) {
            return element.getAttribute("ref");
        }
        throw new ConfigurationException("查询器配置声明缺失");
    }


    private String getAccessorBeanName(Element element) {
        element = ParserHelper.getUniqueChildElementByTagName(element, "accessor");

        if (element.hasAttribute("ref")) {
            return element.getAttribute("ref");
        }
        throw new ConfigurationException("存储器配置声明缺失");
    }


    private String[] getResources(String packageName) {
        try {
            String packageSearchPath = "classpath*:" + resolveBasePackage(packageName) + "/" + "**/*.class";
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);

            Set<String> result = new HashSet<>();
            String name = Cached.class.getName();
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
            throw new ConfigurationException(message, e);
        }
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }
}