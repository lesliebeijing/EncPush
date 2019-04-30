package cn.encmed.push.server;

public class PushServerConfig {
    private int port;
    private int SO_BACKLOG;
    private int readerIdleTime;
    private int writerIdleTime;
    private int allIdleTime;

    public int getSO_BACKLOG() {
        return SO_BACKLOG;
    }

    public void setSO_BACKLOG(int SO_BACKLOG) {
        this.SO_BACKLOG = SO_BACKLOG;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReaderIdleTime() {
        return readerIdleTime;
    }

    public void setReaderIdleTime(int readerIdleTime) {
        this.readerIdleTime = readerIdleTime;
    }

    public int getWriterIdleTime() {
        return writerIdleTime;
    }

    public void setWriterIdleTime(int writerIdleTime) {
        this.writerIdleTime = writerIdleTime;
    }

    public int getAllIdleTime() {
        return allIdleTime;
    }

    public void setAllIdleTime(int allIdleTime) {
        this.allIdleTime = allIdleTime;
    }
}
