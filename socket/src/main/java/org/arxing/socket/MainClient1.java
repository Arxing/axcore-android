package org.arxing.socket;

import org.arxing.axutils_java.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClient1 {

    public static void main(String[] args) throws IOException {
        SoClient client = new SoClient.Builder("客戶端").onConnecting(() -> Logger.println("連線中")).onConnected(() -> Logger.println("已連線")).onMessage(s -> Logger.println("訊息:%s", s)).create();
        client.connect("localhost", 12345).subscribe();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String s = br.readLine();
            if (s.equals("stop"))
                client.release();
            else if (s.equals("connect"))
                client.connect("localhost", 12345).subscribe();
            else if (s.equals("comm"))
                client.sendCommand("test", "12345").subscribe();
            else
                client.sendMessage(s).subscribe();
        }
    }
}
