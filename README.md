# 简单BNF范式定义的文本笛卡尔积生成器

[English version](README.en-US.md)

## 支持的语法

* 括号，包括`'(', ')', '[', ']', '<', '>'`，其中`()`与`<>`语义相同，`[]`表示可选
* 或符号 `|`

例如输入

```$xslt
[啊]我[好]<(想[要])|要>吃<肯德基|麦当劳>
```

输出

```$xslt
我想吃肯德基
我想吃麦当劳
我想要吃肯德基
我想要吃麦当劳
我要吃肯德基
我要吃麦当劳
我好想吃肯德基
我好想吃麦当劳
我好想要吃肯德基
我好想要吃麦当劳
我好要吃肯德基
我好要吃麦当劳
啊我想吃肯德基
啊我想吃麦当劳
啊我想要吃肯德基
啊我想要吃麦当劳
啊我要吃肯德基
啊我要吃麦当劳
啊我好想吃肯德基
啊我好想吃麦当劳
啊我好想要吃肯德基
啊我好想要吃麦当劳
啊我好要吃肯德基
啊我好要吃麦当劳
```

## 用法

调用Main类即可，参数如下

`[-o <output-file>] [-append] <input-file> [input-file]...`

### 参数
* `-o` 指定输出文件，默认`output.txt`
* `-append` 指定不覆盖输出文件，向尾部输出
* 可以提供多个输入文件

注：输出编码取决于第一个输入文件的编码

# 依赖

依赖于`com.mozilla.universalchardet`探测文件编码

如果有更好用的请通知我

[地址在此处](https://code.google.com/archive/p/juniversalchardet/)

# 许可

本项目使用了com.mozilla.universalchardet，此库使用[Mozilla Public License](https://www.mozilla.org/en-US/MPL/)

本项目其余部分使用[WTFPL](http://www.wtfpl.net/txt/copying)许可证