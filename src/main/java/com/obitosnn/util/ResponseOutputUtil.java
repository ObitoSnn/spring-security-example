package com.obitosnn.util;

import cn.hutool.json.JSONUtil;
import com.obitosnn.vo.Result;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ObitoSnn
 */
public class ResponseOutputUtil {

    private ResponseOutputUtil() {}

    public static void output(HttpServletResponse response, Result<?> result) {
        try {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            String content = JSONUtil.parse(result).toString();
            response.getWriter().write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
