package com.jjlf.jjkit_utils.utils;


public class PathElement {
    ElementType type;
    Point[] points;
    public PathElement(ElementType type, Point[] points) {
        this.type = type;
        this.points = points;
    }
}