package jp.qr.java_conf.iann8071.ajaxmutator.util;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by iann8071 on 2015/04/13.
 */
public class Files2 {

    public static Collection<File> files(String dirPath) {
        return FileUtils.listFiles(new File(dirPath), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }

    public static BufferedReader newReader(File file) {
        try {
            return Files.newReader(file, Charset.defaultCharset());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void write(CharSequence from, File to) {
        try {
            Files.write(from, to, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(CharSequence from, String to) {
        write(from, new File(to));
    }

    public static List<String> readLines(File from) {
        try {
            return Files.readLines(from, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> readLines(String from) {
        return readLines(new File(from));
    }

    public static void write(byte[] from, File to) {
        try {
            Files.write(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(CharSequence from, File to, Charset charset) {
        try {
            Files.write(from, to, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void diff(File original, File revised, File to) {
        try {
            Patch patch = DiffUtils.diff(readLines(original), readLines(revised));
            FileUtils.writeLines(to, patch.getDeltas().stream().map(delta -> delta.toString()).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void diff(String original, File revised, File to) {
        try {
            Patch patch = DiffUtils.diff(readLines(original), readLines(revised));
            FileUtils.writeLines(to, ImmutableList.builder().addAll(patch.getDeltas().stream().map(delta -> delta.getOriginal().getLines()).collect(Collectors.toList()))
                    .add("\n")
                    .addAll(patch.getDeltas().stream().map(delta -> delta.getRevised().getLines()).collect(Collectors.toList())).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(File file) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
