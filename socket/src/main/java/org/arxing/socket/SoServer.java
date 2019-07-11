package org.arxing.socket;

import com.annimon.stream.Stream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.arxing.axutils_java.JParser;
import org.arxing.axutils_java.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SoServer {
    private final static String EVENT_ACCEPTING = "#event#@accepting";
    private final static String EVENT_CLIENT_CONNECTED = "#event#@client_connected";
    private final static String EVENT_CLIENT_DISCONNECTED = "#event#@client_disconnected";
    private final static String EVENT_ERROR = "#event#@error";
    private final static String EVENT_MESSAGE = "#event#@message";
    private final static String EVENT_COMM = "#event#@comm";
    private final static String EVENT_PING = "#event#@ping";
    private final static String EVENT_PING_REPLY = "#event#@ping_reply";
    private final static String COMM_CLIENT_CONNECTED = "#comm#@client_connected";
    private final static String COMM_NOTIFY_CLOSED = "#comm#@notify_closed";
    private final static String COMM_PING = "#comm#@ping";
    private final static String COMM_PING_REPLY = "#comm#@ping_reply";

    private Action eAccepting;
    private Consumer<String> eClientConnected;
    private Consumer<String> eClientDisconnected;
    private Consumer<Throwable> eError;
    private BiConsumer<String, String> eMessage;
    private BiConsumer<String, String[]> eCommand;
    private Action ePing;
    private Action ePingReply;
    private RxInternal rx = new RxInternal();
    private long pingTimeout = 5000;
    private long pingInterval = 2000;

    @SuppressWarnings("all")
    public static class Builder {
        private SoServer ins;

        public Builder() {
            ins = new SoServer();
        }

        public SoServer create() {
            return ins;
        }

        public Builder onAccepting(Action e) {
            ins.eAccepting = e;
            return this;
        }

        public Builder onClientConnected(Consumer<String> e) {
            ins.eClientConnected = e;
            return this;
        }

        public Builder onClientDisconnected(Consumer<String> e) {
            ins.eClientDisconnected = e;
            return this;
        }

        public Builder onError(Consumer<Throwable> e) {
            ins.eError = e;
            return this;
        }

        public Builder onMessage(BiConsumer<String, String> e) {
            ins.eMessage = e;
            return this;
        }

        public Builder onCommand(BiConsumer<String, String[]> e) {
            ins.eCommand = e;
            return this;
        }

        public Builder onPing(Action e) {
            ins.ePing = e;
            return this;
        }

        public Builder ePingReply(Action e) {
            ins.ePingReply = e;
            return this;
        }

        public Builder pingTimeout(long mills) {
            ins.pingTimeout = mills;
            return this;
        }

        public Builder pingInterval(long mills) {
            ins.pingInterval = mills;
            return this;
        }
    }

    private class InternalClient {
        private String uuid = UUID.randomUUID().toString();
        private String name;
        private Scheduler scheduler;
        private InputStream in;
        private OutputStream out;
        private BufferedReader reader;
        private BufferedWriter writer;
        private Socket socket;
        private boolean isConnected;

        InternalClient(Socket socket) throws IOException {
            this.socket = socket;
            //讓這個client獨立運作在另一個執行緒
            scheduler = Schedulers.newThread();
            //取得輸入/輸出流
            reader = new BufferedReader(new InputStreamReader(in = socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(out = socket.getOutputStream()));
            isConnected = true;
        }

        /**
         * 釋放單獨一個socket的資源
         */
        public void release() {
            rx.bind(sendCommand(COMM_NOTIFY_CLOSED).doFinally(this::releaseInternal).subscribeOn(scheduler).subscribe(() -> {
            }, this::handleErrorInternal));
        }

        /**
         * 對此client發送訊息
         */
        public Completable sendMessage(String message) {
            if (!isConnected)
                return Completable.never();
            return Completable.create(emitter -> {
                JsonObject object = new JsonObject();
                object.addProperty("type", "msg");
                object.addProperty("data", message);
                String sendData = JParser.toJson(object);
                writer.write(sendData);
                writer.write("\n");
                writer.flush();
                emitter.onComplete();
            }).subscribeOn(scheduler).doOnError(e -> Logger.println("send message error: %s", message));
        }

        /**
         * 對此client發送指令
         */
        @CheckReturnValue public Completable sendCommand(String comm, String... params) {
            if (!isConnected)
                return Completable.never();
            return Completable.create(emitter -> {
                JsonObject object = new JsonObject();
                object.addProperty("type", "comm");
                object.addProperty("comm", comm);
                JsonArray jParams = new JsonArray();
                for (String param : params) {
                    jParams.add(param);
                }
                object.add("params", jParams);
                String sendData = JParser.toJson(object);
                writer.write(sendData);
                writer.write("\n");
                writer.flush();
                emitter.onComplete();
            }).subscribeOn(scheduler).doOnError(e -> Logger.println("send command error: %s", comm));
        }

        public void startPing() {
            rx.bind("ping", Observable.interval(pingInterval, TimeUnit.MILLISECONDS).subscribeOn(scheduler).flatMapCompletable(o -> {
                emitEvent(EVENT_PING);
                return sendCommand(COMM_PING);
            }).subscribe(() -> {
            }, e -> {
                stopPing();
                handleErrorInternal(e);
            }));
        }

        public void stopPing() {
            rx.unbindAll("ping");
        }

        private void releaseInternal() throws IOException {
            //關閉接收資料循環
            rx.unbindAll();
            //關閉socket
            if (socket != null)
                socket.close();
            isConnected = false;
            emitEvent(EVENT_CLIENT_DISCONNECTED, this, name);
        }

        /**
         * 啟動接收資料循環
         */
        private void startReceivingData() {
            rx.bind("reader", Completable.fromAction(() -> {
                while (true) {
                    String recvData = reader.readLine();
                    if (recvData == null) {
                        continue;
                    }
                    JsonObject object;
                    try {
                        object = JParser.parse(recvData).getAsJsonObject();
                    } catch (Exception e) {
                        emitEvent(EVENT_ERROR, e);
                        continue;
                    }
                    String type = object.get("type").getAsString();
                    switch (type) {
                        case "comm":
                            String comm = object.get("comm").getAsString();
                            String[] params = Stream.of(object.get("params").getAsJsonArray())
                                                    .map(JsonElement::getAsString)
                                                    .toList()
                                                    .toArray(new String[0]);
                            handleCommand(this, comm, params);
                            break;
                        case "msg":
                            String data = object.get("data").getAsString();
                            handleMessage(this, data);
                            break;
                    }
                }
            }).subscribeOn(Schedulers.newThread()).subscribe(() -> {
            }, this::handleErrorInternal));
        }

        private void handlePingReply() {
            rx.unbindAll("timeout");
            rx.bind("timeout",
                    Completable.complete().delay(pingTimeout, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread()).subscribe(() -> {
                        //若超過時間但還沒被關閉代表遠端timeout
                        Logger.println("%s timeout 即將release", name);
                        release();
                    }, this::handleErrorInternal));
        }

        private void handleErrorInternal(Throwable throwable) {
            if (throwable instanceof SocketException) {
                Logger.println("%s socket已斷開 準備釋放資源", name);
                release();
            } else {
                emitEvent(EVENT_ERROR, throwable);
            }
        }
    }

    private Scheduler scheduler;
    private ServerSocket serverSocket;
    private Map<String, Consumer<String[]>> commands = new ConcurrentHashMap<>();
    private Map<String, InternalClient> clientMap = new ConcurrentHashMap<>();

    private SoServer() {
        scheduler = Schedulers.newThread();
    }

    /**
     * 異步等待客戶端連線
     */
    public void accept(int port) {
        rx.bind("accept", Completable.fromAction(() -> {
            if (serverSocket == null)
                serverSocket = new ServerSocket(port);
            //發射事件
            emitEvent(EVENT_ACCEPTING);
            //多連線
            while (true) {
                //等待連線
                Socket client = serverSocket.accept();
                //取得client
                InternalClient internalClient = new InternalClient(client);
                //紀錄client
                clientMap.put(internalClient.uuid, internalClient);
                //client開始接收資料
                internalClient.startReceivingData();
            }
        }).subscribeOn(Schedulers.newThread()).subscribe());
    }

    /**
     * 釋放所有client的資源
     */
    public void release() {
        rx.bind(Completable.create(emitter -> {
            //先關閉監聽接口
            rx.unbindAll("accept");
            Collection<InternalClient> clients = clientMap.values();
            for (InternalClient client : clients) {
                client.release();
                clientMap.remove(client.uuid);
            }
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        }).subscribeOn(scheduler).subscribe(() -> {
        }, this::handleError));
    }

    public void registerCommand(String comm, Consumer<String[]> runnable) {
        if (!commands.containsKey(comm))
            commands.put(comm, runnable);
    }

    public int getConnectedClients() {
        return clientMap.size();
    }

    public void sendMessageTo(String name, String message) {
        InternalClient client = findClient(name);
        if (client != null && client.isConnected)
            rx.bind(client.sendMessage(message).subscribe(() -> {
            }, client::handleErrorInternal));
    }

    public void sendMessageToAll(String message) {
        for (InternalClient client : clientMap.values()) {
            rx.bind(client.sendMessage(message).subscribe(() -> {
            }, client::handleErrorInternal));
        }
    }

    public void sendCommandTo(String name, String comm, String... params) {
        InternalClient client = findClient(name);
        if (client != null && client.isConnected)
            rx.bind(client.sendCommand(comm, params).subscribe(() -> {
            }, client::handleErrorInternal));
    }

    public void sendCommandToAll(String comm, String... params) {
        for (InternalClient client : clientMap.values()) {
            rx.bind(client.sendCommand(comm, params).subscribe(() -> {
            }, client::handleErrorInternal));
        }
    }

    private InternalClient findClient(String name) {
        return Stream.of(clientMap.values()).filter(o -> name.equals(o.name)).findFirst().orElse(null);
    }

    private void emitEvent(String event, Object... params) {
        try {
            switch (event) {
                case EVENT_ACCEPTING:
                    eAccepting.run();
                    break;
                case EVENT_CLIENT_CONNECTED:
                    ((InternalClient) params[0]).startPing();
                    eClientConnected.accept((String) params[1]);
                    break;
                case EVENT_CLIENT_DISCONNECTED:
                    ((InternalClient) params[0]).stopPing();
                    eClientDisconnected.accept((String) params[1]);
                    break;
                case EVENT_ERROR:
                    eError.accept((Throwable) params[0]);
                    break;
                case EVENT_MESSAGE:
                    eMessage.accept((String) params[0], (String) params[1]);
                    break;
                case EVENT_COMM:
                    eCommand.accept((String) params[0], (String[]) params[1]);
                    break;
                case EVENT_PING:
                    ePing.run();
                    break;
                case EVENT_PING_REPLY:
                    ePingReply.run();
                    break;
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void handleCommand(InternalClient client, String command, String... params) throws Exception {
        if (handleInternalCommands(client, command, params))
            return;
        if (commands.containsKey(command)) {
            Consumer<String[]> runner = commands.get(command);
            List<String> newParams = new ArrayList<>(Arrays.asList(params));
            newParams.add(0, client.uuid);
            runner.accept(newParams.toArray(new String[0]));
        }
    }

    private void handleMessage(InternalClient client, String message) {
        emitEvent(EVENT_MESSAGE, client.name, message);
    }

    private boolean handleInternalCommands(InternalClient client, String command, String... params) {
        switch (command) {
            case COMM_CLIENT_CONNECTED:
                //連線成功後 client會發送指令告訴server它是誰 之後才算成功
                client.name = params[0];
                emitEvent(EVENT_CLIENT_CONNECTED, client, client.name);
                return true;
            case COMM_PING:
                emitEvent(EVENT_PING_REPLY);
                rx.bind(client.sendCommand(COMM_PING_REPLY).subscribe(() -> {
                }, this::handleError));
                return true;
            case COMM_PING_REPLY:
                client.handlePingReply();
                return true;
            case COMM_NOTIFY_CLOSED:
                client.release();
                return true;
        }
        return false;
    }

    private void handleError(Throwable throwable) {
        emitEvent(EVENT_ERROR, throwable);
    }
}
