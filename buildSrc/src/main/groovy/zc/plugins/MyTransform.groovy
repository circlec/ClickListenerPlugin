package zc.plugins

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


class MyTransform extends Transform {

    Project project

    MyTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "MyTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
//                MyInject.injectDir(directoryInput.file.absolutePath, "zc\\demo")
                OnClickListenerInject.injectDir(directoryInput.file.absolutePath, "zc\\demo", project)
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            input.jarInputs.each { JarInput jarInput ->
                OnClickListenerInject.injectDir(jarInput.file.absolutePath, "zc\\demo", project)
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}