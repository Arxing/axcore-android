package org.arxing.publishing


import java.nio.charset.StandardCharsets

class Command {
    private ProcessBuilder builder = new ProcessBuilder()
    private List<String> runner = new ArrayList<>()

    Command() {
        this([])
    }

    Command(ArrayList<String> runner) {
        this.builder.redirectErrorStream(true)
        this.runner.addAll(runner)
    }

    Command cd(File dir) {
        builder.directory(dir)
        return this
    }

    Command cd(String dir) {
        return cd(new File(dir))
    }

    Command exec(String... commands) throws IOException, InterruptedException {
        List<String> commandList = new ArrayList<>(runner)
        for (String command : commands) {
            commandList.add(encode(command))
        }
        builder.command(commandList)
        Process process = builder.start()
        InputStream is = process.getInputStream()
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)
        BufferedReader reader = new BufferedReader(isr)
        String line
        while ((line = reader.readLine()) != null) {
            System.out.println(line)
        }
        isr.close()
        reader.close()
        return this
    }

    Command execFormat(String format, Object... objects) throws IOException, InterruptedException {
        return exec(String.format(format, objects))
    }

    Command printMessage(String format, Object... objects) {
        System.out.println(String.format(format, objects))
        return this
    }

    private String encode(String s) throws UnsupportedEncodingException {
        return s
    }

    private String decode(String s) throws UnsupportedEncodingException {
        return s
    }
}
