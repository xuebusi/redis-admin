package com.xuebusi.cms.common.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Html文档解析工具类
 *
 * @author: shiyanjun
 * @Date: 2018/10/25 下午6:02
 */
public class JsoupUtil {

    /**
     * 将html文本转换成文档
     *
     * @param html
     * @return
     */
    public static Document parseHtmlToDoc(String html) {
        Document doc;
        try {
            doc = removeHtmlSpace(html);
        } catch (Exception e) {
            return null;
        }
        return doc;
    }

    /**
     * 将HTML页面中的 " & n b s p " 空格符去除并转换成Document
     *
     * @param str
     * @return
     */
    public static Document removeHtmlSpace(String str) {
        Document doc = Jsoup.parse(str);
        String result = doc.html().replace("&nbsp;", "");
        return Jsoup.parse(result);
    }
}
