package com.twj.plugin

import com.twj.webp_converter_pugin.ImageUtil
import com.twj.webp_converter_pugin.WebpUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree

class WebpConverterPlugin implements Plugin<Project> {

    // 定义gradle配置名称
    static final String EXT_CONFIG_NAME = 'webpPlugin'

    @Override
    void apply(Project project) {
        //创建配置项
        project.extensions.create(EXT_CONFIG_NAME, PluginExtension)
        project.afterEvaluate {
            //读取配置文件
            PluginExtension extension = project.extensions.findByName(EXT_CONFIG_NAME)
            //不开启检测的话，直接返回
            if (!extension.isCheckSize) {
                return
            }

            def subProject = project.getRootProject().getSubprojects();
            subProject.eachWithIndex {
                Project sub, int index ->
                    println "--- Project-- ': $index -- 模块名：$sub.name'"
                    //遍历所有子 Project 的 src/main/res 文件目录
                    FileTree fileTree = sub.fileTree(dir: 'src/main/res')
                    fileTree.each { File file ->

                        Iterable<String> whiteList = extension.whiteList
                        int largeImageThreshold = extension.largeImageThreshold
                        boolean canConvertWebp = extension.canConvertWebp

                        if (!whiteList.contains(file.getName()) && ImageUtil.isImage(file)
                                && ImageUtil.isBigSizeImage(file, largeImageThreshold)) {
                            int size = Math.round(file.length() / 1024)
                            System.out.println("模块 $sub.name 中找到可优化大图片：$file.name " +
                                    "其体积为为：$size  Kb")
                            //开启转换Webp开关
                            if (canConvertWebp) {
                                WebpUtil.formatWebp(file)
                            }
                        }

                    }
            }
        }

    }

}