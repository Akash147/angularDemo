package com.akash;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by siranami on 1/7/16.
 */
public class Utilities {
    public static boolean isEmptyString(String data) {
        if(data!=null && !data.isEmpty())
            return false;
        return true;
    }

    /**
     * Strips the request url path by slash and return List.
     * @param req
     * @return List<String> of request url
     */
    public static List<String> stripRequestUrl(HttpServletRequest req){
        String path = req.getPathInfo() + "/";
        List<String> pathComponents = new ArrayList<String>(Arrays.asList(path.split("/")));
        pathComponents.removeAll(Collections.singleton(null));
        pathComponents.removeAll(Collections.singleton(""));
        return pathComponents;
    }
}
