/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.math;

import java.util.Collection;
import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MathUtil {
    
    public static <T extends Weighted> T getWeightedRandom(final Random random, final T[] ts) {
        int sum = 0;
        for (final T t : ts) {
            sum += t.getWeight();
        }
        int rand = random.nextInt(sum) + 1;
        for (T t : ts) {
            if (rand <= t.getWeight()) {
                return t;
            }
            rand -= t.getWeight();
        }
        return null;
    }
    
    public static <T extends Weighted> T getWeightedRandom(final Random random, final Collection<T> ts) {
        int sum = 0;
        for (final T t : ts) {
            sum += t.getWeight();
        }
        int rand = random.nextInt(sum) + 1;
        for (T t : ts) {
            if (rand <= t.getWeight()) {
                return t;
            }
            rand -= t.getWeight();
        }
        return null;
    }
    
    public static <T> T getWeightedRandom(final Random random, final T[] ts, final int[] weights) {
        int sum = 0;
        for (final int i : weights) {
            sum += i;
        }
        int rand = random.nextInt(sum) + 1;
        for (int i = 0; i < ts.length; i++) {
            if (rand <= weights[i]) {
                return ts[i];
            }
            rand -= weights[i];
        }
        return ts[0];
    }
    
    public static int getWeightedRandom(final Random random, final int[] ts, final int[] weights) {
        int sum = 0;
        for (final int i : weights) {
            sum += i;
        }
        int rand = random.nextInt(sum) + 1;
        for (int i = 0; i < ts.length; i++) {
            if (rand <= weights[i]) {
                return ts[i];
            }
            rand -= weights[i];
        }
        return ts[0];
    }
    
    public static Vector2 randomDirection2D(final Random random, final float begin, final float end, Vector2 target) {
        if (target == null) {
            target = new Vector2();
        }
        final float rand = begin + (end - begin) * random.nextFloat();
        target.set(MathUtils.cos(rand), MathUtils.sin(rand));
        return target;
    }
    
    public static int toPowerOfTwo(final int n) {
        return 1 << (32 - Integer.numberOfLeadingZeros(n - 1));
    }
    
    public static boolean isPowerOfTwo(final int n) {
        return (n & -n) == n;
    }
    
    public static boolean containsBit(byte bits, byte bit) {
        return (bits & bit) == bit;
    }
    
    /**
     * 
     * Test whether the given ray with the origin
     * <code>(originX, originY, originZ)</code> and direction
     * <code>(dirX, dirY, dirZ)</code> intersects the axis-aligned box given as its
     * minimum corner <code>(minX, minY, minZ)</code> and maximum corner
     * <code>(maxX, maxY, maxZ)</code>, and return the values of the parameter
     * <i>t</i> in the ray equation <i>p(t) = origin + t * dir</i> of the near and
     * far point of intersection.
     * <p>
     * This method returns <code>true</code> for a ray whose origin lies inside the
     * axis-aligned box.
     * <p>
     * If many boxes need to be tested against the same ray, then the
     * {@link RayAabIntersection} class is likely more efficient.
     * <p>
     * Reference: <a href="https://dl.acm.org/citation.cfm?id=1198748">An Efficient
     * and Robust Ray–Box Intersection</a>
     * 
     * @see #intersectRayAab(Vector3fc, Vector3fc, Vector3fc, Vector3fc, Vector2f)
     * @see RayAabIntersection
     * @author Kai Burjack (org.joml)
     * @param originX the x coordinate of the ray's origin
     * @param originY the y coordinate of the ray's origin
     * @param originZ the z coordinate of the ray's origin
     * @param dirX    the x coordinate of the ray's direction
     * @param dirY    the y coordinate of the ray's direction
     * @param dirZ    the z coordinate of the ray's direction
     * @param minX    the x coordinate of the minimum corner of the axis-aligned box
     * @param minY    the y coordinate of the minimum corner of the axis-aligned box
     * @param minZ    the z coordinate of the minimum corner of the axis-aligned box
     * @param maxX    the x coordinate of the maximum corner of the axis-aligned box
     * @param maxY    the y coordinate of the maximum corner of the axis-aligned box
     * @param maxZ    the y coordinate of the maximum corner of the axis-aligned box
     * @param result  a vector which will hold the resulting values of the parameter
     *                <i>t</i> in the ray equation <i>p(t) = origin + t * dir</i> of
     *                the near and far point of intersection iff the ray intersects
     *                the axis-aligned box
     * @return <code>true</code> if the given ray intersects the axis-aligned box;
     *         <code>false</code> otherwise
     */
    public static boolean intersectRayAab(float originX, float originY, float originZ, float dirX, float dirY,
            float dirZ, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vector2 result) {
        float invDirX = 1.0f / dirX, invDirY = 1.0f / dirY, invDirZ = 1.0f / dirZ;
        float tNear, tFar, tymin, tymax, tzmin, tzmax;
        if (invDirX >= 0.0f) {
            tNear = (minX - originX) * invDirX;
            tFar = (maxX - originX) * invDirX;
        } else {
            tNear = (maxX - originX) * invDirX;
            tFar = (minX - originX) * invDirX;
        }
        if (invDirY >= 0.0f) {
            tymin = (minY - originY) * invDirY;
            tymax = (maxY - originY) * invDirY;
        } else {
            tymin = (maxY - originY) * invDirY;
            tymax = (minY - originY) * invDirY;
        }
        if (tNear > tymax || tymin > tFar)
            return false;
        if (invDirZ >= 0.0f) {
            tzmin = (minZ - originZ) * invDirZ;
            tzmax = (maxZ - originZ) * invDirZ;
        } else {
            tzmin = (maxZ - originZ) * invDirZ;
            tzmax = (minZ - originZ) * invDirZ;
        }
        if (tNear > tzmax || tzmin > tFar)
            return false;
        tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
        tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
        tNear = tzmin > tNear ? tzmin : tNear;
        tFar = tzmax < tFar ? tzmax : tFar;
        if (tNear < tFar && tFar >= 0.0f) {
            result.x = tNear;
            result.y = tFar;
            return true;
        }
        return false;
    }
    
}
