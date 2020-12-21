package com.example.vehiclecontrol.orientationProvider;

import android.opengl.Matrix;

public class MatrixF4x4 {
    public float[] matrix;

    /**
     * Instantiates a new matrixf4x4. The Matrix is assumed to be Column major, however you can change this by using the
     * setColumnMajor function to false and it will operate like a row major matrix.
     */
    public MatrixF4x4() {
        // The matrix is defined as float[column][row]
        this.matrix = new float[16];
        Matrix.setIdentityM(this.matrix, 0);
    }

}
