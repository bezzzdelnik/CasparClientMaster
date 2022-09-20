package avt.caspar.client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;


public class EchoClient {
    private SocketChannel pingSocket = null;
    private PrintWriter outBuffer = null;
    private BufferedReader inBuffer = null;
    private String command;
    private String hostName;
    private int port;

    public void setHost(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setCommand(String command){
        this.command = command;
    }

    public SocketChannel getSocketChannel() {
        return pingSocket;
    }

    public Socket getPingSocket() {
        return pingSocket.socket();
    }

    public BufferedReader getInBuffer() {
        return inBuffer;
    }

    public void setPingSocket(SocketChannel pingSocket) {
        this.pingSocket = pingSocket;
    }

    public void setInBuffer(BufferedReader inBuffer) {
        this.inBuffer = inBuffer;
    }

    public PrintWriter getOutBuffer() {
        return outBuffer;
    }

    public void closeConnections() throws IOException {
        inBuffer.close();
        outBuffer.close();
        pingSocket.close();
    }

    public void telnetConnection() throws IOException{
        pingSocket = SocketChannel.open();
        pingSocket.connect(new InetSocketAddress(hostName, port));
        outBuffer = new PrintWriter(pingSocket.socket().getOutputStream(), true);
        inBuffer = new BufferedReader(new InputStreamReader(pingSocket.socket().getInputStream()), 1024);
    }

    public void telnetSendCommand(){
        outBuffer.println(command);
    }

    public void telnetSendCommand(String cmd){
        outBuffer.println(cmd);
    }
}