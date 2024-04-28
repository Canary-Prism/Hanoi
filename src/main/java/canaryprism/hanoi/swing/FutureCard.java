package canaryprism.hanoi.swing;

import java.util.concurrent.CompletableFuture;

public final class FutureCard extends Item {

    private final CompletableFuture<Void> future;

    public FutureCard(CompletableFuture<Void> future) {
        this.future = future;
    }

    public CompletableFuture<Void> getFuture() {
        return future;
    }
}
