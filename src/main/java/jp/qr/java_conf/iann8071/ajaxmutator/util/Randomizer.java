package jp.qr.java_conf.iann8071.ajaxmutator.util;

import java.util.List;

/**
 * Created by iann8071 on 2015/04/24.
 */
public class Randomizer {

    public static String differentString(List<String> list, String original){
        String result = "";
        while((result = list.get((int) (Math.random() * list.size()))).equals(original) && list.size() > 1);
        return result;
    }

    public static String string(List<String> list){
        return list.get((int) (Math.random() * list.size()));
    }
}
