/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int width;
    private int height;
    private int[][] pixels;


    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();

        width = picture.width();
        height = picture.height();
        pixels = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i][j] = picture.getRGB(j, i);
            }
        }
    }


    public Picture picture() {
        Picture pic = new Picture(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pic.setRGB(j, i, pixels[i][j]);
            }
        }
        return pic;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double energy(int x, int y) {
        if (x < 0 || x > width) throw new IllegalArgumentException();
        if (y < 0 || y > height) throw new IllegalArgumentException();

        if (x == 0 || y == 0) return 1000;
        else if (x == width - 1 || y == height - 1) return 1000;
        else {
            double deltaX2 = findDeltaX(x, y);
            double deltaY2 = findDeltaY(x, y);
            return Math.sqrt(deltaX2 + deltaY2);
        }
    }

    private double findDeltaY(int x, int y) {
        int r1 = (pixels[y - 1][x] >> 16) & 0xFF;
        int g1 = (pixels[y - 1][x] >> 8) & 0xFF;
        int b1 = (pixels[y - 1][x] >> 0) & 0xFF;

        int r2 = (pixels[y + 1][x] >> 16) & 0xFF;
        int g2 = (pixels[y + 1][x] >> 8) & 0xFF;
        int b2 = (pixels[y + 1][x] >> 0) & 0xFF;

        return Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2);
    }

    private double findDeltaX(int x, int y) {
        int r1 = (pixels[y][x - 1] >> 16) & 0xFF;
        int g1 = (pixels[y][x - 1] >> 8) & 0xFF;
        int b1 = (pixels[y][x - 1] >> 0) & 0xFF;

        int r2 = (pixels[y][x + 1] >> 16) & 0xFF;
        int g2 = (pixels[y][x + 1] >> 8) & 0xFF;
        int b2 = (pixels[y][x + 1] >> 0) & 0xFF;

        return Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2);
    }

    private void tranpose() {
        int[][] tranpose = new int[width][height];
        for (int i = 0; i < tranpose.length; i++) {
            for (int j = 0; j < tranpose[0].length; j++) {
                tranpose[i][j] = pixels[j][i];
            }
        }

        pixels = null;
        pixels = new int[width][height];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                pixels[i][j] = tranpose[i][j];
            }
        }

        int temp = width;
        width = height;
        height = temp;
    }

    public int[] findHorizontalSeam() {
        tranpose();

        int[] path = findVerticalSeam();

        tranpose();
        return path;
    }

    public int[] findVerticalSeam() {

        double[][] energy = new double[height][width];
        double[][] distanceTo = new double[height][width];
        int[][] edgeTo = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i > 0)
                    distanceTo[i][j] = Double.POSITIVE_INFINITY;
                energy[i][j] = energy(j, i);
            }
        }

        for (int i = 0; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                double currentEn = distanceTo[i][j];
                double enToLeft = currentEn + energy[i + 1][j - 1];
                double enToMid = currentEn + energy[i + 1][j];
                double enToRight = currentEn + energy[i + 1][j + 1];

                if (j != 1 && enToLeft < distanceTo[i + 1][j - 1]) {
                    edgeTo[i + 1][j - 1] = j;
                    distanceTo[i + 1][j - 1] = enToLeft;
                }
                if (enToMid < distanceTo[i + 1][j]) {
                    edgeTo[i + 1][j] = j;
                    distanceTo[i + 1][j] = enToMid;
                }
                if (j != width - 2 && enToRight < distanceTo[i + 1][j + 1]) {
                    edgeTo[i + 1][j + 1] = j;
                    distanceTo[i + 1][j + 1] = enToRight;
                }
            }
        }

        double minEn = Double.POSITIVE_INFINITY;
        int pos = 0;

        for (int j = 1; j < width - 1; j++) {
            if (distanceTo[height - 1][j] < minEn) {
                minEn = distanceTo[height - 1][j];
                pos = j;
            }
        }

        int[] path = new int[height];

        for (int i = height - 1; i >= 0; i--) {
            path[i] = pos;
            pos = edgeTo[i][pos];
        }

        return path;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != width) throw new IllegalArgumentException();
        if (width <= 1) throw new IllegalArgumentException();

        tranpose();
        removeVerticalSeam(seam);
        tranpose();

    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != height) throw new IllegalArgumentException();
        if (height <= 1) throw new IllegalArgumentException();


        for (int i = 0; i < height; i++) {
            System.arraycopy(pixels[i], seam[i] + 1, pixels[i], seam[i],
                             pixels[i].length - (seam[i] + 1));
        }
        width--;
    }

    public static void main(String[] args) {

    }
}
