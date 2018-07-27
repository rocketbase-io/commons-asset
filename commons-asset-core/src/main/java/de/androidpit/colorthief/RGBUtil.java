/*
 * Java Color Thief
 * by Sven Woltmann, Fonpit AG
 *
 * http://www.androidpit.com
 * http://www.androidpit.de
 *
 * License
 * -------
 * Creative Commons Attribution 2.5 License:
 * http://creativecommons.org/licenses/by/2.5/
 *
 * Thanks
 * ------
 * Lokesh Dhakar - for the original Color Thief JavaScript version
 * available at http://lokeshdhakar.com/projects/color-thief/
 */

package de.androidpit.colorthief;

public class RGBUtil {

    /**
     * Packs the RGB array into one int for storing into db or sending via network.
     *
     * @param rgb array containing red, green and blue values of the color to encode
     * @return packed into int RGB value
     * @throws IllegalArgumentException if rgb has not exactly 3 elements
     */
    public static int packRGB(int[] rgb) {
        if (rgb.length != 3) {
            throw new IllegalArgumentException("RGB array should contain exactly 3 values.");
        }
        return rgb[0] << 16 | rgb[1] << 8 | rgb[2];
    }

    /**
     * Unpacks RGB array from the given int, e.g. received from calling {@link #packRGB(int[])}).
     *
     * @param packedRgb value containing RGB
     * @return array containing red, green and blue values of the color
     */
    public static int[] unpackRGB(int packedRgb) {
        int[] rgb = new int[3];
        rgb[0] = packedRgb >> 16 & 0xFF;
        rgb[1] = packedRgb >> 8 & 0xFF;
        rgb[2] = packedRgb & 0xFF;
        return rgb;
    }

    /**
     * @param rgbArray the array of arrays containing unpacked RGB values
     * @return the array of packed RGB values
     * @throws IllegalArgumentException if at least one of rgb has not exactly 3 elements
     */
    public static int[] packRGBArray(int[][] rgbArray) {
        int[] packedArray = new int[rgbArray.length];
        for (int n = 0; n < rgbArray.length; ++n) {
            packedArray[n] = packRGB(rgbArray[n]);
        }
        return packedArray;
    }

    /**
     * @param packedRgbArray the array of packed RGB values
     * @return the array of arrays containing unpacked RGB values
     */
    public static int[][] unpackRGBArray(int[] packedRgbArray) {
        int[][] rgbArray = new int[packedRgbArray.length][3];
        for (int n = 0; n < packedRgbArray.length; ++n) {
            rgbArray[n] = unpackRGB(packedRgbArray[n]);
        }
        return rgbArray;
    }


    /**
     * Creates a string representation of an RGB array.
     *
     * @param rgb
     *            the RGB array
     *
     * @return the string representation
     */
    private static String createRGBString(int[] rgb) {
        return "rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")";
    }

    /**
     * Creates an HTML hex color code for the given RGB array (e.g. <code>#ff0000</code> for red).
     *
     * @param rgb
     *            the RGB array
     *
     * @return the HTML hex color code
     */
    private static String createRGBHexString(int[] rgb) {
        String rgbHex = Integer.toHexString(rgb[0] << 16 | rgb[1] << 8 | rgb[2]);

        // Left-pad with 0s
        int length = rgbHex.length();
        if (length < 6) {
            rgbHex = "00000".substring(0, 6 - length) + rgbHex;
        }

        return "#" + rgbHex;
    }


}