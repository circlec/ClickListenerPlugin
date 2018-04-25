package zc.plugins

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginTest implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.add("zhouchao", PluginTestExtension)
        project.task("showPersonInfo") << {
            println("姓名：" + project.zhouchao.name)
            println("年龄：" + project.zhouchao.age)
            println("地址：" + project.zhouchao.address)
        }
        def android = project.extensions.getByType(AppExtension)
        //注册一个Transform
        def classTransform = new MyTransform(project)
        android.registerTransform(classTransform)

    }
}