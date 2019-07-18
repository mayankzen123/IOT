package com.example.mayank.iot.Socket;

import android.os.Message;
import com.example.mayank.iot.Fragment.SocketFragment.ClientHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    BufferedReader bufferedReader;
    String dstAddress;
    int dstPort;
    ClientHandler handler;
    PrintWriter printWriter;
    private boolean running;
    Socket socket;

    public ClientThread(String addr, int port, ClientHandler handler2) {
        this.dstAddress = addr;
        this.dstPort = port;
        this.handler = handler2;
    }

    public void setRunning(String closeConn, boolean running2) {
        sendCommand(closeConn);
        this.running = running2;
    }

    private void sendState(String state) {
        this.handler.sendMessage(Message.obtain(this.handler, 0, state));
    }

    public void sendCommand(String msgToSend) {
        if (this.printWriter != null) {
            this.printWriter.println(msgToSend);
        }
    }

    public void run() {
        sendState("connecting");
        this.running = true;
        try {
            this.socket = new Socket(this.dstAddress, this.dstPort);
            sendState("connected");
            this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            while (this.running) {
                if (this.bufferedReader.ready()) {
                    String line = this.bufferedReader.readLine();
                    if (line != null) {
                        this.handler.sendMessage(Message.obtain(this.handler, 1, line));
                    }
                }
            }
            if (this.bufferedReader != null) {
                try {
                    this.bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (this.printWriter != null) {
                this.printWriter.close();
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        } catch (IOException e3) {
            e3.printStackTrace();
            if (this.bufferedReader != null) {
                try {
                    this.bufferedReader.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            if (this.printWriter != null) {
                this.printWriter.close();
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (this.bufferedReader != null) {
                try {
                    this.bufferedReader.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            if (this.printWriter != null) {
                this.printWriter.close();
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
            throw th;
        }
        this.handler.sendEmptyMessage(2);
    }
}
