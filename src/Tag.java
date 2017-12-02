package com.multimedia.gui;

public class Tag implements java.io.Serializable {
    int Position;
    int Length;
    char NextSymbol;
    public Tag(int position, int length, char nextSymbol) {
        Position = position;
        Length = length;
        NextSymbol = nextSymbol;
    }
}
