package org.wiamotit1e

import java.nio.file.Path
import kotlinx.coroutines.runBlocking

fun main() {
    println("欢迎使用视频字幕生成工具！")
    println("=========================")
    
    // 获取API密钥
    print("请输入AssemblyAI API密钥: ")
    val 密钥 = readlnOrNull() ?: ""
    
    if (密钥.isEmpty()) {
        println("错误: 必须提供API密钥才能使用此服务")
        return
    }
    
    val 服务 = AssemblyAI服务(密钥)
    
    while (true) {
        println("\n请选择操作:")
        println("1. 上传文件")
        println("2. 处理已上传的文件（转文字）")
        println("3. 检查处理结果")
        println("4. 退出程序")
        print("请输入选项 (1-4): ")
        
        when (readlnOrNull()) {
            "1" -> {
                print("\n请输入音频/视频文件的完整路径：")
                val 文件路径 = readlnOrNull()
                
                if (文件路径.isNullOrEmpty()) {
                    println("错误: 文件路径不能为空")
                    continue
                }
                
                val 文件 = Path.of(文件路径)
                if (!文件.toFile().exists()) {
                    println("错误: 文件不存在: $文件路径")
                    continue
                }
                
                println("正在上传文件...")
                runBlocking {
                    try {
                        println("文件上传成功！上传URL: ${服务.上传文件(文件)}")
                    } catch (e: Exception) {
                        println("上传失败: ${e.message}")
                    }
                }
            }
            "2" -> {
                println("\n请输入需要处理的 URL：")
                val 当前上传URL = readlnOrNull()
                
                println("正在请求转文字服务...")
                runBlocking {
                    try {
                        
                        println("转文字任务已创建，任务ID: ${服务.转文字(当前上传URL!!)}")
                    } catch (e: Exception) {
                        println("转文字请求失败: ${e.message}")
                    }
                }
            }
            "3" -> {
                print("\n请输入当前任务ID：")
                val 当前任务ID = readlnOrNull()
                
                println("正在检查处理结果...")
                runBlocking {
                    try {
                        val 结果 = 服务.获取结果(当前任务ID!!)
                        println("\n转文字结果:")
                        println(结果)
                        
                        // 获取字幕事件列表
                        val 字幕事件列表 = 服务.在结果中获取句子作为字幕事件列表(当前任务ID!!, "Default")
                        println("\n字幕事件列表:")
                        字幕事件列表.forEach { println(it) }
                    } catch (e: Exception) {
                        println("获取结果失败: ${e.message}")
                    }
                }
            }
            "4" -> {
                println("感谢使用，再见！")
                break
            }
            else -> println("无效选项，请重新选择")
        }
    }
}