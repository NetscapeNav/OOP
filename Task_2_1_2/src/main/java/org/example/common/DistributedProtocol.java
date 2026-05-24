package org.example.common;

public final class DistributedProtocol {
    public static final int DEFAULT_CHUNK_SIZE = 1024;

    public static final int CONNECT_TIMEOUT_MS = 1000;
    public static final int HEARTBEAT_INTERVAL_MS = 500;
    public static final int HEARTBEAT_TIMEOUT_MS = 3000;
    public static final int RETRY_DELAY_MS = 300;

    public static final int MSG_ACCEPTED = 1;
    public static final int MSG_HEARTBEAT = 2;
    public static final int MSG_RESULT = 3;

    private DistributedProtocol() {
    }
}
