package com.mycompany.analyzer.core.pipeline;

import com.mycompany.analyzer.core.entity.EntityCollection;
import com.mycompany.analyzer.core.entity.Persisted;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Writer responsible for writing a collection of entities given as one input from processing layer.
 * @param <E> entity to be persisted.
 * @param <T> entity collection passed to the writer
 */
public class EntityCollectionItemWriter<E extends Persisted, T extends EntityCollection<E>> implements ItemWriter<T>, ItemStream, InitializingBean {

    private ItemWriter<E> delegate;

    @Override
    public void write(final List<? extends T> collectionList) throws Exception {
        final List<E> consolidatedList = new ArrayList<>();
        for (final T collection : collectionList) {
            consolidatedList.addAll(collection.getEntities());
        }
        delegate.write(consolidatedList);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(delegate, "You must set a delegate!");
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).open(executionContext);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).update(executionContext);
        }
    }

    @Override
    public void close() {
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).close();
        }
    }

    public void setDelegate(ItemWriter<E> delegate) {
        this.delegate = delegate;
    }
}
