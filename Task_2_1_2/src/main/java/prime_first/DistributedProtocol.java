package prime_first;

final class DistributedProtocol {
    static final int DEFAULT_CHUNK_SIZE = 1024;

    static final int CONNECT_TIMEOUT_MS = 1000;
    static final int HEARTBEAT_INTERVAL_MS = 500;
    static final int HEARTBEAT_TIMEOUT_MS = 3000;
    static final int RETRY_DELAY_MS = 300;

    static final int MSG_ACCEPTED = 1;
    static final int MSG_HEARTBEAT = 2;
    static final int MSG_RESULT = 3;

    private DistributedProtocol() {
    }
}
