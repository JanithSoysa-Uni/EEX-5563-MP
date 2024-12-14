/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.miniproject5563.buddysystem;

/**
 *
 * @author Janith Srimal
 */
import java.util.ArrayList;
import java.util.Scanner;

public class BuddySystem {
    private final int totalMemory;
    private final ArrayList<MemoryBlock> memory;

    public BuddySystem(int totalMemory) {
        this.totalMemory = totalMemory;
        memory = new ArrayList<>();
        // Start with one free block of totalMemory size
        memory.add(new MemoryBlock(0, totalMemory, true, 0));
    }

    // Represents a memory block
    static class MemoryBlock {
        int start;          // Starting address
        int size;           // Block size
        boolean isFree;     // true if the block is free, false if allocated
        int fragmentation;  // Internal fragmentation for allocated blocks

        MemoryBlock(int start, int size, boolean isFree, int fragmentation) {
            this.start = start;
            this.size = size;
            this.isFree = isFree;
            this.fragmentation = fragmentation;
        }
    }

    // Allocates memory for a given size
    public void allocate(int size) {
        int requestedSize = size;
        size = roundUpToPowerOfTwo(size); // Buddy system allocates power-of-2 blocks

        for (MemoryBlock block : memory) {
            if (block.isFree && block.size >= size) {
                // Split the block until we get a suitable size
                while (block.size > size) {
                    split(block);
                }
                block.isFree = false;
                block.fragmentation = block.size - requestedSize;
                System.out.println("Allocated " + requestedSize + " KB at address: " + block.start);
                displayMemoryState();
                return;
            }
        }
        System.out.println("Allocation failed: Not enough memory");
    }

    // Deallocates memory from a given starting address
    public void deallocate(int address) {
        for (MemoryBlock block : memory) {
            if (block.start == address && !block.isFree) {
                block.isFree = true;
                block.fragmentation = 0;
                System.out.println("Deallocated block at address: " + address);
                merge();
                displayMemoryState();
                return;
            }
        }
        System.out.println("Deallocation failed: Invalid address");
    }

    // Splits a larger block into two smaller buddies
    private void split(MemoryBlock block) {
        int newSize = block.size / 2;
        MemoryBlock buddy = new MemoryBlock(block.start + newSize, newSize, true, 0);
        block.size = newSize;
        memory.add(buddy);
        System.out.println("Split block of size " + (newSize * 2) + " KB into two buddies of size " + newSize + " KB");
    }

    // Merges free buddy blocks
    private void merge() {
        memory.sort((a, b) -> Integer.compare(a.start, b.start)); // Sort blocks by start address
        for (int i = 0; i < memory.size() - 1; i++) {
            MemoryBlock current = memory.get(i);
            MemoryBlock next = memory.get(i + 1);

            if (current.isFree && next.isFree && current.size == next.size &&
                    current.start + current.size == next.start) {
                current.size *= 2;
                memory.remove(next);
                System.out.println("Merged two buddies into a block of size " + current.size + " KB");
                i--; // Recheck the merged block
            }
        }
    }

    // Rounds up a number to the nearest power of 2
    private int roundUpToPowerOfTwo(int size) {
        int power = 1;
        while (power < size) {
            power *= 2;
        }
        return power;
    }

    // Displays the memory state with allocated and free blocks
    public void displayMemoryState() {
        System.out.println("Memory State:");
        for (MemoryBlock block : memory) {
            if (block.isFree) {
                // Display free blocks without the start address
                System.out.println("Block: " + block.size + " KB | Free");
            } else {
                // Display allocated blocks with internal fragmentation and start address
                System.out.println("Block: " + block.size + " KB | Allocated | Internal Fragmentation: " + block.fragmentation + " KB | Address: " + block.start);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BuddySystem buddySystem = new BuddySystem(1024); // Initialize with 1024 KB of memory

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Allocate Memory");
            System.out.println("2. Deallocate Memory");
            System.out.println("3. Display Memory State");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter memory size to allocate (in KB): ");
                    int size = scanner.nextInt();
                    buddySystem.allocate(size);
                    break;
                case 2:
                    System.out.print("Enter starting address to deallocate (Memory Address): ");
                    int address = scanner.nextInt();
                    buddySystem.deallocate(address);
                    break;
                case 3:
                    buddySystem.displayMemoryState();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}