package topicmodeling;

import config.Config;
import utility.StreamGobbler;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class Executor {
    private static final String MALLET_PATH = Config.MALLET_PATH;

    private ProcessBuilder processBuilder;
    private Consumer<String> consumer;

    public Executor(String jobName, Integer theNumberOfTopic, Consumer<String> consumer) throws IOException {
        this.consumer = consumer;

        processBuilder = new ProcessBuilder().redirectErrorStream(true);
        processBuilder.command("/bin/sh",
                "topic-modeling.sh",
                jobName, String.valueOf(theNumberOfTopic));
        processBuilder.directory(new File(MALLET_PATH));
    }

    public void run() {
        try {
            Process process = processBuilder.start();
            StreamGobbler stdout = new StreamGobbler(process.getInputStream(),
                    "OUTPUT", consumer);
            stdout.run();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("The exit code of command is " + exitCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException when running command.", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("There are something wrong with command.", e);
        }
    }

    public static void main(String[] args) throws IOException {
//        Executor e = new Executor("JHotDraw", 80);
//        e.run();
    }
}
