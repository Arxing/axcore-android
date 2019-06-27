package org.arxing.apiconnector;

import android.util.Pair;

import com.annimon.stream.Stream;

import org.arxing.utils.AssertUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;


public class FormRequestBody extends RequestBody {
    private Map<String, List<String>> map = new HashMap<>();

    public void addParam(String key, String value) {
        addParam(key, Collections.singletonList(value));
    }

    public void addParam(String key, List<String> values) {
        if (!map.containsKey(key))
            map.put(key, new ArrayList<>());
        map.get(key).addAll(values);
    }

    public Observable<Pair<String, String>> getParamsObservable() {
        return Observable.create(emitter -> Stream.of(map.entrySet()).forEach(entry -> {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            AssertUtils.error(values.size() == 0, new AssertionError("Value.size must > 0."));
            if (values.size() == 1)
                emitter.onNext(new Pair<>(key, values.get(0)));
            else {
                Stream.of(values).map(value -> new Pair<>(key + "[" + values.indexOf(value) + "]", value)).forEach(emitter::onNext);
            }
        }));
    }
}
