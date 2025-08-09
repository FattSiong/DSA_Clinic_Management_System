/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 *
 * @author tiong
 * @param <T> The data type of the elements to be stored in the list.
 */
public class DoublyLinkedList<T> implements ListInterface<T> {

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
