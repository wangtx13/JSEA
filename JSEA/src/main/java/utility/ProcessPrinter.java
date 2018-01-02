package utility;

import java.util.function.Consumer;

public class ProcessPrinter implements Consumer<String> {
    private Consumer<String> delegate;

    public ProcessPrinter(Consumer<String> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void accept(String s) {
        delegate.accept("<p>" + s + "</p>");
    }
}
