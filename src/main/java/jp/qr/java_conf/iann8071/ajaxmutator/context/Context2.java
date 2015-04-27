package jp.qr.java_conf.iann8071.ajaxmutator.context;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Files2;

import java.io.File;
import java.util.*;

/**
 * Created by iann8071 on 2015/04/14.
 */
public class Context2 {
    private static String mUrl = "http://localhost/";
    private static String mOutputRootPath = "/Users/iann8071/Desktop";
    private static final String mJsDirRelativePath = "/js";
    private static final String mOriginalDirRelativePath = "/original";
    private static final String mMutantDirRelativePath = "/mutant";
    private static final String mDiffDirRelativePath = "/diff";
    private static Map<String, Integer> originalFileCounter = new HashMap<>();
    private static Map<String, Integer> mutantFileCounter = new HashMap<>();
    private static Map<String, Integer> diffFileCounter = new HashMap<>();

    public static String url(){
        return mUrl;
    }

    public static void url(String url){
        mUrl = url;
    }

    public static void outputDirRoot(String outputDirRootPath){
        mOutputRootPath = outputDirRootPath;
    }

    public static String jsOriginalDirPath(){
        return mOutputRootPath + mJsDirRelativePath + mOriginalDirRelativePath;
    }
    public static Collection<File> jsOriginalFiles() {
        return Files2.files(jsOriginalDirPath());
    }

    public static File jsNewOriginalFile(String original){
        originalFileCounter.put(original, Optional.ofNullable(originalFileCounter.get(original)).orElse(-1) + 1);
        return new File(jsOriginalDirPath() + "/" + Joiner.on("$").join(original, originalFileCounter.get(original)));
    }

    public static File jsNewOriginalFile(File original){
        return jsNewOriginalFile(original.getName());
    }

    public static String jsMutantDirPath(){
        return mOutputRootPath + mJsDirRelativePath + mMutantDirRelativePath;
    }

    public static File jsNewMutantFile(String original){
        mutantFileCounter.put(original, Optional.ofNullable(mutantFileCounter.get(original)).orElse(-1) + 1);
        return new File(jsMutantDirPath() + "/" + Joiner.on("$").join(original, mutantFileCounter.get(original)));
    }

    public static File jsNewMutantFile(File mutant){
        return jsNewMutantFile(mutant.getName());
    }

    public static String jsDiffDirPath(){
        return mOutputRootPath + mJsDirRelativePath + mDiffDirRelativePath;
    }

    public static File jsNewDiffFile(String original){
        diffFileCounter.put(original, Optional.ofNullable(diffFileCounter.get(original)).orElse(-1) + 1);
        return new File(jsDiffDirPath() + "/" + Joiner.on("$").join(original, diffFileCounter.get(original)) + ".diff");
    }
}
