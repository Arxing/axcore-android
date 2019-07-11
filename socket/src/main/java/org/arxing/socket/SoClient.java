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
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SoClient {
    private final static String EVENT_CONNECTING = "#event#@connecting";
    private final static String EVENT_CONNECTED = "#event#@connected";
    private final static String EVENT_DISCONNECTED = "#event#@socket_closed";
    private final static String EVENT_ERROR = "#event#@error";
    private final static String EVENT_MESSAGE = "#event#@message";
    private final static String EVENT_COMM = "#event#@comm";
    private final static String COMM_CLIENT_CONNECTED = "#comm#@client_connected";
    private final static String COMM_NOTIFY_CLOSED = "#comm#@notify_closed";
    private final static String COMM_PING = "#comm#@ping";
    private final static String COMM_PING_REPLY = "#comm#@ping_reply";
    private long pingTimeout = 5000;
    private long pingInterval = 2000;
    private Action eConnecting;
    private Action eConnected;
    private Action eDisconnected;
    private Consumer<Throwable> eError;
    private Consumer<String> eMessage;
    private BiConsumer<String, String[]> eCommand;

    private Scheduler scheduler;
    private String name;
    private InputStream in;
    private OutputStream out;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private int maxReconnectTimes = 50;
    private int reconnectTimes;
    private Map<String, Consumer<String[]>> commands = new HashMap<>();
    private RxInternal rx = new RxInternal();

    public static class Builder {
        private SoClient ins;

        public Builder(String name) {
            ins = new SoClient(name);
        }

        public SoClient create() {
            return ins;
        }

        public Builder onConnecting(Action e) {
            ins.eConnecting = e;
            return this;
        }

        public Builder onConnected(Action e) {
            ins.eConnected = e;
            return this;
        }

        public Builder onError(Consumer<Throwable> e) {
            ins.eError = e;
            return this;
        }

        public Builder onMessage(Consumer<String> e) {
            ins.eMessage = e;
            return this;
        }

        public Builder onCommand(BiConsumer<String, String[]> e) {
            ins.eCommand = e;
            return this;
        }

        public Builder onDisconnected(Action e) {
            ins.eDisconnected = e;
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

    private SoClient(String name) {
        this.name = name;
        scheduler = Schedulers.newThread();
    }

    @CheckReturnValue public Completable connect(String host, int port) throws Exception {
        return Completable.create(emitter -> {
            emitEvent(EVENT_CONNECTING);
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(in = socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(out = socket.getOutputStream()));
            startReceivingData();
            rx.bind(sendCommand(COMM_CLIENT_CONNECTED, name).subscribe(() -> emitEvent(EVENT_CONNECTED)));
        }).toObservable().retryWhen(observable -> observable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            if (++reconnectTimes <= maxReconnectTimes)
                return Observable.timer(5000, TimeUnit.MILLISECONDS);
            else
                return Observable.error(throwable);
        })).ignoreElements();
    }

    public void release() {
        Logger.println("release...");
        rx.bind(sendCommand(COMM_NOTIFY_CLOSED).doFinally(this::releaseInternal).subscribe(() -> {
        }, e -> {
        }));
    }

    private void releaseInternal() throws IOException {
        //關閉接收資料循環
        rx.unbindAll();
        if (socket != null)
            socket.close();
        emitEvent(EVENT_DISCONNECTED);
    }

    @CheckReturnValue public Completable sendMessage(String message) {
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

    @CheckReturnValue public Completable sendCommand(String comm, String... params) {
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

    private void emitEvent(String event, Object... params) {
        try {
            switch (event) {
                case EVENT_CONNECTING:
                    eConnecting.run();
                    break;
                case EVENT_CONNECTED:
                    startPing();
                    eConnected.run();
                    break;
                case EVENT_ERROR:
                    eError.accept((Throwable) params[0]);
                    break;
                case EVENT_MESSAGE:
                    eMessage.accept((String) params[0]);
                    break;
                case EVENT_DISCONNECTED:
                    eDisconnected.run();
                    break;
                case EVENT_COMM:
                    eCommand.accept((String) params[0], (String[]) params[1]);
                    break;
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String command, String... params) throws Exception {
        if (handleInternalCommands(command, params))
            return;
        if (commands.containsKey(command)) {
            Consumer<String[]> runner = commands.get(command);
            runner.accept(params);
        }
    }

    private void handleMessage(String message) {
        emitEvent(EVENT_MESSAGE, message);
    }

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
                        handleCommand(comm, params);
                        break;
                    case "msg":
                        String data = object.get("data").getAsString();
                        handleMessage(data);
                        break;
                }
            }
        }).subscribeOn(Schedulers.newThread()).subscribe(() -> {
        }, this::handleError));
    }

    private void startPing() {
        rx.bind("ping",
                Observable.interval(pingInterval, TimeUnit.MILLISECONDS)
                          .subscribeOn(scheduler)
                          .flatMapCompletable(o -> sendCommand(COMM_PING))
                          .subscribe(() -> {
                          }, e -> {
                              stopPing();
                              handleError(e);
                          }));
    }

    private void stopPing() {
        rx.unbindAll("ping");
    }

    private boolean handleInternalCommands(String command, String... params) {
        switch (command) {
            case COMM_PING:
                rx.bind(sendCommand(COMM_PING_REPLY).subscribe(() -> {
                }, this::handleError));
                return true;
            case COMM_PING_REPLY:
                handlePingReply();
                return true;
        }
        return false;
    }

    private void handlePingReply() {
        //若超過時間但還沒被關閉代表遠端timeout
        rx.bind("timeout",
                Completable.complete()
                           .delay(pingTimeout, TimeUnit.MILLISECONDS)
                           .subscribeOn(Schedulers.newThread())
                           .subscribe(this::release, this::handleError));
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof SocketException) {
            Logger.println("socket已斷開 準備釋放資源");
            release();
        } else {
            emitEvent(EVENT_ERROR, throwable);
        }
    }
}
