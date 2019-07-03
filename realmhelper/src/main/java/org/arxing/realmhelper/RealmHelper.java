package org.arxing.realmhelper;

import android.content.Context;
import android.support.annotation.CheckResult;

import org.arxing.axutils_android.AssertUtils;
import org.arxing.axutils_android.Logger;
import org.arxing.axutils_android.function.Consumer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmHelper {
    private static Logger logger = new Logger("RealmHelper");
    private static RealmHelper instance;
    private static boolean initialied;
    private Realm realm;
    private Map<String, Realm> cacheRealms = new ConcurrentHashMap<>();

    public static RealmHelper getInstance() {
        if (instance == null) {
            synchronized (RealmHelper.class) {
                if (instance == null)
                    instance = new RealmHelper();
            }
        }
        return instance;
    }

    private void updateRealm() {
        realm = getOrCreateRealm();
    }

    private Realm getOrCreateRealm() {
        String threadName = Thread.currentThread().getName();
        if (!cacheRealms.containsKey(threadName)) {
            cacheRealms.put(threadName, Realm.getDefaultInstance());
        }
        return cacheRealms.get(threadName);
    }

    public static void init(Context context) {
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
        initialied = true;
    }

    public void release() {
        try {
            AssertUtils.exception(realm == null);
            AssertUtils.exception(realm.isClosed());
            realm.close();
            initialied = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Transaction beginTransaction(String tag) {
        return new Transaction(tag);
    }

    public Transaction beginTransaction() {
        return beginTransaction(null);
    }

    public void execute(Realm.Transaction transaction) {
        updateRealm();
        realm.executeTransaction(transaction);
    }

    public void executeAsync(Realm.Transaction transaction, Realm.Transaction.OnSuccess success, Realm.Transaction.OnError error) {
        updateRealm();
        realm.executeTransactionAsync(transaction, success, error);
    }

    private void commitInternal() {
        realm.commitTransaction();
    }

    public class Transaction {

        Transaction(String tag) {
            updateRealm();
            if (realm.isInTransaction()) {
                logger.w("Realm未提交(tag=%s) 將強制關閉此次交易", tag);
                realm.cancelTransaction();
            }
            realm.beginTransaction();
        }

        public void commit() {
            commitInternal();
        }

        @CheckResult public <T extends RealmModel> Result<T> createObject(Class<T> type, Object primaryValue) {
            T object = realm.createObject(type, primaryValue);
            return new Result<>(this, object);
        }

        @CheckResult public <T extends RealmModel> Result<T> createObject(Class<T> type) {
            T object = realm.createObject(type);
            return new Result<>(this, object);
        }

        @CheckResult public <T extends RealmModel> Result<T> createObjectFromJson(Class<T> type, String json) {
            T object = realm.createObjectFromJson(type, json);
            return new Result<>(this, object);
        }

        @CheckResult public <T extends RealmModel> NoResult insert(T model) {
            realm.insert(model);
            return new NoResult(this);
        }

        @CheckResult public <T extends RealmModel> NoResult insertOrUpdate(T t) {
            realm.insertOrUpdate(t);
            return new NoResult(this);
        }

        @CheckResult public <T extends RealmModel> NoResult insertAll(List<T> model) {
            realm.insert(model);
            return new NoResult(this);
        }

        @CheckResult public <T extends RealmModel> NoResult insertOrUpdate(List<T> model) {
            realm.insertOrUpdate(model);
            return new NoResult(this);
        }

        @CheckResult public <T extends RealmModel> NoResult deleteAll(Class<T> type) {
            realm.delete(type);
            return new NoResult(this);
        }

        @CheckResult public NoResult run(Runnable runnable) {
            runnable.run();
            return new NoResult(this);
        }

        public <T extends RealmModel> Query<T> where(Class<T> type) {
            RealmQuery<T> realmQuery = realm.where(type);
            return new Query<>(this, realmQuery, type);
        }
    }

    public class Query<T extends RealmModel> {
        private Transaction transaction;
        private RealmQuery<T> query;
        private Class<T> type;

        Query(Transaction transaction, RealmQuery<T> realmQuery, Class<T> type) {
            this.transaction = transaction;
            this.query = realmQuery;
            this.type = type;
        }

        /*判斷*/

        /*==*/
        public Query<T> equalTo(String field, Boolean value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, Byte value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, byte[] value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, Date value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, Double value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, Float value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, Integer value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, Long value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, Short value) {
            query = query.equalTo(field, value);
            return this;
        }

        public Query<T> equalTo(String field, String value) {
            query = query.equalTo(field, value);
            return this;
        }

        /*!=*/
        public Query<T> notEqualTo(String field, Boolean value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, Byte value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, byte[] value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, Date value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, Double value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, Float value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, Integer value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, Long value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, Short value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        public Query<T> notEqualTo(String field, String value) {
            query = query.notEqualTo(field, value);
            return this;
        }

        /*>*/
        public Query<T> greaterThan(String field, Date value) {
            query = query.greaterThan(field, value);
            return this;
        }

        public Query<T> greaterThan(String field, double value) {
            query = query.greaterThan(field, value);
            return this;
        }

        public Query<T> greaterThan(String field, float value) {
            query = query.greaterThan(field, value);
            return this;
        }

        public Query<T> greaterThan(String field, int value) {
            query = query.greaterThan(field, value);
            return this;
        }

        public Query<T> greaterThan(String field, long value) {
            query = query.greaterThan(field, value);
            return this;
        }


        /*>=*/
        public Query<T> greaterThanOrEqualTo(String field, Date value) {
            query = query.greaterThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> greaterThanOrEqualTo(String field, double value) {
            query = query.greaterThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> greaterThanOrEqualTo(String field, float value) {
            query = query.greaterThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> greaterThanOrEqualTo(String field, int value) {
            query = query.greaterThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> greaterThanOrEqualTo(String field, long value) {
            query = query.greaterThanOrEqualTo(field, value);
            return this;
        }


        /*<*/
        public Query<T> lessThan(String field, Date value) {
            query = query.lessThan(field, value);
            return this;
        }

        public Query<T> lessThan(String field, double value) {
            query = query.lessThan(field, value);
            return this;
        }

        public Query<T> lessThan(String field, float value) {
            query = query.lessThan(field, value);
            return this;
        }

        public Query<T> lessThan(String field, int value) {
            query = query.lessThan(field, value);
            return this;
        }

        public Query<T> lessThan(String field, long value) {
            query = query.lessThan(field, value);
            return this;
        }


        /*<=*/
        public Query<T> lessThanOrEqualTo(String field, Date value) {
            query = query.lessThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> lessThanOrEqualTo(String field, double value) {
            query = query.lessThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> lessThanOrEqualTo(String field, float value) {
            query = query.lessThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> lessThanOrEqualTo(String field, int value) {
            query = query.lessThanOrEqualTo(field, value);
            return this;
        }

        public Query<T> lessThanOrEqualTo(String field, long value) {
            query = query.lessThanOrEqualTo(field, value);
            return this;
        }

        /*list.size*/

        public Query<T> isEmpty(String field) {
            query = query.isEmpty(field);
            return this;
        }

        public Query<T> isNotEmpty(String field) {
            query = query.isNotEmpty(field);
            return this;
        }

        /*field==null*/

        public Query<T> isNull(String field) {
            query = query.isNull(field);
            return this;
        }

        public Query<T> isNotNull(String field) {
            query = query.isNotNull(field);
            return this;
        }

        /*between*/

        public Query<T> between(String field, Date from, Date to) {
            query = query.between(field, from, to);
            return this;
        }

        public Query<T> between(String field, double from, double to) {
            query = query.between(field, from, to);
            return this;
        }

        public Query<T> between(String field, float from, float to) {
            query = query.between(field, from, to);
            return this;
        }

        public Query<T> between(String field, int from, int to) {
            query = query.between(field, from, to);
            return this;
        }

        public Query<T> between(String field, long from, long to) {
            query = query.between(field, from, to);
            return this;
        }

        /*字串判斷*/

        public Query<T> beginWith(String field, String value) {
            query = query.beginsWith(field, value);
            return this;
        }

        public Query<T> endWith(String field, String value) {
            query = query.endsWith(field, value);
            return this;
        }

        public Query<T> contains(String field, String value) {
            query = query.contains(field, value);
            return this;
        }

        public Query<T> like(String field, String value) {
            query = query.like(field, value);
            return this;
        }


        /*排序*/

        public SortHelper sorts() {
            return new SortHelper(this);
        }

        /*連接*/

        public Query<T> and() {
            query = query.and();
            return this;
        }

        public Query<T> not() {
            query = query.not();
            return this;
        }

        public Query<T> beginGroup() {
            query = query.beginGroup();
            return this;
        }

        public Query<T> endGroup() {
            query = query.endGroup();
            return this;
        }

        /*運算*/

        public Result<Long> sum(String field) {
            Number data = query.sum(field);
            return new Result<>(transaction, data.longValue());
        }

        public Result<Double> average(String field) {
            double data = query.average(field);
            return new Result<>(transaction, data);
        }

        public Result<Number> min(String field) {
            Number data = query.min(field);
            return new Result<>(transaction, data);
        }

        public Result<Date> minDate(String field) {
            Date data = query.minimumDate(field);
            return new Result<>(transaction, data);
        }

        public Result<Number> max(String field) {
            Number data = query.max(field);
            return new Result<>(transaction, data);
        }

        public Result<Date> maxDate(String field) {
            Date data = query.maximumDate(field);
            return new Result<>(transaction, data);
        }

        public Result<Long> count() {
            long data = query.count();
            return new Result<>(transaction, data);
        }

        public Query<T> limit(long limit) {
            query = query.limit(limit);
            return this;
        }

        public Result<Boolean> exists() {
            boolean data = query.count() > 0;
            return new Result<>(transaction, data);
        }

        /*查詢*/

        /**
         * 查詢全部結果
         */
        public Results<T> findAll() {
            RealmResults<T> realmResults = query.findAll();
            return new Results<>(transaction, realmResults);
        }

        public Result<T> findFirst() {
            T data = query.findFirst();
            return new Result<>(transaction, data);
        }

        public Result<T> findFirstOrCreate() {
            T data = query.findFirst();
            if (data == null) {
                data = realm.createObject(type);
            }
            return new Result<>(transaction, data);
        }

        public Result<T> findFirstOrCreate(Object primaryValue) {
            T data = query.findFirst();
            if (data == null) {
                data = realm.createObject(type, primaryValue);
            }
            return new Result<>(transaction, data);
        }

        public class SortHelper {
            private Query<T> host;
            private List<String> fields = new ArrayList<>();
            private List<Sort> sorts = new ArrayList<>();

            SortHelper(Query<T> host) {
                this.host = host;
            }

            public SortHelper asc(String field) {
                fields.add(field);
                sorts.add(Sort.ASCENDING);
                return this;
            }

            public SortHelper desc(String field) {
                fields.add(field);
                sorts.add(Sort.DESCENDING);
                return this;
            }

            public Query<T> commitSorts() {
                host.query = host.query.sort(fields.toArray(new String[0]), sorts.toArray(new Sort[0]));
                return host;
            }
        }
    }

    private class ResultInternal {
        Transaction transaction;

        public ResultInternal(Transaction transaction) {
            this.transaction = transaction;
        }

        public Transaction then() {
            return transaction;
        }
    }

    public class Results<TResult extends RealmModel> extends ResultInternal {
        RealmResults<TResult> realmResults;

        Results(Transaction transaction, RealmResults<TResult> realmResults) {
            super(transaction);
            this.realmResults = realmResults;
        }

        public List<TResult> getAll() {
            List<TResult> data = realm.copyFromRealm(realmResults);
            return data;
        }

        public boolean deleteAll() {
            boolean success = realmResults.deleteAllFromRealm();
            return success;
        }

        public List<TResult> commitAndGetAll() {
            List<TResult> data = realm.copyFromRealm(realmResults);
            commit();
            return data;
        }

        public boolean commitAndDeleteAll() {
            boolean success = realmResults.deleteAllFromRealm();
            commit();
            return success;
        }

        public Results<TResult> config(Consumer<List<TResult>> consumer) {
            List<TResult> data = realm.copyFromRealm(realmResults);
            consumer.apply(data);
            return this;
        }

        public void commit() {
            commitInternal();
        }
    }

    public class Result<TResult> extends ResultInternal {
        TResult objResult;

        Result(Transaction transaction, TResult result) {
            super(transaction);
            this.objResult = result;
        }

        public TResult get() {
            return objResult;
        }

        public TResult commitAndGet() {
            commit();
            return objResult;
        }

        public Result<TResult> config(Consumer<TResult> consumer) {
            consumer.apply(objResult);
            return this;
        }

        public void commit() {
            commitInternal();
        }
    }

    public class NoResult extends ResultInternal {

        NoResult(Transaction transaction) {
            super(transaction);
        }

        public void commit() {
            commitInternal();
        }
    }
}
