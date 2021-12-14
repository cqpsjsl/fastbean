package com.jiangsonglin.fastbean.copier;

import com.jiangsonglin.fastbean.convert.ConverterChain;
import com.jiangsonglin.fastbean.convert.DefaultConverterChain;
import net.sf.cglib.core.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/10
 */
public abstract class BeanUtilsCopier {
    private static final BeanUtilsCopier.BeanCopierKey KEY_FACTORY =
            (BeanUtilsCopier.BeanCopierKey) KeyFactory.create(BeanUtilsCopier.BeanCopierKey.class);
    private static final Type CONVERTER =
            TypeUtils.parseType("com.jiangsonglin.fastbean.convert.ConverterChain");
    private static final Type BEAN_COPIER =
            TypeUtils.parseType("com.jiangsonglin.fastbean.copier.BeanUtilsCopier");
    private static final Signature COPY =
            new Signature("copy", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER});
    private static final Signature CONVERT =
            TypeUtils.parseSignature("Object convert(Object, Class)");

    interface BeanCopierKey {
        Object newInstance(String source, String target, HashMap<String, String> nameMapping, Set<String> ignoreSet);
    }

    public static FastBeanCopier create(Class source, Class target, HashMap<String, String> nameMapping, Set<String> ignoreSet) {
        Generator gen = new Generator();
        gen.setSource(source);
        gen.setTarget(target);
        gen.setIgnoreSet(ignoreSet);
        gen.setNameMapping(nameMapping);
        return gen.create();
    }

    /**
     *  复制对象 浅拷贝
     * @param from 源对象
     * @param to 目标对象
     * @param converterChain 转换器链 可为空 {@link DefaultConverterChain}
     */
    abstract public void copy(Object from, Object to, ConverterChain converterChain);

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(BeanUtilsCopier.class.getName());
        private Class source;
        private Class target;
        /**
         * filed name mapping, key is target,value is source
         */
        private HashMap<String, String> nameMapping;
        /**
         * 忽略set, value is target
         */
        private Set<String> ignoreSet;

        public Generator() {
            super(SOURCE);
        }

        public void setSource(Class source) {
            if (!Modifier.isPublic(source.getModifiers())) {
                setNamePrefix(source.getName());
            }
            this.source = source;
        }

        public void setTarget(Class target) {
            if (!Modifier.isPublic(target.getModifiers())) {
                setNamePrefix(target.getName());
            }

            this.target = target;
        }

        public void setNameMapping(HashMap<String, String> nameMapping) {
            this.nameMapping = nameMapping;
        }

        public void setIgnoreSet(Set<String> ignoreSet) {
            this.ignoreSet = ignoreSet;
        }

        @Override
        protected ClassLoader getDefaultClassLoader() {
            return source.getClassLoader();
        }

        @Override
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(source);
        }

        public FastBeanCopier create() {
            Object key = KEY_FACTORY.newInstance(source.getName(), target.getName(), nameMapping, ignoreSet);
            FastBeanCopier copier = new FastBeanCopier();
            copier.copier = ((BeanUtilsCopier) super.create(key));
            return copier;
        }


        @Override
        public void generateClass(ClassVisitor v) {
            Type sourceType = Type.getType(source);
            Type targetType = Type.getType(target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_8,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    BEAN_COPIER,
                    null,
                    Constants.SOURCE_FILE);

            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);
            PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(source);
            PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(target);

            Map names = new HashMap(getters.length);
            for (int i = 0; i < getters.length; i++) {
                names.put(getters[i].getName(), getters[i]);
            }
            Local targetLocal = e.make_local();
            Local sourceLocal = e.make_local();
            e.load_arg(1);
            e.checkcast(targetType);
            e.store_local(targetLocal);
            e.load_arg(0);
            e.checkcast(sourceType);
            e.store_local(sourceLocal);
            e.load_arg(2);
            Label nonNull = e.make_label();
            e.ifnonnull(nonNull);
            Label end = e.make_label();
            e.visitLabel(end);
            Type type = Type.getType(DefaultConverterChain.class);
            e.new_instance(type);
            e.dup();
            e.invoke_constructor(type);
            e.visitVarInsn(Constants.ASTORE, 3);
            e.visitLabel(nonNull);
            e.visitFrame(Constants.F_NEW, 0, null, 0, null);
            for (int i = 0; i < setters.length; i++) {
                PropertyDescriptor setter = setters[i];
                // ignore
                if (ignoreSet != null && ignoreSet.contains(setter.getName())) continue;
                String getNameKey = setter.getName();
                if (nameMapping != null && (getNameKey = nameMapping.get(setter.getName())) == null) {
                    getNameKey = setter.getName();
                }
                PropertyDescriptor getter = (PropertyDescriptor) names.get(getNameKey);
                if (getter != null) {
                    MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
                    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
                    // if type not equals
                    Type setterType = write.getSignature().getArgumentTypes()[0];
                    Type returnType = read.getSignature().getReturnType();
                    if (!getter.getReadMethod().getGenericReturnType().getTypeName().equals(setter.getWriteMethod().getGenericParameterTypes()[0].getTypeName())) {
                        // packing?
                        Class packingClass = packing(setter.getPropertyType(), getter.getPropertyType());
                        if (packingClass != null) {
                            String methodName;
                            if (getter.getPropertyType().isPrimitive()) {
                                // packing
                                methodName = "valueOf";
                            }else {
                                // split
                                methodName = setterType.getClassName()+"Value";
                            }
                            Method member = null;
                            try {
                                member = packingClass.getMethod(methodName);
                            } catch (NoSuchMethodException noSuchMethodException) {
                                continue;
                                //noSuchMethodException.printStackTrace();
                            }
                            MethodInfo methodInfo = ReflectUtils.getMethodInfo(member);
                            e.load_local(targetLocal);
                            e.load_local(sourceLocal);
                            e.invoke(read);
                            e.invoke(methodInfo);
                            e.invoke(write);
                        } else {
                            e.load_local(targetLocal);
                            e.load_arg(2);
                            e.load_local(sourceLocal);
                            e.invoke(read);
                            e.visitLdcInsn(setterType);
                            e.invoke_interface(CONVERTER, CONVERT);
                            e.checkcast(setterType);
                            e.invoke(write);
                        }
                    } else if (compatible(getter, setter)) {
                        e.load_local(targetLocal);
                        e.load_local(sourceLocal);
                        e.invoke(read);
                        e.invoke(write);
                    }
                }
            }
            e.return_value();
            e.end_method();
            ce.end_class();
        }

        private Class packing(Class<?> setterClass, Class<?> returnClass) {
            if (setterClass.isPrimitive()){
                return getTypeClazz(Type.getType(setterClass));
            }else {
                 return getTypeClazz(Type.getType(returnClass));
            }
        }
        private Class getTypeClazz(Type type){
            switch (type.getSort()) {
                case Type.VOID:
                    return Void.class;
                case Type.BOOLEAN:
                    return Boolean.class;
                case Type.CHAR:
                    return Character.class;
                case Type.BYTE:
                    return Byte.class;
                case Type.SHORT:
                    return Short.class;
                case Type.INT:
                    return Integer.class;
                case Type.FLOAT:
                    return Float.class;
                case Type.LONG:
                    return Long.class;
                case Type.DOUBLE:
                    return Double.class;
                default:
                    return null;
            }
        }

        private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
            // TODO: allow automatic widening conversions?
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
        }

        @Override
        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        @Override
        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
