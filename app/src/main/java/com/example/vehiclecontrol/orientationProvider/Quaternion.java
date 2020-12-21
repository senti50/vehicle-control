package com.example.vehiclecontrol.orientationProvider;

public class Quaternion extends Vector4f {

    /**
     * Rotation matrix that contains the same rotation as the Quaternion in a 4x4 homogenised rotation matrix.
     * Remember that for performance reasons, this matrix is only updated, when it is accessed and not on every change
     * of the quaternion-values.
     */
    private MatrixF4x4 matrix;

    /**
     * This variable is used to synchronise the rotation matrix with the current quaternion values. If someone has
     * changed the
     * quaternion numbers then the matrix will need to be updated. To save on processing we only really want to update
     * the matrix when someone wants to fetch it, instead of whenever someone sets a quaternion value.
     */
    private boolean dirty = false;
    private Vector4f tmpVector = new Vector4f();


    /**
     * Creates a new Quaternion object and initialises it with the identity Quaternion
     */
    public Quaternion() {
        super();
        matrix = new MatrixF4x4();
        loadIdentityQuat();
    }

    /**
     * Normalise this Quaternion into a unity Quaternion.
     */
    public void normalise() {
        this.dirty = true;
        float mag = (float) Math.sqrt(points[3] * points[3] + points[0] * points[0] + points[1] * points[1] + points[2]
                * points[2]);
        points[3] = points[3] / mag;
        points[0] = points[0] / mag;
        points[1] = points[1] / mag;
        points[2] = points[2] / mag;
    }

    @Override
    public void normalize() {
        normalise();
    }

    /**
     * Copies the values from the given quaternion to this one
     *
     * @param quat The quaternion to copy from
     */
    public void set(Quaternion quat) {
        this.dirty = true;
        copyVec4(quat);
    }

    /**
     * Multiply this quaternion by the input quaternion and store the result in the out quaternion
     *
     * @param input
     * @param output
     */
    public void multiplyByQuat(Quaternion input, Quaternion output) {

        if (input != output) {
            output.points[3] = (points[3] * input.points[3] - points[0] * input.points[0] - points[1] * input.points[1] - points[2]
                    * input.points[2]); //w = w1w2 - x1x2 - y1y2 - z1z2
            output.points[0] = (points[3] * input.points[0] + points[0] * input.points[3] + points[1] * input.points[2] - points[2]
                    * input.points[1]); //x = w1x2 + x1w2 + y1z2 - z1y2
            output.points[1] = (points[3] * input.points[1] + points[1] * input.points[3] + points[2] * input.points[0] - points[0]
                    * input.points[2]); //y = w1y2 + y1w2 + z1x2 - x1z2
            output.points[2] = (points[3] * input.points[2] + points[2] * input.points[3] + points[0] * input.points[1] - points[1]
                    * input.points[0]); //z = w1z2 + z1w2 + x1y2 - y1x2
        } else {
            tmpVector.points[0] = input.points[0];
            tmpVector.points[1] = input.points[1];
            tmpVector.points[2] = input.points[2];
            tmpVector.points[3] = input.points[3];

            output.points[3] = (points[3] * tmpVector.points[3] - points[0] * tmpVector.points[0] - points[1]
                    * tmpVector.points[1] - points[2] * tmpVector.points[2]); //w = w1w2 - x1x2 - y1y2 - z1z2
            output.points[0] = (points[3] * tmpVector.points[0] + points[0] * tmpVector.points[3] + points[1]
                    * tmpVector.points[2] - points[2] * tmpVector.points[1]); //x = w1x2 + x1w2 + y1z2 - z1y2
            output.points[1] = (points[3] * tmpVector.points[1] + points[1] * tmpVector.points[3] + points[2]
                    * tmpVector.points[0] - points[0] * tmpVector.points[2]); //y = w1y2 + y1w2 + z1x2 - x1z2
            output.points[2] = (points[3] * tmpVector.points[2] + points[2] * tmpVector.points[3] + points[0]
                    * tmpVector.points[1] - points[1] * tmpVector.points[0]); //z = w1z2 + z1w2 + x1y2 - y1x2
        }
    }


    /**
     * Multiplies this Quaternion with a scalar
     *
     * @param scalar the value that the vector should be multiplied with
     */
    public void multiplyByScalar(float scalar) {
        this.dirty = true;
        multiplyByScalar(scalar);
    }

    /**
     * Sets the quaternion to an identity quaternion of 0,0,0,1.
     */
    public void loadIdentityQuat() {
        this.dirty = true;
        setX(0);
        setY(0);
        setZ(0);
        setW(1);
    }


}
