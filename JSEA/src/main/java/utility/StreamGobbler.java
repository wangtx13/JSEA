package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private String streamType;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream,
                         String streamType, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.streamType = streamType;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                consumer.accept(streamType + "::" + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
}
