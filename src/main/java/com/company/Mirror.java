package com.company;

public class Mirror {
    int[] po;
    enum Dir{
        DIR0, /* '\' */
        DIR1  /* '/' */
    }
    private Dir dir;

    public Mirror(int[] m_po, Dir m_dir){
        po = new int[]{0, 0};
        po[0] = m_po[0];
        po[1] = m_po[1];
        dir = m_dir;
    }

    public void setPo(int[] po) {
        this.po[0] = po[0];
        this.po[1] = po[1];
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public int[] getPo() {
        return po;
    }

    public Dir getDir() {
        return dir;
    }
}
