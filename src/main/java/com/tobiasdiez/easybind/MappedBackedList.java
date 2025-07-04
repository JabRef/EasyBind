package com.tobiasdiez.easybind;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

class MappedBackedList<E, F> extends TransformationList<E, F> implements EasyObservableList<E> {

    private final Function<F, E> mapper;
    private final List<E> backingList;
    private final boolean mapOnUpdate;

    public MappedBackedList(ObservableList<? extends F> sourceList, Function<F, E> mapper, boolean mapOnUpdate) {
        super(sourceList);
        this.mapper = mapper;
        this.mapOnUpdate = mapOnUpdate;
        this.backingList = new ArrayList<>(sourceList.size());
        sourceList.stream().map(mapper).forEach(backingList::add);
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends F> change) {
        beginChange();
        while (change.next()) {
            if (change.wasPermutated()) {
                int from = change.getFrom();
                int to = change.getTo();

                // get permutation array
                int[] permutation = new int[to - from];
                for (int i = 0; i < to - from; i++) {
                    permutation[i] = change.getPermutation(i);
                }

                // perform permutation
                Object[] permutedPart = new Object[to - from];
                for (int i = from; i < to; i++) {
                    permutedPart[permutation[i]] = backingList.get(i);
                }

                // update backingList
                for (int i = 0; i < to; i++) {
                    backingList.set(i + from, (E) permutedPart[i]);
                }
                nextPermutation(from, to, permutation);
            } else if (change.wasUpdated()) {
                if (mapOnUpdate) {
                    for (int i = change.getFrom(); i < change.getTo(); i++) {
                        E old = backingList.set(i, mapper.apply(getSource().get(i)));
                        nextSet(i, old);
                    }
                } else {
                    for (int i = change.getFrom(); i < change.getTo(); i++) {
                        nextUpdate(i);
                    }
                }
            } else {
                if (change.wasRemoved()) {
                    int removePosition = change.getFrom();
                    List<E> removed = new ArrayList<>(change.getRemovedSize());
                    for (int i = 0; i < change.getRemovedSize(); i++) {
                        removed.add(backingList.remove(removePosition));
                    }
                    nextRemove(change.getFrom(), removed);
                }
                if (change.wasAdded()) {
                    int addStart = change.getFrom();
                    int addEnd = change.getTo();
                    for (int i = addStart; i < addEnd; i++) {
                        backingList.add(i, mapper.apply(change.getList().get(i)));
                    }
                    nextAdd(addStart, addEnd);
                }
            }
        }
        endChange();
    }

    @Override
    public int getSourceIndex(int index) {
        return index;
    }

    @Override
    public int getViewIndex(int index) {
        return index;
    }

    @Override
    public E get(int index) {
        return backingList.get(index);
    }

    @Override
    public int size() {
        return backingList.size();
    }
}
