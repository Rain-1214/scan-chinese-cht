由于项目自身原因 需要在项目中所有的中文都为繁体字 所以写了一个 检测项目中所有的汉字 是否都为繁体字的检测工具。

## 实现流程
读取目录下的所有文件 然后使用正则提取文件中所有的中文。然后将所有的中文通过 百度翻译 转换成繁体,之后对比转换前的结果 和转换后的结果是否一致。

## 注意事项

目前是自动忽略文件中的注释，由于要检测的项目为前端项目，所以会忽略前端代码中会使用三种类型的注释。如下:

* // ....
* /* .... */
* \<!-- .... -->

Main.java文件当中有 String[] excludeType 的一个数组，指定的是扫描时会忽略的文件类型。

## PS

没有实现自动修改错误的原因 因为在测试的过程当中 发现 百度翻译的结果可能有时候 并不是很满意 比如 ”循环“ -> "迴圈" (20190211)(PPS: Google翻译结果 ”循环“ -> "循環" (时间同上) 虽然我并不知道那个正确,但是肯定有一个有瑕疵)。所以扫描结果需要二次确认。
