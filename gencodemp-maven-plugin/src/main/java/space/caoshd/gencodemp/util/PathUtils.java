package space.caoshd.gencodemp.util;

import java.io.File;
import java.util.regex.Matcher;

public class PathUtils {

    // 正则化文件路径
    public static String normalizePath(String path) {
        String result = path.replaceAll("/+", Matcher.quoteReplacement(File.separator));
        return result.replaceAll("\\\\+", Matcher.quoteReplacement(File.separator));
    }

    // 正则化URL路径
    public static String normalizeUrl(String path) {
        String placeholder = "6445BF55-95C4-41F9-8235-34CE3ED9146D";
        String result = path.replaceAll("\\\\+", "/");
        result = result.replaceAll("/+", "/");
        result = result.replaceAll("http:/+", placeholder);
        return result.replace(placeholder, "http://");
    }

}
