package com.example.projetjavafx.root.mesg.client;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;

        // Set default fill
        setFill(Color.GRAY);
    }


}

