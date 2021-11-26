package com.example.uu;

public  class DoneCalculate implements Comparable<DoneCalculate>{
    String crewName;
    Float score;

    DoneCalculate(String crewName,Float score){
        this.crewName=crewName;
        this.score=score;
    }

    public String getCrewName() {
        return crewName;
    }
    public Float getScore() {
        return score;
    }

    @Override
    public int compareTo(DoneCalculate doneCalculate) {
        if(doneCalculate.getScore()<score){
            return -1;
        }else if(doneCalculate.getScore()>score){
            return 1;
        }
        return 0;
    }
}