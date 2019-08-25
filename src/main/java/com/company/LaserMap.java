package com.company;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LaserMap {
    int rows;
    int cols;
    ArrayList<int[]> mirrors;
    ArrayList<int[]> obstructions;
    String difficulty;
    int[] source;
    Laser.Dir sourceDir;
    int[] target;
    Laser.Dir targetDir;

    public void printMap() {
    }

    enum PositionType{
        MIRROR,
        OBSTRUCT,
        BOUND,
        TARGET,
        UNKNOWN
    }

    static LaserMap initMap(String file) throws IOException {
        StringBuilder json = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("C:\\Mycode\\Java\\LaserPath\\jsonfile\\" + file));
        String line = "";
        while ((line = br.readLine()) != null) {
            json.append(line);
        }
        br.close();
        LaserMap mapFromFile =  new Gson().fromJson(json.toString(), LaserMap.class);
        mapFromFile.deleteInvalidPoint();
        return mapFromFile;
    }

    private void deleteInvalidPoint(){
        for(int[] point : mirrors){
            if (!positionInMap(point)) {
                System.out.println("Found Invalid mirror, delete it [" + point[0] + "," + point[1] + "]");
                mirrors.remove(point);
            }
        }
        for(int[] point : obstructions){
            if (!positionInMap(point)) {
                System.out.println("Found Invalid mirror, delete it [" + point[0] + "," + point[1] + "]");
                obstructions.remove(point);
            }
        }
    }

    boolean setSourceAndTarget(int[] so, Laser.Dir soDir, int[] ta, Laser.Dir taDir){
        if(!validEnterMap(so, soDir) || !validOutMap(ta, taDir))
            return false;
        source = so;
        sourceDir = soDir;
        target = ta;
        targetDir = taDir;
        return true;
    }

    int[] getSource(){
        return source;
    }

    Laser.Dir getSourceDir(){
        return sourceDir;
    }

    int[] getTarget(){
        return target;
    }

    Laser.Dir getTargetDir(){
        return targetDir;
    }

    PositionType getNextPosition(int[] po, Laser.Dir dir, int[] next_po)throws Exception{
        PositionType type;
        int[] nextMirror = getNextMirror(po, dir);
        int[] nextObstruct = getNextObstruct(po, dir);

        if(nextMirror == null && nextObstruct == null){
            if(sameLineWithTarget(po, dir)){
                type = PositionType.TARGET;
                next_po[0] = target[0];
                next_po[1] = target[1];
            }
            else{
                type = PositionType.BOUND;
            }
        }
        else if(nextMirror != null && nextObstruct == null){
            type = PositionType.MIRROR;
            next_po[0] = nextMirror[0];
            next_po[1] = nextMirror[1];
        }
        else if(nextMirror == null && nextObstruct != null){
            type = PositionType.OBSTRUCT;
        }
        else {
            if(nextMirror == nextObstruct)
                throw new Exception("Invalid map! Mirror and Obstruction has the same position");
            else if(firstEncounterMirror(po, dir, nextMirror, nextObstruct)){
                type = PositionType.MIRROR;
                next_po[0] = nextMirror[0];
                next_po[1] = nextMirror[1];
            }
            else
                type = PositionType.OBSTRUCT;
        }
        return type;
    }

    static int getDistance(int[] po, int[] next_po) {
        if(po[0] == next_po[0])
            return Math.abs(po[1] - next_po[1]);
        else if(po[1] == next_po[1])
            return Math.abs(po[0] - next_po[0]);
        else{
            System.out.println("not support this laser path");
        }
        return 0;
    }


    private boolean positionInMap(int[] po){
        return (po[1] >= 0 && po[1] <= cols) && (po[0] >= 0 && po[0] <= rows);
    }

    private boolean validOutMap(int[] po, Laser.Dir dir){
        if(positionInMap(po)) {
            switch (dir) {
                case UP:
                    return po[0] == 0;
                case DOWN:
                    return po[0] == rows;
                case RIGHT:
                    return po[1] == cols;
                case LEFT:
                    return po[1] == 0;
            }
        }
        return false;
    }

    private boolean validEnterMap(int[] po, Laser.Dir dir){
        if(positionInMap(po)) {
            switch (dir) {
                case UP:
                    return po[0] == rows;
                case DOWN:
                    return po[0] == 0;
                case RIGHT:
                    return po[1] == 0;
                case LEFT:
                    return po[1] == cols;
            }
        }
        return false;
    }

    private boolean firstEncounterMirror(int[] po, Laser.Dir dir, int[] mp, int[] op) {
        switch (dir) {
            case UP:
                return mp[0] > op[0];
            case DOWN:
                return op[0] > mp[0];
            case RIGHT:
                return op[1] > mp[1];
            case LEFT:
                return mp[1] > op[1];
        }
        return false;
    }

    private boolean sameLineWithTarget(int[] po, Laser.Dir dir) {
        if(dir != targetDir)
            return false;
        switch(dir){
            case UP:
                if(po[1] == target[1] && po[0] >= target[0])
                    return true;
                break;
            case DOWN:
                if(po[1] == target[1] && po[0] <= target[0])
                    return true;
                break;
            case RIGHT:
                if(po[0] == target[0] && po[1] <= target[1])
                    return true;
                break;
            case LEFT:
                if(po[0] == target[0] && po[1] >= target[1])
                    return true;
                break;
        }
        return false;
    }

    private int[] getNextObstruct(int[] po, Laser.Dir dir) {
        return getSameLinePosition(po, dir, obstructions);
    }

    private int[] getNextMirror(int[] po, Laser.Dir dir){
        return getSameLinePosition(po, dir, mirrors);
    }

    private int[] getSameLinePosition(int[] po, Laser.Dir dir, ArrayList<int[]> array){
        int[] result = null;
        int minGap;
        int gap;
        switch(dir) {
            case UP:
                minGap = po[0];
                for(int[] p : array){
                    if (p[1] != po[1]) continue;
                    gap = po[0] - p[0];
                    if(gap > 0 && gap <= minGap){
                        minGap = gap;
                        result = p;
                    }
                }
                break;
            case DOWN:
                minGap = rows - po[0];
                for(int[] p : array){
                    if (p[1] != po[1]) continue;
                    gap = p[0] - po[0];
                    if(gap > 0 && gap <= minGap){
                        minGap = gap;
                        result = p;
                    }
                }
                break;
            case RIGHT:
                minGap = cols - po[1];
                for(int[] p : array){
                    if (p[0] != po[0]) continue;
                    gap = p[1] - po[1];
                    if(gap > 0 && gap <= minGap){
                        minGap = gap;
                        result = p;
                    }
                }
                break;
            case LEFT:
                minGap = po[1];
                for(int[] p : array){
                    if (p[0] != po[0]) continue;
                    gap = po[1] - p[1];
                    if(gap > 0 && gap <= minGap){
                        minGap = gap;
                        result = p;
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}
