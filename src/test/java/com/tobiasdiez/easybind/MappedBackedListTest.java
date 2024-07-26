package com.tobiasdiez.easybind;

import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappedBackedListTest {

    @Test
    public void testMappedBackedListWithMappingOnUpdate() {
        ObservableList<IntegerProperty> list = FXCollections.observableArrayList(number -> new Observable[]{number});
        ObservableList<Integer> mappedList = EasyBind.mapBacked(list, IntegerProperty::get, true);

        IntegerProperty number = new SimpleIntegerProperty(1);
        list.add(number);

        assertEquals(1, mappedList.get(0));

        number.set(2);

        assertEquals(2, mappedList.get(0));
    }

    @Test
    public void testMappedBackedListWithoutMappingOnUpdate() {
        ObservableList<IntegerProperty> list = FXCollections.observableArrayList(number -> new Observable[]{number});
        ObservableList<Integer> mappedList = EasyBind.mapBacked(list, IntegerProperty::get, false);

        IntegerProperty number = new SimpleIntegerProperty(1);
        list.add(number);

        assertEquals(1, mappedList.get(0));

        number.set(2);

        assertEquals(1, mappedList.get(0));
    }

    @Test
    public void testUnSortedListUpdatesWithMappedBackedList() {
        ObservableList<IntegerProperty> list = FXCollections.observableArrayList(number -> new Observable[]{number});
        ObservableList<Integer> mappedList = EasyBind.mapBacked(list, IntegerProperty::get);
        SortedList<Integer> sortedList = new SortedList<>(mappedList);

        IntegerProperty num1 = new SimpleIntegerProperty(1);
        IntegerProperty num2 = new SimpleIntegerProperty(3);
        IntegerProperty num3 = new SimpleIntegerProperty(2);

        list.addAll(num1, num2, num3);

        // list= [1, 3, 2], sortedList= [1, 3, 2]
        assertEquals(List.of(1, 3, 2), sortedList);

        num2.set(4);

        // list= [1, 4, 2], sortedList= [1, 4, 2]
        assertEquals(List.of(1, 4, 2), sortedList);
    }


    @Test
    public void testSortedListUpdatesWithMappedBackedList() {
        ObservableList<IntegerProperty> list = FXCollections.observableArrayList(number -> new Observable[]{number});
        ObservableList<Integer> mappedList = EasyBind.mapBacked(list, IntegerProperty::get);
        SortedList<Integer> sortedList = new SortedList<>(mappedList, Integer::compareTo);

        IntegerProperty num1 = new SimpleIntegerProperty(1);
        IntegerProperty num2 = new SimpleIntegerProperty(3);
        IntegerProperty num3 = new SimpleIntegerProperty(2);

        list.addAll(num1, num2, num3);

        // list= [1, 3, 2], sortedList= [1, 2, 3]
        assertEquals(List.of(1, 2, 3), sortedList);

        num2.set(4);

        // list= [1, 4, 2], sortedList= [1, 2, 4]
        assertEquals(List.of(1, 2, 4), sortedList);
    }
}
