package x.contextualtriggers.Triggers;

/**
 * Created by Sean on 17/04/2016.
 */
public interface ITaskCallback<T> {
    void executeCallback(T obj);
}
