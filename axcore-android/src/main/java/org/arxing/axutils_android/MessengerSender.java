package org.arxing.axutils_android;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import org.arxing.axutils_android.function.Consumer;
import org.arxing.axutils_android.xhelper.XField;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MessengerSender {
    private WeakReference<Messenger> sender;
    private Consumer<Object> deathListener;
    private int defArg1;
    private int defArg2;
    private Object tag;

    private MessengerSender(Messenger sender) {
        this.sender = new WeakReference<>(sender);
        XField.of(getBinder()).call(o -> {
            try {
                o.linkToDeath(recipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public static MessengerSender of(Messenger service) {
        return new MessengerSender(service);
    }

    private IBinder.DeathRecipient recipient = new IBinder.DeathRecipient() {
        @Override public void binderDied() {
            XField.of(deathListener).call(o -> o.apply(tag));
            XField.of(getBinder()).call(o -> o.unlinkToDeath(recipient, 0));
        }
    };

    public Messenger getMessenger() {
        return sender.get();
    }

    public void sendMessage(Message message) {
        if (getMessenger() == null)
            return;
        try {
            getMessenger().send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public IBinder getBinder() {
        return getMessenger() == null ? null : getMessenger().getBinder();
    }

    /**
     * 檢查Binder對象是否存在
     */
    public boolean pingBinder() {
        return getBinder() != null && getBinder().pingBinder();
    }

    /**
     * 檢查Binder的Process是否存活
     */
    public boolean isBinderAlive() {
        return getBinder() != null && getBinder().isBinderAlive();
    }

    public boolean isAccessible() {
        return pingBinder() && isBinderAlive();
    }

    public void setOnProcessDeathListener(Consumer<Object> listener) {
        this.deathListener = listener;
    }

    public MessageBuilder newMsgBuilder(Message src) {
        return new MessageBuilder(src);
    }

    public MessageBuilder newMsgBuilder() {
        return newMsgBuilder(null);
    }

    public MessengerSender withDefArg1(int defArg1) {
        this.defArg1 = defArg1;
        return this;
    }

    public MessengerSender withDefArg2(int defArg2) {
        this.defArg2 = defArg2;
        return this;
    }

    public MessengerSender withTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public class MessageBuilder {
        private Message message;
        private Bundle bundle;
        private boolean arg1BeenSet;
        private boolean arg2BeenSet;

        private MessageBuilder(Message src) {
            if (src != null)
                message = Message.obtain(src);
            else
                message = Message.obtain();
            Bundle originBundle = message.getData();
            bundle = originBundle == null ? new Bundle() : new Bundle(originBundle);
        }

        public void send() {
            create();
            sendMessage(message);
        }

        public Message create() {
            message.setData(bundle);
            if (!arg1BeenSet)
                message.arg1 = defArg1;
            if (!arg2BeenSet)
                message.arg2 = defArg2;
            return message;
        }

        public MessageBuilder setArg1(int val) {
            message.arg1 = val;
            arg1BeenSet = true;
            return this;
        }

        public MessageBuilder srtArg2(int val) {
            message.arg2 = val;
            arg2BeenSet = true;
            return this;
        }

        public MessageBuilder setWhat(int val) {
            message.what = val;
            return this;
        }

        public MessageBuilder setObj(Object val) {
            message.obj = val;
            return this;
        }

        public MessageBuilder setReplyTo(Messenger reply) {
            message.replyTo = reply;
            return this;
        }

        public MessageBuilder setHandler(Handler handler) {
            message.setTarget(handler);
            return this;
        }

        /* put */

        public MessageBuilder putDataAll(Bundle bundle) {
            bundle.putAll(bundle);
            return this;
        }

        public MessageBuilder putData(String key, IBinder v) {
            bundle.putBinder(key, v);
            return this;
        }

        public MessageBuilder putData(String key, Bundle v) {
            bundle.putBundle(key, v);
            return this;
        }

        public MessageBuilder putData(String key, byte v) {
            bundle.putByte(key, v);
            return this;
        }

        public MessageBuilder putData(String key, byte[] v) {
            bundle.putByteArray(key, v);
            return this;
        }

        public MessageBuilder putData(String key, char v) {
            bundle.putChar(key, v);
            return this;
        }

        public MessageBuilder putData(String key, char[] v) {
            bundle.putCharArray(key, v);
            return this;
        }

        public MessageBuilder putData(String key, CharSequence v) {
            bundle.putCharSequence(key, v);
            return this;
        }

        public MessageBuilder putData(String key, CharSequence[] v) {
            bundle.putCharSequenceArray(key, v);
            return this;
        }

        public MessageBuilder putDataCharArrayList(String key, ArrayList<CharSequence> v) {
            bundle.putCharSequenceArrayList(key, v);
            return this;
        }

        public MessageBuilder putData(String key, float v) {
            bundle.putFloat(key, v);
            return this;
        }

        public MessageBuilder putData(String key, float[] v) {
            bundle.putFloatArray(key, v);
            return this;
        }

        public MessageBuilder putDataIntArrayList(String key, ArrayList<Integer> v) {
            bundle.putIntegerArrayList(key, v);
            return this;
        }

        public MessageBuilder putData(String key, Parcelable v) {
            bundle.putParcelable(key, v);
            return this;
        }

        public MessageBuilder putData(String key, Parcelable[] v) {
            bundle.putParcelableArray(key, v);
            return this;
        }

        public MessageBuilder putDataParcelableArrayList(String key, ArrayList<? extends Parcelable> v) {
            bundle.putParcelableArrayList(key, v);
            return this;
        }

        public MessageBuilder putData(String key, Serializable v) {
            bundle.putSerializable(key, v);
            return this;
        }

        public MessageBuilder putData(String key, short v) {
            bundle.putShort(key, v);
            return this;
        }

        public MessageBuilder putData(String key, short[] v) {
            bundle.putShortArray(key, v);
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public MessageBuilder putData(String key, Size v) {
            bundle.putSize(key, v);
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public MessageBuilder putData(String key, SizeF v) {
            bundle.putSizeF(key, v);
            return this;
        }

        public MessageBuilder putData(String key, SparseArray<? extends Parcelable> v) {
            bundle.putSparseParcelableArray(key, v);
            return this;
        }

        public MessageBuilder putDataStringArrayList(String key, ArrayList<String> v) {
            bundle.putStringArrayList(key, v);
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public MessageBuilder putDataAll(PersistableBundle v) {
            bundle.putAll(v);
            return this;
        }

        public MessageBuilder putData(String key, boolean v) {
            bundle.putBoolean(key, v);
            return this;
        }

        public MessageBuilder putData(String key, boolean[] v) {
            bundle.putBooleanArray(key, v);
            return this;
        }

        public MessageBuilder putData(String key, double v) {
            bundle.putDouble(key, v);
            return this;
        }

        public MessageBuilder putData(String key, double[] v) {
            bundle.putDoubleArray(key, v);
            return this;
        }

        public MessageBuilder putData(String key, int v) {
            bundle.putInt(key, v);
            return this;
        }

        public MessageBuilder putData(String key, int[] v) {
            bundle.putIntArray(key, v);
            return this;
        }

        public MessageBuilder putData(String key, long v) {
            bundle.putLong(key, v);
            return this;
        }

        public MessageBuilder putData(String key, long[] v) {
            bundle.putLongArray(key, v);
            return this;
        }

        public MessageBuilder putData(String key, String v) {
            bundle.putString(key, v);
            return this;
        }

        public MessageBuilder putData(String key, String[] v) {
            bundle.putStringArray(key, v);
            return this;
        }
    }
}
