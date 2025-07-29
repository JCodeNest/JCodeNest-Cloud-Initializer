package cn.jcodenest.framework.xss.core.clean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 基于 JSONP 实现的 XSS 过滤字符串
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class JsoupXssCleaner implements XssCleaner {

    /**
     * 安全列表
     */
    private final Safelist safelist;

    /**
     * 用于在 src 属性使用相对路径时强制转换为绝对路径。
     * 为空时不处理, 值应为绝对路径的前缀（包含协议部分）
     */
    private final String baseUri;

    /**
     * 无参构造, 默认使用 {@link JsoupXssCleaner#buildSafelist} 方法构建一个安全列表
     */
    public JsoupXssCleaner() {
        this.safelist = buildSafelist();
        this.baseUri = "";
    }

    /**
     * 构建一个 Xss 清理的 Safelist 规则, 基于 Safelist#relaxed() 的基础上:
     * 1. 扩展支持了 style 和 class 属性
     * 2. a 标签额外支持了 target 属性
     * 3. img 标签额外支持了 data 协议, 便于支持 base64
     *
     * @return Safelist
     */
    private Safelist buildSafelist() {
        // 使用 jsoup 提供的默认 Safelist 进行拓展
        Safelist relaxedSafelist = Safelist.relaxed();

        // 富文本编辑时一些样式是使用 style 来进行实现的
        // 比如红色字体 style="color:red;", 所以需要给所有标签添加 style 属性
        // 注意: style 属性会有注入风险 <img STYLE="background-image:url(javascript:alert('XSS'))">
        relaxedSafelist.addAttributes(":all", "style", "class");
        // 保留 a 标签的 target 属性
        relaxedSafelist.addAttributes("a", "target");
        // 支持 img 为 base64
        relaxedSafelist.addProtocols("img", "src", "data");

        // 保留相对路径: 保留相对路径时, 必须提供对应的 baseUri 属性, 否则依然会被删除
        // WHITELIST.preserveRelativeLinks(false);

        // 移除 a 标签和 img 标签的一些协议限制, 这会导致 xss 防注入失效, 如 <img src=javascript:alert("xss")>
        // 虽然可以重写 WhiteList#isSafeAttribute 来处理, 但是有隐患, 所以暂时不支持相对路径
        // WHITELIST.removeProtocols("a", "href", "ftp", "http", "https", "mailto");
        // WHITELIST.removeProtocols("img", "src", "http", "https");

        return relaxedSafelist;
    }

    /**
     * 清理有 Xss 风险的文本
     *
     * @param html 原 html
     * @return 清理后的 html
     */
    @Override
    public String clean(String html) {
        return Jsoup.clean(html, baseUri, safelist, new Document.OutputSettings().prettyPrint(false));
    }
}
