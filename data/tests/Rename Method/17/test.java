package net.sourceforge.pmd.internal.util;

import net.sourceforge.pmd.internal.util.ClasspathClassLoaderTest;
import org.junit.Test;

public class ClasspathClassLoaderTestTest {
    
    ClasspathClassLoaderTest classpathClassLoaderTest = new ClasspathClassLoaderTest();

    @Test
    public void testLoadEmptyClasspathWithParent() throws Exception {
        classpathClassLoaderTest.loadEmptyClasspathWithParent();
    }

    @Test
    public void testLoadEmptyClasspathNoParent() throws Exception {
        classpathClassLoaderTest.loadEmptyClasspathNoParent();
    }

    @Test
    public void testLoadFromJar() throws Exception {
        classpathClassLoaderTest.loadFromJar();
    }

    @Test
    public void testLoadFromJava17() throws Exception {
        classpathClassLoaderTest.loadFromJava17();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme