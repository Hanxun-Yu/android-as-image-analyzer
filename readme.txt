#图片格式学习Project
目的在于掌握一些常用图片格式基础知识，以及快速验证
研究以下模块数据在内存中的实际序列
1.libyuv
2.AndroidBitmap
3.Bmp文件




need clone modules:
#android-as-module-lib-common-util
git@gitee.com:crashxun_dev/android-as-module-lib-common-util.git



#模块结构
app 测试libImage，并把结果显示或输出
libImage 对外图形接口（位图转换 编码jpg 编码png 封装bmp文件）
（external git）lib-android-as-module-lib-common-util 安卓公共模块（文件读写等）

#目录
pc_util #yuv rgb pc显示工具

#后续
#libYuv 独立出来，编译不同版本libyuv源码