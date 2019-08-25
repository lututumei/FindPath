package com.company;

import java.util.ArrayDeque;

public class Laser {
    enum Dir{
        UP,
        DOWN,
        RIGHT,
        LEFT
    }
    enum Status{
        FORWARD,
        BACKWARD
    }
    public ArrayDeque<Mirror> shortestPath;
    public int shortestSteps;
    private ArrayDeque<Mirror> passedMirrors;
    private  int passedSteps;
    private Status status;
    private boolean finish; //passed all path
    private Dir currentDir;
    private int[] currentPo;
    private LaserMap lMap;
    private Mirror.Dir preferredMDir;

    Laser(LaserMap map, Mirror.Dir dir)
    {
        lMap = map;
        passedSteps = 0;
        passedMirrors = new ArrayDeque<Mirror>();
        currentDir = map.getSourceDir();
        currentPo = map.getSource();
        status = Status.FORWARD;
        finish = false;
        preferredMDir = dir;

        shortestSteps = Integer.MAX_VALUE;
        shortestPath = new ArrayDeque<Mirror>();
    }

    void start()throws Exception{
        try {
            while (!finish) {
                moveToNextStep();
            }
        }
        catch(Exception e){
            System.out.println("encounter error when laser pass map");
            throw e;
        }
    }

    private void moveToNextStep()throws Exception{
        try {
            switch (status) {
                case FORWARD:
                    laserForward();
                    break;
                case BACKWARD:
                    laserBackward();
                    break;
            }
        }
        catch(Exception e){throw e;}
    }

    private void laserForward() throws Exception{
        if(passedSteps > shortestSteps){
            System.out.println("This path isn't the shortest path, backward");
            status = Status.BACKWARD;
            return;
        }

        int[] nextPo = {0, 0};
        try {
            LaserMap.PositionType nextType = lMap.getNextPosition(currentPo, currentDir, nextPo);
            if(nextType == LaserMap.PositionType.TARGET){

                passedSteps += LaserMap.getDistance(currentPo, nextPo);
                currentPo = nextPo;
                if(shortestSteps > passedSteps){
                    System.out.println("update shortest path for now: steps " + passedSteps);
                    shortestSteps = passedSteps;
                    for(Mirror mirror : passedMirrors){
                        Mirror m = new Mirror(mirror.getPo(), mirror.getDir());
                        shortestPath.push(m);
                    }
                }
                else
                    System.out.println("not the shortest path: step " + passedSteps);
                status = Status.BACKWARD;
            }
            else if(nextType == LaserMap.PositionType.MIRROR){
                for(Mirror mirror : passedMirrors){
                    if(mirror.getPo()[0] == nextPo[0] && mirror.getPo()[1] == nextPo[1]){
                        System.out.println("Loop, exit this path");
                        status = Status.BACKWARD;
                        return;
                    }
                }
                passedSteps += LaserMap.getDistance(currentPo, nextPo);
                Mirror m = new Mirror(nextPo, preferredMDir);
                passedMirrors.push(m);
                status = Status.FORWARD;
                currentDir = changeDir(currentDir, preferredMDir);
                currentPo = nextPo;
            }
            else{
                status = Status.BACKWARD;
            }
        }
        catch(Exception e) {throw e;}
    }

    private void laserBackward(){
        if(passedMirrors.size() == 0){
            finish = true;
            return;
        }
        Mirror lastMirror = passedMirrors.peek();
        Mirror.Dir secUsedMDir = (preferredMDir == Mirror.Dir.DIR0 ? Mirror.Dir.DIR1 : Mirror.Dir.DIR0);
        if(lastMirror.getDir() == preferredMDir){
            lastMirror.setDir(secUsedMDir);
            status = Status.FORWARD;
            Dir dirBeforeMirror = changeDir(currentDir, preferredMDir);
            currentDir = changeDir(dirBeforeMirror, secUsedMDir);
        }
        else{
            passedMirrors.pop();
            if(passedMirrors.size() == 0){
                finish = true;
                return;
            }
            Mirror lastSecMirror = passedMirrors.peek();
            passedSteps -= LaserMap.getDistance(lastMirror.getPo(), lastSecMirror.getPo());
            status = Status.BACKWARD;
            currentDir = changeDir(currentDir, secUsedMDir);
            currentPo = lastSecMirror.getPo();
        }
    }
    private Dir changeDir(Dir sourceDir, Mirror.Dir mDir){
        switch(sourceDir){
            case UP:
                return mDir == Mirror.Dir.DIR0 ? Dir.LEFT : Dir.RIGHT;
            case DOWN:
                return mDir == Mirror.Dir.DIR0 ? Dir.RIGHT : Dir.LEFT;
            case RIGHT:
                return mDir == Mirror.Dir.DIR0 ? Dir.DOWN : Dir.UP;
            case LEFT:
                return mDir == Mirror.Dir.DIR0 ? Dir.UP : Dir.DOWN;
        }
        System.out.println("invalid laser direction");
        return Dir.RIGHT;
    }
}
