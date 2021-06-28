 package com.haipaite.common.ramcache.schema;

 import com.haipaite.common.ramcache.exception.ConfigurationException;
 import java.util.List;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.helpers.FormattingTuple;
 import org.slf4j.helpers.MessageFormatter;
 import org.springframework.util.xml.DomUtils;
 import org.w3c.dom.Element;






 public abstract class ParserHelper
 {
   private static final Logger logger = LoggerFactory.getLogger(ParserHelper.class);







   public static boolean hasChildElementsByTagName(Element parent, String tagName) {
     List<Element> elements = DomUtils.getChildElementsByTagName(parent, tagName);
     if (elements.size() > 0) {
       return true;
     }
     return false;
   }







   public static Element getUniqueChildElementByTagName(Element parent, String tagName) {
     List<Element> elements = DomUtils.getChildElementsByTagName(parent, tagName);
     if (elements.size() != 1) {
       FormattingTuple message = MessageFormatter.format("Tag Name[{}]的元素数量[{}]不唯一", tagName, Integer.valueOf(elements.size()));
       logger.error(message.getMessage());
       throw new ConfigurationException(message.getMessage());
     }
     return elements.get(0);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\schema\ParserHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */