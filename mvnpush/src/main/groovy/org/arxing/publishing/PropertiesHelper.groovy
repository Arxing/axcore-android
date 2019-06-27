package org.arxing.publishing

import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import org.gradle.internal.Pair

class PropertiesHelper {
    private String name
    private List<Pair<String, String>> pairs = new ArrayList<>()

    PropertiesHelper(String name) {
        this.name = name
    }

    void addProperty(String name, String val) {
        pairs.add(new Pair<>(name, val))
    }

    void save(String folder) {
        String content = Stream.of(pairs).map { pr ->
            "${pr.left}=${pr.right}"
        }.collect(Collectors.joining("\n"))
        String fileName = "${name}.properties"
        File file = new File(folder, fileName)
        FileOutputStream fos = new FileOutputStream(file)
        OutputStreamWriter osw = new OutputStreamWriter(fos)
        BufferedWriter bw = new BufferedWriter(osw)
        bw.write(content)
        bw.flush()
        bw.close()
        if (PushPlugin.showProperties)
            println "===> save gradle.properties at ${file.toString()}"
    }
}
