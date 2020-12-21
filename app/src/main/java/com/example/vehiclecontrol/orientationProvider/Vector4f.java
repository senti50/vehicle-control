package com.example.vehiclecontrol.orientationProvider;

/**
 * Representation of a four-dimensional float-vector
 */
public class Vector4f {

    /**
     * The points.
     */
    protected float[] points = {0, 0, 0, 0};

    /**
     * Instantiates a new vector4f.
     */
    public Vector4f() {
        this.points[0] = 0;
        this.points[1] = 0;
        this.points[2] = 0;
        this.points[3] = 0;
    }


    /**
     * To array.
     *
     * @return the float[]
     */
    public float[] array() {
        return points;
    }

    public void copyVec4(Vector4f vec) {
        this.points[0] = vec.points[0];
        this.points[1] = vec.points[1];
        this.points[2] = vec.points[2];
        this.points[3] = vec.points[3];
    }


    /**
     * Multiply by scalar.
     *
     * @param scalar the scalar
     */
    public void multiplyByScalar(float scalar) {
        this.points[0] *= scalar;
        this.points[1] *= scalar;
        this.points[2] *= scalar;
        this.points[3] *= scalar;
    }


    /**
     * Normalize.
     */
    public void normalize() {
        if (points[3] == 0)
            return;

        points[0] /= points[3];
        points[1] /= points[3];
        points[2] /= points[3];

        double a = Math.sqrt(this.points[0] * this.points[0] + this.points[1] * this.points[1] + this.points[2]
                * this.points[2]);
        points[0] = (float) (this.points[0] / a);
        points[1] = (float) (this.points[1] / a);
        points[2] = (float) (this.points[2] / a);
    }


    public float getX() {
        return this.points[0];
    }

    public float getY() {
        return this.points[1];
    }

    public float getZ() {
        return this.points[2];
    }

    public float getW() {
        return this.points[3];
    }


    public void setX(float x) {
        this.points[0] = x;
    }

    public void setY(float y) {
        this.points[1] = y;
    }


    public void setZ(float z) {
        this.points[2] = z;
    }


    public void setW(float w) {
        this.points[3] = w;
    }

    public float x() {
        return this.points[0];
    }

    public float y() {
        return this.points[1];
    }

    public float z() {
        return this.points[2];
    }

    public float w() {
        return this.points[3];
    }

    public void x(float x) {
        this.points[0] = x;
    }

    public void y(float y) {
        this.points[1] = y;
    }

    public void z(float z) {
        this.points[2] = z;
    }

    public void w(float w) {
        this.points[3] = w;
    }


}