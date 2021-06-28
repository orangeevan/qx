package com.haipaite.common.resource.other;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Index;
import com.haipaite.common.utility.ReflectionUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;


public class GetterBuilder {
    private static final Logger logger = LoggerFactory.getLogger(GetterBuilder.class);

    private static class FieldGetter implements Getter {
        private final Field field;

        public FieldGetter(Field field) {
            ReflectionUtility.makeAccessible(field);
            this.field = field;
        }

        @Override
        public Object getValue(Object object) {
            Object value = null;
            try {
                value = this.field.get(object);
            } catch (Exception e) {
                GetterBuilder.logger.error("标识符属性访问异常", e);
                throw new RuntimeException("标识符属性访问异常");
            }
            return value;
        }
    }

    private static class MethodGetter
            implements Getter {
        private final Method method;

        public MethodGetter(Method method) {
            ReflectionUtility.makeAccessible(method);
            this.method = method;
        }

        @Override
        public Object getValue(Object object) {
            Object value = null;
            try {
                value = this.method.invoke(object, new Object[0]);
            } catch (Exception e) {
                FormattingTuple message = MessageFormatter.format("标识方法访问异常", e);
                GetterBuilder.logger.error(message.getMessage());
                throw new RuntimeException(message.getMessage());
            }
            return value;
        }
    }


    private static class IdentityInfo {
        public final Field field;

        public final Method method;


        public IdentityInfo(Class<?> clz) {
            Field[] fields = ReflectionUtility.getDeclaredFieldsWith(clz, Id.class);
            if (fields.length > 1) {
                FormattingTuple formattingTuple = MessageFormatter.format("类[{}]的属性唯一标识声明重复", clz);
                GetterBuilder.logger.error(formattingTuple.getMessage());
                throw new RuntimeException(formattingTuple.getMessage());
            }
            if (fields.length == 1) {
                this.field = fields[0];
                this.method = null;
                return;
            }
            Method[] methods = ReflectionUtility.getDeclaredGetMethodsWith(clz, Id.class);
            if (methods.length > 1) {
                FormattingTuple formattingTuple = MessageFormatter.format("类[{}]的方法唯一标识声明重复", clz);
                GetterBuilder.logger.error(formattingTuple.getMessage());
                throw new RuntimeException(formattingTuple.getMessage());
            }
            if (methods.length == 1) {
                this.method = methods[0];
                this.field = null;
                return;
            }
            FormattingTuple message = MessageFormatter.format("类[{}]缺少唯一标识声明", clz);
            GetterBuilder.logger.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }

        public boolean isField() {
            if (this.field != null) {
                return true;
            }
            return false;
        }
    }


    public static Getter createIdGetter(Class<?> clz) {
        IdentityInfo info = new IdentityInfo(clz);
        Getter identifier = null;
        if (info.isField()) {
            identifier = new FieldGetter(info.field);
        } else {
            identifier = new MethodGetter(info.method);
        }
        return identifier;
    }


    private static class FieldIndexGetter
            extends FieldGetter
            implements IndexGetter {
        private final String name;

        private final boolean unique;

        private final Comparator comparator;


        public FieldIndexGetter(Field field) {
            super(field);
            Index index = field.<Index>getAnnotation(Index.class);
            this.name = index.name();
            this.unique = index.unique();

            Class<? extends Comparator> clz = index.comparatorClz();
            if (!clz.equals(Comparator.class)) {
                try {
                    this.comparator = clz.newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException("索引比较器[" + clz.getName() + "]无法被实例化");
                }
            } else {
                this.comparator = null;
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isUnique() {
            return this.unique;
        }

        @Override
        public Comparator getComparator() {
            return this.comparator;
        }

        @Override
        public boolean hasComparator() {
            if (this.comparator != null) {
                return true;
            }
            return false;
        }
    }


    private static class MethodIndexGetter
            extends MethodGetter
            implements IndexGetter {
        private final String name;

        private final boolean unique;

        private final Comparator comparator;


        public MethodIndexGetter(Method method) {
            super(method);
            Index index = method.<Index>getAnnotation(Index.class);
            this.name = index.name();
            this.unique = index.unique();

            Class<? extends Comparator> clz = index.comparatorClz();
            if (!clz.equals(Comparator.class)) {
                try {
                    this.comparator = clz.newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException("索引比较器[" + clz.getName() + "]无法被实例化");
                }
            } else {
                this.comparator = null;
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isUnique() {
            return this.unique;
        }

        @Override
        public Comparator getComparator() {
            return this.comparator;
        }

        @Override
        public boolean hasComparator() {
            if (this.comparator != null) {
                return true;
            }
            return false;
        }
    }


    public static Map<String, IndexGetter> createIndexGetters(Class<?> clz) {
        Field[] fields = ReflectionUtility.getDeclaredFieldsWith(clz, Index.class);
        Method[] methods = ReflectionUtility.getDeclaredGetMethodsWith(clz, Index.class);

        List<IndexGetter> getters = new ArrayList<>(fields.length + methods.length);
        for (Field field : fields) {
            IndexGetter getter = new FieldIndexGetter(field);
            getters.add(getter);
        }
        for (Method method : methods) {
            IndexGetter getter = new MethodIndexGetter(method);
            getters.add(getter);
        }

        Map<String, IndexGetter> result = new HashMap<>(getters.size());
        for (IndexGetter getter : getters) {
            String name = getter.getName();
            if (result.put(name, getter) != null) {
                FormattingTuple message = MessageFormatter.format("资源类[{}]的索引名[{}]重复", clz, name);
                logger.error(message.getMessage());
                throw new RuntimeException(message.getMessage());
            }
        }
        return result;
    }
}