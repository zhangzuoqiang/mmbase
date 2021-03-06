package org.mmbase.applications.vprowizards.spring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * @javadoc
 * @author Ernst Bunders
 * @version $Id$
 */

public class ClassInstanceFactory<T> {

    private static org.mmbase.util.logging.Logger log = org.mmbase.util.logging.Logging.getLoggerInstance(ClassInstanceFactory.class);

    private Class<? extends T> classToInstantiate;

    public Class<? extends T> getClazz() {
        return classToInstantiate;
    }

    @SuppressWarnings("unchecked")
    public void setClassName(String className) {
        try {
            Class<? extends T> classFromName = (Class<? extends T>) Class.forName(className);
            checkIfclassHasNoargConstructor(classFromName);
            classToInstantiate = classFromName;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Class name '%s' can not be loaded", className));
        }
    }

    private void checkIfclassHasNoargConstructor(Class<? extends T> c) {
        for (Constructor<?> m : c.getConstructors()) {
            if (constructorIsPublicNoarg(m))
                return;
        }
        throw new IllegalArgumentException(String.format("Class '%s' has no public no-arg constructor", c.getName()));
    }

    private boolean constructorIsPublicNoarg(Constructor<?> m) {
        return m.getParameterTypes().length == 0 && Modifier.isPublic(m.getModifiers());
    }

    public void setClass(Class<? extends T> classToInstantiate) {
        checkIfclassHasNoargConstructor(classToInstantiate);
        this.classToInstantiate = classToInstantiate;
    }

    public T newInstance() {
        try {
            return classToInstantiate.newInstance();
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        }
        return null;
    }

}
