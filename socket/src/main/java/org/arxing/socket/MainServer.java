package org.arxing.socket;

import org.arxing.axutils_java.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainServer {

    public static void main(String[] args) throws IOException {
        SoServer server = new SoServer.Builder().registerComm("test", (s, ss) -> {
            Logger.println("收到指令test");
        }).onAccepting(() -> Logger.println("等待連線")).onClientConnected(s -> Logger.println("%s 已連線", s)).onMessage((s, m) -> Logger.println("[%s]訊息:%s", s, m)).create();
        server.registerCommand("test", (s, ss) -> {
            Logger.println("執行test data=%s", ss[0]);
        });

        server.accept(12345);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String s = br.readLine();
            if (s.equals("stop"))
                server.release();
            else if (s.equals("accept"))
                server.accept(12345);
            else
                server.sendMessageToAll(s);
        }
    }
}
