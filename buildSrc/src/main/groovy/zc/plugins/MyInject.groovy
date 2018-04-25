package zc.plugins

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor

class MyInject {
    private static ClassPool pool = ClassPool.getDefault()
    private static String injectStr = "System.out.println(\"hello\" ); "

    static void injectDir(String path, String packageName) {
        pool.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (filePath.endsWith(".class")
                        && !filePath.contains("R") && !filePath.contains("R.class") && !filePath.contains("BuildConfig.class")) {
                    int index = filePath.indexOf(packageName)
                    boolean isMyPackage = index != -1
                    if (isMyPackage) {
                        int end = filePath.length() - 6
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        CtClass c = pool.getCtClass(className)
                        if (c.isFrozen()) {
                            c.defrost()
                        }
                        CtConstructor[] cts = c.getDeclaredConstructors()
                        if (cts == null || cts.length == 0) {
                            CtConstructor constructor = new CtConstructor(new CtClass[0], c)
                            constructor.insertBeforeBody(injectStr)
                            c.addConstructor(constructor)
                        } else {
                            cts[0].insertBeforeBody(injectStr)
                        }
                        c.writeFile(path)
                        c.detach()
                    }
                }
            }
        }
    }
}