/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 *
 * @author tiong
 * @param <T> The data type of the elements to be stored in the list.
 */
    public interface ListInterface<T extends Comparable<T>> {

    /**
     * Adds a new entry to the end of this list.
     *
     * @param newEntry The object to be added as a new entry.
     * @return True if the addition is successful, false otherwise.
     */
    public boolean add(T newEntry);

    /**
     * Adds a new entry at a specified position within this list.
     *
     * @param newPosition An integer that specifies the desired position of the new entry.
     * @param newEntry    The object to be added as a new entry.
     * @return True if the addition is successful, false otherwise.
     * @throws IndexOutOfBoundsException if newPosition < 1 or newPosition > getLength() + 1.
     */
    public boolean add(int newPosition, T newEntry);

    /**
     * Removes the entry at a given position from this list.
     *
     * @param givenPosition An integer that indicates the position of the entry to be removed.
     * @return A reference to the removed entry or null if the list is empty or the position is invalid.
     * @throws IndexOutOfBoundsException if givenPosition < 1 or givenPosition > getLength().
     */
    public T remove(int givenPosition);

    /**
     * Replaces the entry at a given position in this list.
     *
     * @param givenPosition An integer that indicates the position of the entry to be replaced.
     * @param newEntry      The object that will replace the entry at the specified position.
     * @return True if the replacement is successful, false otherwise.
     * @throws IndexOutOfBoundsException if givenPosition < 1 or givenPosition > getLength().
     */
    public boolean replace(int givenPosition, T newEntry);

    /**
     * Retrieves the entry at a given position in this list.
     *
     * @param givenPosition An integer that indicates the position of the desired entry.
     * @return A reference to the indicated entry.
     * @throws IndexOutOfBoundsException if givenPosition < 1 or givenPosition > getLength().
     */
    public T getEntry(int givenPosition);

    /**
     * Sees whether this list contains a given entry.
     *
     * @param anEntry The object that is the desired entry.
     * @return True if the list contains anEntry, or false if not.
     */
    public boolean contains(T anEntry);

    /**
     * Gets the current number of entries in this list.
     *
     * @return The integer number of entries currently in the list.
     */
    public int getLength();

    /**
     * Sees whether this list is empty.
     *
     * @return True if the list is empty, or false if not.
     */
    public boolean isEmpty();

    /**
     * Checks if the list is full. For a linked implementation, this will typically always return false.
     *
     * @return False, as a linked list is not limited by a fixed size.
     */
    public boolean isFull();

    /**
     * Removes all entries from this list.
     */
    public void clear();

    /**
     * Creates an iterator to traverse the list.
     *
     * @return An iterator that allows traversing the list's entries.
     */
    public Iterator<T> getIterator();
    
    /**
     * Performs a binary search for a given entry in the list.
     * The list must be sorted for this to work correctly.
     *
     * @param anEntry The entry to search for.
     * @return The 1-based position of the entry if found, otherwise -1.
     */
    public int binarySearch(T anEntry);
    
    /**
     * Finds the first item in the list that matches the given condition.
     *
     * @param condition The condition (a Predicate) to test each element against.
     * @return The first element that matches the condition, or null if no match is found.
     */
    public T find(Predicate<T> condition);
    
    /**
     * Sorts the elements in the list in ascending order.
     */
    public void sort();
    
    /**
     * Sorts the elements in the list in descending order.
     */
    public void sortDescending();
    
    /**
     * Sorts the elements in the list according to the order induced by the specified
     * Comparator.
     *
     * @param comparator The Comparator used to determine the order of the list.
     */
    public void sort(Comparator<T> comparator);
    
    /**
     * Sorts the elements in the list in descending order according to the order
     * induced by the specified Comparator.
     *
     * @param comparator The Comparator used to determine the order of the list.
     */
    public void sortDescending(Comparator<T> comparator);
    
    /**
     * Compares two objects for equality in a null-safe manner.
     *
     * @param a The first object.
     * @param b The second object.
     * @return True if the objects are equal or both are null, false otherwise.
     */
    public boolean customEquals(Object a, Object b);
}
