package zc.plugins

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class OnClickListenerInject {

    private static ClassPool pool = ClassPool.getDefault()
    public static final String JAVA_ASSIST_PACKAGE = "zc.demo"
//    public static final String JAVA_ASSIST_APP = "zc.demo.MainActivity"
//    public static final String JAVA_ASSIST_APP_BUTTERKNIFE = "zc.demo.MainActivity\$\$ViewBinder"

    static void injectDir(String path, String packageName, Project project) {
        pool.appendClassPath(path)
        String androidJarPath = project.android.bootClasspath[0].toString()
        pool.appendClassPath(androidJarPath)
        importClass(pool)
        File dir = new File(path)
        if (!dir.isDirectory()) {
            return
        }
        dir.eachFileRecurse { File file ->
            String filePath = file.absolutePath
            if (filePath.endsWith(".class") && !filePath.contains('R$')
                    && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
                int index = filePath.indexOf(packageName)
                boolean isMyPackage = index != -1
                if (!isMyPackage) {
                    return
                }
                String className = JavassistUtils.getClassName(index, filePath)
                CtClass c = pool.getCtClass(className)
                for (CtMethod method : c.getDeclaredMethods()) {
                    //找到 onClick(View) 方法
                    if (checkOnClickMethod(method)) {
                        injectMethod(method)
                        c.writeFile(path)
                    }
                }
            }
        }
    }

/**
 *  doClick是使用butterknife生成的ViewBinder中执行onClick的方法
 * @param method 类中的方法
 * @return 是否是点击事件 是返回true 否返回false
 */
    private static boolean checkOnClickMethod(CtMethod method) {
        return (method.getName().endsWith("onClick") || method.getName().endsWith("doClick")) && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].getName().equals("android.view.View")
    }

/**
 * 在方法中插入代码
 * @param method 需要插入代码的方法
 */
    private static void injectMethod(CtMethod method) {
        method.insertAfter("System.out.println((\$1).getTag());")
    }

    private static void log(String msg, Project project) {
        project.logger.log(LogLevel.ERROR, msg)
    }

    private static void importClass(ClassPool pool) {
//        pool.importPackage(JAVA_ASSIST_APP)
//        pool.importPackage(JAVA_ASSIST_APP_BUTTERKNIFE)
        pool.importPackage(JAVA_ASSIST_PACKAGE)
    }
}