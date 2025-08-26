/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;

/**
 *
 * @author tiong
 * @param <T> The data type of the elements to be stored in the list.
 */
public class DoublyLinkedList<T extends Comparable<T>> implements ListInterface<T> {

    private Node firstNode; // Reference to the first node
    private Node lastNode;  // Reference to the last node
    private int numberOfEntries;

    public DoublyLinkedList() {
        clear();
    }

    @Override
    public final void clear() {
        firstNode = null;
        lastNode = null;
        numberOfEntries = 0;
    }

    @Override
    public boolean add(T newEntry) {
        Node newNode = new Node(newEntry);

        if (isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.next = newNode;
            newNode.previous = lastNode;
        }
        lastNode = newNode;
        numberOfEntries++;
        return true;
    }

    @Override
    public boolean add(int newPosition, T newEntry) {
        if (newPosition >= 1 && newPosition <= numberOfEntries + 1) {
            Node newNode = new Node(newEntry);
            if (newPosition == 1) { // Case 1: Add at the beginning
                newNode.next = firstNode;
                if (firstNode != null) {
                    firstNode.previous = newNode;
                } else {
                    lastNode = newNode; // when list was empty
                }
                firstNode = newNode;
            } else if (newPosition == numberOfEntries + 1) { // Case 2: Add at the end
                newNode.previous = lastNode;
                lastNode.next = newNode;
                lastNode = newNode;
            } else { // Case 3: Add in the middle
                Node nodeBefore = getNodeAt(newPosition - 1);
                Node nodeAfter = nodeBefore.next;
                newNode.next = nodeAfter;
                newNode.previous = nodeBefore;
                nodeBefore.next = newNode;
                nodeAfter.previous = newNode;
            }
            numberOfEntries++;
            return true;
        } else {
            throw new IndexOutOfBoundsException("Illegal position given to add operation.");
        }
    }

    @Override
    public T remove(int givenPosition) {
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            T removedData;
            if (givenPosition == 1) { // Case 1: Remove first entry
                removedData = firstNode.data;
                firstNode = firstNode.next;
                if (firstNode != null) {
                    firstNode.previous = null;
                } else {
                    lastNode = null; // List is now empty
                }
            } else if (givenPosition == numberOfEntries) { // Case 2: Remove last entry
                removedData = lastNode.data;
                lastNode = lastNode.previous;
                lastNode.next = null;
            } else { // Case 3: Remove from middle
                Node nodeToRemove = getNodeAt(givenPosition);
                Node nodeBefore = nodeToRemove.previous;
                Node nodeAfter = nodeToRemove.next;
                nodeBefore.next = nodeAfter;
                nodeAfter.previous = nodeBefore;
                removedData = nodeToRemove.data;
            }
            numberOfEntries--;
            return removedData;
        } else {
            throw new IndexOutOfBoundsException("Illegal position given to remove operation.");
        }
    }

    @Override
    public boolean replace(int givenPosition, T newEntry) {
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            Node desiredNode = getNodeAt(givenPosition);
            desiredNode.data = newEntry;
            return true;
        } else {
            throw new IndexOutOfBoundsException("Illegal position given to replace operation.");
        }
    }

    @Override
    public T getEntry(int givenPosition) {
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            return getNodeAt(givenPosition).data;
        } else {
            throw new IndexOutOfBoundsException("Illegal position given to getEntry operation.");
        }
    }

    @Override
    public boolean contains(T anEntry) {
        boolean found = false;
        Node currentNode = firstNode;
        while (!found && (currentNode != null)) {
            if (anEntry.equals(currentNode.data)) {
                found = true;
            } else {
                currentNode = currentNode.next;
            }
        }
        return found;
    }

    @Override
    public int getLength() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public boolean isFull() {
        return false; // A linked list is never full
    }

    @Override
    public Iterator<T> getIterator() {
        return new DoublyLinkedListIterator();
    }

    @Override
    public int binarySearch(T anEntry) {
        int low = 1; // Use 1-based indexing to match getEntry
        int high = numberOfEntries;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            // Accessing the middle element by traversing the list (inefficient but necessary)
            T midEntry = getEntry(mid);

            int comparison = anEntry.compareTo(midEntry);

            if (comparison == 0) {
                // Entry found, return the 1-based position
                return mid;
            } else if (comparison < 0) {
                // Entry is in the lower half
                high = mid - 1;
            } else {
                // Entry is in the upper half
                low = mid + 1;
            }
        }

        // Entry not found
        return -1;
    }

    /**
     * Sorts the list using the insertion sort algorithm. This implementation is
     * suitable for a linked list as it works by re-linking nodes.
     */
    @Override
    public void sort() {
        if (numberOfEntries <= 1) {
            return; // Already sorted
        }

        Node sortedList = null; // This will be the head of the new sorted list
        Node current = firstNode;

        while (current != null) {
            Node next = current.next; // Save the next node to process

            // Disconnect current node from its previous link for insertion
            current.previous = null;

            // Insert 'current' into the 'sortedList'
            if (sortedList == null || current.data.compareTo(sortedList.data) <= 0) {
                // Insert at the beginning of the sorted list
                current.next = sortedList;
                if (sortedList != null) {
                    sortedList.previous = current;
                }
                sortedList = current;
            } else {
                // Find the correct position to insert in the sorted list
                Node temp = sortedList;
                while (temp.next != null && temp.next.data.compareTo(current.data) < 0) {
                    temp = temp.next;
                }
                // Insert 'current' after 'temp'
                current.next = temp.next;
                if (temp.next != null) {
                    temp.next.previous = current;
                }
                temp.next = current;
                current.previous = temp;
            }
            current = next; // Move to the next node in the original list
        }

        // Update the firstNode to the new sorted list's head
        firstNode = sortedList;

        // Update the lastNode by traversing to the end of the new sorted list
        if (firstNode == null) {
            lastNode = null;
        } else {
            Node temp = firstNode;
            while (temp.next != null) {
                temp = temp.next;
            }
            lastNode = temp;
        }
    }

    /**
     * Sorts the list using the insertion sort algorithm based on a provided
     * Comparator.
     *
     * @param comparator The Comparator that defines the sorting logic.
     */
    @Override
    public void sort(Comparator<T> comparator) {
        if (numberOfEntries <= 1) {
            return; // Already sorted
        }

        Node sortedList = null;
        Node current = firstNode;

        while (current != null) {
            Node next = current.next;
            current.previous = null;

            // The only difference is here: using comparator.compare() instead of compareTo()
            if (sortedList == null || comparator.compare(current.data, sortedList.data) <= 0) {
                current.next = sortedList;
                if (sortedList != null) {
                    sortedList.previous = current;
                }
                sortedList = current;
            } else {
                Node temp = sortedList;
                while (temp.next != null && comparator.compare(temp.next.data, current.data) < 0) {
                    temp = temp.next;
                }
                current.next = temp.next;
                if (temp.next != null) {
                    temp.next.previous = current;
                }
                temp.next = current;
                current.previous = temp;
            }
            current = next;
        }
        firstNode = sortedList;
        updateLastNode(); // Helper method to find and set the lastNode
    }

    private void updateLastNode() {
        if (firstNode == null) {
            lastNode = null;
        } else {
            Node temp = firstNode;
            while (temp.next != null) {
                temp = temp.next;
            }
            lastNode = temp;
        }
    }

    private Node getNodeAt(int givenPosition) {
        Node currentNode = firstNode;
        // Traverse the chain to locate the desired node
        for (int counter = 1; counter < givenPosition; counter++) {
            currentNode = currentNode.next;
        }
        return currentNode;
    }

    private class DoublyLinkedListIterator implements Iterator<T> {

        private Node nextNode;

        private DoublyLinkedListIterator() {
            nextNode = firstNode;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public T next() {
            if (hasNext()) {
                Node returnNode = nextNode;
                nextNode = nextNode.next;
                return returnNode.data;
            } else {
                throw new NoSuchElementException("Illegal call to next(); iterator is after end of list.");
            }
        }
    }

    private class Node {

        private T data;
        private Node next;
        private Node previous;

        private Node(T data) {
            this(data, null, null);
        }

        private Node(T data, Node nextNode, Node previousNode) {
            this.data = data;
            next = nextNode;
            previous = previousNode;
        }
    }
}
