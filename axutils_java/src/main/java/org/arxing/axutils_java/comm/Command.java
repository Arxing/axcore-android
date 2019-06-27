package org.arxing.axutils_java.comm;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command {
    private ProcessBuilder builder = new ProcessBuilder();
    private List<String> runner = new ArrayList<>();

    public Command() {
        this(new String[]{});
    }

    public Command(String[] runner) {
        this.builder.redirectErrorStream(true);
        this.runner.addAll(Arrays.asList(runner));
    }

    public Command cd(File dir) {
        builder.directory(dir);
        return this;
    }

    public Command cd(String dir) {
        return cd(new File(dir));
    }

    public Command exec(String... commands) throws IOException, InterruptedException {
        List<String> commandList = new ArrayList<>(runner);
        for (String command : commands) {
            commandList.add(encode(command));
        }
        builder.command(commandList);
        Process process = builder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        isr.close();
        reader.close();
        return this;
    }

    public Command execFormat(String format, Object... objects) throws IOException, InterruptedException {
        return exec(String.format(format, objects));
    }

    public Command printMessage(String format, Object... objects) {
        System.out.println(String.format(format, objects));
        return this;
    }

    private String encode(String s) throws UnsupportedEncodingException {
        return s;
    }

    private String decode(String s) throws UnsupportedEncodingException {
        return s;
    }
}
